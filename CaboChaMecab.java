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
	public Sentence ExecuteCaboCha(String cabocha_path, String text){
		Sentence sentence = null;
		try{
			Cabocha cabocha = new Cabocha(cabocha_path);  //"C://Program Files/CaboCha/bin/cabocha.exe"
			sentence = cabocha.execute(text);
			for(int i=0;i<sentence.getChunks().size();i++){
				System.out.print(sentence.getChunks().get(i).getBase()+",");
				//for(int j=0;j<sentence.getChunks().get(i).getTokens().size();j++){
				//	System.out.print(sentence.getChunks().get(i).getTokens().get(j).getSurface()+",");
				//}
			}
		}catch (InterruptedException ex) {
		}catch (IOException e) {
			System.out.println("Error!");
		}
		return sentence;
	} 
	public ArrayList<String> GetChunkSet(String cabocha_path, String text){
		ArrayList<String> chunks = new ArrayList<String>();
		try{
			Cabocha cabocha = new Cabocha(cabocha_path);  //"C://Program Files/CaboCha/bin/cabocha.exe"
			Sentence sentence = cabocha.execute(text);
			for(int i=0;i<sentence.getChunks().size();i++){
				chunks.add(sentence.getChunks().get(i).getBase());
			}
		}catch (InterruptedException ex) {
		}catch (IOException e) {
			System.out.println("Error!");
		}
		return chunks;
	} 
	
	public List<Morpheme> ExecuteMeCab(String dictype, String text){
		try {
			Tagger tag = new Tagger("ipadic");  //"ipadic"
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
