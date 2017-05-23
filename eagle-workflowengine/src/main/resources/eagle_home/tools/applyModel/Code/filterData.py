import ConfigParser
from aeConfig import AEConfig
import utils
import cPickle as pickle
import datetime
import os
import pandas as pd
from sklearn import (linear_model, preprocessing, metrics, cross_validation, feature_selection, svm, ensemble, tree, neural_network, cluster, grid_search, neighbors, decomposition)
from sklearn.naive_bayes import (GaussianNB, MultinomialNB)
from sklearn.feature_extraction.text import TfidfVectorizer,CountVectorizer
from sklearn.externals.six import StringIO
from scipy import io
from scipy import sparse 
import scipy
import numpy as np
import random
import sys
import math
import time
from pandas import Categorical
#from memory_profiler import profile


#from statsmodels.sandbox.tests import model_results

# StoreFiles function 
# Description: 
#	1)  Create the output directory if the path do not exist  

def StoreFiles(aeConfig, datafile):
	outputFilePrefix = os.path.basename(os.path.realpath(datafile))
	outputFilePrefix = outputFilePrefix.replace("/",".")
	
	outputDir = str(os.path.dirname(os.path.realpath(datafile)))       #default output dir is same dir as input data file
	if aeConfig.outputDir!="":
  		outputDir = aeConfig.outputDir                                 # gets the output directory path if its blank 

	if not os.path.exists(outputDir):								   # if path do not exist creates one	
  		print "[INFO] Output directory does not exist, creating: %s" % (outputDir)
  		os.makedirs(outputDir)
	return outputDir, outputFilePrefix	

def loadfiles(x):
	f = open(x , 'rb')
	return pickle.load(f)
	f.close()

def createfiles(x, y):##### Need to specify the path
	f = open(y, 'wb')
	pickle.dump( x, f)
	f.close()
	
#----------------------------------------------------------------------#
# Filter Function
#----------------------------------------------------------------------#
# Filter data based on given filter =,>,< . The column names and values are separated based on given filter symbol.
# If the filter symbol is not empty the train data file is filtered based on given separator 

def filterData(filters, datafile, dat):
    
  	Filter = filters
        if(len(Filter.split('='))==2 or len(Filter.split('<'))==2 or len(Filter.split('>'))==2):
    	    if(len(Filter.split('='))==2):
     	    	FilterOperator="="
    	    if(len(Filter.split('<'))==2):
      		FilterOperator="<"
    	    if(len(Filter.split('>'))==2):
      		FilterOperator=">"
	    #if(len(Filter.split('>='))==2):
            #    FilterOperator=">="	
	    thisSplit = Filter.split(FilterOperator)
    	    FilterColumn = thisSplit[0]
    	    FilterValue = float(thisSplit[1])
	    numRowsOriginal = datafile.shape[0]
            print ("FILTERING" + dat +  "DATA ... ")
            sys.stdout.flush()
            datafile = utils.filterDataframe[FilterOperator](datafile, FilterColumn, FilterValue)
            numRowsNew = datafile.shape[0]    	    
	    if numRowsNew == 0:
	        print "\n[ERROR] Filtering resulted in 0 rows in training dataset, exiting. Following filter was used:\n\t%s" %(Filter)
		sys.exit()
	    datafile.index = range(datafile.shape[0])
        else:
            print "[ERROR] Unable to handle filter for  data: %s" % (Filter)
    	    sys.exit()
	return datafile

#----------------------------------------------------------------------#
# Headers Function
#----------------------------------------------------------------------#
# Description :  Reads the file headers to extract variables
# 	1) read input file headers and strip quotes
#   2) Stores variables names as list 
#   3) Gets index of dependent variable from the list, overwrites if the variable name already exist
#   4) Print out the categorical , numerical and text variables 
#   5) Prints out index of the categorical, numerical and text variables

