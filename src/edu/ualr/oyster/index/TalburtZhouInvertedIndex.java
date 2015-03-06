/*
 * Copyright 2012 John Talburt, Eric Nelson
 *
 * This file is part of Oyster created in the ERIQ Research Center at University of Arkansas at Little Rock.
 * 
 * Oyster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Oyster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Oyster.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package edu.ualr.oyster.index;

import edu.ualr.oyster.data.ClusterRecord;
import edu.ualr.oyster.data.OysterIdentityRecord;
import edu.ualr.oyster.utilities.DaitchMokotoffSoundex;
import edu.ualr.oyster.utilities.IBMAlphaCode;
import edu.ualr.oyster.utilities.NYSIISCode;
import edu.ualr.oyster.utilities.Scan;
import edu.ualr.oyster.utilities.Soundex;
import edu.ualr.oyster.utilities.acma.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map; 
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.lang.Double;

import java.util.logging.Logger;

/**
 * An Inverted Index is a mapping structure that maps a word to its location 
 * within the data file. The goal of an inverted index is to optimize the speed 
 * of the query: find the documents where word X occurs [Zobel, J. Moffat, A. 
 * (July 2006). "Inverted Files for Text Search Engines". ACM Computing Surveys 
 * (New York: Association for Computing Machinery) 38 (2): 6]. This simple index
 * allows fast full document search of all reference items that it has read up 
 * to the point of the index query. This index will simply be a map object. The 
 * key will be the reference item (name, DOB, etc) and the value list will be a 
 * collection of sequence numbers. 
 * 
 * The this Index differs in the key that is used for a data element. The reason
 * for this new index is two fold,
 * <ul>
 * <li>One, it enables some fuzziness to be applied to the index,</li>
 * <li>Two, it allows for a better segmentation of the data set there by 
 * creating smaller sized candidate sets while still getting the most reliable 
 * candidates</li>
 * </ul>
 * It takes a record and creates an entry based on each rule in the ruleset. For
 * Example given the following:
 * 
 * Given this ruleset
 * Item="First", MatchCode="LED(80)"
 * Item="Last", MatchCode="Exact_Ignore_Case"
 * Item="SSN", MatchCode="Transpose"
 * 
 * This formula is used:
 * "Soundex(First)"+"Substring(UpperCase(Last),0,4)"+"CharsInOrder(SSN)"
 * 
 * If the Input record had the values "Phillip, Johnson, 432-43-7652", then the
 * index key value would be: "P410JOHNS22334567"
 * Where
 * "P410" is the Soundex value of "Phillip"
 * "JOHNS" is the first 5 chars in upper case of "Johnson"
 * "223344567" are the 9 alphanumeric chars of "432-43-7652" in sorted order
 * 
 * @author Eric D. Nelson
 */

// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.47CC8817-13E6-2FA6-29A3-1D3DE2906C50]
// </editor-fold> 
public class TalburtZhouInvertedIndex extends InvertedIndex {

    private int slidingWindow = -1;
    
    /** The rules to be used for the ER run */
    private ArrayList<IndexRule> rules = null;
    
    /** Standard Soundex utility */
    private Soundex soundex;

    /** Standard Soundex utility */
    private DaitchMokotoffSoundex dmSoundex;

    /** IBM AlphaCode Soundex utility */
    private IBMAlphaCode alphaCode;

    /** NYSIISCode Soundex utility */
    private NYSIISCode nysiis;
    
    /** Scan Utility */
    private Scan scan;
    
    private final static Logger logger_results = Logger.getLogger("RESULTS");
    
    /**
     * Creates a new instance of <code>TalburtZhouInvertedIndex</code>.
     */
    public TalburtZhouInvertedIndex () {
        super();
        
        soundex = new Soundex();
        dmSoundex = new DaitchMokotoffSoundex();
        alphaCode = new IBMAlphaCode();
        nysiis = new NYSIISCode();
        scan = new Scan();
    }

    public int getSlidingWindow() {
        return slidingWindow;
    }

    public void setSlidingWindow(int slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    public ArrayList<IndexRule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<IndexRule> rules) {
        this.rules = rules;
    }
    
