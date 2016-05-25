README

In config file specify home directory. This is the directory which will have the features required as input for the text classification.

For eg. currently the config file is

Home_dir = /export/projects/unbalanced-classification/demo

Suppose the two categories for classification are category1 and category2, then we must have a folder

Home_dir/category1-category2

For eg.
 
In our case we have two categories crude and jobs for classification. So there must be a folder named crude-grain under demo.

/export/projects/unbalanced-classification/demo/crude-jobs

Next, we must have a sub-folder inside this for each category

Home_dir/category1-category2/category1

Home_dir/category1-category2/category2

Now, each of these folders must have 6 sub-folders each representing 6 feature representations for each category (uni-bool, uni-tfidf, bi-bool, bi-tfidf, tri-bool, tri-tfidf)

For category1

Home_dir/category1-category2/category1-uni-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-uni-tfidf

Home_dir/category1-category2/category1-uni-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-uni-bool

Home_dir/category1-category2/category1-bi-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-bi-tfidf

Home_dir/category1-category2/category1-bi-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-bi-bool

Home_dir/category1-category2/category1-tri-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-tri-tfidf

Home_dir/category1-category2/category1-tri-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/crude-tri-bool

For category2

Home_dir/category1-category2/category2-uni-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-uni-tfidf

Home_dir/category1-category2/category2-uni-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-uni-bool

Home_dir/category1-category2/category2-bi-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-bi-tfidf

Home_dir/category1-category2/category2-bi-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-bi-bool

Home_dir/category1-category2/category2-tri-tfidf
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-tri-tfidf

Home_dir/category1-category2/category2-tri-bool
For eg. /export/projects/unbalanced-classification/demo/crude-jobs/jobs-tri-bool

Inputs

Let Code_dir be the diretory which contains the OSVM_text_classification code
For our example Code_dir = /export/projects/unbalanced-classification/src/LGAP_Text_Classification_Project/OSVM_code

Feature to be used for classification

In the file OSVM.py (at the path Code_dir/OSVM.py) set the string path to point to the folder containing features for 
the positive class features. Similarly, the string path_neg (defined just after path) contains the path to the same feature (as in doc_features) for the negative class features.

For eg. for the crude-grain classification task

Positve class = crude
Negative class = grain
Feature = uni-tfidf

Positive class features
path = config.Home_dir + '/' + 'crude-grain' + '/' + 'crude' + '/' + 'crude-uni-tfidf'

Negative class features
path_neg = config.Home_dir + '/' + 'crude-grain' + '/' + 'grain' + '/' + 'grain-uni-tfidf'

home_dir is equal to the string in the config file defined as Home_dir

Parameters

C (penalty)
This is a global double variable named C in the OSVM.py file. This variable can be changed to experiment with different penalties foe the OSVM model.

Number of iterations
This is a global static int variable named iter in the OSVM.py file. This variable can be used to change the number of iterations for running the classifier.

Output

The 5 classification metrics are written in the file Metric_results.csv with 1 row for each iteration. The file is generated in the same directory as Code_dir
at the path Code_dir/Metric_results.csv