def Headers(datafile, aeConfig, dependentVar, cats, nums, txts):
	hasHeader=True
  	numLinesToSkip = 1

  	with open(datafile, 'r') as f:                    # reading file headers to get the variable names           
    		headerLine =  f.readline()
    		headerLine = headerLine.strip()
    		if(aeConfig.stripQuotes==True):
      			print "[PRE] Stripping quotes from header line"
      			headerLine = headerLine.replace('"', '');
	  			
  	headerSplit = headerLine.split(aeConfig.inputFileSeparator)        # get header as list by using the seperator mentioned in config file
  	if(len(headerSplit) == 1 and aeConfig.autoDetectSeparator == True):
		foundSuccSplit = False
		for seps in [',', '\t', '|']:
			newHeaderSplit = headerLine.split(seps)
    			if(len(newHeaderSplit)>1):
      				aeConfig.inputFileSeparator = seps
      				print "File appears to be '%s'-delimited. If not, set autoDetectSeparator=false" %(seps)
      				headerSplit = newHeaderSplit
				foundSuccSplit = True
				break
		if foundSuccSplit == False:
			print("[ERROR] Unable to split training data using supplied delimiter, or using standard delimiters. Exiting")
      			sys.exit()

  	print "[DATA] Header: %s" % (headerLine)
 
 	dependentCol = 0               # get index of dependent variable from the list 
	try:
		dependentCol = headerSplit.index(dependentVar)
	except ValueError:
    		if aeConfig.useDerivedDependent:              # if derived dependent variable 
      			print "[INFO] Need to use derived dependent var '%s'" % (dependentVar)
      			print dependentVar
			if dependentVar in headerSplit:
        			print "[WARNING] Column with same name as derived dependent variable '%s' already exists. Will be overwritten." % (dependentVar)
      			else:
        			headerSplit.append(dependentVar)
        			dependentCol = headerSplit.index(dependentVar)
        			print "[INFO] Adding derived dependent variable '%s' as column #%d'" % (dependentVar,dependentCol)
    		else:
      			print "[ERROR] Dependent variable '%s' not found in header row. Exiting." % (dependentVar)
      			

  	print "[DATA] Dependent Var is column %d" % (dependentCol)


	catColsIndexes = []
	numColsIndexes = []
	txtColsIndexes = []

    #  Stores and prints the categorical, numerical and text variables names and index
    
  	if aeConfig.catCols == "all":   #use everything except the dependendt col
    		if(dependentCol == 0):
      			catColsIndexes = range(1,len(headerSplit))
    		elif(dependentCol==(len(headerSplit)-1)):
        		catColsIndexes = range(0,len(headerSplit)-1)
      		else:
        		catColsIndexes = range(0,dependentCol)
        		catColsIndexes.extend(range(dependentCol+1,len(headerSplit)))
	else:
    		print ("[DATA] Categorical columns: "),
    		for thisCol in cats:                              # printing the category columns        
      			print ("%s " % (thisCol)),
      			catColsIndexes.append(headerSplit.index(thisCol))
    		print ""
    		if(aeConfig.numCols != ""):                       # printing the numerical columns    
     			print ("[DATA] Numerical columns: "),
      			for thisCol in nums:
        			print ("%s " % (thisCol)),
        			numColsIndexes.append(headerSplit.index(thisCol))
      			print ""
    		if(aeConfig.txtCols != ""):                        # printing the text columns 
      			for thisCol in txts:
        			print "[DATA] Text column: %s\n" % (thisCol)
        			txtColsIndexes.append(headerSplit.index(thisCol))
  		print "Categorical column indexes: %s" % (str(catColsIndexes))   
  		print "Numerical column indexes: %s" % (str(numColsIndexes))
  		print "Text column indexes: %s" % (str(txtColsIndexes))
	
	return catColsIndexes, numColsIndexes, txtColsIndexes

# Headers_test function 
# Description :  read headers for test dataset

def Headers_test(testDataFile, aeConfig):
	hassHeader=True
	numLinesToSkip = 1
	with open(testDataFile, 'r') as f:        # open testdatafile to read
     		headerLine = f.readline()
     		headerLine = headerLine.strip() 
	if(aeConfig.stripQuotes==True):           #  strip quotes from header if find it true in aeConfig
     		print "[PRE] Stripping quotes from header line"
     		headerLine = headerLine.replace('"', '');
     		headerSplit = headerLine.split(',')
	if(len(headerSplit) == 1 and aeConfig.autoDetectSeparator == True):
     		newHeaderSplit = headerLine.split('\t')
     		if(len(newHeaderSplit)>1):
        		aeConfig.inputFileSeparator = '\t'
        		print "File appears to be TAB-delimited. If not, set autoDetectSeparator=false"
        		headerSplit = newHeaderSplit
	return aeConfig, headerLine

#----------------------------------------------------------------------#
# Combining Column
#----------------------------------------------------------------------#
# Description : Combine the category columns based on the combine degree given . e.g. if we have 2 as combine degree 
# then we will have 2 way interaction of all the categorical variables . ['var1','var2','var3']
# outcome will be ['var1','var2','var3','var1:var2','var2:var3','var3:var1'] with 3 more variables added as categorical column 


def CombineCols(combineDegree, datafile, catColsName, aeConfig, outputHeader):
	catColsNames_original = catColsName
	print catColsNames_original
	for thisDegree in range(2, combineDegree+1):
		all_data = datafile[catColsNames_original]
		new_data, new_cols = utils.group_data(all_data, thisDegree, True, aeConfig.combineColumnsMaxCard)	
		new_datafile = new_data.iloc[range(all_data.shape[0])][new_cols]
        	new_datafile = new_datafile.set_index(datafile.index)
        	datafile = datafile.join(new_datafile[new_cols])
		catColsName.extend(new_datafile.columns)
        	for thisColName in new_datafile.columns:
                	outputHeader = "%s,%s" % (outputHeader, thisColName)
		print np.shape(datafile)
		print catColsName
	return catColsName, datafile, outputHeader

