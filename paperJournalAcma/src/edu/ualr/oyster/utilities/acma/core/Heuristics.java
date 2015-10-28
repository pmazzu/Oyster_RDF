package edu.ualr.oyster.utilities.acma.core;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import edu.ualr.oyster.utilities.acma.log.LogAdministrator;
import edu.ualr.oyster.utilities.acma.log.MyStopWatch;
import edu.ualr.oyster.utilities.acma.source.Sources;


public class Heuristics extends Similarity {
	
	private final static Logger logger_results = Logger.getLogger("RESULTS");
	private final static Logger logger_summary = Logger.getLogger("SUMMARY");
	private final static Logger logger_log = Logger.getLogger("LOG");
	
//**********************************************************************************************
//				CALCULATE THE SIMILARITY GRADE OF ALL THE DIFFERENT COMBINATIONS OF AUTHORS   //
//**********************************************************************************************
	public static void heuristic(Similarity simil, String[] args){
		
		int totalComparisons = 0;
		long totalExecutionTime = 0;
		
		String mode = LogAdministrator.getMode();
		MyStopWatch timer = new MyStopWatch();
		StringBuilder sb = new StringBuilder();
		int heuristic = Integer.parseInt(args[5]);
		
		Aggregation aggregation = simil.getAggregation();
				
		timer.reset();
		timer.start();
		
		switch(heuristic){
			//**********************************************************************************************
			 //				CALCULATE THE SIMILARITY GRADE OF ALL THE DIFFERENT COMBINATIONS OF AUTHORS   //
			//**********************************************************************************************
			case 0:
				
				simil.initialization_oyster(args);
				
				Comparison.allAgaistAll(simil.getSource(), simil.getTarget(), simil);
			
				aggregation.calculate_average(simil);				
									
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
				}
									
				break;
			
			//**********************************************************************************************
			 //				CALCULATE THE SIMILARITY GRADE OF ALL THE DIFFERENT COMBINATIONS OF AUTHORS   //
			//**********************************************************************************************
			case 1:
				simil.initialization_oyster(args);
				
				Comparison.allAgaistAll(simil.getSource(), simil.getTarget(), simil);
			
				//aggregation.calculate_average(simil);				
				aggregation.calculate_max_average(simil); // Despu�s de hablar con H�ctor el 7/4/2014
			
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
				}
									
				break;
			
				//****************************************************************************************************
				 //				CALCULATE THE SIMILARITY GRADE OF THE MAIN DIAGONAL   					      		 //
				// After the alignment, calculate the similarity of the main diagonal.  If it is below the threshold //
				// proceed with the row&column comparison process
				//***************************************************************************************************//
			case 2:
				
				//simil.initialization_oyster(args,1); //1: semicolon 2:space
				simil.initialization_oyster(args);
				
				// Alignment process
				Alignment.fingerprint_over_elements(simil.getSource());
				Alignment.fingerprint_over_elements(simil.getTarget());
				
				simil.setSource(Alignment.sorting_elements(simil.getSource()));
				simil.setTarget(Alignment.sorting_elements(simil.getTarget()));
										
				//Comparison process
				Comparison.similarity_of_main_diagonal(simil.getSource(), simil.getTarget(), simil);
			
