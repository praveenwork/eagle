####################################################################################
# Deep Blue Analytics Inc. CONFIDENTIAL
# Copyright  2013 Deep Blue Analytics Incorporated, a Delaware, USA company
# All Rights Reserved.
# NOTICE:  All information contained herein is, and remains
# the property of Deep Blue Analytics Inc. and its suppliers,
# if any.  The intellectual and technical concepts contained
# herein are proprietary to Deep Blue Analytics Inc.
# and its suppliers and may be covered by U.S. and Foreign Patents,
# patents in process, and are protected by trade secret or copyright law.
# Dissemination of this information or reproduction of this material
# is strictly forbidden unless prior written permission is obtained
# from Deep Blue Analytics Inc.
####################################################################################

import sys
import os
import pickle
import pandas as pd
import filterData as fd
import numpy as np
import traceback
import clientUtils
import getopt

# Global variables
DEFAULT_MODEL = "find_best_model"
DEFAULT_OUTPUT_DIR = "output"
DEFAULT_PKL_FILE_NAME = "ih_model_file.pkl"

#python clientModelApply.py input/sample_test_1k.csv

def applyModel(inputfile, picklefile, outfile, apply_algorithm):
	try:
		all_in_one_pkl_file = open(picklefile, 'rb')
		all_in_one_pkl_files_tuple = pickle.load(all_in_one_pkl_file)
		model_sample_space = all_in_one_pkl_files_tuple[0]
		print "Uploaded model run info with %s successfully" %model_sample_space.keys()
		specs_for_test = all_in_one_pkl_files_tuple[1]
		print "Uploaded model persistence info with %s successfully" % specs_for_test.keys()
		ae_config_dict = all_in_one_pkl_files_tuple[2]
		aeConfig=ae_config_dict['aeConfiguration']
		dependentVar = aeConfig.dependentVar
		print dependentVar    
	except Exception  as e:
		print e
		#print "File %s do not exist. Make sure the path is correct or file exit" %mymodelfile
		sys.exit()

	persisted_mymodels = model_sample_space.keys()
	persisted_business_case_id = model_sample_space[persisted_mymodels[0]]['model_setting']['business_case_id'] 
	persisted_var_type = model_sample_space[persisted_mymodels[0]]['model_setting']['var_type']
	print "[[INFO]] Persisted models were %s " %persisted_mymodels
	print "[[INFO]] Persisted model run is of - %s - type " %persisted_var_type

	if apply_algorithm in persisted_mymodels or apply_algorithm == "find_best_model":
		print "[[INFO]] Model Selection matches with persisted model info"
	else:
		print "[[ERROR]] Something wrong with Model Selection"
		sys.exit()

	nanValues = aeConfig.nanValues.split(',')   # need a list to feed in as na_values while reading a file
	print "%s values replaced with NaN" %(nanValues)

	normalizedHeadersList = clientUtils.getNormalizeHeaderList(inputfile)
	test_data_df_original = pd.read_csv(open(inputfile), quotechar='"', skipinitialspace=True, sep=aeConfig.inputFileSeparator, na_values= nanValues , error_bad_lines = False,converters={'compensation_option':str}, skiprows=1, names=normalizedHeadersList)

	if test_data_df_original.shape[0]>1:
		print "File is workable by AE"
	else:
		print "[[INFO]] File is empty "
		sys.exit()

	colsToUse = specs_for_test['cols2use']     
		
	try:                 
		#----------------------------------------------------------------------------#
	    #Filtering
		if aeConfig.testFilter!="":
		            test_data_df_original = fd.filterData(aeConfig.testFilter,test_data_df_original, 'test')
		            
		test_data_df=test_data_df_original.copy()

		#----------------------------------------------------------------------------#
		 # combine columns
		combineDegree = 2
		if (aeConfig.makeTriplets==True):
		  combineDegree = 3
		  
		output_header_original = specs_for_test['outputHeader']
		catColsNames_org = specs_for_test['catColsNames']
		if (aeConfig.combineColumns == True):   # will not work in standalone because if combine column is true the variables should also be present in the csv or database file
		    print catColsNames_org
		    catColsNames, test_data_df,  outputHeader = fd.CombineCols( combineDegree, test_data_df, catColsNames_org, aeConfig, output_header_original)
		
		#----------------------------------------------------------------------------#
	    #Factorizing
		catColsNames = specs_for_test['catColsNames']
		numColsNames = specs_for_test['numColsNames']
		if aeConfig.factorizeDependent == True:
		    tr_labs=np.asarray(test_data_df.as_matrix(columns=[dependentVar]))
		    leng = len(np.unique(tr_labs))
		    if leng >2 and aeConfig.multiClass == False:
		        print '[INFO] The problem is assumed to be a Regression problem'
		        print '[INFO] Factorization not done'
		    else:
		        if aeConfig.multiClass == True:
		            aeConfig.factorizeDependent = False
		            print '[INFO] Factorization for Multi-Class problem'
		        else:
		            print '[INFO] Factorization for a Binary Classification Problem'
		            fact = specs_for_test['Fact'] 
		            test_data_df = fact.Factorize_test(test_data_df)
		
		test_data_categorical=np.asarray(test_data_df.as_matrix(columns=catColsNames))        ##categorical columns
		test_data_numerical=np.asarray(test_data_df.as_matrix(columns=numColsNames))          ##numerical columns
		    
		#----------------------------------------------------------------------------#
	    #OneHotEncoder
		outputDir = outfile #= outfile 
		specs_for_test['outputDir'] = outfile
		print outputDir
		outputFilePrefix = specs_for_test['outputFilePrefix']
		factorMap=specs_for_test['factorMap']
		encoderToUse = specs_for_test['encoderToUse']
		#outputFilePrefix, factorMap, encoderToUse   ?
		print specs_for_test['catColsNames']
		if(len(catColsNames)>0):
		    test_data_transformed_onehot, oneHotColumnHeaders, factorMap_test  = fd.oneHotTest(catColsNames, test_data_df, outputDir, outputFilePrefix, factorMap, encoderToUse)
		else:
		    test_data_transformed_onehot = test_data_categorical
		    oneHotColumnHeaders = []
		    factorMap_test = []
		
		#----------------------------------------------------------------------------#
	    #Imputation    
		imp = specs_for_test['Imp']
		#test_data_numerical, rowsToUse_Train, scalingFactors, med = imp.Impute_test(test_data_numerical)
		#print type(scalingFactors)
		#test_numerical_data = imp.Impute_test(test_data_numerical)
		test_data_numerical = imp.Impute_test(test_data_numerical)

		#----------------------------------------------------------------------------#
	    #Dense to Sparse   
		numColsIndexes = specs_for_test['numColsIndexes']   
		if (len(numColsIndexes)>0):
		        test_data_numerical_sparse, test_data_transformed_onehot = fd.sparsify(test_data_numerical, test_data_transformed_onehot)    
		        
		#----------------------------------------------------------------------------#
	    #Combining Text Columns
		txtColsIndexes = specs_for_test['txtColsIndexes']
		txtColsNames = specs_for_test['txtColsNames']
		txtVectorizers = specs_for_test['textVectorizer']
		
		test_data_transformed_onehot_with_text = test_data_transformed_onehot.copy()
		if(len(txtColsIndexes)>0):
		      for thisTxtCol in txtColsNames:
		                if thisTxtCol in test_data_df.columns:
		                  test_data_df[thisTxtCol] = test_data_df[thisTxtCol].apply(str)
		
		oneHotColumnHeaders, test_data_transformed_onehot_with_text, txtColsAddedIdx_test = fd.combining_txt(oneHotColumnHeaders, test_data_df, test_data_transformed_onehot,txtColsIndexes, txtColsNames, txtVectorizers)
		
		#----------------------------------------------------------------------------#
	    #Sparse to Dense
		if specs_for_test['model_specs']['convertToDense'] == True or aeConfig.createTrees == True :
		    print "[INFO] Converting test data to dense format..."
		    if(isinstance(test_data_transformed_onehot_with_text,np.ndarray) == False):
		        test_data_transformed_onehot_with_text = test_data_transformed_onehot_with_text.toarray()
		    else:
		        test_data_transformed_onehot_with_text = test_data_transformed_onehot_with_text
		                
		#----------------------------------------------------------------------------#
	    #Model Test
		if(apply_algorithm=="find_best_model"):
		    is_best_model = [key for key, value in model_sample_space.iteritems() if value['is_best_model'] == True]
		    print "[[INFO]] Best model in last run was %s, and thus implementing the same" %is_best_model
		    modelToUse = model_sample_space[is_best_model[0]]['mod2use'] 
		    model_setting = model_sample_space[is_best_model[0]]['model_setting']
		else:    
		    modelToUse = model_sample_space[apply_algorithm]['mod2use']
		    model_setting = model_sample_space[apply_algorithm]['model_setting']
		
		persisted_model_run_file_id = outfile # +apply_algorithm+"."+dependentVar+"."+curr_feeddate+"."+curr_feedsequence+"."+"pred_persistence"
		specs_for_test['testDataFile'] = persisted_model_run_file_id
	except Exception, err:
		    print(traceback.format_exc())
		    #print(sys.exc_info()[0])
		    print "Inconsistencies in data while applying the model. Please call customer support for further help"
		    sys.exit(99)  ##  if change this to any other from 99 ( must to reported to workflow, that code has changed )
		    
	clientUtils.Test_On(modelToUse, model_setting, specs_for_test, test_data_transformed_onehot_with_text, dependentVar, test_data_df, colsToUse, True)
	print "[[INFO]] Prediction file saved at location : %s" %os.path.split(outfile)[0] 

