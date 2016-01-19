package edu.ualr.oyster.utilities.acma.core;

import java.util.logging.Logger;
//import java.util.*;


//import edu.ualr.oyster.utilities.acma.log.LogAdministrator;


public class ACMA {
	
	private final static Logger logger_results = Logger.getLogger("RESULTS");
	
	//private final static Logger logger_summary = Logger.getLogger("SUMMARY");
	//private final static Logger logger_log = Logger.getLogger("LOG");
	
	public Heuristics multivalued_attr_similarity_calc(String list_1, String list_2, String comparators, String aggrMode, String separator, int stage, String threshold){
		
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
		
		//String threshold_noSimil = "0.8";
		String heuristic_id = "4";
		
		String[] arguments = new String[8];
		
		String[] list_1_splitted=list_1.split("#");
		String[] list_2_splitted=list_2.split("#");
//		arguments[0] = list_1; 
//		arguments[1] = list_2;
		String list_1_id = list_1_splitted[0];
		String list_2_id = list_2_splitted[0];
		arguments[0] = list_1_splitted[1]; 
		arguments[1] = list_2_splitted[1];
		arguments[2] = threshold;
//		arguments[2] = threshold_noSimil;
		arguments[3] = comparators;
		arguments[4] = aggrMode;
		arguments[5] = heuristic_id;
		arguments[6] = separator;

//		boolean similar;
//		double[] results = new double[2];
			
		Heuristics simil = new Heuristics();
					
		Heuristics.heuristic(simil, arguments);
					
		if(stage == 2){
			logger_results.info(list_1_id + "|" + list_2_id + "|" +simil.getSimilarityGrade() + "|" + simil.getGroup() + "|" + simil.getComparisons() + "|" + simil.getExecutionTimeNs() + "|" + simil.getExecutionTimeMs() + "|" + simil.getExecutionTimeS() + "\r\n"); //inside cluster comparison
		}
//		else{
//			logger_summary.info(simil.getSimilarityGrade() + "\r\n"); // index comparison
//			//logger_summary.info("Lists compared: " + list_1 + " | " + list_2 + " | " + simil.getSimilarityGrade()+"\r\n");
//		}
		
		//LogAdministrator.removeHandler("LOG");
		//LogAdministrator.removeHandler("RESULTS");
		//LogAdministrator.removeHandler("SUMMARY");
		
		//return similar;
				
		return simil;
	}
}
