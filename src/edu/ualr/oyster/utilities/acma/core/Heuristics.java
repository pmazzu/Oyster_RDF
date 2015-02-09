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
		int heuristic = Integer.parseInt(args[6]);
		
		Aggregation aggregation = simil.getAggregation();
				
		timer.reset();
		timer.start();
		
		switch(heuristic){
			//**********************************************************************************************
			 //				CALCULATE THE SIMILARITY GRADE OF ALL THE DIFFERENT COMBINATIONS OF AUTHORS   //
			//**********************************************************************************************
			case 0:
				
				simil.initialization_oyster(args,1); //1: semicolon 2:space
				
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
				
				simil.initialization_oyster(args,1); //1: semicolon 2:space
				
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
				
				simil.initialization_oyster(args,1); //1: semicolon 2:space
				
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
			
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
				}
			
				break;
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION with Partial Average Threshold  		     //
				//			stop hole process when partial similarity average is below threshold			//
				//**********************************************************************************************
			case 4:
				
				simil.initialization_oyster(args,2); //1: semicolon 2:space
				
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true); // Comparison process
				aggregation.calculate_max_average(simil); // Calculate the similarity grade				
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
				}
								
				break;
				
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION									  		       //
				 //	Stop calculating similarity values in a respective row when one of them exceeds threshold  //
				//**********************************************************************************************
			case 5:
				
				simil.initialization_oyster(args,2); //1: semicolon 2:space
				
  			    // Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, false);
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil);				
				
//				if(mode == "DEBUG"){
//					logger_summary.info("Similarity Grade: " + simil.getSimilarityGrade());
//				}
														
				break;
				
				//**********************************************************************************************
				 //				ROW & COLUMN HEURISTIC DELETION									  		       	//
			 	//	All the simil values of each line are calculated.  The one selected is which exceeds		//
				//	the threshold											  		     						//
				//**********************************************************************************************
			case 6:
				
				simil.initialization_oyster(args,1); //1: semicolon 2:space
				
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
				
				simil.initialization_oyster(args,1); //1: semicolon 2:space
				
				Alignment.fingerprint_over_elements(simil.getSource());
				Alignment.fingerprint_over_elements(simil.getTarget());
				
				simil.setSource(Alignment.sorting_elements(simil.getSource()));
				simil.setTarget(Alignment.sorting_elements(simil.getTarget()));

				if(mode == "DEBUG"){
					sb.delete(0, sb.length());
					Iterator<Entity> itr_string_source = simil.getSource().iterator();
					
					while(itr_string_source.hasNext()){
						Entity entity = itr_string_source.next();
						sb.append(entity.getRealName() + " | ");
					}
					logger_summary.info("Author i - string: " + sb.toString() + " | Longitud: " + simil.getSource().size());
					
					sb.delete(0, sb.length());
					Iterator<Entity> itr_string_target = simil.getTarget().iterator();
					
					while(itr_string_target.hasNext()){
						Entity entity = itr_string_target.next();
						sb.append(entity.getRealName() + " | ");
					}
					logger_summary.info("Author j - string: " + sb.toString() + " | Longitud: " + simil.getTarget().size());

				}
				
				
				//simil.scan();
				
				// Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true);
				//Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, false);//prueba para OYSTER
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil); 
						
//				if(mode == "DEBUG"){
//					
//					logger_summary.info("similarities MATRIX");
//					
//					sb.delete(0, sb.length());
//					for(int i=0; i < simil.getRows();i++){
//						for(int j=0; j < simil.getColumns();j++){
//							sb.append(simil.getSimMatrixValue(i, j));
//							sb.append(" | ");
//						}
//						logger_summary.info(sb.toString());
//						
//						sb.delete(0, sb.length());
//					}
//					logger_summary.info("**********************************************************************");
//					
//					
//					
//					logger_summary.info("Similarity Grade|" + simil.getSimilarityGrade() + "|Comparisons made|" + simil.getComparisons() + "|Assertions|" + simil.getAssertions() + "|Rows|" + simil.getRows() + "|Columns|"+simil.getColumns());	
//				}
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
				
				//TODO REMOVE FROM HERE
				//****************************************************
				// 						Visualize RAW DATA
				//****************************************************		

