package extraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONObject;

public class Extractor {
	
	private static List<JSONObject> dict = new ArrayList<JSONObject>();
	private static final List<String> partOfSpeech = Arrays.asList("a.", "adv.", "conj.", "interj.", "n.", "p.", "prep.", "pron.", "v.");
	
	private static List<String> loadTextFile (String inputfile){
		
		List<String> lines = new ArrayList<String>();
    	
    	try{
    		try{
	    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile), "UTF8"));
				try{
					String line = null;
				
					while ((line = br.readLine()) != null) {
						lines.add(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
    		} catch (UnsupportedEncodingException u){
    			u.printStackTrace();
    		}
		} catch (FileNotFoundException f){
			f.printStackTrace();
		}
    	
    	return lines;
		
	}
	
	private static String getPos (String line){
		
		String pos = "n."; // default pos
		String[] tokens = line.split(" ");
		
		forwardSearch:
		for (String token : tokens){
			for (String aPos : partOfSpeech){
				if (token.startsWith(aPos)){
					pos = aPos;
					break forwardSearch;
				}
			}	
		}
		return pos;
	}
	
	@SuppressWarnings("unchecked")
	private static void save (String word, String pos, Vector<String> defs, String syns){
		
		JSONObject entry = new JSONObject();
		entry.put("word", word);
		entry.put("pos", pos);
		entry.put("definitions", defs);
		
		if (syns.length() > 0){
			entry.put("synonyms", syns.trim());
		}	
		
		dict.add(entry);
	}
	
	private static void writeDict (String outputfile){
		
		String dictionary = dict.toString();
		
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputfile), StandardCharsets.UTF_8);
	        writer.write(dictionary);
	        writer.flush();
	        writer.close();
	    }catch(IOException e){  
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void getDefinitions (String inputfile, String outputfile){
		
		List<String> lines = loadTextFile(inputfile);
		
		Vector<String> defs = new Vector<String>();
		String currentWord = "";
		String currentDef = "";
		String currentPos = "";
		String currentSyns = "";
		
		String defPattern1 = "^Defn:\\s.+";
		String defPattern2 = "^\\d+\\.\\s.+";
		String defPattern3 = "^\\s?\\(.\\)\\s.+";
		
		boolean lookForPos = false;
		boolean readingDef = false;
		boolean readingSyn = false;
		
		for (int i=27; i < lines.size(); i++){
			String line = lines.get(i);
			
			if (lookForPos){ // get the part-of-speech
				currentPos = getPos(line);
				lookForPos = false;
			}
			
			if (readingSyn){ // reading the synonym list
				if (line.length() != 0){
					line = line.replaceFirst("\\s--\\s([Tt][Oo]\\s)?", "");
					currentSyns += line + " ";
				}
				else{
					readingSyn = false;
				}
			}
			
			if (readingDef){ // reading a definition
				if (line.length() != 0 && !line.matches(defPattern1) && !line.matches(defPattern2) && !line.matches(defPattern3)){
					currentDef += " " + line;
				}
				else{	
					defs.add(currentDef);
					
					currentDef = "";
					readingDef = false;
				}
			}
			
			if (line.matches("^[A-Z]+[A-Z\\s\\d-_;']*$")){ // a new word	
				// save last entry
				if (defs.size() > 0){
					save (currentWord, currentPos, (Vector<String>)defs.clone(), currentSyns);
					defs.clear();
					currentSyns = "";
				}	
				
				currentWord = line;
				lookForPos = true;
			}
			
			if (line.matches(defPattern1)){ // a new definition, pattern 1	
				currentDef = line.replace("Defn: ", "");
				readingDef = true;
			}
			
			if (line.matches(defPattern2)){ // a new definition, pattern 2
				line = line.replaceFirst("\\d+\\.\\s", "");
				
				if (!line.startsWith("(") && !line.startsWith("pl. (") && !line.equals("pl.") && !line.startsWith("Etym:")){
					currentDef = line;
					readingDef = true;
				}
			}
			
			if (line.matches(defPattern3)){ // a new definition, pattern 3				
				line = line.replaceFirst("\\s?\\(.\\)\\s", "");
				
				if (!line.startsWith("(") && !line.startsWith("Etym:") && !line.equals("pl.")){
					currentDef = line;
					readingDef = true;
				}
			}
			
			if (line.matches("^Syn.$")){ // synonym list comes next
				readingSyn = true;
			}
		}
		
		// save the last entry and write the dictionary to file
		defs.add(currentDef);
		save(currentWord, currentPos, defs, currentSyns);
		writeDict(outputfile);
	}
	
	
	public static void main (String[] args){
		
		getDefinitions("dictionary.txt", "dictionary.json");
	
	}

}