# oneHotTrain function 
# Description :  transforms categorical variables into multiple columns
#   1) if a variable has 30 possible values , it creates 30 new columns 
#   2) dump factor map information into a file in the give output directory 

 
def oneHotTrain(catColsNames, datafile, outputDir, outputFilePrefix):
        today = datetime.date.today().strftime("%Y%m%d")
        oneHotColumns = catColsNames
        for colname in oneHotColumns:
                print ("%s, " % (colname))
        print ""
        dataForEncoderFit = datafile[oneHotColumns]
        print "\nEncoding training data ..."
        (data_transformed_onehot, factorMap, encoderTo, newColNames) = utils.OneHotEncoderFaster(dataForEncoderFit, oneHotColumns)
        (data_transformed_onehot, factorMap, encoder, newColNames_train) = utils.OneHotEncoderFaster(datafile, oneHotColumns, factorMap, encoderTo)
        
        print "\n[INFO] train data has %d cols, %d rows (post one-hot)" % (data_transformed_onehot.shape[1], data_transformed_onehot.shape[0])

        onehotKeymapFile_train = "%s/%s.%s.onehotkeymap.train" %(outputDir, outputFilePrefix, today)
        pickle.dump(factorMap, file(onehotKeymapFile_train,'w'))
        oneHotDataFile = "%s/%s.%s.onehot" % (outputDir, outputFilePrefix, today)
        print "\nSaving transformed one hot data set to %s" % (oneHotDataFile)
	oneHotColumnHeaders = newColNames_train
        
        return data_transformed_onehot, oneHotColumnHeaders, factorMap, encoder


def oneHotTest(catColsNames, datafile, outputDir, outputFilePrefix, factorMap, encoderToUse):
        oneHotColumns = catColsNames            # names of columns to be hot encoded
        today = datetime.date.today().strftime("%Y%m%d")
        for colname in oneHotColumns:
                print ("%s, " % (colname))     # prints name of columns in log file
        print ""

        print "Encoding testing data ..."
        (data_transformed_onehot, factorMap_test, encoder_test, newColNames_test) = utils.OneHotEncoderFaster(datafile, oneHotColumns, factorMap, encoderToUse)
        
        print "[INFO] test data has %d cols, %d rows (post one-hot)" % (data_transformed_onehot.shape[1], data_transformed_onehot.shape[0])
        onehotKeymapFile_test = "%s/%s.%s.onehotkeymap.test" % (outputDir, outputFilePrefix, today)
        return data_transformed_onehot, newColNames_test, factorMap_test

#----------------------------------------------------------------------#
# Factor Class
#----------------------------------------------------------------------#
# Description : Here we factorize the string values of the categorical dependent variable
# For example, if dependent variable has Yes and No, they will be translated to 1 and 0
# User specifies for which class 1 will be assigned by a configuration variable positive_label
# During factorization, original data frame is not touched. We are translating and creating a new data frame
# 1) Factorize dependent variable on defined positive class labels
#        a) a positive label if define 0 will be labeled as 1 in dependent variable for categorical       
# 2) Save the factorize information to apply it to test dataset
# 3) Factorization for multiclass problem is also an option

class Factorize:
	
	def __init__(self,  vari, pos_labels):
		self.datafile = None
		self.vari = vari
		self.pos_labels = pos_labels
		self.thisFactor = None

	# Factorize_train and Factorize_test  function
	# Description :  Factorize train the dependent variables
	# 	1) assign labels for the keys as per positive labels
	#   2) replace labels with the numerical values with positive labels defined by the user   
		
	def Factorize_train(self, datafile):
		self.datafile = datafile
		df_levels = pd.get_dummies(datafile[self.vari])
		keys = list(df_levels.columns) 
		if(str(df_levels.columns[0]) == self.pos_labels):     # assign labels for the keys as per positive labels
			keys = keys[::-1]
		else:
			keys = keys
		print keys	
		dependentValues = (self.datafile[self.vari]).apply(str)  # convert to string each value of dependent values
		self.thisFactor = Categorical(dependentValues,keys)
		#self.thisFactor = pd.Categorical.from_array(dependentValues)   # make a categorical type of dependent values
		print type(self.thisFactor)
		#print self.thisFactor.levels
		#self.datafile[self.vari] = df_levels[self.pos_label]
                #To fix deprecation warning in python dependencies
		self.datafile[self.vari]=[self.thisFactor.categories.get_loc(elt) for elt in (self.datafile[self.vari])]   # replace each label with positive labels
		print 'Factorizing'
		return self.datafile, self.thisFactor
	
	def Factorize_test(self, datafile):
		self.datafile = datafile
		#To fix deprecation warning in python dependencies
		self.datafile[self.vari]=[self.thisFactor.categories.get_loc(elt) for elt in (self.datafile[self.vari])]
		return self.datafile		