//				if(mode == "DEBUG"){
////					for(int i=0; i < simil.getRows();i++){
////					sb.append(simil.getSource()[i].getRealName() + " | ");
////					}	
//					sb.delete(0, sb.length());
//					Iterator<Entity> itr_rows = simil.getSource().iterator();
//					while(itr_rows.hasNext()){
//						sb.append(itr_rows.next().getRealName() + " | ");
//					}
//					logger_summary.info("Author i - Raw: " + sb.toString() + " | Longitud: " + simil.getRows());
//				}
//
//				if(mode == "DEBUG"){
//					sb.delete(0, sb.length());
//					Iterator<Entity> itr_target = simil.getTarget().iterator();
//					while(itr_target.hasNext()){
//						sb.append(itr_target.next().getRealName()+ " | ");
//					}
////					for(int i=0; i < simil.getColumns();i++){
////						sb.append(simil.getTarget()[i].getRealName()+ " | ");
////					}
//					logger_summary.info("Author j - Raw: " + sb.toString() + " | Longitud: " + simil.getColumns());
//				}
				
				if(mode == "RESULT"){
					logger_results.info("Comparison_ID" + "|" + "Pub_SIIP" + "|" + "Pub_ISI" + "|" + "Rows" + "|" + "Columns" + "|" + "Comparisons" + "|"+ "Similar?"+"|"+"Max_Author"+"|"+"Assertions"+"|"+"Similarity_Grade"+"|"+"PORC_COMMON" + "|" + "Block"); //header
				}
			//TODO REMOVE TO HERE
													
				//simil.setSource(Alignment.sorting_elements(simil.getSource()));
				//simil.setTarget(Alignment.sorting_elements(simil.getTarget()));
															
				// Comparison process
				Comparison.calculate_similarity_of_some_visible_entities(simil.getSource(), simil.getTarget(), simil, true);
				// Calculate the similarity grade
				aggregation.calculate_max_average(simil); 
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity Grade|" + simil.getSimilarityGrade() + "|Comparisons made|" + simil.getComparisons() + "|Assertions|" + simil.getAssertions() + "|Rows|" + simil.getRows() + "|Columns|"+simil.getColumns());					
				}
				break;
				
			case 9:
				
				double similarityGrade_int = 0;
				double similarityGrade_string = 0;
				
				boolean similarityIntPerformed = false;
				boolean similarityStringPerformed = false;
				
				ArrayList<Entity> source_string = new ArrayList<Entity>();
				ArrayList<Entity> target_string = new ArrayList<Entity>();
				
				ArrayList<Entity> source_int = new ArrayList<Entity>();
				ArrayList<Entity> target_int = new ArrayList<Entity>();
							
				simil.initialization_oyster(args,2); //1: semicolon 2:space

				Iterator<Entity> itr_rows = simil.getSource().iterator();
				while(itr_rows.hasNext()){
					Entity entity = itr_rows.next();
					
//					if (entity.isString(entity.getRealName())){
//						entity.setPosition(source_string.size());
//						source_string.add(entity);
//					}else{
//						entity.setRealNameWithoutFormatting(entity.getFingerPrintName());
//						entity.setPosition(source_int.size());
//						source_int.add(entity);
//					}				
					
					if(entity.hasAnyNumber){
						entity.setRealNameWithoutFormatting(entity.getFingerPrintName());
						entity.setPosition(source_int.size());
						source_int.add(entity);
					}else{
						entity.setPosition(source_string.size());
						source_string.add(entity);
					}
				}
				
				Iterator<Entity> itr_target = simil.getTarget().iterator();
				while(itr_target.hasNext()){
					Entity entity = itr_target.next();
					
//					if (entity.isString(entity.getRealName())){
//						entity.setPosition(target_string.size());
//						target_string.add(entity);
//					}else{
//						entity.setRealNameWithoutFormatting(entity.getFingerPrintName());
//						entity.setPosition(target_int.size());
//						target_int.add(entity);
//					}
					if(entity.hasAnyNumber){
						entity.setRealNameWithoutFormatting(entity.getFingerPrintName());
						entity.setPosition(target_int.size());
						target_int.add(entity);
					}else{
						entity.setPosition(target_string.size());
						target_string.add(entity);
					}
				}
				
				int rows,columns;
				
				if(source_int.size()>0 & target_int.size()>0){
					
					similarityIntPerformed = true;
					
					if(source_int.size() < target_int.size()){
						rows = target_int.size();
						columns = source_int.size();
						ArrayList<Entity> temp = source_int;
						source_int = target_int;
						target_int = temp;
					} else {
						rows = source_int.size();
						columns = target_int.size();
					}
					
					simil.setSimiMatrix(rows, columns);				
					
					//TODO REMOVE IT FROM HERE
					if(mode == "DEBUG"){
						sb.delete(0, sb.length());
						Iterator<Entity> itr_int_source = source_int.iterator();
						
						while(itr_int_source.hasNext()){
							Entity entity = itr_int_source.next();
							sb.append(entity.getRealName() + " | ");
						}
						logger_summary.info("Author i - int: " + sb.toString() + " | Longitud: " + source_int.size());
					
						sb.delete(0, sb.length());
						Iterator<Entity> itr_int_target = target_int.iterator();
						
						while(itr_int_target.hasNext()){
							Entity entity = itr_int_target.next();
							sb.append(entity.getRealName() + " | ");
						}
						logger_summary.info("Author j - int: " + sb.toString() + " | Longitud: " + target_int.size());
					}
					
					//TODO TO HERE
					
					// Comparison process			
					Comparison.calculate_similarity_of_some_visible_entities(source_int, target_int, simil, false);
					
					// Calculate the similarity grade integers
					aggregation.calculate_max_average(simil);
					
					similarityGrade_int = simil.getSimilarityGrade();
					
					logger_summary.info("similarities INT");
					
					sb.delete(0, sb.length());
					for(int i=0; i < simil.getRows();i++){
						for(int j=0; j < simil.getColumns();j++){
							sb.append(simil.getSimMatrixValue(i, j));
							sb.append(" | ");
						}
						logger_summary.info(sb.toString());
						
						sb.delete(0, sb.length());
					}
					logger_summary.info("**********************************************************************");
				}
				

				if (source_string.size() > 0 & target_string.size() > 0){
					
					similarityStringPerformed = true;
					
					if(source_string.size() < target_string.size()){
						rows = target_string.size();
						columns = source_string.size();
						ArrayList<Entity> temp = source_string;
						source_string = target_string;
						target_string = temp;
					} else {
						rows = source_string.size();
						columns = target_string.size();
					}
					
					simil.setSimiMatrix(rows, columns);
									
					if(mode == "DEBUG"){
						sb.delete(0, sb.length());
						Iterator<Entity> itr_string_source = source_string.iterator();
						
						while(itr_string_source.hasNext()){
							Entity entity = itr_string_source.next();
							sb.append(entity.getRealName() + " | ");
						}
						logger_summary.info("Author i - string: " + sb.toString() + " | Longitud: " + source_string.size());
						
						sb.delete(0, sb.length());
						Iterator<Entity> itr_string_target = target_string.iterator();
						
						while(itr_string_target.hasNext()){
							Entity entity = itr_string_target.next();
							sb.append(entity.getRealName() + " | ");
						}
						logger_summary.info("Author j - string: " + sb.toString() + " | Longitud: " + target_string.size());

					}
					
					Comparison.calculate_similarity_of_some_visible_entities(source_string, target_string, simil, false);
					
					// Calculate the similarity grade strings
					aggregation.calculate_max_average(simil);
					
					similarityGrade_string = simil.getSimilarityGrade();
					
//					
					logger_summary.info("similarities STRING");
					
					sb.delete(0, sb.length());
					for(int i=0; i < simil.getRows();i++){
						for(int j=0; j < simil.getColumns();j++){
							sb.append(simil.getSimMatrixValue(i, j));
							sb.append(" | ");
						}
						logger_summary.info(sb.toString());
						
						sb.delete(0, sb.length());
					}
					logger_summary.info("**********************************************************************");
				}
				

				if (similarityIntPerformed && similarityStringPerformed){
					simil.setSimilarityGrade((similarityGrade_int+similarityGrade_string)/2.0);
				} else if (similarityIntPerformed){
					simil.setSimilarityGrade(similarityGrade_int);
				} else if (similarityStringPerformed){
					simil.setSimilarityGrade(similarityGrade_string);
				}
				
				if(mode == "DEBUG"){
					logger_summary.info("Similarity int|"+similarityGrade_int+"|Similarity string|"+similarityGrade_string+"|Similarity Grade|" + simil.getSimilarityGrade() + "|Comparisons made|" + simil.getComparisons() + "|Assertions|" + simil.getAssertions() + "|Rows|" + simil.getRows() + "|Columns|"+simil.getColumns());					
				}
				
				break;
				
			}
		
		timer.stop();
		simil.setPorcCommon();
		simil.setBlock();
		
		//****************************************************
		// 				SHOW THE SIMILARITY VALUES
		//****************************************************		
		if(mode == "DEBUG"){
			
			//****************************************************
			// 						Visualize FingerPrinted DATA
			//****************************************************	
			
//			if(mode == "DEBUG"){
////				sb.delete(0, sb.length());
////				for(int i=0; i < simil.getRows();i++){
////					sb.append(simil.getSource()[i].getRealName() + " | ");
////				}
//				sb.delete(0, sb.length());
//				Iterator<Entity> itr_rows = simil.getSource().iterator();
//				while(itr_rows.hasNext()){
//					sb.append(itr_rows.next().getRealName() + " | ");
//				}				
//				logger_summary.info("Author i - FingerPrinted: " + sb.toString() + " | Longitud: " + simil.getRows());
//			}
			
//			if(mode == "DEBUG"){
////				sb.delete(0, sb.length());
////				for(int i=0; i < simil.getColumns();i++){
////					sb.append(simil.getTarget()[i].getRealName()+ " | ");
////				}			
//				sb.delete(0, sb.length());
//				Iterator<Entity> itr_target = simil.getTarget().iterator();
//				while(itr_target.hasNext()){
//					sb.append(itr_target.next().getRealName()+ " | ");
//				}
//				logger_summary.info("Author j - FingerPrinted: " + sb.toString() + " | Longitud: " + simil.getColumns());
//			}
			
//			logger_summary.info("Heuristic: " + heuristic);
//			
//			sb.delete(0, sb.length());
//			for(int i=0; i < simil.getRows();i++){
//				for(int j=0; j < simil.getColumns();j++){
//					sb.append(simil.getSimMatrixValue(i, j));
//					sb.append(" | ");
//				}
//				logger_summary.info(sb.toString());
//				
//				sb.delete(0, sb.length());
//			}
//			logger_summary.info("**********************************************************************");
//		}
		
//		if(mode == "RESULT"){
//			sb.delete(0, sb.length());
//			sb.append(simil.getSource()[0].getPubId()).append("-").append(simil.getTarget()[0].getPubId());
//			
//			if (simil.getSimilarityGrade() >= simil.getThreshold()){
//				logger_results.log(Level.INFO, sb.toString() + "|" + simil.getSource()[0].getPubId() + "|" + simil.getTarget()[0].getPubId() + "|" + simil.getRows() + "|" + simil.getColumns() + "|" + simil.getComparisons() + "|" +"yes" + "|" + simil.getRows() + "|" + simil.getAssertions() + "|" + simil.getSimilarityGrade() + "|" + simil.getPorcCommon() + "|" + simil.getBlock());
//			}else{
//				logger_results.log(Level.INFO, sb.toString() + "|" + simil.getSource()[0].getPubId() + "|" + simil.getTarget()[0].getPubId() + "|" + simil.getRows() + "|" + simil.getColumns() + "|" + simil.getComparisons() + "|" +"no"  + "|" + simil.getRows() + "|" + simil.getAssertions() + "|" + simil.getSimilarityGrade() + "|" + simil.getPorcCommon() + "|" + simil.getBlock());
//			}
//		}

//		if(mode == "RESULT"){		
//		
//			Iterator<Entity> itr_source = simil.getSource().iterator();
//			Iterator<Entity> itr_target = simil.getTarget().iterator();
//		
//			if(itr_source.hasNext() && itr_target.hasNext()){
//				Entity source;
//				Entity target;
//				source = itr_source.next();
//				target = itr_target.next();
//				sb.delete(0, sb.length());
//				sb.append(source.getPubId()).append("-").append(target.getPubId());
//				
//				if (simil.getSimilarityGrade() >= simil.getThreshold()){
//					logger_results.log(Level.INFO, sb.toString() + "|" + source.getPubId() + "|" + target.getPubId() + "|" + simil.getRows() + "|" + simil.getColumns() + "|" + simil.getComparisons() + "|" +"yes" + "|" + simil.getRows() + "|" + simil.getAssertions() + "|" + simil.getSimilarityGrade() + "|" + simil.getPorcCommon() + "|" + simil.getBlock());
//				}else{
//					logger_results.log(Level.INFO, sb.toString() + "|" + source.getPubId() + "|" + target.getPubId() + "|" + simil.getRows() + "|" + simil.getColumns() + "|" + simil.getComparisons() + "|" +"no"  + "|" + simil.getRows() + "|" + simil.getAssertions() + "|" + simil.getSimilarityGrade() + "|" + simil.getPorcCommon() + "|" + simil.getBlock());
//				}				
//			}
		}
								
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
}