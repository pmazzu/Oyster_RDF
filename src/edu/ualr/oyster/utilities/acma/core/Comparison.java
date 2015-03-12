package edu.ualr.oyster.utilities.acma.core;

import java.util.logging.Logger;

import edu.ualr.oyster.utilities.acma.log.LogAdministrator;
import edu.ualr.oyster.utilities.acma.string_matching.JaroWinklerDistance;
import edu.ualr.oyster.utilities.acma.string_matching.LongestCommonSubstring;

import java.util.*;

import edu.ualr.oyster.utilities.NYSIISCode;
import edu.ualr.oyster.utilities.OysterNickNameTable;
import edu.ualr.oyster.utilities.OysterAliasTable;

public class Comparison{

	private final static Logger logger_results = Logger.getLogger("RESULTS");
	private final static Logger logger_summary = Logger.getLogger("SUMMARY");
	private final static Logger logger_log = Logger.getLogger("LOG");
	
	/**
	 * When the similarity of two authors exceeds the threshold,
	 * all the cells in that column and rows will not be compared anymore.
	 * Thats why each author has an attribute called "visible", which will be set to TRUE or FALSE
	 * depending the results of the comparison.  
	 * @param The similarity matrix, and the authors to be compared
	 * @return N/A 
	 */

//	public static void calculate_similarity_of_some_visible_entities(Entity[] authors_i, Entity[] authors_j, Similarity simil, boolean check){
//		
//		double value = 0;
//		double max = 0;
//		int row = 0, column = 0; 
//		float checkpoint = 0;
//		
//		String mode = LogAdministrator.getMode();
//		
//		if (check){
//			checkpoint = Math.round(authors_i.length * 0.8)-1; // the -1 is because the array index starts from 0 (i)
//		}
//		
//		
//		for(int i=0; i<authors_i.length;i++){
//			if(authors_i[i].getVisibility()){
//				max = 0;
//				for(int j=0; j<authors_j.length;j++){
//					if(authors_j[j].getVisibility()){
//						value = similarity(authors_i[i].getRealName(), authors_j[j].getRealName(), simil);
//						
//						simil.increaseComparisons();
//						simil.updateSimMatrix(i, j, value);
//						
////						if (mode == "DEBUG"){
////							logger_log.info("Similarity value: " + simil.getSimMatrixValue(i, j) + "Row: " + i + "Column: " + j);
////						}
//						
//						if (value > max && value >= simil.getThreshold()){
//							//could be that two authors in the same row have a similarity higher that the threshold
//							//but we need the highest one
//							max = value;
//							row = i;
//							column = j;
//							simil.increaseAssertions();
//							j = authors_j.length;
//						}
//					}
//				}
//				
//				if (max != 0){
//					authors_i[row].setVisibilityFalse(); // the author of this row will not be compared again against other author
//					authors_j[column].setVisibilityFalse(); // the author of this column will not be compared again against other author
//				}
//										
//				if(check){
//					if (i == checkpoint){
//						simil.calculate_partial_similarity_grade(i);
//						if (mode == "DEBUG"){
//							logger_log.info("Partial similarity: " + simil.getPartialSimilarityGrade());
//						}
//						if(simil.getPartialSimilarityGrade() < simil.getNoSimilarityThreshold()){
//							i = authors_i.length;; // It is not useful continue with the comparison if the .7 of the authors similarity is below the threshold
//						}
//					}
//				}		
//			}
//		}
//	}
	
