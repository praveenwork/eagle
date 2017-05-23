import numpy as np
import pandas as pd
from scipy import (sparse)
from sklearn import (naive_bayes, linear_model, preprocessing, metrics, cross_validation,feature_selection)
from itertools import combinations
import pdb
import time
import sys

def enum(**enums):
  return type('Enum', (), enums)

def filterLT(thisDF, thisColumn, thisValue):
  return thisDF[(thisDF[thisColumn]<thisValue)].copy()

def filterGT(thisDF, thisColumn, thisValue):
  return thisDF[(thisDF[thisColumn]>thisValue)].copy()

def filterEQ(thisDF, thisColumn, thisValue):
  return thisDF[(thisDF[thisColumn]==thisValue)].copy()

filterDataframe = {"<": filterLT,
                   ">": filterGT,
		   "=": filterEQ
                  }

def is_numeric(s):	##is string s numeric?
  try:
    float(s)
    return True
  except ValueError:
    return False

def ConfigSectionMap(thisConfig, section):
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

def getTopColumnsGreedy(thisdata, thislabels, thisperc, modelToUse):
  thisperc = min(1,thisperc)
  greedyAUCs = []
  print "[UTILS] Trying greedy selection on %d columns ... " % (thisdata.shape[1])
  modelToUse.fit(thisdata, thislabels.ravel())
  greedyAUCs = abs(modelToUse.coef_.ravel())
  #for thiscn in range(thisdata.shape[1]):
    #modelToUse.fit(thisdata[:,thiscn], thislabels.ravel())
    #probabilities_train = modelToUse.predict_proba(thisdata[:,thiscn])[:, 1]
    #fpr, tpr, thresholds = metrics.roc_curve(thislabels, probabilities_train)
    #thisMetric = metrics.auc(fpr, tpr)	##ROC	
    #thisMetric = abs(modelToUse.coef_.ravel()[0])	##coefficient value
    #greedyAUCs.append(thisMetric)
    #if((thiscn % 2000)==0):
      #print "[UTILS] %d remaining" % (thisdata.shape[1] - thiscn)

  #print greedyAUCs
  nanIdx = (np.where(np.isnan(greedyAUCs)==True))[0]

  if(len(nanIdx)>0):
    print "setting NAN Greedy AUCs to 0"
    greedyAUCs[nanIdx] = 0    ##set NAN values to 0

  #pdb.set_trace()
  sortedIdx = np.argsort(greedyAUCs)  ##sort the scores and retrieve indices
  sortedIdxRev = sortedIdx[::-1]
  greedyAUCs_sorted = np.asarray(greedyAUCs)[sortedIdxRev]
  #print greedyAUCs_sorted
  topDecileNew = sortedIdxRev[range(1+np.argwhere((np.cumsum(greedyAUCs_sorted)/np.sum(greedyAUCs_sorted))>=thisperc)[0])]
  print "[utils.getTopColumns] Selecting %d columns of %d, based on chi sq" % (len(topDecileNew), len(sortedIdx))
  return topDecileNew

def getTopColumns(thisdata, thislabels, thisperc):
  thisperc = min(1,thisperc)
  thisChi2 = feature_selection.chi2(thisdata, thislabels)  ##run chi-sq test
  chisq_scores = thisChi2[0]  ##extract scores
  chisq_scores[np.where(np.isnan(chisq_scores)==True)] = 0    ##set NAN values to 0
  sortedIdx = np.argsort(chisq_scores)  ##sort the scores and retrieve indices
  sortedIdxRev = sortedIdx[::-1]
  chisq_scores_sorted = chisq_scores[sortedIdxRev]
  #print chisq_scores_sorted
  topDecileNew = sortedIdxRev[range(1+np.argwhere((np.cumsum(chisq_scores_sorted)/np.sum(chisq_scores_sorted))>=thisperc)[0])]
  print "[utils.getTopColumns] Selecting %d columns of %d, based on chi sq" % (len(topDecileNew), len(sortedIdx))
  return topDecileNew
   
  ### below is old code. previously we were returning the top 90% num of columns, now we return the top columns that add up to 90% of the chi sq
  decileSize = int(thisperc * len(chisq_scores))  ##take the top XX %
  bottomDecile = sortedIdx[1:(decileSize-1)]
  topDecile = sortedIdx[range(len(chisq_scores)-decileSize, len(chisq_scores))]
  print sortedIdx
  print chisq_scores
  print sortedIdxRev
  print("Chi-Sq Scores: Bottom Decile:%.2f, Top Decile: %.2f" % (sum(chisq_scores[bottomDecile]), sum(chisq_scores[topDecile]) ))
  print("Returning %d columns of %d" % (len(topDecile), thisdata.shape[1]))
  return topDecile

