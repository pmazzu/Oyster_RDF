package edu.ualr.oyster.utilities.acma.log;

import java.io.IOException;
import java.util.logging.*;

public class LogAdministrator {
	
	private static FileHandler fh_log = null;
	private static FileHandler fh_results = null;
	private static FileHandler fh_summary = null;
	
	private static String mode = "DEBUG"; //DEBUG RESULT PERFORMANCE
	
	//private static String outputPath = "C:/Users/Pablo/Dropbox/Trabajo/SIIP/Workspaces/MyEclipse 9/Aggregation_operator/src/test_files/Summary_log.txt";
	private static String resultsPath = "C:/Users/Pablo/Desktop/logs/results_log.txt";
	private static String logFile = "C:/Users/Pablo/Dropbox/Trabajo/SIIP/Workspaces/MyEclipse 9/Aggregation_operator/src/test_files/log.txt";
	private static String outputPath = "C:/Users/Pablo/Desktop/logs/Summary_log.txt";
	
	public static String getMode(){
		return mode;
	}
	
	public static void init_log(){
		 try {
			 fh_log=new FileHandler(logFile, true);
		 } 
		 catch (IOException e) {
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("LOG");
		 fh_log.setFormatter(new SimpleFormatter(){
				@Override
				public String format(LogRecord record){
					if(record.getLevel() == Level.INFO) {
	                    return "[INFO]  | " + record.getMessage() + " | " + record.getSourceClassName() +"\r\n";
	                  } else if(record.getLevel() == Level.WARNING) {
	                    return "[WARN]  | " + record.getMessage() + " | " + record.getSourceClassName() + "\r\n";
	                  } else if(record.getLevel() == Level.SEVERE) {
	                    return "[ERROR] | " + record.getMessage() + " | " + record.getSourceClassName() +"\r\n";
	                  } else {
	                    return "[OTHER] | " + record.getMessage() + " | " + record.getSourceClassName() +"\r\n";
	                }
					
				}
		 });
		 l.addHandler(fh_log);
		 l.setLevel(Level.CONFIG);
	}

	public static void init_results(){
		 try {
			 fh_results=new FileHandler(resultsPath, true);
		 } 
		 catch (IOException e) {
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("RESULTS");
		 fh_results.setFormatter(new SimpleFormatter() {
				@Override
				public String format(LogRecord record){
					String message = record.getMessage() + "\r\n";	
					return message;
				}
		 });
		 l.addHandler(fh_results);
		 l.setLevel(Level.CONFIG);
	}
	
	public static void removeHandler(String handler){
		Logger l = Logger.getLogger(handler); 
		
		if (handler == "RESULTS"){
			l.removeHandler(fh_results);
			fh_results.close();
		}else if (handler == "LOG"){
			l.removeHandler(fh_log);
			fh_log.close();
		}else if (handler == "SUMMARY"){
			l.removeHandler(fh_summary);
			fh_summary.close();
		}
	}

	public static void init_summary(){
		 try {
			 fh_summary=new FileHandler(outputPath, true);
		 } 
		 catch (IOException e) {
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("SUMMARY");
		 fh_summary.setFormatter(new SimpleFormatter(){
				@Override
				public String format(LogRecord record){
					String message = record.getMessage() + "\r\n";	
					return message;
				}
		 });
		 l.addHandler(fh_summary);
		 l.setLevel(Level.CONFIG);
	}	
}