def printhelp():
	print "Usage: python prophesy.zip --input=<input_csv_file_path> [--picklefile=<pickle_file_path>] [--output=<output_csv_file_path>] [--model=<model_name>]"

# Validate user arguments
def getParsedArgs(numberOfArgs):
	if numberOfArgs > 1 and numberOfArgs <= 5:   # check the no. of argument 
		try:
			opts, args = getopt.getopt(sys.argv[1:],"",['input=','output=', 'model=', 'picklefile='])
		except getopt.GetoptError as err:
			print str(err)
			sys.exit()

		if opts == [] or opts[0][0] != "--input":
			print "ERROR: Parameter input is required"
			sys.exit()		
		if args != []:
			print "ERROR: Args can not be null"
			sys.exit()		

		inputfilepath = outputfilepath = model_name = picklefilepath = ""
		
		for opt, arg in opts:
			if opt == "--input":
				inputfilepath = arg
			elif opt == "--output":
				outputfilepath = arg
			elif opt == "--model":
				model_name = arg
			elif opt == "--picklefile":
				picklefilepath = arg

		# Input filepath validation		
		if not os.path.exists(inputfilepath):
			print "ERROR: Input path does not exist"
			printhelp() 
			sys.exit()
		elif not os.path.isfile(inputfilepath) or not inputfilepath.endswith(".csv"):
			print "ERROR: Input is not a csvfile, Please give proper input csvfile" 
			printhelp()
			sys.exit()
		else:
			inputfile = inputfilepath

		# pickle filepath validation
		if picklefilepath != "" or len(picklefilepath) > 0:
			if not os.path.exists(picklefilepath):
				print "ERROR: Pickle path does not exist"
				printhelp() 
				sys.exit()
			elif not os.path.isfile(picklefilepath) or not picklefilepath.endswith(".pkl"):
				print "ERROR: Please give proper pickle file" 
				printhelp()
				sys.exit()
			else:
				picklefile = picklefilepath
		else:
			if not os.path.exists(DEFAULT_PKL_FILE_NAME):
				print "ERROR: There is no",DEFAULT_PKL_FILE_NAME,"in current directory." 
				printhelp()
				sys.exit()
			else:
				picklefile = DEFAULT_PKL_FILE_NAME
		
		# output filepath validation
		if outputfilepath != "" or len(outputfilepath) > 0:	# When user not gives an output file path then system will make output file at current directory
			if not os.path.exists(os.path.split(outputfilepath)[0]):
				print "ERROR: Output path does not exist"
				printhelp()
				sys.exit()
			elif not outputfilepath.endswith(".csv"):
				print "ERROR: Output is not a csvfile, Please give proper output csvfile" 
				printhelp()
				sys.exit()
			else:
				outputfile = outputfilepath
		else:
			inputParent, inputFileName = os.path.split(inputfile)
			inputFileBaseName, inputFileExtension = os.path.splitext(inputFileName)
			if not os.path.exists(DEFAULT_OUTPUT_DIR):
				os.mkdir(DEFAULT_OUTPUT_DIR)
			outputfile = DEFAULT_OUTPUT_DIR + os.sep + inputFileBaseName + "_applied" + inputFileExtension

		# model name validation
		if model_name != "" or len(model_name) > 0:
			if model_name in ("dtr","etr","gbr","lr", "dtc", "etc", "gbc", "logistic", "nb", "sgdc"):
				apply_algorithm = model_name
			else:
				print model_name,"is not a valid model_name","please give model name from (dtr,etr,gbr,lr, dtc, etc, gbc, logistic, nb, sgdc)"
				printhelp()
				sys.exit()
		else:
			apply_algorithm = DEFAULT_MODEL
	else:
		printhelp()
		sys.exit()
	
	return inputfile, picklefile, outputfile, apply_algorithm

