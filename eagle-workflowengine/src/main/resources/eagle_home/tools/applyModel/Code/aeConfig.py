import ConfigParser
import sys
from datetime import datetime
from datetime import date
##please update the AEConfig & SAConfig class as well  
class AEConfig:
  def ConfigSectionMap(self, thisConfig, section):
    dict1 = {}
    
    options = thisConfig.options(section)
    for option in options:
        try:
            dict1[option] = thisConfig.get(section, option)
            if dict1[option] == -1:
                DebugPrint("skip: %s" % option)
        except:
            print("exception on %s!" % option)
            dict1[option] = None
    return dict1


  def __init__(self, thisConfigFile):
    
    #Config = ConfigParser.ConfigParser()
    #Config.read(thisConfigFile)
    Config = thisConfigFile
    Config.sections()
    
    configMapModel = self.ConfigSectionMap(Config, "model");
    configMapData = self.ConfigSectionMap(Config, "data");
    configMapTrees = self.ConfigSectionMap(Config, "trees");
    configMapInsights = self.ConfigSectionMap(Config, "insights");
    
    self = config_attributes(self, configMapModel, configMapData, configMapTrees, configMapInsights) 
      
        
def write_cfg(tenant_id, cfg, Sections):
        for rows in tenant_id:
                section = rows[3]
                attr = rows[4]
                attrVal = rows[5]
                if section not in Sections:
                        Sections.append(section)
                        cfg.add_section(section)
                cfg.set(section, attr, attrVal)
        return cfg, Sections

       
class SAConfig:
  def ConfigSectionMap(self, thisConfig, section):
    dict1 = {}
    
    options = thisConfig.options(section)
    for option in options:
        try:
            dict1[option] = thisConfig.get(section, option)
            if dict1[option] == -1:
                DebugPrint("skip: %s" % option)
        except:
            print("exception on %s!" % option)
            dict1[option] = None
    return dict1


  def __init__(self, thisConfigFile):
    Config = ConfigParser.ConfigParser()
    Config.read(thisConfigFile)
    #Config = thisConfigFile
    Config.sections()
    
    configMapModel = self.ConfigSectionMap(Config, "model");
    configMapData = self.ConfigSectionMap(Config, "data");
    configMapTrees = self.ConfigSectionMap(Config, "trees");
    configMapInsights = self.ConfigSectionMap(Config, "insights"); 
    
    self = config_attributes(self, configMapModel, configMapData, configMapTrees, configMapInsights)
    
    # INSIGHT SECTION #
    self.feeddate = date.today()
    if(configMapInsights.has_key('feeddate')):
        self.feeddate = configMapInsights['feeddate']

    self.feedsequence = 1
    if(configMapInsights.has_key('feedsequence')):
        self.feedsequence = configMapInsights['feedsequence']
    
    # MODEL SECTION #
    self.tenant_id='tenant0000'
    if(configMapModel.has_key("tenant_id")):
        self.tenant_id = configMapModel["tenant_id"]    