def combineColumns(thisdata, col1, col2):
  newcol1=[hash(tuple(v)) for v in thisdata[:,[col1,col2]]]
  newcolkeys = set(newcol1)
  newcolvals = range(len(newcolkeys))
  newcoldict = dict(zip(newcolkeys, newcolvals))
  newcol2 = [newcoldict[v] for v in newcol1]
  return newcol2

def group_data(in_data, degree=3, append=False, max_cardinality=500):
  out_data = []

  if(append==True):
    out_data = in_data.copy()
  colnames = []
  m,n = in_data.shape
  
  for indices in combinations(range(n), degree):
    thiscolname = in_data.columns[list(indices)[0]]
    thiscolcardinality = len(in_data[in_data.columns[list(indices)[0]]].unique())
    totalcardinality = thiscolcardinality
    thisGroupBy = in_data.groupby(list(in_data.columns[list(indices)]))
    group_ids = thisGroupBy.grouper.group_info[0]
    
    for i in range(1,degree):
      thiscolname = "%s:%s" % (thiscolname, in_data.columns[list(indices)[i]])
      thiscolcardinality = len(in_data[in_data.columns[list(indices)[i]]].unique())
      totalcardinality = totalcardinality * thiscolcardinality
    
    if totalcardinality>max_cardinality:
      print "[INFO] SKIPPING new column %s (will result in new column with cardinality of %d)" % (thiscolname, totalcardinality)
      continue
    colnames.append(thiscolname)
    print "[INFO] Creating new column %s" % (thiscolname)
    
    if(append==True):	##create a new column in the original data frame with the new column name
      thisZip = zip(*[(((thisGroupBy.grouper).levels[i])[(thisGroupBy.grouper).labels[i]]).tolist() for i in range(degree)])
      thisZipString = "%s"
      
      for zi in range(1,degree):
        thisZipString = "%s:%%s" % (thisZipString) 
      out_data[thiscolname] = [thisZipString % t for t in thisZip]
    
    else:
      out_data.append(group_ids)
  if(append==True):
    return out_data, colnames
  
  else:
    thisDataFrame = pd.DataFrame(np.array(out_data).T, columns=colnames)
    return thisDataFrame, colnames

def OneHotEncoderFast(thisDataFrame, colsToEncode, factorMap=None, encoderToUse=None, mapOnly=False):
  time1 = int(round(time.time() * 1000))
  showTime1 = False
  if factorMap==None:
    showTime1 = True	#show time to build factor map
    print ("[ONEHOT] Building FactorMap ... "),
    sys.stdout.flush()
    factorMap = dict()
    for i in range(len(colsToEncode)):
      colname=colsToEncode[i]
      #thisFactor = pd.Categorical((thisDataFrame[colname]).apply(str))
      uniqueData = (thisDataFrame[colname]).unique()
      thisFactor = pd.Categorical(pd.Series(uniqueData).apply(str))
      factorMap[colname] = thisFactor
      
    if mapOnly==True:
      print ""
      return factorMap

  time2 = int(round(time.time() * 1000))
  if showTime1:		#only show this time if we built the factor map above
    print "%d millis" % (time2 - time1)

  print ("[ONEHOT] Encoding columns ..."),
  sys.stdout.flush()
  newDataFrame = thisDataFrame.copy()
  outcolnames = []
  Nvals = []
  print len(colsToEncode)
  for i in range(len(colsToEncode)):
    colname=colsToEncode[i]
    thisFactor = factorMap[colname]
    #To fix deprecation warning in python dependencies
    for j in thisFactor.categories:
      outcolnames.append( "%s=%s" % (colname,  j))
    newDataFrame[colname] = (thisFactor.categories).get_indexer((thisDataFrame[colname]).apply(str))
    Nvals.append(len(thisFactor.categories))  
  
  thisEncoder = preprocessing.OneHotEncoder()
  if encoderToUse==None:
    thisRetVal = thisEncoder.fit_transform(newDataFrame[colsToEncode])
    
  else:
    thisRetVal = encoderToUse.transform(newDataFrame[colsToEncode])
    thisEncoder = encoderToUse
  time3 = int(round(time.time() * 1000))
  print "%d millis" % (time3 - time2)
  return thisRetVal, factorMap, thisEncoder, outcolnames


