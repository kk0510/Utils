import com.cignoir.node.Sentence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kosuke
 */

//Wikipedia関連のメソッド集
public class WikiCommon extends ManageDB{
	public final String DEFAULT_CABOCHA_PATH = "C://Program Files/CaboCha/bin/cabocha.exe";
	public static final String MATCH_HIRAGANA = "^[\\u3040-\\u309F]+$";
	public static final int PRINT_NONE = 0;
	public static final int PRINT_CHUNK = 1;
	public static final int PRINT_TOKENS = 2;
	public static final int PRINT_TOKENSANDPOS = 3;
	public HashMap<String, String> ciMap = new HashMap<String, String>();
	public HashMap<String, ArrayList<String>> ciMaps = new HashMap<String, ArrayList<String>>();
	public String[] redirectstrs = new String[258];
	// 単語の頻度
	public TreeMap<String, Integer> wordsFreq = new TreeMap<String, Integer>();
	
	WikiCommon(){
		redirectstrs[256] = "#転送";
		redirectstrs[257] = "#リダイレクト";
		for(int i=0;i<256;i++){
			redirectstrs[i] = "#";
		}
	}
	
	
	public static String Gomitori(String rawtext){
		String resulttext = rawtext.replaceAll("画像:.*?.jpg", "").replaceAll("画像:.*?.jpeg", "").replaceAll("画像:.*?.JPG", "").replaceAll("画像:.*?.png", "").replaceAll("画像:.*?.gif", "").replaceAll("画像:.*?.svg", "").replaceAll("（.*?）", "").replaceAll("(.*?)", "").replaceAll("wikt:", "").replaceAll("の一覧", "");//.replaceAll("（", "").replaceAll("）", "").replaceAll("\\(", "").replaceAll("\\)", "");;
		
		return resulttext;
	}
	
	public static boolean isRedirectArticle(String text){
		if(text.contains("#転送")) return true;
		else if(text.contains("#リダイレクト")) return true;
		else if(text.contains("#REDIRECT")) return true;
		else if(text.contains("#Redirect")) return true;
		else if(text.contains("#redirect")) return true;
		else if(text.contains("#reDIRECT")) return true;
		else if(text.contains("#REDiRECT")) return true;
		else if(text.contains("#REDIreCT")) return true;
		else if(text.contains("#REDirect")) return true;
		else if(text.contains("#REDirecT")) return true;
		else if(text.contains("#REDIREcT")) return true;
		else if(text.contains("#REDIReCT")) return true;
		else if(text.contains("#REDIRECt")) return true;
		else return false;
	}
	
	public static boolean isNormalArticle(String text){
		
		String str[] = {".*（[\\u3040-\\u309F]"};
		Pattern p[] = new Pattern[str.length];
		Matcher m[] = new Matcher[str.length];
		for(int i=0;i<str.length;i++){
			p[i] = Pattern.compile(str[i]);
			m[i] = p[i].matcher(text);
			if(m[i].matches()){
				return false;
			}
		}
		if(text.contains("#REDIRECT")) return false;
		else if(text.contains("#転送")) return false;
		else if(text.contains("#Redirect")) return false;
		else if(text.contains("#redirect")) return false;
		else if(text.contains("*")) return false;
		else if(text.contains("Infobox")) return false;
		else if(text.contains("{{")) return false;
		else if(text.contains("==")) return false;
		else if(text.contains("Portal:")) return false;
		else if(text.contains("（") && !text.contains("）")) return false;
		else if(text.contains("）") && !text.contains("（")) return false;
		else if(text.contains("翻訳記事です")) return false;
		else return true;	
	}
	
	public static boolean isNormalTitle(String title){
		String str[] = {"\\d+月","\\d+年代","\\d+年","\\d+月\\d+日","\\d+世紀",".*一覧"};
		Pattern p[] = new Pattern[str.length];
		Matcher m[] = new Matcher[str.length];
		for(int i=0;i<str.length;i++){
			p[i] = Pattern.compile(str[i]);
			m[i] = p[i].matcher(title);
			if(m[i].matches()){
				return false;
			}
		}
		
		return true;
	}
	
	//このオブジェクトに値を格納しておく。
	public void putWord(String word){
		if(wordsFreq.containsKey(word)){
			wordsFreq.put(word, wordsFreq.get(word)+1);
		}
		else{
			wordsFreq.put(word, 1);
		}
	}
	