def config_attributes(self, configMapModel, configMapData, configMapTrees, configMapInsights):
    
    #### MODEL SECTION ####
    if(configMapModel.has_key("usemodel")):
        self.modelType = (configMapModel["usemodel"]).lower()
        if(self.modelType in ['logistic','lsvc','lr','etr','gbr','dtr','svr','etc','sgdc','gbc','dtc','find_best_model','nb']):
            self.modelType = self.modelType
        else:
            print "Your Options ['logistic','lsvc','lr','etr','gbr','dtr','svr','etc','sgdc','gbc','dtc','find_best_model']"
            sys.exit()
    
    self.noPredictiveModel=False
    if(configMapModel.has_key("nopredictivemodel")):
      if (configMapModel["nopredictivemodel"]).lower()=="true":
        self.noPredictiveModel=True

    self.balanceTrainSample=False
    if(configMapModel.has_key("balancetrainsample")):
      if (configMapModel["balancetrainsample"]).lower()=="true":
        self.balanceTrainSample = True

    self.removeColsWithMostlyZeros=False
    if(configMapModel.has_key("remcolswith0s")):
      if (configMapModel["remcolswith0s"]).lower()=="true":
        self.removeColsWithMostlyZeros = True

    self.tryGreedySelection=False
    if(configMapModel.has_key("trygreedyselect")):
      if (configMapModel["trygreedyselect"]).lower()=="true":
        self.tryGreedySelection = True

    self.printFeatureImportance=False
    if(configMapModel.has_key("printfeatureimp")):
      if (configMapModel["printfeatureimp"]).lower()=="true":
        self.printFeatureImportance = True
    
    self.CV=True
    if(configMapModel.has_key("cv")):
        if (configMapModel["cv"]).lower()=="false":
            self.CV = False
    
    self.balanceTrainSampleKMeans=False
    if(configMapModel.has_key("balancetrainsamplekmeans")):
      if (configMapModel["balancetrainsamplekmeans"]).lower()=="true":
        self.balanceTrainSampleKMeans = True

    self.underSampleKMeans=False
    if(configMapModel.has_key("undersamplekmeans")):
      if (configMapModel["undersamplekmeans"]).lower()=="true":
        self.underSampleKMeans = True

    self.chisqperc = 100
    if(configMapModel.has_key("chisqperc")):
      self.chisqperc = int(configMapModel["chisqperc"])

    self.cvfolds = 10
    if(configMapModel.has_key("cvfolds")):
      self.cvfolds = int(configMapModel["cvfolds"])
    
    self.cvperc = 0.80
    if(configMapModel.has_key("cvperc")):
        self.cvperc = float(configMapModel["cvperc"])/100
    
    self.yearlyCVColumn = None
    if(configMapModel.has_key("cvyearlycol")):
      self.yearlyCVColumn = configMapModel["cvyearlycol"]
    
    self.yearlyCVStartDate = ""
    if(configMapModel.has_key("cvyearlystartdate")):
      try:
          chars_to_remove = ['.', '/', '-']
          self.yearlyCVStartDate = self.yearlyCVStartDate.translate(None, ''.join(chars_to_remove))
          self.yearlyCVStartDate = int(configMapModel["cvyearlystartdate"])
      except ValueError:
        print "[WARNING] %s is an invalid start date. Ignoring ..." % (configMapModel["cvyearlystartdate"])
        
    self.regularizationPenalty = 2
    if(configMapModel.has_key("regularizationpenalty")):
      self.regularizationPenalty = float(configMapModel["regularizationpenalty"])
    
    self.regularizationType = "l2"
    if(configMapModel.has_key("regularizationtype")):
        if (configMapModel["regularizationtype"]).lower()=="l1":
            self.regularizationType = "l1"
    
    self.eval_metric = "auc"
    if(configMapModel.has_key("perf")):  # not using in present AE code
        if(configMapModel["perf"].lower() in ['auc','mse']):
            self.eval_metric = configMapModel["perf"].lower()
        else:
            print("Evaluation measure can either be AUC or MSE ")
            sys.exit()

