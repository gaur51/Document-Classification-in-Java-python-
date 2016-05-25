print(__doc__)

import os 
import random
import math
from sklearn import svm
import csv
import config

#load directory paths from the config file
f= open('config.py','r')

path=config.Home_dir + '/' + 'crude-grain' + '/' + 'crude' + '/' + 'crude-uni-tfidf' 
path_neg=config.Home_dir + '/' 'crude-grain' + '/' + 'grain' + '/' + 'grain-uni-tfidf' 

f.close


iter = 51

with open('metric_results.csv', 'wb') as fp:
 
 #start the experiments
 for i in range(1,iter):
     
  if(i==1):
   a = csv.writer(fp, delimiter=',')
   data = [['sensitivity', 'specificity', 'precision', 'gmean', 'accuracy']]
   a.writerows(data)
   
   
  countneg=0 
  count=0
  arrneg =[]
  arr =[]
  X_train =[]
  X_test =[]
  X_outliers =[]
  
  #Count total documents in positive set 
  files = os.listdir(path)  
  for file in files:   
     count = count +1
       
     arr.append(count)
    
     
  #Shuffle positive set randomly
  random.shuffle(arr)
  
  #Count total documents in negative set 
  files_n = os.listdir(path_neg)  
  for file in files_n:   
     countneg = countneg +1
       
     arrneg.append(countneg)
    
     
  #Shuffle negative set randomly
  random.shuffle(arrneg)

  count_curr = 0
  count_pos = 0
  
  #Divide positive set into training set(50%) and positive test set(50%) 
  files_1 = os.listdir(path) 

  for file in files_1:   
   
     os.chdir(path)
     count_curr = count_curr +1 
   
     new = []
   
     f= open(file,'r')
    
     for line in f:  
        new.append(line)  
     
     co = arr.index(count_curr)      
     val = int(count/4)
     if(co>val):
        X_train.append(new)
                 
     else:
        X_test.append(new)
        count_pos = count_pos + 1         
     f.close()
        
  count_neg_tot = 0
  count_neg = 0
  
  #Select 50% documents for negative test set from the randomly shuffled negative documents
  
  files_2 = os.listdir(path_neg) 

  for file in files_2:   
    
     os.chdir(path_neg)
     count_neg_tot = count_neg_tot + 1
   
     new = []
   
     f= open(file,'r') 
     for line in f:  
        new.append(line)
     
     
     coneg = arrneg.index(count_neg_tot)
     
     val = int(countneg/4)
     if(coneg<val):    
        X_outliers.append(new)
        count_neg = count_neg + 1
     f.close()    
     
  #SVM
  clf = svm.SVM(nu=0.46, kernel="linear", gamma=0.1)
  clf.fit(X_train)
  y_pred_train = clf.predict(X_train)
  y_pred_test = clf.predict(X_test)
  y_pred_outliers = clf.predict(X_outliers)
  n_error_train = y_pred_train[y_pred_train == -1].size
  n_error_test = y_pred_test[y_pred_test == -1].size
  n_error_outliers = y_pred_outliers[y_pred_outliers == 1].size

  tp = float(count_pos-n_error_test) + 1
  tn = float(count_neg-n_error_outliers) + 1
  fn = float(n_error_test) + 1
  fp = float(n_error_outliers) + 1    
  
  #Calculate metrics
        
  sensitivity = tp/(tp+fn)
  specificity = tn/(tn+fp)
  precision = tp/(tp+fp)
  gmean = math.sqrt(sensitivity*specificity)
  accuracy = (tn+tp)/(tp+tn+fp+fn)
  
  #Write into file
 
  data = [[sensitivity, specificity , precision , gmean , accuracy]]
  a.writerows(data)
  