    /**
     * Add an entry to the <code>TalburtZhouInvertedIndex</code>.
     * @param obj the entry to be added.
     */
    @Override
    public void addEntry(Object obj) {
        if (obj != null) {
            OysterIdentityRecord oir = (OysterIdentityRecord) obj;

            // get the RefID
            String value = oir.get("@RefID");
            Set<String> hashes = getHash(oir, rules);
            for (Iterator<String> it = hashes.iterator(); it.hasNext();) {
               	//BEGIN CHANGE - MAZZUCCHI
            	//String hash = it.next();                             
            	String  hash = isThereAnySimilarHashValue(index,it.next());
                
                Set<String> s = index.get(hash);             
                //END CHANGE - MAZZUCCHI

                if (s == null) {
                    s = new LinkedHashSet<String>();
                }

                String[] values = value.split("[|]");
                s.addAll(Arrays.asList(values));
                index.put(hash, s);
                
                //
//                System.out.println("Miembros");
//                for (Iterator<String> itr = s.iterator();itr.hasNext();){
//                	System.out.println(hash+"|"+itr.next());
//                }
                
            }
        } else {
            System.out.println("Null obj insertion into index");
        }
    }

    /**
     * Add an entry to the <code>TalburtZhouInvertedIndex</code>.
     * @param refID the refID to set the reference items to.
     * @param obj the entry to be added.
     */
    @Override
    public void addEntry(String refID, Object obj) {
        if (obj != null) {
            OysterIdentityRecord oir = (OysterIdentityRecord) obj;
            Set<String> hashes = getHash(oir, rules);

            for (Iterator<String> it = hashes.iterator(); it.hasNext();) {
            	//BEGIN CHANGE - MAZZUCCHI
            	//String hash = it.next();                             
            	String  hash = isThereAnySimilarHashValue(index,it.next());
                
                Set<String> s = index.get(hash);             
                //END CHANGE - MAZZUCCHI
            	
                if (s == null) {
                    s = new LinkedHashSet<String>();
                }

                s.add(refID);
                index.put(hash, s);
                
//                //
//                for (Iterator<String> itr = s.iterator();itr.hasNext();){
//                	System.out.println(hash+"|"+itr.next());
//                }
                
            }
        } else {
            System.out.println("Null obj insertion into index");
        }
    }
    
    private String isThereAnySimilarHashValue(Map<String, Set<String>> index, String lookingForSimilarities){
    	//if there is not any similar entity, the same entity received as parameter will be added in the hash table 
    	String candidates = lookingForSimilarities;
    	
    	ACMA acma = new ACMA();
    	double max_similarity = 0.00;
    	Heuristics simil = null;
    	double porcSimil = 0.0;
    	
		logger_results.info("Similar HashValue"+"\r\n"); 
    	for (Iterator<String> it = index.keySet().iterator();it.hasNext();){
    		String hash = it.next();
    		
    		logger_results.info(hash+"|"+lookingForSimilarities + "|");
    		simil =  acma.multivalued_attr_similarity_calc(hash, lookingForSimilarities, "0.70", "rms", "SUBSET",1);
    		porcSimil = (double)simil.getAssertions() / (double)simil.getRows();

			//System.out.println(hash + "|" + lookingForSimilarities+"|"+simil.getSimilarityGrade()+"|"+porcSimil);
    		
    		//if (simil.getAssertions() > 0 & porcSimil > 0.5){
    		if (porcSimil >= 0.5){
    			if (max_similarity <= simil.getSimilarityGrade()){
    				candidates = hash;
    				max_similarity = simil.getSimilarityGrade();
    			}	
    		}
    	}
    	
    	return candidates;
    }
    
    /**
     * Associates the specified value with the specified key in this <code>
     * TalburtZhouInvertedIndex</code>. If the <code>TalburtZhouInvertedIndex</code>
     * previously contained a mapping for this key, the old value is replaced by
     * the specified value. 
     * @param key the key with which the specified value is to be associated.
     * @param o the value to be associated with the specified key.
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public void add(String key, Object o){
        Set<String> s = index.get(key.trim());
        Set<String> set = (Set<String>) o;
        if (s != null) {
            s.addAll(set);
            
            index.put(key.trim(), s);
        }
        else {
            index.put(key.trim(), set);
        }
    }
    
    /**
     * Associates the specified value with the specified key in this <code>
     * TalburtZhouInvertedIndex</code>. If the <code>TalburtZhouInvertedIndex</code>
     * previously contained a mapping for this key, the old value is replaced by
     * the specified value. 
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     */
    @Override
    public void setEntryAt(Object key, String value) {
        OysterIdentityRecord oir = (OysterIdentityRecord) key;
        
        if (oir != null) {
            Set<String> hashes = getHash(oir, rules);

            for (Iterator<String> it = hashes.iterator(); it.hasNext();) {
                String hash = it.next();
                Set<String> s = index.get(hash);

                if (s == null) {
                    s = new LinkedHashSet<String>();
                }

                if (!s.contains(value) && !hash.equals("")) {
                    s.add(value);
                    index.put(hash, s);
                }
            }
        } else {
            System.out.println("Null obj insertion into index");
        }
    }

