import java.io.*;    
import java.util.*;
import java.lang.*;

public class LGAP {
    static String doc_features_path ;
    static String positive_class ;
    static String negative_class;
          
    static int total_docs ;
    static int total_docs_temp;
    static int total_docs_pos  ;
    static int total_docs_neg  ;
    static int total_docs_neg_temp ;
    static String home_dir;
    static double thresh;
    
         
   static  Map<Integer,String> IndextoDoc;
   static Map<String,Integer> DoctoIndex ;
   static  Map<Integer,String> IndextemptoDoc ;
   static Map<String,Integer> DoctemptoIndex ;
   static ArrayList<ArrayList<Double>> doclist ;
   static ArrayList<Integer> Shuff_list;
   static  Map<Integer,String> IndextoDoc_pos ;
   static Map<String,Integer> DoctoIndex_pos ;
   static ArrayList<ArrayList<Double>> doclist_pos ;    
   static int total_runs = 1;
   static double n = 50;    
   static  Map<Integer,String> IndextoDoc_neg_temp;
   static Map<String,Integer> DoctoIndex_neg_temp ;
   static  Map<Integer,String> IndextoDoc_neg ;
   static Map<String,Integer> DoctoIndex_neg ;
   static ArrayList<ArrayList<Double>> doclist_neg ;
   static ArrayList<Integer> Shuff_list_neg ; 
    
   static BufferedWriter br;
    public static void main(String[] args) {
  //      CSV file for writing results
                    try{
         br = new BufferedWriter(new FileWriter("Metric_results.csv"));
        }
        catch(IOException ex){
            
        }
    //Start the experiment
       for(int f=1;f<=total_runs;f++){
   
              load_directories();
              
        //Count total positve class documents     
        final File folder = new File(doc_features_path);
          
          for (final File fileEntry : folder.listFiles()) {      
            String doc = fileEntry.getName();
            total_docs_temp++;
          
            Shuff_list.add(total_docs_temp);
            
            DoctemptoIndex.put(doc, total_docs_temp);
            IndextemptoDoc.put(total_docs_temp,doc);
          }
          
          //Randomly shuffle the positive document list
          Collections.shuffle(Shuff_list);
          
          int size_test = (Shuff_list.size())/4;
           
          //Generate positive document test set by selecting 25% from the randomly shuffled set
          for (int q=0;q<size_test && q<Shuff_list.size();q++){
              int ind = Shuff_list.get(q);
             String doc = IndextemptoDoc.get(ind);
            total_docs_pos++;
            String file_path= positive_class +"\\"+doc ;
            
            DoctoIndex_pos.put(doc, total_docs_pos);
            IndextoDoc_pos.put(total_docs_pos,doc);
            ArrayList<Double> feature_doc = new ArrayList<Double>();
           
           feature_doc=Get_feature_vector(file_path);
           
           doclist_pos.add(feature_doc);
              
              
          }
      //Generate training set by selecting 50% from the remaining positive document randomly shuffled set
          for(int r= size_test;r<Shuff_list.size();r++){
              int ind = Shuff_list.get(r);
              String doc = IndextemptoDoc.get(ind);
           ArrayList<Double> feature_doc = new ArrayList<Double>();
             total_docs++;
            String file_path= doc_features_path +"\\"+doc ;
            
            
            DoctoIndex.put(doc, total_docs);
            IndextoDoc.put(total_docs,doc);
           feature_doc=Get_feature_vector(file_path);
           doclist.add(feature_doc);
              
          }       
      
           //Count documents in the negative class 
                final File folder_neg = new File(negative_class);

          for (final File fileEntry : folder_neg.listFiles()) {      
            String doc = fileEntry.getName();
            total_docs_neg_temp++;
           
            Shuff_list_neg.add(total_docs_neg_temp);
            DoctoIndex_neg_temp.put(doc, total_docs_neg_temp);
            IndextoDoc_neg_temp.put(total_docs_neg_temp,doc);
                                     
                               
    } 
          //Randomly shuffle the negative document set
          Collections.shuffle(Shuff_list_neg);
          
          int size_test_neg = (Shuff_list_neg.size())/4;
          //Select 25% documents from the randomly shuffled negative set for testing
          for (int q=0;q<size_test_neg && q<Shuff_list_neg.size();q++) {
              
            int ind = Shuff_list_neg.get(q);
            String doc = IndextoDoc_neg_temp.get(ind);
            total_docs_neg++;
            String file_path= negative_class +"\\"+doc ;
            
            DoctoIndex_neg.put(doc, total_docs_neg);
            IndextoDoc_neg.put(total_docs_neg,doc);
            ArrayList<Double> feature_doc = new ArrayList<Double>();
            
            feature_doc=Get_feature_vector(file_path);
            doclist_neg.add(feature_doc);
            
              
          }
                         
                 
          // Training
          
        for(int i=1;i<=total_docs;i++) {
        
                ArrayList<Integer> Ncloselistindices = new ArrayList<Integer>();
                ArrayList<Double> temp1 = new ArrayList<Double>();
            for(int p=0;p<doclist.get(i-1).size();p++) {
              double r=doclist.get(i-1).get(p);
              temp1.add(r);
          }
           // Get n closest doc list indexes to doc i in list nclosestindices;      
                int k= (int)n;
            Ncloselistindices= GetNclosest(temp1,k);
            
             ArrayList<ArrayList<Double>> Nclosedocs = new ArrayList<ArrayList<Double>>();
             ArrayList<Double> temp = new ArrayList<Double>();                   
            //     for each list index j in list Ncloseslistindices add list doclist(j) to Nclosesdocs lists
            
             for(int j=0;j<Ncloselistindices.size();j++){
                   ArrayList<Double> temp2 = new ArrayList<Double>();

                 int ind = Ncloselistindices.get(j);
         for(int p=0;p<doclist.get(ind).size();p++){
              double r=doclist.get(ind).get(p);
              temp2.add(r);
          }
                Nclosedocs.add(temp2);
             }

            //Call function to get manahabolis distance between list doclist(i) and  Nclosedocs
          for(int l=0;l<doclist.get(i-1).size();l++){
              double r=doclist.get(i-1).get(l);
              temp.add(r);
          }
          double manhab = GetManhabolisdistance(temp,Nclosedocs);
         
          thresh+=manhab;
          thresh=thresh/total_docs;
         }
        
           
   Lgap_classify(f,br);
    }
       try{
       br.close(); 
       }
       catch(IOException ex){
           
       }
  
}
    