	public static void calculate_similarity_of_some_visible_entities(ArrayList<Entity> authors_i, ArrayList<Entity> authors_j, Similarity simil, boolean check){
		
		double value = 0;
		float checkpoint = 0;
		int row_counter = 0;
		Iterator<Entity> itr_i;
		Iterator<Entity> itr_j;
		Entity author_row;
		Entity author_column = null;
		
		String mode = LogAdministrator.getMode();
		
		if (check){
			checkpoint = Math.round(authors_i.size() * 0.8)-1;// the -1 is because the array index starts from 0 (i)
		}
								
		first_loop:
		while(true){
			itr_i = authors_i.iterator();
			//second_loop:
			while(itr_i.hasNext()){
				author_row = itr_i.next();
				itr_i.remove();//will not use this entity anymore.  I keep just the reference in author_row
				value=0;
				itr_j = authors_j.iterator();
				third_loop:
				while(itr_j.hasNext()){
					author_column = itr_j.next();
			
					
//					if(author_row.getRealName().contains(" ") || author_column.getRealName().contains(" ")){
//						ACMA acma = new ACMA();
//						Heuristics tempSimil;
//						
//						tempSimil = acma.multivalued_attr_similarity_calc(author_row.getRealName(), author_column.getRealName(), Double.toString(simil.getThreshold()),simil.getCompString() , simil.getAggregation().getMode()," " , 1);
//						value = tempSimil.getSimilarityGrade();
//						simil.setComparisons(simil.getComparisons()+tempSimil.getComparisons());						
//						
//					}else{
//						value = similarity(author_row.getRealName(), author_column.getRealName(), simil);
//						simil.increaseComparisons();
//					}
					
					
					value = similarity(author_row.getRealName(), author_column.getRealName(), simil);
					//logger_summary.info("Partial similarity: " + value + " row: " + author_row.getRealName() + " column: " + author_column.getRealName() + "\r\n");
					simil.increaseComparisons();
					
					double roundedValue = (double) Math.round(100*value)/100;
					simil.updateSimMatrix(author_row.getPosition(),
							              author_column.getPosition(),
							              roundedValue);
						
					
					if (roundedValue >= simil.getThreshold()){
						itr_j.remove();
						simil.increaseAssertions();
						break third_loop;
					}
				}
				
				if(check){
					if ( row_counter == checkpoint){
						simil.calculate_partial_similarity_grade(row_counter,simil.getAggregation().getMode());
//						if (mode == "DEBUG"){
//							logger_log.info("Partial similarity: " + simil.getPartialSimilarityGrade());
//						}
						if(simil.getPartialSimilarityGrade() < simil.getNoSimilarityThreshold()){
							break first_loop; // It is not useful continue with the comparison if the .7 of the authors similarity is below the threshold
						}
					}
				}
				row_counter = row_counter + 1;
			}
			if(!itr_i.hasNext()){
				break first_loop;	
			}
		}
	}

//	public static void calculate_similarity_of_all_visible_entities(Entity[] authors_i, Entity[] authors_j, Similarity simil){
//
//		double value = 0;
//		double max = 0;
//		int row = 0, column = 0; 
//		
//		for(int i=0; i<authors_i.length;i++){
//			if(authors_i[i].getVisibility()){
//				max = 0;
//				for(int j=0; j<authors_j.length;j++){
//					if(authors_j[j].getVisibility()){
//						value = similarity(authors_i[i].getRealName(), authors_j[j].getRealName(),simil);
//						
//						simil.increaseComparisons();
//						simil.updateSimMatrix(i, j, value);
//						
//						if (value > max && value >= simil.getThreshold()){
//							//could be that two authors in the same row have a similarity higher that the threshold
//							//but we need the highest one
//							max = value;
//							row = i;
//							column = j;
//							simil.increaseAssertions();
//						}
//					}
//				}
//				
//				if (max != 0){
//					authors_i[row].setVisibilityFalse(); // the author of this row will not be compared again against other author
//					authors_j[column].setVisibilityFalse(); // the author of this column will not be compared again against other author
//				}
//												
//			}
//		}
//	}
	
	public static void calculate_similarity_of_all_visible_entities(ArrayList<Entity> authors_i, ArrayList<Entity> authors_j, Similarity simil){
		
		double value = 0;
		double max = 0;
		Iterator<Entity> itr_i;
		Iterator<Entity> itr_j;
		Entity author_row;
		Entity author_column = null;
		Entity author_j_to_delete = null;
			
		itr_i = authors_i.iterator();
		
		while(itr_i.hasNext()){
			author_row = itr_i.next();
			itr_i.remove();//will not use this entity anymore.  I keep just the reference in author_row
			// the author of this row will not be compared again against other author
			value=0;
			max=0;
			itr_j = authors_j.iterator();
			//third_loop:
			while(itr_j.hasNext()){
				author_column = itr_j.next();
		
				value = similarity(author_row.getRealName(), author_column.getRealName(), simil);
				simil.increaseComparisons();
				simil.updateSimMatrix(author_row.getPosition(),
						              author_column.getPosition(),
						              value);
					
				if (value > max && value >= simil.getThreshold()){
					max = value;
					author_j_to_delete = author_column;
					simil.increaseAssertions();
					//break third_loop;
				}
			}
			if (max != 0){
				authors_j.remove(author_j_to_delete); // the author of this column will not be compared again against other author
			}
		}
	}

//	public static void similarity_of_main_diagonal(Entity[] authors1, Entity[] authors2, Similarity simil){
//		
//		int index = 0;
//		double value = 0;
//		
//		while (index < authors1.length & index < authors2.length){
//			
//			value = similarity(authors1[index].getRealName(), authors2[index].getRealName(), simil);
//			simil.increaseComparisons();
//				
//			if (value >= simil.getThreshold()){
//				simil.increaseAssertions();
//				authors1[index].setVisibilityFalse(); // the author of this row will not be compared again against other author
//				authors2[index].setVisibilityFalse(); // the author of this column will not be compared again against other author
//			}
//				
//			simil.updateSimMatrix(index, index, value);	
//			index++;
//		}
//	}
	
