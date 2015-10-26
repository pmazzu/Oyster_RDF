package edu.ualr.oyster.utilities.acma.core;

import java.util.logging.Logger;
//import java.util.*;


import edu.ualr.oyster.utilities.acma.log.LogAdministrator;


public class ACMA {
	
	private final static Logger logger_results = Logger.getLogger("RESULTS");
	private final static Logger logger_summary = Logger.getLogger("SUMMARY");
	private final static Logger logger_log = Logger.getLogger("LOG");
	
	public Heuristics multivalued_attr_similarity_calc(String list_1, String list_2, String comparators, String aggrMode, String separator, int stage){
		
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
		
		String[] arguments = new String[8];
			
		arguments[0] = list_1; 
		arguments[1] = list_2;
//		arguments[2] = threshold;
		arguments[2] = threshold_noSimil;
		arguments[3] = comparators;
		arguments[4] = aggrMode;
		arguments[5] = heuristic_id;
		arguments[6] = separator;
		
		boolean similar;
		double[] results = new double[2];
			
		Heuristics simil = new Heuristics();
					
		Heuristics.heuristic(simil, arguments);
					
		if(stage == 1){
			logger_results.info(simil.getSimilarityGrade() + "\r\n"); // index comparison
		}else{
			logger_summary.info(simil.getSimilarityGrade() + "\r\n"); //inside cluster comparison
			//logger_summary.info("Lists compared: " + list_1 + " | " + list_2 + " | " + simil.getSimilarityGrade()+"\r\n");
		}
		
		//LogAdministrator.removeHandler("LOG");
		//LogAdministrator.removeHandler("RESULTS");
		//LogAdministrator.removeHandler("SUMMARY");
		
		//return similar;
		
		return simil;
	}
}