def OneHotEncoderFaster(thisDataFrame, colsToEncode, factorMap=None, encoderToUse=None, mapOnly=False):
  time1 = int(round(time.time() * 1000))
  showTime1 = False
  if factorMap==None:
    showTime1 = True    #show time to build factor map
    print ("[ONEHOT] Building FactorMap ... "),
    sys.stdout.flush()
    factorMap = dict()
    for i in range(len(colsToEncode)):
      colname=colsToEncode[i]
      #thisFactor = pd.Categorical((thisDataFrame[colname]).apply(str))
      uniqueData = (thisDataFrame[colname]).unique()
      thisFactor = pd.Categorical(pd.Series(uniqueData).apply(str))
      factorMap[colname] = thisFactor
      
    if mapOnly==True:
      print ""
      return factorMap

  time2 = int(round(time.time() * 1000))
  if showTime1:         #only show this time if we built the factor map above
    print "%d millis" % (time2 - time1)

  print ("[ONEHOT] Encoding columns ..."),
  sys.stdout.flush()
  newDataFrame = thisDataFrame.copy()
  outcolnames = []
  vals = -1
  remVals = []
  Nvals = []
  print len(colsToEncode)
  for i in range(len(colsToEncode)):
    colname=colsToEncode[i]
    thisFactor = factorMap[colname]
    #To fix deprecation warning in python dependencies
    for j in thisFactor.categories:
      outcolnames.append( "%s=%s" % (colname,  j))
    newDataFrame[colname] = (thisFactor.categories).get_indexer((thisDataFrame[colname]).apply(str))
    Nvals.append(len(thisFactor.categories)+1)
    vals += len(thisFactor.categories) +1
    newDataFrame[colname] = newDataFrame[colname].replace(-1, len(thisFactor.categories))	
    remVals.append(vals)
  thisEncoder = preprocessing.OneHotEncoder(n_values = np.asarray(Nvals))		
  
  if encoderToUse == None:
    thisRetVal = thisEncoder.fit_transform(newDataFrame[colsToEncode])   
    thisRetVal = np.delete(thisRetVal.toarray(), remVals, 1)
 
  else:
    thisRetVal = encoderToUse.transform(newDataFrame[colsToEncode])
    thisRetVal = np.delete(thisRetVal.toarray(), remVals, 1)
    thisEncoder = encoderToUse
  time3 = int(round(time.time() * 1000))
  print "%d millis" % (time3 - time2)
  return thisRetVal, factorMap, thisEncoder, outcolnames




