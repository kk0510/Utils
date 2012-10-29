import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.*;

/**
 *
 * @author kosuke
 */
public class GoitoXML {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String line = "";
		String regex = "^[0-9]";
		Pattern p = Pattern.compile(regex);
		
		
		int count = 0;
		try {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/kosuke/Desktop/日本語語彙大系テキスト化/goinountree.txt"),"utf8"));
		
                while((line = br.readLine()) != null){
			File file = new File("C:/Users/kosuke/Desktop/日本語語彙大系テキスト化/goinoun.xml");
			FileWriter filewriter = new FileWriter(file, true);
			Matcher m = p.matcher(line);
			String output = new String();
			if(m.find()){
				String[] first = line.split(" ");
				String[] second = first[1].split("　");  //second[0]=カテゴリ名, second[1]=[段1/親/子孫], →属性大系へ(カットする)
				String[] third = second[1].replace("[", "").replace("]", "").split("/");
				output = "</category>\r\n<category "
					+ "id=\"" + first[0] + "\" "
					+ "name=\"" + second[0] + "\" " 
					+ "depth=\"" + third[0].replace("段", "") + "\" "
					+ "directparent=\"" + third[1].replace("親", "") + "\" "  
					+ "children=\"" + third[2].replace("子孫", "") + "\""
					+ ">";
				/*String output = "<category>"
					+ "<id>"+first[0]+"</id>"
					+ "<name>"+second[0]+"<name>"
					+ "<depth>"+third[0].replace("段", "")+"</depth>"
					+ "<directparent>"+third[1].replace("親", "")+"</directparent>"
					+ "<children>"+third[2].replace("子孫", "")+"</children>";
				output = output + "</category>";*/
				filewriter.write(output+"\r\n");
			}
			else{
				String[] instances = line.trim().split(" ");
				for(int i=0;i<instances.length;i++){
					if(instances[i].length()>0){
						count++;
						output = output + "<instance id=\""+Integer.toString(count)+"\">" +instances[i]+"</instance>\r\n";
					}
				}
				filewriter.write(output);
			}
			
			filewriter.close();
			
                }
                System.out.println("おわり"+count);
                
                br.close();
                
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
