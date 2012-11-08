import java.io.*;
import java.util.*;


/**
 *
 * @author kosuke
 */
public class LinkAnalyze {
        static HashMap<String, HashSet> linksets = new HashMap<String, HashSet>();
        static HashSet<String> words = new HashSet<String>();
    public static void main(String args[]){
        String line="";
        String title = "";
        String text="";
	String link_hyouki = "";
	String link_uri = "";
        int start = -1;
	int bar = -1;
	int end = -1;
	int colon = -1;
        int count = -1;
        try {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/kosuke/Desktop/jawikidump.xml"),"utf-8"));
		while((line = br.readLine()) != null){
			if(line.contains("<text ")){
				line = line.substring(line.indexOf(">"));
			}
			while(line.indexOf("[[",start+1)>-1 && line.indexOf("|",bar+1)>-1 && line.indexOf("]]",end+1)>-1){
			
			    start = line.indexOf("[[",start+1);
			    colon = line.indexOf(":",colon+1);
			    bar = line.indexOf("|",bar+1);
			    end = line.indexOf("]]",end+1);
			    if(colon<start && start<bar && bar<end){
				    link_hyouki = line.substring(bar+1,end);
				    link_uri = line.substring(start+2,bar);
				    words.add(link_uri);
				    //System.out.println(link_hyouki+"→"+link_uri);
				    //System.out.println(start+","+colon+","+bar+","+end+line);
				    
			    }
			    else{
				    continue;
			    }
		    
			}
			start = 0;
			bar = 0;
			end = 0;
			colon = 0;
                   
                        
                }
                System.out.println(words.size());
                br.close();
	} catch (FileNotFoundException e) {
	e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
	}      
    }
    
   
}