				// Calculate similarity Grade
				aggregation.calculate_max_average(simil);
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade main diagonal: " + simil.getSimilarityGrade());
				}						
				
				if (simil.getSimilarityGrade() < simil.getThreshold()){
					
					simil.setAssertions(0);
					simil.setEfficency(0);
					simil.setSimilarityGrade(0);
					
					// Comparison process
					Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true);
					// Calculate the similarity grade
					aggregation.calculate_max_average(simil);				
				}
			
				break;
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION with Partial Average Threshold  		     //
				//			stop hole process when partial similarity average is below threshold			//
				//**********************************************************************************************
			case 4:
				
				simil.initialization_oyster(args);				
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, false); // Comparison process Se prueba no haciendo check
				aggregation.calculate_max_average(simil); // Calculate the similarity grade				
				
				break;
				
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION									  		       //
				 //	Stop calculating similarity values in a respective row when one of them exceeds threshold  //
				//**********************************************************************************************
			case 5:

				simil.initialization_oyster(args);
  			    // Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, false);
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil);				
																		
				break;
				
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION									  		       	//
			 	//	All the simil values of each line are calculated.  The one selected is which exceeds		//
				//	the threshold											  		     						//
				//**********************************************************************************************
			case 6:
				
				//simil.initialization_oyster(args,1); //1: semicolon 2:space
				simil.initialization_oyster(args);
				
				// Comparison process
				Comparison.calculate_similarity_of_all_visible_entities(simil.getSource(), simil.getTarget(), simil);
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil);				
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
				}
								
				break;

				//**********************************************************************************************
				 //				FINGERPRINT & ROW/COLUMN HEURISTIC DELETION									  		       	//
			 	//	FingerPrint for alignment, and Row&Column for comparison
				//**********************************************************************************************
			case 7:
				
				//simil.initialization_oyster(args,1); //1: semicolon 2:space
				simil.initialization_oyster(args);
				
				Alignment.fingerprint_over_elements(simil.getSource());
				Alignment.fingerprint_over_elements(simil.getTarget());
				
				simil.setSource(Alignment.sorting_elements(simil.getSource()));
				simil.setTarget(Alignment.sorting_elements(simil.getTarget()));
				
				// Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true);
				//Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, false);//prueba para OYSTER
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil); 
						
				break;
				
				//**********************************************************************************************
				 //				SYNONIMS SEARCH									  		       	               //
				//**********************************************************************************************
			case 8:
			
				Sources source = new Sources();
				
				ArrayList<String> arrayList1 = source.generateArrayList(args[0]);
				ArrayList<String> arrayList2 = source.generateArrayList(args[1]);
				
				Synonym synonym = new Synonym();
				
				//Extended vocabulary - Get keyword's synonyms
				arrayList1 = synonym.getSynonyms(arrayList1);
				arrayList2 = synonym.getSynonyms(arrayList2);
				
				simil.initialization_h8(args,arrayList1,arrayList2);
															
				// Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true);
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil); 
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade|" + simil.getSimilarityGrade() + "|Comparisons made|" + simil.getComparisons() + "|Assertions|" + simil.getAssertions() + "|Rows|" + simil.getRows() + "|Columns|"+simil.getColumns());					
				}
				break;

			//**********************************************************************************************//
			//	              UNSTRUCTURED DATA COMPARISON						  		        		    //
			//It takes the alphanumerics and divide them in two groups: numbers and characters are put 	    //
			//separately, so the similarity calculation is more accurate, as numbers are compared against   //
			//numbers and characters against characters													    //
			//**********************************************************************************************//
			case 9:
				
				double similarityGrade_int = 0;
				double similarityGrade_string = 0;
				
				boolean similarityIntPerformed = false;
				boolean similarityStringPerformed = false;
				
				ArrayList<Entity> source_string;
				ArrayList<Entity> target_string;
				
				ArrayList<Entity> source_int;
				ArrayList<Entity> target_int;
				
				ArrayList<Entity>[] intAndString_source;
				ArrayList<Entity>[] intAndString_target;
							
				simil.initialization_oyster(args); //1: semicolon 2:space

				//Separates the alphanumeric in two groups: characters on one side and numbers on the other
				intAndString_source = Heuristics.separateNumbersFromCharacters(simil.getSource().iterator());
				intAndString_target = Heuristics.separateNumbersFromCharacters(simil.getTarget().iterator());
						
				source_int = intAndString_source[0];
				source_string = intAndString_source[1];
				target_int = intAndString_target[0];
				target_string = intAndString_target[1];
				
				// If both source and target have numbers to compare, the comparison is carried on
				if(source_int.size()>0 & target_int.size()>0){
					similarityIntPerformed = true;
					similarityGrade_int = Heuristics.fairComparison(simil, source_int, target_int);
					
				}
				// If both source and target have characters to compare, the comparison is carried on
				if (source_string.size() > 0 & target_string.size() > 0){
					similarityStringPerformed = true;
					similarityGrade_string = Heuristics.fairComparison(simil, source_string, target_string);			
				}
				
				// Calculate the similarity grade considering which comparison had been performed
				if (similarityIntPerformed && similarityStringPerformed){
					simil.setSimilarityGrade((similarityGrade_int+similarityGrade_string)/2.0);
				} else if (similarityIntPerformed){
					simil.setSimilarityGrade(similarityGrade_int);
				} else if (similarityStringPerformed){
					simil.setSimilarityGrade(similarityGrade_string);
				}
				
				break;
		}
		
		timer.stop();
		simil.setPorcCommon();
		simil.setBlock();

//		if(mode == "PERFORMANCE"){
//			
//			totalComparisons = totalComparisons + simil.getComparisons();
//			totalExecutionTime = totalExecutionTime + timer.getElapsedTime();
//			
//			logger_log.log(Level.INFO, "Total Comparisonn: " + totalComparisons);
//			logger_log.log(Level.INFO, "Elapsed Time - ns: " + totalExecutionTime);
//			logger_log.log(Level.INFO, "Elapsed Time - ms: " + TimeUnit.MILLISECONDS.convert(totalExecutionTime, TimeUnit.NANOSECONDS));
//			logger_log.log(Level.INFO, "Elapsed Time - sec: " + TimeUnit.SECONDS.convert(totalExecutionTime, TimeUnit.NANOSECONDS));
//		}
	}
	
	private static ArrayList<Entity>[] separateNumbersFromCharacters(Iterator<Entity> itr){
		
		ArrayList<Entity> string = new ArrayList<Entity>();
		ArrayList<Entity> integer = new ArrayList<Entity>();
		ArrayList<Entity>[] intSeparatedFromString = (ArrayList<Entity>[])new ArrayList[2];
				
		 while(itr.hasNext()){
			Entity entity = itr.next();
			
			if(entity.hasAnyNumber){
				entity.setRealNameWithoutFormatting(entity.getFingerPrintName());
				entity.setPosition(integer.size());
				integer.add(entity);
			}else{
				entity.setPosition(string.size());
				string.add(entity);
			}
		}
		 
		 intSeparatedFromString[0] = integer;
		 intSeparatedFromString[1] = string;
		 		 
		 return intSeparatedFromString;
		
	}
	
	private static double fairComparison(Similarity simil, ArrayList<Entity> source, ArrayList<Entity> target){
		
		int rows,columns;
		double similarityGrade = 0;
		Aggregation aggregation = simil.getAggregation();
					
		if(source.size() < target.size()){
			rows = target.size();
			columns = source.size();
			ArrayList<Entity> temp = source;
			source = target;
			target = temp;
		} else {
			rows = source.size();
			columns = target.size();
		}
		
		simil.setSimiMatrix(rows, columns);				
		
		// Comparison process			
		Comparison.calculate_similarity_of_some_visible_entities(source, target, simil, false);
		
		// Calculate the similarity grade integers
		aggregation.calculate_max_average(simil);
		
		similarityGrade = simil.getSimilarityGrade();
		
		return similarityGrade;
		
	}
}