  static ArrayList<Double> Get_feature_vector(String path)
   {
       
       //Reads the feature vectors from the given path
       
        ArrayList<Double> feature = new ArrayList<Double>();
           String  thisLine = null;
      try{
        
         BufferedReader br = new BufferedReader(new FileReader(path));
         while ((thisLine = br.readLine()) != null) {
            double a = Double.parseDouble(thisLine);
       
            feature.add(a);
         }       
      }catch(Exception e){
         e.printStackTrace();
      }
        return feature;
   }   
    
  static double GetManhabolisdistance(ArrayList<Double> z,ArrayList<ArrayList<Double>> list ){
      
      //Computes the Manhabolis distance for training object z from the nearest neighbour set list for the local gaussian approximation
      
   ArrayList<ArrayList<Double>> covariance = new ArrayList<ArrayList<Double>>();
   ArrayList<Double> mean = new ArrayList<Double>();
   ArrayList<Double> inter = new ArrayList<Double>();
    int i,m;
    double tot; 
    int len=list.get(0).size();

    for(i=0;i<len;i++) {    
        tot=0;
        for(m=0;m<list.size();m++){
        tot+=list.get(m).get(i);
        }
        mean.add(tot);
    }
      for(i=0;i<mean.size();i++) {  
    mean.set(i,mean.get(i)/(double)list.size());
   } 
        for(i=0;i<list.size();i++) {
            
        for(m=0;m<list.get(i).size();m++){
        double f= list.get(i).get(m);
         double x1=mean.get(m);
             list.get(i).set(m, f-x1);
      
        }
     
    }
    
  int j,r,d,l;
  double quan;
  d=list.get(0).size();
 
  for(i=0;i<d;i++){
      ArrayList<Double> tmp = new ArrayList<Double>();
      for(j=0;j<d;j++){
          quan=0;
   for(r=0;r<list.size();r++){
       quan+=list.get(r).get(i)*list.get(r).get(j);
      }       
     tmp.add(quan);
    }
      covariance.add(tmp);
  }

    for(i=0;i<z.size();i++){     
     z.set(i,z.get(i)-mean.get(i));           
        
  }

  for(l=0;l<d;l++){   
      quan = 0;
      for(j=0;j<z.size();j++){
      quan+=z.get(j)*covariance.get(j).get(l);
  }
   
  inter.add(quan);
  }
    double dist=0;
  for(i=0;i<d;i++){
     
     dist+=inter.get(i)*z.get(i);
      
  }
       return dist;
    }
    
static  ArrayList<Integer> GetNclosest(ArrayList<Double> z,int n){
    //Computes the n nearest neighbour set for z
     Map<Double,Integer> distind = new HashMap<Double,Integer>();
 
     ArrayList<Integer> Indices = new ArrayList<Integer>();
     ArrayList<Double> distances = new ArrayList<Double>();
     for(int i=0;i<doclist.size();i++){
          ArrayList<Double> Doc = new ArrayList<Double>();
          Doc=doclist.get(i);
          
          if(z.size()!=Doc.size()){
              System.out.println("Sizes of vectors are different");
          }
          
          else{
              double dist=0;
              for(int j=0;j<z.size();j++) {
             // dist=dist+(z.get(j)-Doc.get(j))*(z.get(j)-Doc.get(j));
             dist+=z.get(j)*Doc.get(j);
          }
               
         distind.put(dist,i);
         distances.add(dist);
     }
     
     }
     Collections.sort(distances);
     if(distances.size()<n){
         System.out.println("Minimum" +n + "documents required");
     }
     else {
     for(int l=0;l<n;l++){
         double dis= distances.get(l);
         int ind= distind.get(dis);
         Indices.add(ind);
     }
     }
        return Indices; 
        
}

//Testing
static void Lgap_classify(int iter, BufferedWriter br) {
    
    //Classifies each object in the test set
   double count, count_pos,count_neg;
   count_pos=count_neg=0;
           for(int i=1;i<=total_docs_pos;i++) {
        
                ArrayList<Integer> Ncloselistindices = new ArrayList<Integer>();
                ArrayList<Double> temp1 = new ArrayList<Double>();
                      for(int p=0;p<doclist_pos.get(i-1).size();p++){
              double r=doclist_pos.get(i-1).get(p);
              temp1.add(r);
          }
           // Get n closest doc list indexes to doc i in list nclosestindices;      
                int k= (int)n;
            Ncloselistindices= GetNclosest(temp1,k);
            
             ArrayList<ArrayList<Double>> Nclosedocs = new ArrayList<ArrayList<Double>>();
             ArrayList<Double> temp = new ArrayList<Double>();                   
            //     for each list index j in list Ncloseslistindices add list doclist(j) to Nclosesdocs lists
            
             for(int j=0;j<Ncloselistindices.size();j++){
                   ArrayList<Double> temp2 = new ArrayList<Double>();

                 int ind = Ncloselistindices.get(j);
         for(int p=0;p<doclist.get(ind).size();p++){
              double r=doclist.get(ind).get(p);
              temp2.add(r);
          }
                Nclosedocs.add(temp2);
             }
             //Call function to get manahabolis distance between list doclist(i) and  Nclosedocs
          for(int l=0;l<doclist_pos.get(i-1).size();l++){
              double r=doclist_pos.get(i-1).get(l);
              temp.add(r);
          }
          double manhab = GetManhabolisdistance(temp,Nclosedocs);
          if(manhab>=thresh){
              count_pos++;
          }
  
        }
           
             for(int i=1;i<=total_docs_neg;i++) {
        
                ArrayList<Integer> Ncloselistindices = new ArrayList<Integer>();
                ArrayList<Double> temp1 = new ArrayList<Double>();
             for(int p=0;p<doclist_neg.get(i-1).size();p++){
              double r=doclist_neg.get(i-1).get(p);
              temp1.add(r);
          }
           // Get n closest doc list indexes to doc i in list nclosestindices;      
                int k= (int)n;
            Ncloselistindices= GetNclosest(temp1,k);
            
             ArrayList<ArrayList<Double>> Nclosedocs = new ArrayList<ArrayList<Double>>();
             ArrayList<Double> temp = new ArrayList<Double>();                   
            //     for each list index j in list Ncloseslistindices add list doclist(j) to Nclosesdocs lists
            
             for(int j=0;j<Ncloselistindices.size();j++){
                   ArrayList<Double> temp2 = new ArrayList<Double>();

                 int ind = Ncloselistindices.get(j);
         for(int p=0;p<doclist.get(ind).size();p++){
              double r=doclist.get(ind).get(p);
              temp2.add(r);
          }
                Nclosedocs.add(temp2);
             }

            //Call function to get manahabolis distance between list doclist(i) and  Nclosedocs
          for(int l=0;l<doclist_neg.get(i-1).size();l++){
              double r=doclist_neg.get(i-1).get(l);
              temp.add(r);
          }
          double manhab = GetManhabolisdistance(temp,Nclosedocs);
          if(manhab<thresh){
              count_neg++;
          }
      
        }  
                          
        double tp = count_pos + 1;
        double tn = count_neg + 1;
        double fn = total_docs_pos - count_pos + 1;
        double fp = total_docs_neg - count_neg + 1;
        
        double sensitivity, precision, specificity, gmean, accuracy;
        
        //Calculate metrics
        sensitivity=tp/(tp+fn);
        specificity=tn/(tn+fp);
        precision=tp/(tp+fp);
        gmean = Math.sqrt(sensitivity*specificity);
        accuracy=(tn+tp)/(tp+tn+fp+fn);
        
       // Write metrics to file        
         try{

             StringBuilder sb = new StringBuilder();
             if(iter==1){
                 
              sb.append("sensitivity" + "," );
                   
                   sb.append("specificity" + "," );
                   
                    sb.append("precision" + "," );
                    
                     sb.append("gmean" + "," );
                     
                      sb.append("accuracy" + "," );
                      
                       sb.append("\n");
             }            
                    sb.append(sensitivity + "," );
                   
                   sb.append(specificity + "," );
                   
                    sb.append(precision + "," );
                    
                     sb.append(gmean + "," );
                     
                      sb.append(accuracy + "," );
                                    
                    sb.append("\n");
                                  
                                 
      
                  br.write(sb.toString());
                       
         }
             
             catch(IOException ex){
                System.out.println(ex);
             }   
              
 }

static void load_directories(){
        
    //Load diectories by reading in from the config file and initializing the directory paths for reading in features
       
          Properties prop = new Properties();
	InputStream input = null;

	try {

		input = new FileInputStream("config.properties");

		// load the properties file
		prop.load(input);

		// get the property value
		home_dir = prop.getProperty("Home_dir");
		

	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
          
   doc_features_path = home_dir + "/" + "crude-jobs" +  "/" + "jobs" + "/" + "jobs-tri-tfidf";
   positive_class = doc_features_path;
   negative_class = home_dir + "/"  + "crude-jobs" +  "/" + "crude" + "/" + "crude-tri-tfidf";
   
   //Initialize variables
    total_docs = 0;
    total_docs_temp = 0;
    total_docs_pos  = 0;
    total_docs_neg  = 0;
    total_docs_neg_temp = 0;
    thresh=0;
     
    IndextoDoc = new HashMap<Integer,String>();
      DoctoIndex = new HashMap<String,Integer>();
    IndextemptoDoc = new HashMap<Integer,String>();
     DoctemptoIndex = new HashMap<String,Integer>();
     doclist = new ArrayList<ArrayList<Double>>();
    Shuff_list = new ArrayList<Integer>();
    IndextoDoc_pos = new HashMap<Integer,String>();
    DoctoIndex_pos = new HashMap<String,Integer>();
    doclist_pos = new ArrayList<ArrayList<Double>>();
    
    IndextoDoc_neg_temp = new HashMap<Integer,String>();
    DoctoIndex_neg_temp = new HashMap<String,Integer>();
    IndextoDoc_neg = new HashMap<Integer,String>();
    DoctoIndex_neg = new HashMap<String,Integer>();
    doclist_neg = new ArrayList<ArrayList<Double>>();
    Shuff_list_neg = new ArrayList<Integer>();
   
    
}

}
  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