	public static void similarity_of_main_diagonal(ArrayList<Entity> authors_i, ArrayList<Entity> authors_j, Similarity simil){
		
		double value = 0;
		Iterator<Entity> itr_i;
		Iterator<Entity> itr_j;
		Entity author_row;
		Entity author_column;
		
		itr_i = authors_i.iterator();
		itr_j = authors_j.iterator();
		
		while(itr_i.hasNext() && itr_j.hasNext()){
			author_row = itr_i.next();
			author_column = itr_j.next();
			value = similarity(author_row.getRealName(), author_column.getRealName(), simil);
			simil.increaseComparisons();	
			simil.updateSimMatrix(author_row.getPosition(), author_column.getPosition(), value);
		}
	}

//	public static void allAgaistAll(Entity[] authors1, Entity[] authors2, Similarity simil){
//		
//		double value = 0;
//		
//		for(int i = 0; i < authors1.length;i++){
//			for(int j = 0; j < authors2.length;j++){
//				
//				value = similarity(authors1[i].getRealName(), authors2[j].getRealName(), simil);
//				
//				simil.increaseComparisons();
//							
//				if (value >= simil.getThreshold()){
//					simil.increaseAssertions();
//				}
//				
//				simil.updateSimMatrix(i, j, value);
//				
//			}
//		}
//	}
	
	public static void allAgaistAll(ArrayList<Entity> authors_i, ArrayList<Entity> authors_j, Similarity simil){
		
		double value = 0;
		
		Iterator<Entity> itr_i;
		Iterator<Entity> itr_j;
		Entity author_row;
		Entity author_column;
		
		itr_i = authors_i.iterator();
		
		while(itr_i.hasNext()){
			author_row = itr_i.next();
			itr_j = authors_j.iterator();
			while(itr_j.hasNext()){
				author_column = itr_j.next();
				value = similarity(author_row.getRealName(), author_column.getRealName(), simil);
				simil.increaseComparisons();
				
				if (value >= simil.getThreshold()){
					simil.increaseAssertions();
				}
				simil.updateSimMatrix(author_row.getPosition(), author_column.getPosition(), value);
			}
		}
	}

	public static double similarity(String author1, String author2, Similarity simil){
		
		double similarity_jw = 0; // Jaro-Winkler
		double similarity_lcs = 0; //Longest Common Substring
		double similarity_rms = 0; // Root Mean Square
		double similarity_nickname = 0; //nickname
		double similarity_alias = 0; //nickname
		double similarity_exact = 0; //exact matching
		double similarity_nysiis = 0; // NYSII algorithm
		ArrayList<Double> similarity_values = new ArrayList<Double>();
				
		//String mode = LogAdministrator.getMode();
		
		int comparators = simil.getComparatorsList().length;
		
		if (comparators >0){	
			
			for(int i=0;i<comparators;i++){
				
				switch (simil.getComparatorIndex(simil.getComparatorsList()[i])){
					case 0://rms
						
						JaroWinklerDistance jw = new JaroWinklerDistance();
						
						//Because the JW implementation returns 0 when are equals and 1 when they are different
						similarity_jw = 1 - jw.distance(author1, author2);
								
						LongestCommonSubstring lcs = new LongestCommonSubstring();
						
						similarity_lcs = lcs.lcs_similarity_grade(author1, author2);
								
						similarity_rms = Math.sqrt( ( Math.pow(similarity_jw, 2) + Math.pow(similarity_lcs, 2) ) / 2);
						
						similarity_values.add(similarity_rms);
						
						break;
					case 1://nickname
						
					    /** Nickname/Alias lookup */
					    OysterNickNameTable nnTable;
						
						nnTable = new OysterNickNameTable();
						
						if (nnTable.isNicknamePair(author1, author2)){
							similarity_nickname = 1;
						}
						
						similarity_values.add(similarity_nickname);
						
						break;
					case 2://exact Matching
						
						if (author1.equals(author2)){
							similarity_exact = 1;
						}
						
						similarity_values.add(similarity_exact); 
						
						break;
					case 3: //alias
					    /** Alias lookup */
					    OysterAliasTable aTable;
						
					    aTable = new OysterAliasTable();
						
						if (aTable.isAliasPair(author1, author2)){
							similarity_alias = 1;
						}
						
						similarity_values.add(similarity_alias);
												
						break;
					case 4: //NYSII
						
						/** New York State Identification and Intelligence System operator */
					    NYSIISCode nysiis;
						
				        nysiis = new NYSIISCode();
				        
				        if (nysiis.compareNYSIISCodes(author1, author2)){
				        	similarity_nysiis = 1;;
				        }
				        
				        similarity_values.add(similarity_nysiis);
						
						break;
						
					case 5://jw
						
						JaroWinklerDistance jaroW = new JaroWinklerDistance();
						
						//Because the JW implementation returns 0 when are equals and 1 when they are different
						similarity_jw = 1 - jaroW.distance(author1, author2);
														
						similarity_values.add(similarity_jw);
						
						break;
						
					default:
						break;
				}
			}
			
//			if (mode == "DEBUG"){
//				logger_log.info("author1: " + author1 + " author2: " + author2 + " similarity_rms: " + similarity_rms + " similarity_nick: " + similarity_nickname + " similarity_soundex: " + similarity_soundex);
//			}
			
		}
		
		return max(similarity_values);
	}
	
	
	public static double max(ArrayList<Double> similarity_values){
			
		Iterator<Double> itr = similarity_values.iterator();
		
		double max = -1;
		double simil_value = 0;
		
		while(itr.hasNext()){
			simil_value = itr.next();
			
			if(simil_value > max){
				max = simil_value;
			}	
		}
		
		return max;
		
	}
	
}