import java.util.*;
import java.io.*;
import com.cignoir.cabocha.*;
import com.cignoir.enums.PosDiv;
import com.cignoir.node.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.reduls.igo.Tagger;
import net.reduls.igo.Morpheme;

/**
 *
 * @author kosuke
 */
/* ライブラリ"igo043.jar"と"cabocha4j.jar"を含める必要がある */
public class CaboChaMecab {
	public static Sentence ExecuteCaboCha(String cabocha_path, String text){
		Sentence sentence = null;
		try{
			Cabocha cabocha = new Cabocha(cabocha_path);  //"C://Program Files/CaboCha/bin/cabocha.exe"
			sentence = cabocha.execute(text);	
		}catch (InterruptedException ex) {
		}catch (IOException e) {
			System.out.println("Error!");
		}
		return sentence;
	} 
	
	public static List<Morpheme> ExecuteMeCab(String dictype, String text){
		try {
			Tagger tag = new Tagger(dictype);  //"ipadic"
			List<Morpheme> list = tag.parse(text);
			return list;
		} catch (FileNotFoundException ex) {
			Logger.getLogger(CaboChaMecab.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(CaboChaMecab.class.getName()).log(Level.SEVERE, null, ex);
		}   
		return null;
	} 
}