class Impute:
	
	def __init__(self, colname, medians, scales, rowsToUse_train):
		self.colname = colname
		self.medians = medians
		self.scales = scales
		self.datafile = None
	 	self.trainMedians = dict()
		self.scalingFactors = dict()
		self.rowsToUse_train = rowsToUse_train

	# Impute_train and Impute_test function 
	# Description : replace the numerical columns as blank with medians	
	# 	1) Get the name of the variable to be imputed
	#   2) Loop through all the variables and replace the missing values with the medians
	
	def Impute_train(self,datafile):
		self.datafile = datafile
		rowsToSkip_train = []
		rowsToSkip_test = []
		for thisNumCol in range(self.datafile.shape[1]):
  			thisColName = self.colname[thisNumCol]
  			thisColRowsToSkip=(np.where(np.isnan(self.datafile[:,thisNumCol])))[0]
  			thisColRowsToUse=(np.where(~np.isnan(self.datafile[:,thisNumCol])))[0]
			#print thisColRowsToUse	
  			if len(thisColRowsToUse)==0:
    				self.datafile[:,thisNumCol] = np.zeros(self.datafile.shape[0])

  			if(self.medians == True):                  # replacing the missing values with median if replace median is True
    				thisMedian = np.median(self.datafile[thisColRowsToUse,thisNumCol])
    				if np.isnan(thisMedian):
      					thisMedian=0
				self.trainMedians[thisColName] = thisMedian
    				self.datafile[thisColRowsToSkip,thisNumCol] = thisMedian
    				
				if(len(thisColRowsToSkip)>0):
      					print "Replacing %d rows of numerical Column %d (%s) in Train Data with median value (%.4f) from Train Data\n " % (len(thisColRowsToSkip), thisNumCol, thisColName, thisMedian)
			

			else:   ##if we're not replacing the N/A values with the median, don't use them for training
    				rowsToSkip_train = np.union1d(rowsToSkip_train, thisColRowsToSkip)
    				self.rowsToUse_train = np.intersect1d(self.rowsToUse_train, thisColRowsToUse)
    				print "\n[INFO] Skipping %d rows in train data due to NAN values" % (len(rowsToSkip_train))
			
  			if(self.scales==True):
    				thisMax = 1
    				if len(thisColRowsToUse)>0:
      					thisMax = np.max(np.abs(self.datafile[thisColRowsToUse,thisNumCol]))
					#print thisMax
    				if thisMax!=0 and ~np.isnan(thisMax):
      					print "[INFO] Scaling column '%s' by: %.2f in train data (min: %.4f, max:%.4f)" % (thisColName, thisMax, np.min(self.datafile[:,thisNumCol]), np.max(self.datafile[:,thisNumCol]))

      					self.datafile[:,thisNumCol] = np.divide(self.datafile[:,thisNumCol], thisMax)
      					self.scalingFactors[thisColName] = thisMax
		
		return self.datafile, self.rowsToUse_train, self.scalingFactors, self.trainMedians
	
	def Impute_test(self,datafile):
		self.datafile = datafile 	

		#ANIL: DELETE
		print "Column Names", self.colname, "\n Length:", len(self.colname)
		print "SHAPE: number of columns", range(self.datafile.shape[1])
			
		for thisNumCol in range(self.datafile.shape[1]):
			# ANIL: DELETE AFTER test
			print "Col Number", thisNumCol
    			thisColName = self.colname[thisNumCol]
    			thisMedian = 0
    			if thisColName in self.trainMedians:
      				thisMedian = self.trainMedians[thisColName]
    			
			thisScalingFactor=1
    			if thisColName in self.scalingFactors:
      				thisScalingFactor = self.scalingFactors[thisColName]
			if self.scales and thisScalingFactor!=1:
      				print "[INFO] Scaling column '%s' by %.2f in test data (min: %.4f, max:%.4f)" % (thisColName, thisScalingFactor, np.min(self.datafile[:,thisNumCol]), np.max(self.datafile[:,thisNumCol]))
      				self.datafile[:,thisNumCol] = np.divide(self.datafile[:,thisNumCol], thisScalingFactor)
    			if (self.medians == True):
      				thisColNaRows=(np.where(np.isnan(self.datafile[:,thisNumCol])))[0]
      				if(len(thisColNaRows)>0):
        				print "[INFO] Replacing %d rows of numerical Column %d (%s) in Test Data with median value (%.4f) from Train Data " % (len(thisColNaRows), thisNumCol, thisColName, thisMedian)
        				self.datafile[thisColNaRows, thisNumCol] = thisMedian	


		return self.datafile

#----------------------------------------------------------------------#
# Sparsify Function
#----------------------------------------------------------------------#
	
# Sparse matrices can be used in arithmetic operations: they support addition, subtraction, multiplication, division, and matrix power.
# Advantages of the CSR format
	# efficient arithmetic operations CSR + CSR, CSR * CSR, etc.
	# efficient row slicing
	# fast matrix vector products
# Disadvantages of the CSR format
	# slow column slicing operations (consider CSC)
	# changes to the sparsity structure are expensive (consider LIL or DOK)
	
