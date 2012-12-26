import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author kosuke
 */
public class ManageDB{// implements DBInterFace{
	/* 以下で言うkeyは、DBにおけるprimarykeyとは限らず、一意に決まるものを漠然と指す */
	public static String userName = "root";
	public static String passWord = "root";
	public static String dbAddress = "jdbc:mysql://localhost:3306/wikipediaontology20111107";
	public static Connection con = null;
	public static Statement st = null;
	public static ResultSet rs = null;
	public static PreparedStatement pst = null;
	public static HashMap<String, String> strMap = new HashMap<String, String>();
	public static HashMap<String, ArrayList<String>> strArrayMaps = new HashMap<String, ArrayList<String>>();
	public static String[] redirectstrs = new String[258];
	
	// 単語の頻度
	public TreeMap<String, Integer> wordsFreq = new TreeMap<String, Integer>();
	
	/* コンストラクタ */
	ManageDB(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress,userName,passWord);
			st = con.createStatement();
			System.out.println("成功");
			
		} catch (ClassNotFoundException e) {
			System.out.println("ドライバを読み込めませんでした "+ e);
		} catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
	}
	
	/* keyのみをリストで獲得 */
	/* key名はString型とする */
	public static ArrayList<String> getKeysFromTable(String tableName, String keyName){
		ArrayList<String> resultList = new ArrayList<String>();
		try{
			rs = st.executeQuery("select * from " + tableName);
			while(rs.next()){
				resultList.add(rs.getString(keyName));
			}
		}catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
		return resultList;
	}
	
	/* keyとvalueが1対1のとき */
	public HashMap<String, String> getSingleKeyandValuePairsFromTable(String tableName, String keyName, String valueName){
		HashMap<String, String> resultMaps = new HashMap<String,String>();
		try{
			rs = st.executeQuery("select * from " + tableName);
			while(rs.next()){
				String key = rs.getString(keyName);
				String value = rs.getString(valueName);
				resultMaps.put(key, value);
			}
		}catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
		return resultMaps;	
	}
	
	
	/* valueが1対多、複数獲得されるとき */
	public HashMap<String, ArrayList<String>> getMultipleKeyandValuePairsFromTable(String tableName, String keyName, String valueName){
		HashMap<String, ArrayList<String>> resultMaps = new HashMap<String, ArrayList<String>>();
		ArrayList<String> resultList = new ArrayList<String>();
		 try {
			rs = st.executeQuery("select * from " + tableName);
			while(rs.next()){
				resultList = new ArrayList<String>();
				String key = rs.getString(keyName);
				String value = rs.getString(valueName);
				if(resultMaps.containsKey(key)){	//既にそのkeyが登録されている場合
					resultList = resultMaps.get(key);	//リストを取得
					resultList.add(value);
					resultMaps.put(key, resultList);
				}
				else{	//まだそのkeyが登録されていない場合
					resultList.add(value);
					resultMaps.put(key, resultList);
				}
			}
		} catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
		return resultMaps;
	}
	
	/* String型対応 keyとvalue, IDは数値で1～自然増加 */
	public void insertIntoTable(String tableName, int idName, int id, String keyName, String key,  String valueName, String value){
		
		try {
			String query = "insert into " + tableName + "(" + idName +"," + keyName + "," + valueName + ") values(?,?,?)";
			pst = con.prepareStatement(query);
			pst.setInt(1, id);
			pst.setString(2, key);
			pst.setString(3, value);
			int result = pst.executeUpdate();
		} catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
		
	}
	
	

	public void closeDB(){
		try{
			rs.close();
			st.close();
			con.close();
		}catch (SQLException e) { 
			System.out.println("データベース接続エラー　"+ e);
		}
	}
	
	
}