def OneHotEncoder(thisDataFrame, colsToEncode, keymap=None):
  """
  OneHotEncoder takes data frame with categorical columns and converts it to a sparse binary matrix.
  Returns sparse binary matrix and keymap mapping categories to indicies.  If a keymap is supplied on 
  input it will be used instead of creating one and any categories appearing in the data that are not 
  in the keymap are ignored
  """
  time1 = int(round(time.time() * 1000))
  print ("[ONEHOT] Building Keymap ... "),
  if keymap is None:
    keymap = []
    for i in range(len(colsToEncode)):
      colname = colsToEncode[i]
      #print "[ONEHOT] Building Keymap for %s" % (colname)
      thiscol = thisDataFrame[colname]
      uniques = set(pd.unique(thiscol))
      keymap.append(dict((key, val) for val, key in enumerate(uniques)))
  total_pts = thisDataFrame.shape[0]
  outdat = []
  outcolnames = []
  time2 = int(round(time.time() * 1000))
  print "%d millis" % (time2 - time1)
  print ("[ONEHOT] Encoding columns ..."),
  for i in range(len(colsToEncode)):
    #pdb.set_trace()
    colname = colsToEncode[i]
    #print "[ONEHOT] Processing %s" % (colname)
    thiscol = thisDataFrame[colname]
    thiskm = keymap[i]
    thisoutlabels = thiskm.keys()
    for thiskey, thisvalue in thiskm.items():
      thisoutlabels[thisvalue] = "%s=%s" % (colname, thiskey) 
    num_labels = len(thiskm)
    spmat = sparse.lil_matrix((total_pts, num_labels))
    for rownum, val in enumerate(thiscol):
      if val in thiskm:
        spmat[rownum, thiskm[val]] = 1
    outdat.append(spmat)
    outcolnames.extend(thisoutlabels)
  outdat = sparse.hstack(outdat).tocsr()
  time3 = int(round(time.time() * 1000))
  print "%d millis" % (time3 - time2)
  return outdat, keymap, outcolnames

def printLRCoefficients(thisModel, columnNames, coefficientsFile):
  print "[INFO] Printing Logistic Regression coefficients to file %s" % (coefficientsFile)
  model_coefficients = thisModel.coef_[0]
  model_intercept = thisModel.intercept_[0]
  this_formula = "%f" % (model_intercept)
  coefficientsFid = open(coefficientsFile, 'w')
  coefficientsFid.write('0,_intercept_,%.10f\n' % (model_intercept))
  for i in range(len(model_coefficients)):
    this_coef = model_coefficients[i]
    this_feature = columnNames[i]
    coefficientsFid.write('%d,%s,%.10f\n' % (i+1, this_feature, this_coef))
    if this_coef!=0.00:
      this_formula = "%s + (%f * %s)" % (this_formula, this_coef, this_feature)
  coefficientsFid.close()
  print "[MODEL] %s" % (this_formula)
  return this_formula

class YearlySplit():	##class to create yearly CV folds of test/training data 
  def __init__(self, dates, start_date=None, verbose=True):
    self.n_iter=0
    self.verbose = verbose
    if len(dates)<=1:
      raise ValueError('list of dates must be at of length 2, at least')
    self.dateType = type(dates[0])
    if all(isinstance(dt, self.dateType) for dt in dates)==False:
      raise ValueError('list of dates must all be of the same type')
    self.start_date = start_date
    dates.sort()
    if start_date is None:
      self.start_date=dates[0]
    self.dates = dates
    self.years = []
    if self.dateType==int or self.dateType==np.int64:
      if min(self.dates)<19000101:
        raise ValueError('date values before the 20th century (Jan 01, 1900) are not permitted')
      if max(self.dates)>=30000101:
        raise ValueError('date values beyond the 30th century (Dec 31, 2999) are not permitted')
      self.years = np.unique(np.divide([dt for dt in self.dates if dt>=self.start_date], 10000)).tolist()
      self.years.sort()
      self.n_iter = len(self.years)-1
      if len(self.years)<=1:
        raise ValueError('date values for at least 2 years must be provided to do a yearly split')
    else:
      raise ValueError('Currently only YYYYMMDD date values of type int are supported')

  def __iter__(self):
    for year in self.years[1:]:
      start = (year*10000) + 101    #YYYYMMDD format for Jan 01 for a given year
      end = (year*10000) + 1231     #YYYYMMDD format for Dec 31 for a given year
      ind_test = [idx for idx in range(len(self.dates)) if self.dates[idx]>=start and self.dates[idx]<=end]	##test indices for given year
      ind_train = [idx for idx in range(len(self.dates)) if self.dates[idx]<start]              ##train indices for all dates prior 
      if self.verbose:
        print "train: %d - %d" % (self.dates[ind_train[0]], self.dates[ind_train[len(ind_train)-1]])
        print "test: %d - %d" % (self.dates[ind_test[0]], self.dates[ind_test[len(ind_test)-1]])
      yield ind_train, ind_test