def sparsify(datafiles, datafiles_trans):
	datafiles_sparse = sparse.csr_matrix(datafiles)   # convert matrix to csr type
	if datafiles.shape[0] == 1:
		datafiles_sparse = datafiles_sparse.T
	print "[INFO] Adding %d numerical columns (%d rows)" % (datafiles_sparse.shape[1], datafiles_sparse.shape[0])
	print "[INFO] Before: %d x %d" % (datafiles_trans.shape[0], datafiles_trans.shape[1])
	if datafiles_trans.shape[1]>0:
		datafiles_trans = sparse.csr_matrix(sparse.hstack([datafiles_trans, datafiles_sparse]))
	else:
		datafiles_trans = datafiles_sparse ##no categorical columns specified, only numerical
	print "[INFO] After: %d x %d" % (datafiles_trans.shape[0], datafiles_trans.shape[1])

	return datafiles_sparse, datafiles_trans

#----------------------------------------------------------------------#
# Model Function
#----------------------------------------------------------------------#
# Description :  apply model 
#	1) Removes blank, empty values in dependent variable
# 	2) Run models for categorical and numerical variable
#   3) Perform LOOCV if the dataset is less than 200  
#   4) Select records based on given yearly CV date to perform yearly CV 

def Model(aeConfig, train_data_transformed_onehot, train_data_df, train_labels,dependentVar,model_setting):
	#if(aeConfig.removeDependentVarNaN==True):
	#	myindex = np.where(np.isfinite(train_data_df[dependentVar].values)) #train_data_df.ix[train_data_df[dependentVar].values >=0].index
	#	train_labels = train_labels[myindex]
	#	train_data_transformed_onehot = train_data_transformed_onehot[myindex]
	
	#decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])	
	#mod2 = {'isRegression': False}
	cv = None
	clf = None
	mod2use = None
	model_sample_space = dict()
	
	################################s#########  declaring all the model we can build in strategy insight   ############################
	###########################   may contain both regression and classification model  ##############################################
	categorical_universe = aeConfig.categorical_model_universe.split(',')
	numerical_universe = aeConfig.regression_model_universe.split(',')
	#numerical_universe + categorical_universe ",".join(l3)
	si_models= ['lr','etr','gbr','dtr','svr','etc','sgdc','gbc','dtc','logistic','lsvc','nb']    
	si_function = [lr,etr,gbr,dtr,svr,etc,sgdc,gbc,dtc,logistic,lsvc,nb] 	
	model_options = dict(zip(si_models,si_function))   # dictionary created to call functions 
	
	# The following section make a list of model, selected to be trained based on if categorical numerical 
	 
	if(model_setting['var_type']=="Numerical" and len(np.unique(train_labels))>2):
		trying_model = numerical_universe
		if(model_setting['modelType'] == 'find_best_model'):
			trying_model = trying_model
		else:
			if(model_setting['modelType'] in trying_model):      # checking if numerical variable if defined runs the numerical model
				print "Building model on '< Numerical Dependent >' Variable"
				trying_model = [model_setting['modelType']]
			else:
				print "Wrong model type chosen for Regression dependent variable .. Choose out of %s " %(trying_model)	
				sys.exit()
	else:
		if(model_setting['multiclass'] == False and 'nb' in categorical_universe):
			categorical_universe.remove('nb')   # removing na from binary because AUC not available 
		trying_model =  categorical_universe   # lsvc is out #float error correction
		if(model_setting['modelType'] == 'find_best_model'):
			trying_model = trying_model
		else:
			if(model_setting['modelType'] in trying_model):  # checking if categorical variable if defined runs the categorical model
				trying_model = [model_setting['modelType']]
				print "Building model on '< Categorical Dependent >' Variable"			
			else:
				print "Wrong model type chosen for classification dependent variable  .. Choose out of %s " %(trying_model)
				sys.exit()

	#print " --------------------Finding Model to built-----------------------------------"
	for mym in trying_model:    # iteration for all the models   
		mymodel = dict()
		mym_setting = model_setting  				# storing default model setting that can be given by the user 
		model_chosen = model_options[mym]  			# setting model chosen as a function need to be called 
		mod2use,model_setting,clf = model_chosen(aeConfig,mym_setting,train_data_transformed_onehot)    # call the function name need to be trained
		print "\n\n"
		mymodel['mod2use'] = mod2use
		mymodel['model_setting'] = model_setting
		mymodel['clf'] = clf 	
		model_sample_space.update({mym:mymodel})        # 
	seed = random.randint(1,50) 						# always use a seed for randomized procedures		
	cv_test_size = 1-aeConfig.cvperc 					# generating CV for model building
	
	#   
  	if(model_setting['cv_type'] == 'fixed'):
  		cv = cross_validation.ShuffleSplit(train_data_transformed_onehot.shape[0], n_iter=aeConfig.cvfolds, test_size=cv_test_size, random_state=seed)
		model_setting['yearlyOutput']=None
  	
  	# if cv_type is yearly then creates a cross validation set at random
  	if(model_setting['cv_type'] == 'yearly'):   
		#cv = cross_validation.ShuffleSplit(train_data_transformed_onehot.shape[0], n_iter=aeConfig.cvfolds, test_size=cv_test_size, random_state=seed)
		if aeConfig.yearlyCVColumn <> 'None':
  			if aeConfig.yearlyCVColumn not in train_data_df.columns:
    				raise ValueError('Yearly CV column is not in dataset. Not performing yearly CV.')
  			else:
  					cvdate = np.array(pd.to_datetime(train_data_df[aeConfig.yearlyCVColumn]).map(lambda x: x.strftime('%Y%m%d')),dtype='int')
  					cv = utils.YearlySplit(cvdate, aeConfig.yearlyCVStartDate)
			  		model_setting['yearlyOutput']=None
    
    # if cv_type is loocv then creates a cross validation set at random if dataset is less than 200 
	if(model_setting['cv_type'] == 'loocv'):
		if (len(train_labels)>=10 and len(train_labels) <=200):     # 
			cv = cross_validation.LeaveOneOut(len(train_labels))
			model_setting['yearlyOutput']=None
		else:
			print("Too large dataset for using loocv. Should be less than or equal to 200 records.")
			sys.exit()

	# if createTrees == True then call create an object to create tree 
	if (aeConfig.createTrees == True and model_setting['multiclass'] == False):
		if len(np.unique(train_labels))>2:      #Regression
			model_chosen = model_options['etr']
			print "[MODEL] %d unique dependent values, building regression model" % (len(np.unique(train_labels)))
			model4Tree,model_setting2,clf2 = model_chosen(aeConfig,model_setting,train_data_transformed_onehot)
		else:
			model_chosen = model_options['etc']
			model4Tree,model_setting2,clf2 = model_chosen(aeConfig,model_setting,train_data_transformed_onehot)
	else:
			model4Tree = []
	return  cv, model4Tree, train_data_transformed_onehot, train_data_df, train_labels, model_sample_space
		

