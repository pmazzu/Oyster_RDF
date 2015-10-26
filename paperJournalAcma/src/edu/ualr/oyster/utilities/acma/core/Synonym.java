package edu.ualr.oyster.utilities.acma.core;

import java.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ualr.oyster.ErrorFormatter;
import edu.ualr.oyster.utilities.OysterNickNameTable;


public class Synonym {

	//http://norm.al/2009/04/14/list-of-english-stop-words/
	//http://www.webpageanalyse.com/blog/lists-of-stop-words-in-9-languages
	String[] stopwords = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
	
	private Map <String, ArrayList<String>> synonymsTable;
		
	public void removeStopWords(Similarity simil){
		
		Iterator<Entity> itr_source = simil.getSource().iterator();
				
		while(itr_source.hasNext()){
			Entity source = itr_source.next();
			if(stopwords.equals(source.getRealName())){
				itr_source.remove();
			}				
		}	
		
		Iterator<Entity> itr_target = simil.getTarget().iterator();
		
		while(itr_target.hasNext()){
			Entity target = itr_target.next();
			if(stopwords.equals(target.getRealName())){
				itr_target.remove();
			}				
		}	
	}
	
	public ArrayList<String> getSynonyms(ArrayList<String> arrayList){
		
		synonymsTable = new LinkedHashMap <String, ArrayList<String>>();

        load("C:/Oyster_test/data/synonyms.dat");
        		
//		simil.setSource(synonymsAdded);
//		
//		synonymsAdded = loadSynonyms(simil.getTarget());
//		
//		simil.setTarget(synonymsAdded);
		
		return loadSynonyms(arrayList);
	}
	
	private ArrayList<String> loadSynonyms(ArrayList<String> arrayList){
				
		Iterator<String> itr = arrayList.iterator();
		ArrayList<String> setOfSynonyms = new ArrayList<String>();
		ArrayList<String> previousGroups = new ArrayList<String>();
		ArrayList<String> toBeAdded = new ArrayList<String>();
		
		while(itr.hasNext()){
			String source = itr.next();
			//get all groups where the KEYWORD could appear
			ArrayList<String> groups = synonymsTable.get(source);
			
			if (groups != null){
				//a keyword could be in several groups
				Iterator<String> itr_group = groups.iterator();
//				toBeDeleted.add(source);
				
				while(itr_group.hasNext()){
					String group = itr_group.next();
					
					if(!previousGroups.contains(group)){
						previousGroups.add(group);
						ArrayList<String> synonyms = synonymsTable.get(group);
						
						if(synonyms != null){
							//to avoid duplicates, as the keyword is already loaded in the synonym table
							synonyms.remove(source);
							//if the respective keyword has any synonym available, then add them in the arrayList
							Iterator<String> itr_syn = synonyms.iterator();

							while(itr_syn.hasNext()){
								String synonym = itr_syn.next();
								setOfSynonyms.add(synonym);						
							}
						}
					}
				}
			}else{
				toBeAdded.add(source);
			}
		}
		
		if(!toBeAdded.isEmpty()){
			
//			Iterator<String> itr_newSyn = newSynonyms.iterator();
//			
//			while(itr_newSyn.hasNext()){
//				String newSynonym = itr_newSyn.next();
//				Entity entity = new Entity();
//				entity.setRealName(newSynonym);
//				entity.setPosition(nextPosition);
//				nextPosition = nextPosition + 1;
//				arrayList.add(entity);
//			}
			setOfSynonyms.addAll(toBeAdded);
		}
		
		return setOfSynonyms;
	}
	
	private void load(String filename){
        String read;
        String [] text;
        BufferedReader infile = null;
        
        try{
            File file = new File(filename);
            infile = new BufferedReader(new FileReader(file));
            while((read = infile.readLine()) != null){
                if (!read.startsWith("!!")) {
                    text = read.split("[\t]");
                    
                    // load proper KEYWORD as key and GROUP as value
                    ArrayList<String> al;
                    al = synonymsTable.get(text[0].toUpperCase(Locale.US));
                    if (al == null) {
                        al = new ArrayList<String>();
                    }
                    //could be that a KEYWORD has to be in two different groups
                    if (!al.contains(text[1].toUpperCase(Locale.US))) {
                        al.add(text[1].toUpperCase(Locale.US));
                    }
                    
                    synonymsTable.put(text[0].toUpperCase(Locale.US), al);
                    
                    // now load the GROUP as the key and the KEYWORD as the value
                    al = synonymsTable.get(text[1].toUpperCase(Locale.US));
                    if (al == null) {
                        al = new ArrayList<String>();
                    }
                    
                    if (!al.contains(text[0].toUpperCase(Locale.US))) {
                        al.add(text[0].toUpperCase(Locale.US));
                    }
                    
                    synonymsTable.put(text[1].toUpperCase(Locale.US), al);
                }
            }
            
        } catch(IOException ex){
            Logger.getLogger(OysterNickNameTable.class.getName()).log(Level.WARNING, ErrorFormatter.format(ex), ex);
        } finally {
            try {
                if (infile != null) {
                    infile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OysterNickNameTable.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
            }
        }
	}
	
}