############################################################################################################
# Entry point for activate model.
# There are mainly four argument input, picklefile, output, model.
# input is mandatory argument and rest of these are optional.
# where, 
#	- input expects csv_file_path, i.e, /home/avisunpc1/Desktop/TS.csv
#	- picklefile expects pickle_file_path, i.e, /home/avisunpc1/Desktop/xyz.pkl
#		- when user does not give picklefile then system finds "ih_model_file.pkl" in current directory, 
#		  If there is no "ih_model_file.pkl" in current directory then system throws an error.
#	- output expects output_csv_file_path, i.e, /home/avisunpc1/Desktop/xyz.csv
#		- when user does not give output csv file path then system will make "output" directory in current 
#		  directory and stores output csv file named as "input_csv_file_name"+"_applied.csv" in it.
#	- model expects model abbreviation from (dtr,etr,gbr,lr, dtc, etc, gbc, logistic, nb, sgdc)
#		- when user does not give model then system activates model with find_best_model.
############################################################################################################
numArgs = len(sys.argv)
print "numArgs",numArgs
inputfile, picklefile, outputfile, apply_algorithm = getParsedArgs(numArgs)

print "\ninputfile:", inputfile, "\npicklefile:",picklefile, "\noutputfile:", outputfile, "\napply_algorithm:",apply_algorithm
#sys.exit()
applyModel(inputfile, picklefile, outputfile, apply_algorithm)