# textVect function 
# Description :  text mining the text columns found in the variable list
# Convert a collection of raw documents to a matrix of TF-IDF features. Removes the strop words in any 
# Exclude bags of words that occur too frequently ( > 10% of total words) or too infrequently (less than 1% of words)

def textVect(txts, datafile, lb, ub):
	tV = dict()
  	for thisTxtColName in txts:
    		tV[thisTxtColName] = TfidfVectorizer(stop_words=None, ngram_range=(lb,ub),max_df=0.1, min_df=0.01)
    		print "[TEXT] Fitting text Vectorizer for '%s' for all data" % (thisTxtColName)
    		sys.stdout.flush()
    		tV[thisTxtColName].fit(datafile[thisTxtColName])
    	return tV


def fitTextVectorizer(thisCvn, thisTxtColName):
#	print "[TEXT] Fitting text Vectorizer (%d grams) for '%s' for CV Instance %d" % (ngram_range_ub,thisTxtColName, thisCvn)
  	sys.stdout.flush()
#   	cvInstances[thisCvn].txtVectorizers[thisTxtColName].fit(train_data_df[thisTxtColName][cvInstances[thisCvn].train_index])


# combining_txt function 
# Description :  Add text based columns to train dataset
# 1) 

def combining_txt(oneHotColumnHeaders, datafile, datafile_trans, txtColsIndexes, txtColsNames, txtVectorizers):
	datafile_trans_text = datafile_trans.copy()
	txtColsAddedIdx = []
	textColHeaders = []
	if len(txtColsIndexes)>0 :
		thisTxtColIdx = 0
  		for thisTxtColName in txtColsNames:
    			txtTimeStart = int(round(time.time() * 1000))
    			sys.stdout.flush()
    			thisTxtVectorizer = txtVectorizers[thisTxtColName]
    			txtVectors_data = thisTxtVectorizer.transform(datafile[thisTxtColName])
    			txtFeatureNames_data = thisTxtVectorizer.get_feature_names()
    			if txtVectors_data.shape[0]==1:
      				txtVectors_data = txtVectors_data.T
    			txtColsAddedIdx = range(datafile_trans.shape[1], datafile_trans.shape[1]+txtVectors_data.shape[1])
    			print "[TXT] Adding %d text-based columns to Train data" % (len(txtColsAddedIdx))
			print " Column Heaer Lenth:%d" %len(oneHotColumnHeaders)
    			if(thisTxtColIdx==0):
				# if there are no numerical or categorical columns, just merge with text DF
				if len(oneHotColumnHeaders)>0 :
      					datafile_trans_text = sparse.csr_matrix(sparse.hstack([datafile_trans, txtVectors_data]))
      				else:
      					datafile_trans_text = sparse.csr_matrix(sparse.hstack([txtVectors_data]))

      				#datafile_trans_text = sparse.csr_matrix(sparse.hstack([datafile_trans, txtVectors_data]))
    			else:
      				datafile_trans_text = sparse.csr_matrix(sparse.hstack([datafile_trans_text, txtVectors_data]))
    			textColHeaders.extend(["%s=%s" % (thisTxtColName, element) for element in txtFeatureNames_data])
    			thisTxtColIdx=thisTxtColIdx+1
    			txtTimeEnd = int(round(time.time() * 1000))
    			print "[TXT] Column '%s' processed in %.3f s" % (thisTxtColName, float(txtTimeEnd - txtTimeStart)/1000)
  		oneHotColumnHeaders = oneHotColumnHeaders + textColHeaders

	return oneHotColumnHeaders, datafile_trans_text, txtColsAddedIdx
	 


