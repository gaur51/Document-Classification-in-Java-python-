import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class gendocfeature {
//Initialize variables
    static int total_docs = 0;
    static int total_words = 0;
    static int total_bigrams = 0;
    static int total_trigrams = 0;
    
    static int stop_words_count = 0;
    
    //Set threshold limits
    static int unigram_threshold_freq = 5;
    static int bigram_threshold_limit =f 3;
    static int trigram_threshold_limit = 3;
    
    static Map<String,Integer> TokentoIndex = new HashMap<String,Integer>();
    static Map<Integer,String> IndextoToken = new HashMap<Integer,String>();
    static Map<Integer,Integer>  Tokenlastdoc = new HashMap<Integer,Integer>();
    static Map<Integer,Integer>  Tokenfreq = new HashMap<Integer,Integer>();
    static Map<Integer,Integer>  Tokendocfreq = new HashMap<Integer,Integer>();
               
    static Map<String,Integer>   BigramtoIndex = new HashMap<String,Integer>();
    static Map<Integer,String> IndextoBigram= new HashMap<Integer,String>();
    static Map<Integer,Integer> BigramlastDoc = new HashMap<Integer,Integer>();
    static Map<Integer,Integer> Bigramdocfreq = new HashMap<Integer,Integer>();
    static Map<Integer,Integer>Bigramcorpusfreq = new HashMap<Integer,Integer>();
   
    static Map<String,Integer>  TrigramtoIndex = new HashMap<String,Integer>();
    static  Map<Integer,String> IndextoTrigram= new HashMap<Integer,String>();
    static  Map<Integer,Integer> TrigramlastDoc = new HashMap<Integer,Integer>();
    static Map<Integer,Integer> Trigramdocfreq = new HashMap<Integer,Integer>();
    static Map<Integer,Integer> Trigramcorpusfreq = new HashMap<Integer,Integer>();
    
    static  Map<Integer,String> IndextoDoc = new HashMap<Integer,String>();
    static Map<String,Integer> DoctoIndex = new HashMap<String,Integer>();
    
    static Map<String,Integer> Set_stopwords = new HashMap<String,Integer>();
    
    static String to_map_1;
    static String to_map_2;  
    static String mapp_pair;	
    
    static String Tokenized_Dir; 
    static String Tokenized_Dir_map1;
    static String Tokenized_Dir_map2;
    static String home_dir;

    static String Output_Dir_uni_tfidf ;
    static String Output_Dir_uni_bool ;
    static String Output_Dir_bi_tfidf ;
    static String Output_Dir_bi_bool ;
    static String Output_Dir_tri_tfidf ;
    static String Output_Dir_tri_bool;

    static String Output_uni_vec;
    static String Output_bi_vec ;
    static String Output_tri_vec ; 
    
    static String Lexicon ;

    static String Stop_words ;
 
    public static void main(String[] args) {
        //Read the config file
             load_directories();   
        
             String line = null;
         //load stop words    
             BufferedReader in=null;
             String stopwords = "";
             try{
             in = new BufferedReader(new FileReader(Stop_words));
             line = in.readLine();
              while(line != null)
             {
                 
                    stopwords+=line+" ";
                    line = in.readLine();
                    
             }
             }
             catch(IOException ex){
                System.out.println(ex);
             }               
             String line_1 = null;
              
             BufferedReader in_1=null;
                String[]stop= stopwords.split(" ");
                
                           for(String jj:stop){
                              if( Set_stopwords.get(jj)==null){
                                  stop_words_count++;
                                  Set_stopwords.put(jj,stop_words_count);
                              }
                                      }
        //Read lexicon  and generate unigrams after stop word removal and unigram threshold      
             try{
             in_1 = new BufferedReader(new FileReader(Lexicon));
             line_1 = in_1.readLine();
              while(line_1 != null)
             {
                    String[]content;
                    content= line_1.split(":");
                    
                    String token= content[0];
                    token=token.toLowerCase();
                    int token_freq=0;
		    int doc_freq=0;
			if(!content[1].equals("") && !content[2].equals("")){
                    token_freq = Integer.parseInt(content[1]);
                    doc_freq = Integer.parseInt(content[2]);
                    }
                    if(check_pattern(token)==false && Set_stopwords.get(token)==null && token_freq>=unigram_threshold_freq){
                     
                        if(TokentoIndex.get(token)==null){
                            total_words++; 
                             TokentoIndex.put(token, total_words);
                             IndextoToken.put(total_words,token);                       
                          Tokenfreq.put(total_words, token_freq);
                            Tokendocfreq.put(total_words, doc_freq);
                            
                     }                                              
                        
                    }                                                    
                                        
                    line_1 = in_1.readLine();            
                }
             }
             catch(IOException ex){
                System.out.println(ex);
                   
             }  

                      //Count tok files to be converted to rep files
          final File folder = new File(Tokenized_Dir);
          for (final File fileEntry : folder.listFiles()) {      
            String doc = fileEntry.getName();
           
            total_docs++;
                       
            DoctoIndex.put(doc, total_docs);
            IndextoDoc.put(total_docs,doc);                                  
         
                  
    }
                 // Generate the bi-grams and triagrams
                  Generate_bi_tri_gram(total_docs);
                 
                  //Threshold bigrams and trigrams with the specified threshold limits 
                 Threshold_bi_tri();
                   

       try{
      
              PrintWriter writer1 = new PrintWriter(Output_uni_vec, "UTF-8");
              PrintWriter writer2 = new PrintWriter(Output_bi_vec, "UTF-8");
              PrintWriter writer3 = new PrintWriter(Output_tri_vec, "UTF-8");
       
              for(int i=1;i<=total_words;i++){
                  writer1.println(IndextoToken.get(i));
              }
               for(int i=1;i<=total_bigrams;i++){
                  writer2.println(IndextoBigram.get(i)); 
              }
                for(int i=1;i<=total_trigrams;i++){
                    writer3.println(IndextoTrigram.get(i));
              }
               
         writer1.close();
         writer2.close();
         writer3.close();
       }
          catch(IOException ex){
            System.out.println(ex);
        }
       
        
       //Convert tok files to rep files
                 final File folder_1 = new File(Tokenized_Dir);
               
          for (final File fileEntry : folder_1.listFiles()) {      
                      String doc = fileEntry.getName();
            
           String doc_file = Tokenized_Dir +"/"+doc ;
                     
        doc=doc.replace(".tok","");
                                   
             convert_doc_to_unigram(doc_file,total_words,doc);
             
             convert_doc_to_bigram(doc_file,total_bigrams,doc); 
             
             convert_doc_to_trigram(doc_file,total_trigrams,doc);                          
                       
          } 
         
 
                 
              
          }
       
    
   static void convert_doc_to_unigram(String doc_file,int bag_of_words,String doc)
   {
      ArrayList<Double> doc_tfidf_uni = new ArrayList<Double>(bag_of_words);
      ArrayList<Integer> doc_bool = new ArrayList<Integer>(bag_of_words);
             for(int j=1;j<=bag_of_words;j++){
                  double val=0;
                  doc_tfidf_uni.add(val);
                  doc_bool.add(0);
              }

          String line_2 = null;
              
             BufferedReader in_2=null;
             
             try{
             in_2 = new BufferedReader(new FileReader(doc_file));
             line_2 = in_2.readLine();
              while(line_2 != null)
             {
                    String[] content;
                    
                    content= line_2.split(" ");
                    
                    for(String token:content){
                        token=token.toLowerCase();
                         if(TokentoIndex.get(token)!=null){
                                      int token_index=TokentoIndex.get(token);
                 double one =1;
                   double freq=doc_tfidf_uni.get(token_index-1)+one;
                  doc_tfidf_uni.set(token_index-1,freq);
                  doc_bool.set(token_index-1,1);
                          
                            
                        }                                              
                        
                        
                    }
                    
                    line_2 = in_2.readLine();            
                }
             }
             catch(IOException ex){
                System.out.println(ex);
             }     

        String out_file_uni_tfidf = Output_Dir_uni_tfidf+"/"+ doc + "-" + "uni" +"-" + "tfidf" + ".rep" ;
        String out_file_uni_bool = Output_Dir_uni_bool+"/"+ doc + "-" + "uni" +"-" + "bool" + ".rep";
        try{
              PrintWriter writer = new PrintWriter(out_file_uni_tfidf, "UTF-8");

         for(int i=0;i<doc_tfidf_uni.size();i++){
             double tf=(double)doc_tfidf_uni.get(i);
             double df=(double)Tokendocfreq.get(i+1);
              double idf = Math.log((double)total_docs/(double)(1+df));
              double tf_idf=tf*idf;

           writer.println(tf_idf);
             
       }
         writer.close();

           PrintWriter writer_1 = new PrintWriter(out_file_uni_bool, "UTF-8");
      for(int i=0;i<doc_bool.size();i++){

           writer_1.println(doc_bool.get(i));             
         
       }           
                  writer_1.close();
                    }
                 catch(Exception ex){
                     System.out.println(ex);
                 }                 
                  
      }  
   
   static void convert_doc_to_bigram(String doc_file,int bag_of_words,String doc){
             ArrayList<Double> doc_tfidf_bi = new ArrayList<Double>(bag_of_words);
      ArrayList<Integer> doc_bool = new ArrayList<Integer>(bag_of_words);
             for(int j=1;j<=bag_of_words;j++){
                  double val=0;
                  doc_tfidf_bi.add(val);
                  doc_bool.add(0);
              }

          String line_2 = null;
              
             BufferedReader in_2=null;
             
             try{
             in_2 = new BufferedReader(new FileReader(doc_file));
             line_2 = in_2.readLine();
              while(line_2 != null)
             {
                    String[] content;
                    
                    content= line_2.split(" ");
                    String prev=null;
                    String tok=null;
                    String curr=null;
                    for(String token:content){
                        token=token.toLowerCase();
                        if(check_pattern(token)==false && Set_stopwords.get(token)==null){
                            
                        curr=token;
                        
                        if(prev!=null){
                            tok=prev+"-"+curr;

                        }
                        
                         if(tok!=null && BigramtoIndex.get(tok)!=null){
                                      int token_index=BigramtoIndex.get(tok);
 
                 double one =1;
                   double freq=doc_tfidf_bi.get(token_index-1)+one;
                  doc_tfidf_bi.set(token_index-1,freq);
                  doc_bool.set(token_index-1,1);                         
                            
                        }                                              
                        
                        prev=curr;
                    }
                    }
                    line_2 = in_2.readLine();            
                }
             }
             catch(IOException ex){
                System.out.println(ex);
      
             }     
    
         String out_file_bi_tfidf = Output_Dir_bi_tfidf+"/"+ doc + "-" + "bi" +"-" + "tfidf" + ".rep" ;
        String out_file_bi_bool = Output_Dir_bi_bool+"/"+ doc + "-" + "bi" +"-" + "bool" + ".rep";
      
        try{
      
              PrintWriter writer = new PrintWriter(out_file_bi_tfidf, "UTF-8");

         for(int i=0;i<doc_tfidf_bi.size();i++){
      
             double tf=(double)doc_tfidf_bi.get(i);
      
              double df=0;
              if(Bigramdocfreq.get(i+1)!=null){
             df=(double)Bigramdocfreq.get(i+1);
              }
      
              double idf = Math.log((double)total_docs/(double)(1+df));
      
              double tf_idf=tf*idf;

           writer.println(tf_idf);
             
       }
         writer.close();
 
         PrintWriter writer_1 = new PrintWriter(out_file_bi_bool, "UTF-8");
      for(int i=0;i<doc_bool.size();i++){

           writer_1.println(doc_bool.get(i));             
         
       }           
                  writer_1.close();
                    }
                 catch(Exception ex){
                     System.out.println(ex);
                 } 
       
   }
           
   static void convert_doc_to_trigram(String doc_file,int bag_of_words,String doc){
             ArrayList<Double> doc_tfidf_tri = new ArrayList<Double>(bag_of_words);
      ArrayList<Integer> doc_bool = new ArrayList<Integer>(bag_of_words);
             for(int j=1;j<=bag_of_words;j++){
                  double val=0;
                  doc_tfidf_tri.add(val);
                  doc_bool.add(0);
              }

          String line_2 = null;
              
             BufferedReader in_2=null;
             
             try{
             in_2 = new BufferedReader(new FileReader(doc_file));
             line_2 = in_2.readLine();
              while(line_2 != null) {
             
                    String[] content;
                    
                    content= line_2.split(" ");
                    String prevtoprev=null;
                    String prev=null;
                    String tok =null;
                    String curr=null;
                    for(String token:content){
                        token=token.toLowerCase();
                        if(check_pattern(token)==false && Set_stopwords.get(token)==null){
                        
                        curr=token;
                        if(prevtoprev!=null && prev!=null){
                            tok = prevtoprev + "-" + prev + "-" +curr;
                        }
                        
                      if(tok!=null && TrigramtoIndex.get(tok)!=null){
                          int token_index=TrigramtoIndex.get(tok);

                         double one =1;

                          double freq=doc_tfidf_tri.get(token_index-1)+one;
                           doc_tfidf_tri.set(token_index-1,freq);
                           doc_bool.set(token_index-1,1);
                          
                            
                        }                                              
                        
                        prevtoprev=prev;
                        prev=curr;
                    }
                    }
                    line_2 = in_2.readLine();            
                }
             }
             catch(IOException ex){
                System.out.println(ex);
             }     
 
        String out_file_tri_tfidf = Output_Dir_tri_tfidf+"/"+doc  + "-" + "tri" +"-" + "tfidf" + ".rep";
        String out_file_tri_bool = Output_Dir_tri_bool+"/"+ doc + "-" + "tri" +"-" + "bool" + ".rep";
        try{
              PrintWriter writer = new PrintWriter(out_file_tri_tfidf, "UTF-8");
 
         for(int i=0;i<doc_tfidf_tri.size();i++){
             double tf=(double)doc_tfidf_tri.get(i);
                 double df=0;
              if(Trigramdocfreq.get(i+1)!=null){
             df=(double)Trigramdocfreq.get(i+1);
              }
              double idf = Math.log((double)total_docs/(double)(1+df));
              double tf_idf=tf*idf;

           writer.println(tf_idf);
             
       }
         writer.close();

           PrintWriter writer_1 = new PrintWriter(out_file_tri_bool, "UTF-8");
      for(int i=0;i<doc_bool.size();i++){

           writer_1.println(doc_bool.get(i));             
         
       }           
                  writer_1.close();
                    }
                 catch(Exception ex){
                     System.out.println(ex);
                 } 
       
   }
   
   
   static void Generate_bi_tri_gram(int docs){
              String line_2 = null;              
             BufferedReader in_2=null;
       
         
          for (int k=1;k<=docs;k++) {    
            
            String doc = IndextoDoc.get(k);
             
            String doc_file = Tokenized_Dir +"/"+doc ;
          
            try{
             in_2 = new BufferedReader(new FileReader(doc_file));
             line_2 = in_2.readLine();
              while(line_2 != null)
             {
                    String[] content;
                    
                    content= line_2.split(" ");
                    String curr=null;
                    String tok =null;
                    String prev=null;
                    for(String token:content){
                        token=token.toLowerCase();
                        if(check_pattern(token)==false && Set_stopwords.get(token)==null){
                            
                        curr=token;
                        if(prev!=null){
                            tok=prev+"-"+curr;
                        }
                         if(tok!=null && BigramtoIndex.get(tok)!=null){
                             int ind = BigramtoIndex.get(tok);
 
                                      int p=BigramlastDoc.get(ind);
                                      if(p!=k){
                                      Bigramdocfreq.put(ind,Bigramdocfreq.get(ind)+1);    
                                      }            
                            Bigramcorpusfreq.put(ind,Bigramcorpusfreq.get(ind)+1);                      
                          BigramlastDoc.put(ind,k);  
                        }
                         else{
                             if(tok!=null){
                             total_bigrams++;
                             BigramtoIndex.put(tok,total_bigrams);
                             IndextoBigram.put(total_bigrams,tok);
                             Bigramdocfreq.put(total_bigrams,1);
                             BigramlastDoc.put(total_bigrams,k);
                             Bigramcorpusfreq.put(total_bigrams,1); 
                            
                         }
                         }
                        prev=curr;
                    }
                    }
                    String prevtoprev=null;
                     prev=null;
                    
                    curr = null;
                    tok =null;
                       for(String token:content){
                           token=token.toLowerCase();
                           if(check_pattern(token)==false && Set_stopwords.get(token)==null){
                               
                        curr=token;
                        if(prevtoprev!=null && prev!=null){
                            tok = prevtoprev + "-" + prev + "-" +curr;
                        }
                        
                      if(tok!=null && TrigramtoIndex.get(tok)!=null){
                               int ind = TrigramtoIndex.get(tok);
                                      
                                      int p=TrigramlastDoc.get(ind);
                                      if(p!=k){
                                      Trigramdocfreq.put(ind,Trigramdocfreq.get(ind)+1);    
                                      }                              
                                Trigramcorpusfreq.put(ind,Trigramcorpusfreq.get(ind)+1);         
                                                         
                          TrigramlastDoc.put(ind,k); 
                                                     
                        }  
                           else{
                             if(tok!=null){
                                 
                             total_trigrams++;
                             TrigramtoIndex.put(tok,total_trigrams);
                             IndextoTrigram.put(total_trigrams,tok);
                             Trigramdocfreq.put(total_trigrams,1);
                             TrigramlastDoc.put(total_trigrams,k);
                            
                                 Trigramcorpusfreq.put(total_trigrams,1);
                             
                             }
                         }
                        prevtoprev=prev;
                        prev=curr;
                         }
                        
                     
                       }
                      
                       line_2 = in_2.readLine(); 
                    }                                
                                     
                                       
                    
                               
            }
                            catch(IOException ex){
                System.out.println(ex);
          } 
             }
           
                               
              
    }       
       
   

    static boolean check_pattern(String token) {
              
      String pattern = "(.*)(\\d+)(.*)";
      Pattern regex = Pattern.compile("[$&+,:;=?@#|'<>.-^*()%!--''``]");
      Pattern r = Pattern.compile(pattern);

      // Now create matcher object.
      Matcher m = r.matcher(token);
      Matcher m1= regex.matcher(token);
      if (m.find() || m1.find()) {
         return true;
      } 
      if(token.equals("ab")){
        return true;
      }

        return false;
  }
     
   static void Threshold_bi_tri(){
        int count=total_bigrams;
        int count_bi=0;
        for (int i=1;i<=count;i++){
            int ind = Bigramcorpusfreq.get(i);
            String a = IndextoBigram.get(i);
            if(ind <bigram_threshold_limit){
                IndextoBigram.remove(i);
                 BigramtoIndex.remove(a);
     BigramlastDoc.remove(i);
    Bigramcorpusfreq.remove(i);
            }
            else{
                count_bi++;
                                  
                 int doc_num=BigramlastDoc.get(i);
                 int bi_freq =Bigramcorpusfreq.get(i);
                  BigramtoIndex.remove(a);
    IndextoBigram.remove(i);
     BigramlastDoc.remove(i);
    Bigramcorpusfreq.remove(i);
                      BigramtoIndex.put(a,count_bi);
    IndextoBigram.put(count_bi,a);
     BigramlastDoc.put(count_bi,doc_num);
    Bigramcorpusfreq.put(count_bi,bi_freq);
            }
            
        }
               total_bigrams=count_bi;
                count=total_trigrams;
                int count_tri=0;
        for (int i=1;i<=count;i++){
             String a = IndextoTrigram.get(i);
            int ind = Trigramcorpusfreq.get(i);
            if(ind <trigram_threshold_limit){
                IndextoTrigram.remove(i);
                TrigramtoIndex.remove(a);
     TrigramlastDoc.remove(i);
    Trigramcorpusfreq.remove(i);
            }
            else{
                count_tri++;
                int doc_num=TrigramlastDoc.get(i);
                 int tri_freq =Trigramcorpusfreq.get(i);
                  TrigramtoIndex.remove(a);
    IndextoTrigram.remove(i);
     TrigramlastDoc.remove(i);
    Trigramcorpusfreq.remove(i);
                      TrigramtoIndex.put(a,count_tri);
    IndextoTrigram.put(count_tri,a);
     TrigramlastDoc.put(count_tri,doc_num);
    Trigramcorpusfreq.put(count_tri,tri_freq);
            }
            }
            total_trigrams=count_tri;
        }
    
  
  static void load_directories(){
      
      Properties prop = new Properties();
	InputStream input = null;

	try {

		input = new FileInputStream("config.properties");

		// load a properties file
		prop.load(input);

		// get the property value and print it out
		home_dir = prop.getProperty("Home_dir");
		to_map_1 = prop.getProperty("Category");


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
        
                        
                    
             Stop_words = home_dir+ "/" + "stop_words.txt";
             
             Lexicon = home_dir + "/" + to_map_1 + "/" + "lexicon" + ".txt";
            
            Output_uni_vec= home_dir + "/" + to_map_1 + "/" + to_map_1 + "-uni-vec.txt";
            Output_bi_vec = home_dir  + "/" + to_map_1 + "/" + to_map_1 +"-bi-vec.txt";
           Output_tri_vec = home_dir + "/" + to_map_1+ "/"  + to_map_1 +"-tri-vec.txt"; 
                            
             Tokenized_Dir = home_dir + "/"+ to_map_1 + "/" + "tok";     
             
             Output_Dir_uni_tfidf = home_dir+"/" +  to_map_1 + "/" +  "uni-tfidf";
           Output_Dir_uni_bool = home_dir +"/"  + to_map_1 + "/" + "uni-bool";
           Output_Dir_bi_tfidf = home_dir +"/" +  to_map_1 + "/" +  "bi-tfidf";
           Output_Dir_bi_bool = home_dir +"/" +   to_map_1 + "/"  + "bi-bool";
           Output_Dir_tri_tfidf = home_dir +"/"  + to_map_1 + "/"  + "tri-tfidf";
             Output_Dir_tri_bool = home_dir +"/" +   to_map_1 + "/"  + "tri-bool";
           
      
  }
                    
     
        
    }
             
     
          
    

  
    
                  
                        
 
      
      
       