	public TreeMap<String, Integer> SortWordFreq(){
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(new DescendingOrder(wordsFreq));
		sortedMap.putAll(wordsFreq);
		return sortedMap;
	}
	
	
	
	public static void ShowFileContents(String FileName){
		HashSet<String> classes = new HashSet<String>();
		try {
		    String line = "";
		    int count = 0;
		    int notcount = 0;
		    int numofarticle = 0;
		    String title = "";
		    String contents = "";
		    boolean redirectflag = false;
		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName),"utf-8"));
		    while((line = br.readLine()) != null){
			    if(line.contains("<<<<")){
				    if(redirectflag){
					    System.out.println(contents);
					    redirectflag = false;
					    contents = "";
				    }
				title = line.replaceAll("<<<<", "").replaceAll(">>>>", "");
			    }
			    contents = contents + line;
			    if(isRedirectArticle(line)){
				redirectflag = true;
			    }
			    /*if(line.contains(",")){
				String temp[] = line.split(",");
				for(int i=1;i<temp.length;i++){
					classes.add(temp[i]);
					count++;
				}
				numofarticle++;
			    }
			    else{
				    System.out.println(line);
				    notcount++;
			    }*/
		    }
		    
		    br.close();
		    System.out.println(classes.size());
		    System.out.println(count);
		    System.out.println(notcount);
		    System.out.println(numofarticle);
		    //fw.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}	
	
	
	}
	public static void ReadFileContents(String FileName){
		//HashSet<String> classes = new HashSet<String>();
		int[] distances = new int[1000];
		for(int i=0;i<1000;i++){
			distances[i] = 0;
		}
		try {
		    String line = "";
		    int count = 0;
		   
		    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName),"utf-8"));

		    while((line = br.readLine()) != null){
			   if(line.contains(",")){
				   String[] classes = line.split(",");
				   String instance = classes[0];
				   for(int i=1;i<classes.length;i++){
					   if(instance.contains(classes[i])) count++;
					   else System.out.println(instance+"--"+classes[i]);
					   /*int dis = CalculateLevenshteinDistance(instance,classes[i]);
					   distances[dis]++;
					   if(dis>30){
						   System.out.println(instance+"--"+classes[i]+"--------"+dis);
					   }*/
				   }
			   }
		    }
		    /*for(int i=0;i<distances.length;i++){
			    System.out.println(distances[i]);
		    }*/
		    System.out.println(count);
		    
		    br.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}	
	
	}
	
	public static int CalculateLevenshteinDistance(String strA, String strB){
		int strAlen = strA.length();
		int strBlen = strB.length();
		int [][] row = new int[strAlen+1][strBlen+1];
		int distance = 0;
		
		for(int i=0;i<strAlen+1;i++){
			row[i][0] = i;
		}
		for(int i=0;i<strBlen+1;i++){
			row[0][i] = i;
		}
	
		for(int i=1;i<=strAlen;++i){
			for(int j=1;j<=strBlen;++j){
				int cost = 1;
				if(strA.substring(i-1,i).equals(strB.substring(j-1,j))){
					cost = 0;
				}
				row[i][j] = Math.min(Math.min(row[i-1][j]+1, row[i][j-1]+1), row[i-1][j-1]+cost);
			}
		}
		distance = row[strAlen][strBlen];
	
	
		return distance;
	}
	
	
	public static void GetTriples(){
		//HashMap<String, ArrayList<String>> map = ExtractKeyAndSomeValuesFromDB("select * from propertytriple", "INSTANCE", "DATA");
		
	}
	
	public static void GetAnchorLinkSets(){
		HashMap<String, HashSet> linksets = new HashMap<String, HashSet>();
		String line="";
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
				    /*if(link_hyouki.contains("#")){
					link_hyouki = link_hyouki.substring(0,link_hyouki.indexOf("#"));
				    }*/
				    link_uri = line.substring(start+2,bar);
					//keyがURI、Valuesは表記群
				    if(!link_hyouki.contains(":") && !link_uri.contains(":")){
					if(linksets.keySet().contains(link_uri)){
						linksets.get(link_uri).add(link_hyouki);
					}
					else{
						HashSet s = new HashSet<String>();
						s.add(link_hyouki);
						linksets.put(link_uri, s);
						//System.out.println(link_hyouki);
					}
					count++;
				    }
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
			int idname = 0;
			for(Iterator itr = linksets.keySet().iterator();itr.hasNext();){
				String s = itr.next().toString();
				System.out.println(s);
				HashSet<String> hyouki = new HashSet<String>();
				hyouki = linksets.get(s);
				for(Iterator it = hyouki.iterator();it.hasNext();){
					idname++;
					insertIntoTable("anchorlink","id",idname,"uri",s,"hyouki",it.next().toString());
				}
			}
		
                br.close();
	} catch (FileNotFoundException e) {
	e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
	}
	
	}
	
	
	
	public static void GetCIRelations(){
		HashMap<String, ArrayList<String>> hyponymsets = new HashMap<String, ArrayList<String>>();
		HashSet<String> classkeysets = new HashSet<String>();
		int count = 0;
		String line="";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\kosuke\\Desktop\\cirelations.txt"),"utf-8"));

			while((line = br.readLine()) != null){
				ArrayList<String> hypes = new ArrayList<String>();

				if(line.contains(",")){
					String temp[] = line.split(",");
					String hypo = temp[0];
					for(int i=1;i<temp.length;i++){
						count++;
						
							PreparedStatement pst = null;
							Connection conn = null;
							Statement st = null;
							ResultSet rs = null;
							Class.forName("com.mysql.jdbc.Driver");
							conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wikipediaontology20111107","root","root");
							st = conn.createStatement();
							String query = "insert into hyponym_hypernym_sets(ID, hyponym, hypernym) values(?,?,?)";
							pst = conn.prepareStatement(query);
							pst.setInt(1, Integer.valueOf(count));
							pst.setString(2, hypo);
							pst.setString(3, temp[i]);
							int resultstatement = pst.executeUpdate();
							pst.close();
							//rs.close();
							st.close();
							conn.close();
						
						/*hypes.add(temp[i]);
						classkeysets.add(temp[i]);
						ArrayList<String> hypos = new ArrayList<String>();
						if(hyponymsets.containsKey(temp[i])){  //既にそのクラスがkeyとして存在すれば
							hypos = hyponymsets.get(temp[i]);  //そのクラスの持つインスタンスをリストで獲得
							hypos.add(hypo);  //インスタンスを追加
							hyponymsets.put(temp[i], hypos);    //インスタンスを追加して、マップを更新
						}
						else{
							hypos.add(hypo);
							hyponymsets.put(temp[i], hypos);
						}*/
					}
					
				}

			}
			br.close();

		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("ドライバを読み込めませんでした "+ e);
		} catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
		//int count = 0;
		ArrayList<String> db = getKeysFromTable("is_a_all","class");
		
		//HashSet<String> db_instance = GetKeySetsFromDB("is_a_all",3);
		//HashMap<String, String> maps = GetKeyAndValueSinglePairsFromDB("instance_all", "class", "instance");
		
		
		HashSet<String> instances = new HashSet<String>();
		for(Iterator itr= hyponymsets.keySet().iterator();itr.hasNext();){
			String classname = itr.next().toString();
			ArrayList<String> instances_temp = hyponymsets.get(classname);
			for(int i=0;i<instances_temp.size();i++){
				instances.add(instances_temp.get(i).replaceAll("\\(", "（").replaceAll("\\)", "）").replaceAll("日本の",""));
			}
		}
		
		System.out.println(instances.size());
		/*HashMap<String, String> maps = GetKeyAndValueSinglePairsFromDB("instance_all","instance","class");
		for(Iterator itr = db.iterator();itr.hasNext();){
			String instancename = itr.next().toString();
			if(instances.contains(instancename)){
				count++;
				
				System.out.println(instancename+"-------"+maps.get(instancename)+"-----");
			}
		}*/
		
		
		for(Iterator itr = db.iterator();itr.hasNext();){
			String classname = itr.next().toString();
			CaboChaMecab c = new CaboChaMecab();
			String temp = "";
			if(classname.contains("（")){
				temp = classname.substring(classname.indexOf("（")+1,classname.indexOf("）"));
				if(classkeysets.contains(temp)){
					count++;
				}
				classname = classname.substring(0,classname.indexOf("（"));
			}
			Sentence s = c.ExecuteCaboCha("C://Program Files/CaboCha/bin/cabocha.exe", classname, PRINT_NONE);
			for(int i=0;i<s.getChunks().size();i++){
				String key = s.getChunks().get(i).getBase().replaceAll("（", "").replaceAll("）", "");
				if(classkeysets.contains(key)){
					count++;
					
					System.out.println(key+"----"+classname+"----"+hyponymsets.get(key));
					//break;
				}
			}
		
		}
		
		System.out.println(classkeysets.size()+","+count);
	
	}
	
	
}