################  ALL MODEL FUNCTION SECTION  ####################  
# Description : All the function models 
#	In General - 
#   1) Model fit is created for each selected model in the function
#   2) Grid parameter declared if grid search is set true
#   3) Returns the model, model_setting and clf  


def logistic(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Logistic Regression"
		
	if (aeConfig.chisqperc<100):
		print "[WARNING] Chi-Sq filter (%.2f %%) only works for categorical columns. All numerical cols will be used" % (aeConfig.chisqperc)
	
	mod2use = linear_model.LogisticRegression()	
	
	keys = ['C','penalty']
	value = [[0.01, 0.001, 0.0001],['l1','l2']]
	model_parameters_4_grid = dict(zip(keys,value))
	
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
					
	return mod2use,model_setting,clf 	


def gbr(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Gradient Boosted Regressor"   #  model_setting['var_type']='numerical'  no need now , useless bits on and off
	decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False

	mod2use = ensemble.GradientBoostingRegressor(n_estimators=aeConfig.ensembleEstimators, learning_rate=1.0, max_depth=aeConfig.treeDepth, random_state=0,max_features=decisionTreeMaxFeatures, loss='lad', verbose=1)
   	
   	keys = ['max_depth','max_features','n_estimators']
	value = [[3,9,15],['sqrt','log2'],[20, 50, 100,200]]
	model_parameters_4_grid = dict(zip(keys,value))
	
   	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
   	return mod2use,model_setting,clf
 

def dtr(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Decision Tree Regressor"   # model_setting['var_type']='numerical'  no need 
   	model_setting['convertToDense']=True
   	model_setting['printCoef']=False
   	model_setting['displayTree']=True
   	mod2use = tree.DecisionTreeRegressor()
	
	keys = ['max_depth','max_features']
	value = [[3,9,15],['sqrt','log2']]
	model_parameters_4_grid = dict(zip(keys,value))
		
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
		min_samples_at_leaf=2
		controlling_depth=25  # This can be adjusted using configuration own the line
		try:
			min_samples_at_leaf=math.floor(train_data_transformed_onehot.shape[0]*(0.0001))
			print "Computed minimum samples at leaf:" , min_samples_at_leaf
			if(min_samples_at_leaf<2):
				min_samples_at_leaf=2   # has to be at least two
		except:
			print "CRITICAL:  there is an error in computing minimum samples at leaf"
		mod2use = tree.DecisionTreeRegressor(min_samples_leaf=min_samples_at_leaf,min_samples_split= min_samples_at_leaf, max_depth=controlling_depth)
	
	return mod2use,model_setting,clf

   
def svr(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Support Vector Regressor"  # model_setting['var_type']='numerical'
	
	model_setting['printCoef']=False
	model_setting['convertToDense']=True
	mod2use = svm.SVR()
   	
   	keys = ['C','epsilon','kernel','tol']
	value = [[0.01, 0.001, 0.0001],[ 0.0001,0.0201,0.0401,0.0601,0.0801],['linear','poly','rbf','sigmoid'],[.00001,.0001,.001,.01,.1]]
	model_parameters_4_grid = dict(zip(keys,value))
	
   	if model_setting['gridSearch'] == True:
   		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
	
	return mod2use,model_setting,clf
 

def etr(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Random forest : Extra Tree Regressors"
	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False
	model_setting['printFeatureImportance']=True		
	mod2use = ensemble.ExtraTreesRegressor(n_estimators=aeConfig.ensembleEstimators, max_depth=aeConfig.treeDepth, min_samples_leaf=max(1, (aeConfig.minPercInNode/100)*train_data_transformed_onehot.shape[0]), criterion='mse')
	
	keys = ['criterion','max_depth','max_features','n_estimators']
	value = [['gini','entropy'],[3,9,15],['sqrt','log2'],[20, 50, 100,200]]
	model_parameters_4_grid = dict(zip(keys,value))
	
	if model_setting['gridSearch'] == True:
		model_parameters_4_grid['criterion']=['mse']
   		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=0)
	else:
		clf = []
	return mod2use,model_setting,clf


def lsvc(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Linear Support Vector Classifier"
	model_setting['printCoef'] = False
	mod2use = svm.LinearSVC(loss='l2',dual=True)
	
	keys = ['C','loss']
	value = [[0.01, 0.001, 0.0001],['hinge', 'log','modified_huber','squared_hinge','perceptron']]
	model_parameters_4_grid = dict(zip(keys,value))

	if model_setting['gridSearch'] == True:
		model_parameters_4_grid['loss'] = ['l2','l1']
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
		
	return mod2use,model_setting,clf


def etc(aeConfig,model_setting,train_data_transformed_onehot):
	print "Random Forest: ExtraTreesClassifier"
	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False
	model_setting['printFeatureImportance']=True
	
	mod2use = ensemble.ExtraTreesClassifier(n_estimators=aeConfig.ensembleEstimators, max_depth=aeConfig.treeDepth, min_samples_leaf=max(1, (aeConfig.minPercInNode/100)*train_data_transformed_onehot.shape[0]), oob_score=False, criterion='gini')
	
	keys = ['criterion','max_depth','max_features','n_estimators']
	value = [['gini','entropy'],[3,9,15],['sqrt','log2'],[20, 50, 100,200]]
	model_parameters_4_grid = dict(zip(keys,value))
	
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
	return mod2use,model_setting,clf

 
def sgdc(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Stochastic Gradient Descent"
	mod2use = linear_model.SGDClassifier(learning_rate='optimal',loss='log') #loss="huber", penalty="elasticnet", shuffle=True)
	keys = ['loss','alpha']
	value = [['hinge', 'log','modified_huber','squared_hinge','perceptron'],[.05,.1,.15]]
	model_parameters_4_grid = dict(zip(keys,value))
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
		
	return mod2use,model_setting,clf	


def gbc(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Gradient Boosted Classifier"
	#decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])	
	# If numerical or categorical columns don't exist, pick auto for max features (SQRT of features will be used)
	if train_data_transformed_onehot.shape[1]>0:
		decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])
	else:
		decisionTreeMaxFeatures="auto"

	model_setting['convertToDense']=True

	model_setting['printCoef']=False

	mod2use = ensemble.GradientBoostingClassifier(n_estimators=aeConfig.ensembleEstimators, learning_rate=1.0, max_depth=aeConfig.treeDepth, random_state=0, loss='deviance', max_features=decisionTreeMaxFeatures, verbose=1)
	keys = ['max_depth','max_features','n_estimators']
	value = [[3,9,15],['sqrt','log2'],[20, 50, 100,200]]
	model_parameters_4_grid = dict(zip(keys,value))
    
   	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []   #mod2use = ensemble.GradientBoostingClassifier(n_estimators=aeConfig.ensembleEstimators, learning_rate=1.0, max_depth=3, random_state=0, loss='deviance', max_features=decisionTreeMaxFeatures, verbose=1)
	return mod2use,model_setting,clf

  
def dtc(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Decision Tree Classifier"
	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False
	model_setting['displayTree']=True
	#decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])	
	# if no numerical or categorical present, set to AUTO or SQRT of features
	if train_data_transformed_onehot.shape[1]>0:
		decisionTreeMaxFeatures=np.min([8, train_data_transformed_onehot.shape[1]])
	else:
		decisionTreeMaxFeatures="auto"
	
	mod2use = tree.DecisionTreeClassifier(max_depth=aeConfig.treeDepth,max_features=decisionTreeMaxFeatures,criterion='entropy')  # 
	
	keys = ['criterion','max_depth','max_features']
	value = [['gini','entropy'],[3,9,15],['sqrt','log2']]
	model_parameters_4_grid = dict(zip(keys,value))
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
	return mod2use,model_setting,clf


def nb(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Naive Bayes Classifier"
	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False
	mod2use = GaussianNB()
	#if(model_setting['multiclass'] == True):
	#	mod2use = MultinomialNB()
	keys = ['cld_alpha']
	value = [[0.00001, 0.000001]] # smoothing parameter for classfier
	model_parameters_4_grid = dict(zip(keys,value))
	
	if model_setting['gridSearch'] == True:
		print "Total %d parameters chosen for grid search which are %s" %(len(model_parameters_4_grid),model_parameters_4_grid)
		clf = grid_search.GridSearchCV(mod2use, model_parameters_4_grid, verbose=1)
	else:
		clf = []
	return mod2use,model_setting,clf

	
def lr(aeConfig,model_setting,train_data_transformed_onehot):
	print "[MODEL] Linear Regression"
	
	model_setting['convertToDense']=True
	model_setting['printCoef']=False
	
	mod2use = linear_model.ElasticNet(alpha=0.1, l1_ratio=0, normalize=True,fit_intercept=False) #linear_model.LinearRegression()
	
	#keys = ['cld_alpha']
	#value = [[0.00001, 0.000001]] # smoothing parameter for classfier
	
	if model_setting['gridSearch'] == True:
		print "No Grid seach for regression model"
		clf = []
	else:
		clf = []
		
	return mod2use,model_setting,clf	
