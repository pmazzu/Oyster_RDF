package edu.ualr.oyster.utilities.acma.core;

import java.util.logging.Logger;
//import java.util.*;


import edu.ualr.oyster.utilities.acma.log.LogAdministrator;


public class ACMA {
	
	private final static Logger logger_results = Logger.getLogger("RESULTS");
	private final static Logger logger_summary = Logger.getLogger("SUMMARY");
//	private final static Logger logger_log = Logger.getLogger("LOG");
	
//	public static void main(String[] args) {
//		
//		LogAdministrator.init_log();
//		//LogAdministrator.init_results();
//		//LogAdministrator.init_summary()				
//		
//		String author_list_1 = "Elizondo-Montemayor, Leticia; Alvarez, Mario M.; Hernandez-Torre, Martin; Ugalde-Casas, Patricia A.; Lam-Franco, Lorena; Bustamante-Careaga, Humberto; Castilleja-Leal, Fernando; Contreras-Castillo, Julio; Moreno-Sanchez, Hector; Tamargo-Barrera, Daniela";
//		String author_list_2 = "Elizondo-Montemayor, Leticia; Alvarez, Mario M.; Hernandez-Torre, Martin; Ugalde-Casas, Patricia A.; Lam-Franco, Lorena; Bustamante-Careaga, Humberto; Castilleja-Leal, Fernando; Contreras-Castillo, Julio; Moreno-Sanchez, Hector; Tamargo-Barrera, Daniela";
//		//String author_list_2 = "Guinovart-Diaz, R; Bravo-Castillero, J; Rodriguez-Ramos, R; Martinez-Rosado, R; Serrania, F; Navarrete, M";
//		
//		String threhold = "0.82";
	
//		String mode = LogAdministrator.getMode();
//	
//		if(mode == "RESULT"){
//			logger_results.info("Comparison_ID" + "|" + "Pub_SIIP" + "|" + "Pub_ISI" + "|" + "Rows" + "|" + "Columns" + "|" + "Comparisons" + "|"+ "Similar?"+"|"+"Max_Author"+"|"+"Assertions"+"|"+"Similarity_Grade"+"|"+"PORC_COMMON" + "|" + "Block"); //header
//		}
//		
//		boolean similar = new ACMA().multivalued_attr_similarity_calc(author_list_1, author_list_2, threhold);
//		
//		logger_log.log(Level.INFO, "RESULT: " + similar);
//		
//		LogAdministrator.removeHandler("LOG");
//		//LogAdministrator.removeHandler("RESULTS");
//		//LogAdministrator.removeHandler("SUMMARY");
//			
//	}
//	
	public Heuristics multivalued_attr_similarity_calc(String list_1, String list_2, String threshold, String comparators, String aggrMode, int stage){
		
	//TODO REMOVE FROM HERE
//		LogAdministrator.init_log();
//		LogAdministrator.init_results();
//		LogAdministrator.init_summary();
//		String mode = LogAdministrator.getMode();
//		StringBuilder sb = new StringBuilder();
	//TODO REMOVE TO HERE
		
//		if (stage == 1){
//			logger_summary.info("index_comparison");
//		}else{
//			logger_summary.info("similarity_comparison");
//		}
				
		String threshold_noSimil = "0.8";
		String heuristic_id = "4";
		
		String[] arguments = new String[7];
			
		arguments[0] = list_1; 
		arguments[1] = list_2;
		arguments[2] = threshold;
		arguments[3] = threshold_noSimil;
		arguments[4] = comparators;
		arguments[5] = aggrMode;
		arguments[6] = heuristic_id;
		
		boolean similar;
		double[] results = new double[2];
			
		Heuristics simil = new Heuristics();
				
		Heuristics.heuristic(simil, arguments);
			
//		if (simil.getSimilarityGrade()> simil.getThreshold()){
//			similar = true;
//		}else{
//			similar = false;
//		}
		
		//logger_summary.info("Lists compared: " + list_1 + " | " + list_2 + " | " + simil.getSimilarityGrade());
		
		//logger_summary.info("Similitud: " +  simil.getSimilarityGrade());
		
		if(stage == 1){
			logger_results.info(simil.getSimilarityGrade() + "\r\n"); //inside cluster comparison
		}else{
			logger_summary.info(simil.getSimilarityGrade() + "\r\n"); // index comparison
		}
		
		
		
		//LogAdministrator.removeHandler("LOG");
		//LogAdministrator.removeHandler("RESULTS");
		//LogAdministrator.removeHandler("SUMMARY");
		
		//return similar;
		
//		results[0] = simil.getSimilarityGrade();
//		results[1] = simil.getAssertions();
		
		return simil;
	}
}
