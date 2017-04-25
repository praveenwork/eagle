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

import datetime
import pandas as pd
import re
import numpy as np

def Test_On(modelToUse, model_setting, specs_for_test, test_data_transformed_onehot_with_text, dependentVar, test_data_df_original, colsToUse, is_apply_model):  
	today = datetime.date.today().strftime("%Y%m%d")
	print "First 2 lines of transformed test data ..... "
	print test_data_transformed_onehot_with_text[:,colsToUse][1:2]  # printing first line of code of transformed data
	
	predictions_test = modelToUse.predict(test_data_transformed_onehot_with_text[:,colsToUse])
	test_data_df_original["%s_predicted" % (dependentVar)] = predictions_test
	
	if(model_setting['var_type'] == 'Categorical'):
		test_data_df_original["%s_predicted_labels" % (dependentVar)] = predictions_test
		if(model_setting['multiclass'] == True):
			thisFactor = specs_for_test['thisFactor']
			#keys = np.arange(0,len(thisFactor.classes_))	#np.unique(thisFactor.labels)  # stores information about the factors i.e. labels for the numerical replacement
			#values = thisFactor.classes_ #np.array(thisFactor.levels)  #  key to labels to be replace
			test_data_df_original["%s_predicted_labels" % (dependentVar)] = thisFactor.inverse_transform(test_data_df_original["%s_predicted_labels" % (dependentVar)]) 
		else:
			thisFactor = specs_for_test['thisFactor']
                        #To fix deprecation warning in python dependencies
			keys = np.unique(thisFactor.codes)  # stores information about the factors i.e. labels for the numerical replacement
			values = np.array(thisFactor.categories)  #  key to labels to be replace 
			replace_dependent_var_labels_tmp =  dict(zip(keys.astype(str), values.astype(str)))    # making dictionary to replace the labels
			#Start Changes for latest version compablitiy
			replace_dependent_var_labels = dict()
			for key, value in replace_dependent_var_labels_tmp.iteritems():
				replace_dependent_var_labels[str(key)] = str(value)
			#END *************************************************
			test_data_df_original["%s_predicted_labels" % (dependentVar)] = test_data_df_original["%s_predicted_labels" % (dependentVar)].apply(str)   #  has to be string to read the derived labels columns for predictions   --  if open this bool error for categorical  else non printing of yes and no for labels 
			test_data_df_original["%s_predicted_labels" % (dependentVar)] = test_data_df_original["%s_predicted_labels" % (dependentVar)].replace(replace_dependent_var_labels)
		probabilities_test = modelToUse.predict_proba(test_data_transformed_onehot_with_text[:,colsToUse])[:, 1]
		test_data_df_original["%s_relative_probability" % (dependentVar)] = probabilities_test  # stores the predicted probability 
	
	if(is_apply_model == False):	
		if(os.path.exists(specs_for_test['testDataFile']) and specs_for_test['testFileExists'] == True):
			OutputFile = "%s.%s.OUTPUT.%s.csv" %(specs_for_test['testDataFile'], dependentVar, today)
			print "[INFO] Finished with Test datasets"
		if(os.path.exists(specs_for_test['trainDataFile']) and specs_for_test['testFileExists'] == False):
			OutputFile = "%s.%s.OUTPUT.%s.csv" %(specs_for_test['trainDataFile'], dependentVar, today)
			print "[INFO] Finished with Train datasets"
	else:
		OutputFile = "%s" %(specs_for_test['testDataFile'])
		
	#if(is_apply_model == False):	
	#	testOutputFile = "%s.%s.OUTPUT.%s.csv" %(specs_for_test['testDataFile'], dependentVar, today)
	#else:
	#	testOutputFile = "%s" %(specs_for_test['testDataFile'])
	test_data_df_original.to_csv(OutputFile)
	print "Output written to %s" % (OutputFile)

def getNormalizeHeaderList(inputfile):
	inputDF = pd.read_csv(inputfile, nrows=1)
	normalizedHeadersList = []
	for colName in inputDF.columns:
		normalizedHeadersList.append(getNormalizedHeader(colName))
	return normalizedHeadersList

# Returns normalized header
# Normalized term contains following things:
# If header contains whitespace then it will replaces with "_"(underscore)
# If header contains % then it will replace with "per"
# If header contains # then it will replace with "no"
# If header contains any special character other than alpha-numeric then it will replace with "_"(underscore)
# Header will be convert into lowercase
def getNormalizedHeader(headerName):
	headerName = headerName.replace(" ", "_").replace("%", "per").replace("#", "no").lower()
	headerName = re.sub("[^a-z0-9]", "_", headerName)
	return headerName
