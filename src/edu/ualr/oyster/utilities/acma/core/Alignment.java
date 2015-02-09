package edu.ualr.oyster.utilities.acma.core;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.ArrayList;

import edu.ualr.oyster.utilities.acma.blocking.Fingerprint;

public class Alignment{
	
	/**
	 * Sort the entity arrays using the author's name processed by the FingerPrint Algorithm  
	 * @param an array
	 * @return a sorted array 
	 */
	
//	public static Entity[] sorting_elements(Entity[] authors){
//		
//		int index = 0;
//		TreeSet<Entity> sorted_authors = new TreeSet<Entity>(authorComp);
//		
//        for (Entity author : authors) {
//        	sorted_authors.add(author); 
//        }
//        
//        Iterator<Entity> i = sorted_authors.iterator();
//        
//        while (i.hasNext()) {  // join ordered fragments back together
//            authors[index] = i.next();
//            index++;
//        }
//        
//        return authors;
//	}
	public static ArrayList<Entity> sorting_elements(ArrayList<Entity> authors){
		
		TreeSet<Entity> sorted_authors = new TreeSet<Entity>(authorComp);
		Iterator<Entity> itr = authors.iterator();
        Entity author;        
        
        while(itr.hasNext()){
        	author = itr.next();
        	sorted_authors.add(author);
        	itr.remove();
        }
        
        Iterator<Entity> i = sorted_authors.iterator();
        
        int modifiedPosition = 0;
        
        while (i.hasNext()) {  // join ordered fragments back together
        	author = i.next();
        	author.setPosition(modifiedPosition);
        	modifiedPosition = modifiedPosition + 1;
        	authors.add(author);
        }
        
        return authors;
	}
	
	private static Comparator<Entity> authorComp = new Comparator<Entity>() {
	    @Override
	    public int compare(Entity a1, Entity a2) {
	        return (a1.getFingerPrintName().compareTo(a2.getFingerPrintName()));
	    }
	};
	
	/**
	 * Using the author's real name as parameter to the fingerprint algorithm, this method updates the fingerPrint
	 * name attribute  
	 * @param an array
	 * @return N/A 
	 */
	
//	public static void fingerprint_over_elements(Entity[] authors){
//		
//		Fingerprint fingerPrint = new Fingerprint();
//		
//		for (int i=0; i < authors.length; i++){
//			
//			authors[i].setFingerPrintName(fingerPrint.key(authors[i].getRealName()));
//			
//		}
	

	// NOT USEFUL ANYMORE - NOW, THE FINGERPRINT ALGORITHM IS EXECUTED IN THE INITIALIZATION FACE
	public static void fingerprint_over_elements(ArrayList<Entity> authors){
		
		Fingerprint fingerPrint = new Fingerprint();
		
		Iterator<Entity> itr = authors.iterator();
			
		while(itr.hasNext()){
			Entity author = itr.next();
			author.setFingerPrintName(fingerPrint.key(author.getRealName()));
		}
	}

}