    /**
     * Removes the mapping for this key from this <code>TalburtZhouInvertedIndex
     * </code> if it is present.
     * @param obj the Object to be removed.
     */
    @Override
    public void removeEntry (Object obj) {
        String key = (String) obj;
        index.remove(key);
    }

    /**
     * Remove the refID from the Object in the <code>TalburtZhouInvertedIndex</code>.
     * @param obj the Object to be searched.
     * @param refID the refID to be removed from the Object.
     */
    @Override
    public void removeEntry(Object obj, String refID) {
        OysterIdentityRecord oir = (OysterIdentityRecord) obj;
        
        if (oir != null) {
            Set<String> hashes = getHash(oir, rules);

            for (Iterator<String> it = hashes.iterator(); it.hasNext();) {
                String hash = it.next();
                Set<String> s = index.get(hash);

                if (s != null) {
                    if (s.contains(refID)) {
                        s.remove(refID);

                    if (s.isEmpty()) {
                            Set<String> set = this.index.remove(hash);
                            set = null;
                        } else {
                            this.index.put(hash, s);
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * @param clusterRecord
     * @param lcrd
     * @return 
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public Set<String> getCandidateList(ClusterRecord clusterRecord, Map<Integer, ArrayList<String>> lcrd) {
        Set<String> candidates = new HashSet<String>();
        OysterIdentityRecord oysterIdentityRecord = clusterRecord.getMergedRecord();
        Map<String, Integer> sort = new LinkedHashMap<String, Integer>();

        Set<String> hashes = getHash(oysterIdentityRecord, rules);

        for (Iterator<String> it = hashes.iterator(); it.hasNext();) {
            String hash = it.next();
            //BEGIN CHANGE
//            Set<String> s = index.get(hash);
            Set<String> s = getSimilarCandidates(index,hash);
            //END CHANGE
            
            if (s != null) {
                candidates.addAll(s);

                for (Iterator<String> it2 = s.iterator(); it2.hasNext();) {
                    String key = it2.next();

                    int value = 0;
                    if (sort.containsKey(key)) {
                        value = sort.get(key);
                    }
                    value++;
                    sort.put(key, value);
                }
            }
        }

        int value = 0;
        if (candidateList.containsKey(candidates.size())) {
            value = candidateList.get(candidates.size());
        }
        value++;
        candidateList.put(candidates.size(), value);
        
        // if more than 100 candidates only return the top 1/3 sorted based on most occuring
        if (candidates.size() > 100){
            TreeMap<Integer, Set<String>> sort2 = new TreeMap<Integer, Set<String>>();
            
            for(Iterator<Entry<String, Integer>> it = sort.entrySet().iterator(); it.hasNext();){
                Entry<String, Integer> entry = it.next();
                value = entry.getValue();
                
                Set<String> s2 = sort2.get(value);
                
                if (s2 == null) {
                    s2 = new LinkedHashSet<String>();
                }
                
                s2.add(entry.getKey());
                sort2.put(value, s2);
            }
            
            int check = candidates.size() / 3;
            if (check > 100) {
                check = 100;
            }
            
            candidates.clear();
            
            for (int i = sort2.lastKey(); i >= 0; i--){
                Set<String> s2 = sort2.get(i);
                
                if (s2 != null) {
                    candidates.addAll(s2);
                }
                
                if (candidates.size() > check) {
                    break;
                }
            }
        }

        return candidates;
    }

    private Set<String> getHash(OysterIdentityRecord oir, ArrayList<IndexRule> irs){
        Set<String> hashes = new LinkedHashSet<String>();
        
        for (Iterator<IndexRule> it = irs.iterator(); it.hasNext();) {
            IndexRule ir = it.next();
            String hash = null;
            for (Iterator<String> it2 = ir.getSegments().keySet().iterator(); it2.hasNext();) {
                String attribute = it2.next();
                String hashCode = ir.getSegments().get(attribute);
                
                String token = oir.get(attribute);
                
                if (token != null && !token.trim().isEmpty()){
                    if (hash == null) {
                        hash = "";
                    }
                    hash += decodeMethodSignature(token, hashCode);
                } else {
                    // Change per Dr T. if any element in the data is empty do not create a hash for this record
                    hash = null;
                    break;
                }
            }
            
            if (hash != null && !hash.isEmpty()){
                if (hash.endsWith("\u0000")) {
                    hash = hash.substring(0, hash.length()-1);
                }
                hashes.add(hash);
            }
        }
        
        return hashes;
    }
    
    private Set<String> getSimilarCandidates(Map<String, Set<String>> index, String valueToBeCompared){
    	     	
    	Set<String> candidates = null;
    	
    	ACMA acma = new ACMA();
    	double max_similarity = 0.00;
    	Heuristics simil = null;
 
		logger_results.info("Candidates List"+"\r\n");    	
    	for (Iterator<String> it = index.keySet().iterator();it.hasNext();){
    		String hash = it.next();
    		logger_results.info(hash+"|"+valueToBeCompared + "|");
    		simil =  acma.multivalued_attr_similarity_calc(hash, valueToBeCompared, "0.70", "rms", "SUBSET",1);
    		
    		if (simil.getAssertions() > 0){
    			if (max_similarity <= simil.getSimilarityGrade()){
    				candidates = index.get(hash);
    				max_similarity = simil.getSimilarityGrade();
    			}	
    		} 
    	}
    	
    	return candidates;
    }
    
    private String decodeMethodSignature(String token, String hashCode){
        String result = "", matchType = "", direction = "", charType = "", upperCase = "", order = "";
        int length = 0;
        
        if (hashCode.toUpperCase(Locale.US).startsWith("SOUNDEX")){
            matchType = "Soundex";
        } else if (hashCode.toUpperCase(Locale.US).startsWith("DMSOUNDEX(")){
            matchType = hashCode.trim().substring(10, hashCode.length()-1);
            
            length = Integer.parseInt(matchType);
            matchType = "DMSoundex";
        } else if (hashCode.toUpperCase(Locale.US).startsWith("IBMALPHACODE(")){
            matchType = hashCode.trim().substring(13, hashCode.length()-1);
            
            length = Integer.parseInt(matchType);
            matchType = "IBMAlphaCode";
        } else if (hashCode.toUpperCase(Locale.US).startsWith("NYSIIS(")){
            matchType = hashCode.trim().substring(7, hashCode.length()-1);
            
            length = Integer.parseInt(matchType);
            matchType = "NYSIIS";
        } else if (hashCode.toUpperCase(Locale.US).startsWith("SCAN(")){
            matchType = hashCode.trim().substring(5, hashCode.length()-1);
            String [] args = matchType.split("[,]");
            for (int i = 0; i < args.length; i++){
                switch (i) {
                    case 0:
                        if (args[i].trim().equalsIgnoreCase("LR") || args[i].trim().equalsIgnoreCase("RL")){
                            direction = args[i].trim();
                        }
                        break;
                    case 1:
                        if (args[i].trim().equalsIgnoreCase("ALL") || args[i].trim().equalsIgnoreCase("NONBLANK") ||
                            args[i].trim().equalsIgnoreCase("ALPHA") || args[i].trim().equalsIgnoreCase("LETTER") ||
                            args[i].trim().equalsIgnoreCase("DIGIT")){
                            charType = args[i].trim();
                        }
                        break;
                    case 2:
                        length = Integer.parseInt(args[i].trim());
                        break;
                    case 3:
                        if (args[i].trim().equalsIgnoreCase("ToUpper") || args[i].trim().equalsIgnoreCase("KeepCase")){
                            upperCase = args[i].trim();
                        }
                        break;
                    case 4:
                        if (args[i].trim().equalsIgnoreCase("Same") || args[i].trim().equalsIgnoreCase("L2HKeepDup") ||
                            args[i].trim().equalsIgnoreCase("L2HDropDup")){
                            order = args[i].trim();
                        }
                        break;
                    default: //FIXME: Should throw an error here but for now just a message
                        System.err.println("Invalid Argument List in SCAN method");
                }
            }
            matchType = "Scan";
        }
        
        if (matchType.equalsIgnoreCase("Soundex")){
            result = soundex.getSoundex(token);
        } else if (matchType.equalsIgnoreCase("DMSoundex")){
            result = dmSoundex.getDMSoundex(token)[0];
        } else if (matchType.equalsIgnoreCase("IBMAlphaCode")){
            result = alphaCode.getIBMAlphaCode(token);
        } else if (matchType.equalsIgnoreCase("NYSIIS")){
            result = nysiis.getNYSIISCode(token);
        } else if (matchType.equalsIgnoreCase("Scan")){
            result = scan.getScan(token, direction, charType, length, upperCase, order);
        }
        
        if (length > 0 && result.length() > length && !matchType.equalsIgnoreCase("Scan")){
            result = result.substring(0, length);
        }
        
        if (!"".equals(result)) {
            result += "\u0000";
        }
        
        return result;
    }
}