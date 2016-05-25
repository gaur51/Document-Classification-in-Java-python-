
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
public class Generate_CSV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         String line_1 = null;
              
             BufferedReader in_1=null;
               
         try{
             in_1 = new BufferedReader(new FileReader("doctopic.txt"));
             BufferedWriter br = new BufferedWriter(new FileWriter("doctopics.csv"));
             StringBuilder sb = new StringBuilder();
             line_1 = in_1.readLine();
              while(line_1 != null) {
             
                    String[]content = new String[2] ;
                    content= line_1.split(" ");
                    
                  
                    if(content.length==2){
                          String file= content[0];
                 
                    sb.append(file + ",");
                    
                    String topic = content[1]; 
                   sb.append(topic );
                    }

                    sb.append("\n");
                                   
                                      
                    line_1 = in_1.readLine();            
                }
                  br.write(sb.toString());
              br.close();          
         }
             
             catch(IOException ex){
                System.out.println(ex);
             }               
        
}     
}        
        
        
    
    