#    self.useOneHot = True
#    #self.useOneHotString = configMapModel["onehot"]
#    if(configMapModel.has_key("onehot")):
#      if (configMapModel["onehot"]).lower()=="false":
#        self.useOneHot = False  

    self.gridsearch = False
    if(configMapModel.has_key("gridsearch")):
      if (configMapModel["gridsearch"]).lower()=="true":
          self.gridsearch = True

    self.useMedian=False ##Should we replace NaN values in numerical columns with median?
    if(configMapModel.has_key("usemedian")):
        if (configMapModel["usemedian"]).lower()=="true":
            self.useMedian = True

    self.scaleValues=False ##Should we scale numerical values by the max (so as to be between -1 and 1)
    if(configMapModel.has_key("scalevalues")):
        if (configMapModel["scalevalues"]).lower()=="true":
            self.scaleValues = True

    self.combineColumns = False
    if(configMapModel.has_key("combinecolumns")):
        if configMapModel["combinecolumns"]=="true":
            self.combineColumns=True

    self.combineColumnsMaxCard = 1000
    if(configMapModel.has_key("combinecolumnsmaxcard")):
        self.combineColumnsMaxCard = int(configMapModel["combinecolumnsmaxcard"])

    self.createClusters = False
    if(configMapModel.has_key("createclusters")):
        if configMapModel["createclusters"]=="true":
            self.createClusters=True

    self.numClusters = 5
    if(configMapModel.has_key("numclusters")):
        self.numClusters = int(configMapModel["numclusters"])
    
    self.numNeighbors = 5
    if(configMapModel.has_key("nneighbors")):
        self.numNeighbors = int(configMapModel["nneighbors"])

    self.Pca = False
    if(configMapModel.has_key("pca")):
      if configMapModel["pca"] == "true":
          self.Pca = True
 
    self.ScaleValuesOnly=False ##Should we scale numerical values by the max (so as to be between -1 and 1)
    if(configMapModel.has_key("scalevalues1")):
      if (configMapModel["scalevalues1"]).lower()=="true":
        self.ScaleValuesOnly = True   
   
    self.makeTriplets = False
    if(configMapModel.has_key("maketriplets")):
       if (configMapModel["maketriplets"]).lower()=="true":      
           self.makeTriplets = True

    self.ensembleEstimators = 100
    if(configMapModel.has_key("ensemblestimators")):
      self.ensembleEstimators = int(configMapModel["ensemblestimators"])    
      
      
    self.removeDependentVarNaN = True
    if(configMapModel.has_key("removedependentvarnan")):
      if (configMapModel["removedependentvarnan"]).lower()=="false":
          self.removeDependentVarNaN=False
       
    self.typeCV = "fixed"       # dont know why but "" making impact in funcstion call from dictionary
    if(configMapModel.has_key("typecv")):
        permitted_cv_types=['yearly','loocv','fixed']
        if(configMapModel["typecv"]).lower() in permitted_cv_types:
            self.typeCV = configMapModel["typecv"].lower()
        else:
            print( 'Permitted options for typecv: [%s]' % ', '.join(map(str, permitted_cv_types)))
            sys.exit()

    self.printCoef = False
    if(configMapModel.has_key("printcoef")):
        if (configMapModel["printcoef"]).lower() == "true":
            self.printCoef = True        
    
    
    if(configMapModel.has_key("min_sample_split")):
        if (isinstance(configMapModel["min_sample_split"],int)):
            self.min_sample_split = configMapModel["min_sample_split"]
        else:
            self.min_sample_split = 30
            print " Setting min_sample_split as 30"
            
    if(configMapModel.has_key("max_leaf_nodes")):
        if isinstance(configMapModel["max_leaf_nodes"],int):
            self.max_leaf_nodes = configMapModel["max_leaf_nodes"]
        else:
            self.max_leaf_nodes = None
            print "Setting max leaf node as None"
            
    self.regression_model_universe = ['gbr','dtr','svr','etr']
    if(configMapModel.has_key("regression_model_universe")):
        self.regression_model_universe = configMapModel["regression_model_universe"]
        
    self.categorical_model_universe = ['etc','dtc','logistic','gbc','sgdc']
    if(configMapModel.has_key("categorical_model_universe")):
        self.categorical_model_universe = configMapModel["categorical_model_universe"]
                 
    self.process_parallel = False
    if(configMapModel.has_key("process_parallel")):
        if (configMapModel["process_parallel"]).lower() == "true":
            self.process_parallel = True
    
    self.no_of_cores = 2
    if(configMapModel.has_key("no_of_cores")):
        if(isinstance(configMapModel.has_key("no_of_cores"),int) == True):
            self.no_of_cores = int(configMapModel["no_of_cores"])
        else:
            print "Input integer values"
    
    self.nanValues = None #"'n/a','N/A','NA','nan','NaN','NAN','?'"
    if(configMapModel.has_key("navalues")):
        self.nanValues = configMapModel["navalues"]

    #### DATA SECTION  ####
    self.dataHasHeader = True
    if(configMapData.has_key("header")):
      if (configMapData["header"]).lower()=="false":
        self.dataHasHeader = False

    self.inputFileSeparator = ','
    if(configMapData.has_key("seperator")):
        self.inputFileSeparator = configMapData["seperator"]

    self.noScientific = False
    if(configMapData.has_key("noscientific")):
      if (configMapData["noscientific"]).lower()=="true":
          self.noScientific = True

    self.autoDetectSeparator=True
    if(configMapData.has_key("autodetectseparator")):
      if (configMapData["autodetectseparator"]).lower()=="false":
          self.autoDetectSeparator = False

    self.dependentVar = ""
    if(configMapData.has_key("dependent")):
        self.dependentVar = configMapData["dependent"]

    self.useDerivedDependent = False
    if(configMapData.has_key("usederiveddependent")):
      if (configMapData["usederiveddependent"]).lower()=="true":
          self.useDerivedDependent = True

    self.catCols = ""
    if(configMapData.has_key("catcols")):
        self.catCols = configMapData["catcols"]

    self.numCols = ""
    if(configMapData.has_key("numcols")):
      self.numCols = configMapData["numcols"]
    
    self.txtCols = ""
    if(configMapData.has_key("txtcols")):
        self.txtCols = configMapData["txtcols"]

    self.dateCols = ""
    if(configMapData.has_key("datecols")):
        self.dateCols = configMapData["datecols"]
      
    self.ignoreCols = ""
    if(configMapData.has_key("ignorecols")):
        self.ignoreCols = configMapData["ignorecols"]

    self.stripQuotes=True
    if(configMapData.has_key("stripquotes")):
        if (configMapData["stripquotes"]).lower()=="false":
            self.stripQuotes = False

    self.factorizeDependent=False        ##Should we factorize the dependent variable
    if(configMapData.has_key("factorizedependent")):
        if (configMapData["factorizedependent"]).lower()=="true":
            self.factorizeDependent = True

    self.outputDir = ""
    if(configMapData.has_key("outputdir")):
        self.outputDir = configMapData["outputdir"]

    self.trainFilter = ""
    if(configMapData.has_key("trainfilter")):
        self.trainFilter = configMapData["trainfilter"]

    self.testFilter = ""
    if(configMapData.has_key("testfilter")):
        self.testFilter = configMapData["testfilter"]

    self.multiClass = False
    if(configMapData.has_key("multiclass")):
      if configMapData["multiclass"].lower() == 'true':
          self.multiClass = True

    self.dependentVarType = "Categorical"
    if (configMapData.has_key('dependent_var_type')):
        if configMapData["dependent_var_type"].lower() == "numerical":
            self.dependentVarType = "Numerical"
        elif configMapData["dependent_var_type"].lower() == "categorical":
            self.dependentVarType = "Categorical"
        else:
            print "Choose appropriate dependent varaible type either 'categorical' or 'Numerical'   "
            sys.exit()

    self.convertToDense = False
    if (configMapData.has_key('convert_to_dense')):
        if configMapData['convert_to_dense'].lower() == "true":
            self.convertToDense = True
    
    self.ignore_var_clash=True
    if(configMapData.has_key("ignore_variable_clash")):
        if(configMapData["ignore_variable_clash"]).lower()=="false":
            self.ignore_var_clash=False
    
    self.correlationThreshold = 0.9
    if(configMapData.has_key('corr_threshold')):
        self.correlationThreshold = float(configMapData['corr_threshold'])
    
    self.correlationSample = 1.0
    if(configMapData.has_key('corr_sample')):
        self.correlationSample = float(configMapData['corr_sample'])
    
    self.ignoreMultiCorrelatedFeatures = False
    if(configMapData.has_key('ignore_multi_corr_features')):
        if(configMapData["ignore_multi_corr_features"]).lower()=="true" or (configMapData["ignore_multi_corr_features"]).lower()=="yes":
            self.ignoreMultiCorrelatedFeatures = True
    
    self.ignoreConstantFeatures = False
    if(configMapData.has_key('ignore_const_features')):
        if(configMapData["ignore_const_features"]).lower()=="true" or (configMapData["ignore_const_features"]).lower()=="yes":
            self.ignoreConstantFeatures = True
    
    self.constantFeatureVarianceThreshold = 0.2
    if(configMapData.has_key('const_feature_variance_threshold')):
        self.constantFeatureVarianceThreshold = float(configMapData['const_feature_variance_threshold'])
              
    #### TREE SECTION ####
    
    self.createTrees=False
    if(configMapTrees.has_key("createtrees")):
      if (configMapTrees["createtrees"]).lower()=="true":
        self.createTrees=True
        
    self.treeDepth = 5
    if(configMapTrees.has_key("treedepth")):
      self.treeDepth = int(configMapTrees["treedepth"])
      
    self.minPercInNode = 0
    if(configMapTrees.has_key("minpercinnode")):
        self.minPercInNode = float(configMapTrees["minpercinnode"])
      
    self.uniqueFeatureTreesOnly = False
    if(configMapTrees.has_key("uniquefeaturesonly")):
      if (configMapTrees["uniquefeaturesonly"]).lower()=="true":
          self.uniqueFeatureTreesOnly = True
        
    self.numTreesCreate=200
    if(configMapTrees.has_key("numtreescreate")):
        self.numTreesCreate = int(configMapTrees["numtreescreate"])
      
    self.numTreesPick=10
    if(configMapTrees.has_key("numtreespick")):
        self.numTreesPick = int(configMapTrees["numtreespick"])
      
    self.uniqueRootNodes=False
    if(configMapTrees.has_key("uniquerootnodes")):
        if (configMapTrees["uniquerootnodes"]).lower()=="true":
            self.uniqueRootNodes=True
        
    self.maxFeatures = 5
    if(configMapTrees.has_key('maxfeatures')):
        self.maxFeatures = int(configMapTrees['maxfeatures'])
 
   
    ### INSIGHT SECTION ###

    self.multiprocessing=False
 
    self.ancova = False
    if(configMapInsights.has_key('ancova')):
        if (configMapInsights["ancova"]).lower()=="true":
            self.ancova = True 
    
    self.business_case_id = 1
    if configMapInsights.has_key('business_case_id'):
        self.business_case_id = int(configMapInsights['business_case_id'])

    self.positive_labels = '1'
    if(configMapInsights.has_key('positive_labels')):
        self.positive_labels = (configMapInsights['positive_labels'])

    self.no_of_bins = 1
    if(configMapInsights.has_key('no_of_bins')):
        self.no_of_bins = int(configMapInsights['no_of_bins'])    

    self.change_num2cat = False
    if(configMapInsights.has_key('change_num2cat')):
        if (configMapInsights["change_num2cat"]).lower()=="true":
            self.change_num2cat = True

    self.zscore_threshold = 1
    if(configMapInsights.has_key('zscore_threshold')):
        self.zscore_threshold = float(configMapInsights['zscore_threshold'])
    
    self.not_null_threshold_per = 0
    if(configMapInsights.has_key('not_null_threshold_per')):
        self.not_null_threshold_per = float(configMapInsights['not_null_threshold_per'])
    
    self.maxlimit_on_categories = 30
    if(configMapInsights.has_key('maxlimit_on_categories')):
        self.maxlimit_on_categories = int(configMapInsights['maxlimit_on_categories'])

    self.cutoffval = 0
    if(configMapInsights.has_key('cutoffval')):
        self.cutoffval = float(configMapInsights['cutoffval'])

    self.change_variable = True
    if(configMapInsights.has_key('change_variable')):
        if (configMapInsights["change_variable"]).lower()=="false":
            self.change_variable = False

    self.thresholdcorrelation = 0.85
    if(configMapInsights.has_key('ThresholdCorrelation')):
        self.thresholdcorrelation = float(configMapInsights['ThresholdCorrelation'])    

    self.pvalue_threshold = 0.85
    if configMapInsights.has_key('pvalue_threshold'):
        self.pvalue_threshold = float(configMapInsights['pvalue_threshold'])


    self.change_cat_variable = True
    if(configMapInsights.has_key('change_cat_variable')):
        if (configMapInsights["change_cat_variable"]).lower()=="false":
            self.change_cat_variable = False

    self.catcorrthreshold = 0.7
    if(configMapInsights.has_key('CatCorrThreshold')):
        self.catcorrthreshold = float(configMapInsights['catcorrThreshold'])

    self.category_min_values = 0.25
    if(configMapInsights.has_key('category_min_values')):
        self.category_min_values = float(configMapInsights['category_min_values'])

	self.sigma_precision = 2
    if(configMapInsights.has_key('sigma_precision')):
        self.sigma_precision = int(configMapInsights['sigma_precision'])

	self.sigma_sub_precision = 4
    if(configMapInsights.has_key('sigma_sub_precision')):
        self.sigma_sub_precision = int(configMapInsights['sigma_sub_precision'])
    
    return self
