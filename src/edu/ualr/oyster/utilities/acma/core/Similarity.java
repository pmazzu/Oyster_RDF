package edu.ualr.oyster.utilities.acma.core;

import edu.ualr.oyster.utilities.acma.source.Sources;
//import java.util.Arrays;
import java.util.*;

//import java.util.logging.Logger;

public abstract class Similarity {
	
	//private final static Logger logger_log = Logger.getLogger("LOG");
	
	public Aggregation aggregation = new Aggregation();
	private double [][]similarities;
	private int rows, columns;
	private int assertions = 0, comparisons = 0 ,block = 0;
	private double similarityGrade = 0, threshold = 0, efficency = 0, partialSimilarity = 0, noSimilarityThreshold = 0, porcCommon = 0;
	//public Entity [] source;
	public ArrayList<Entity> source;
	public ArrayList<Entity> target;
	public String[] impl_comparators = {"rms", "nickname", "exact", "alias", "NYSII"};
	public String [] comparators;

	public Aggregation getAggregation(){
		return this.aggregation;
	}
	
	public void setComparators(String [] comparators){
		this.comparators = comparators;
	}
	public String[] getComparatorsList(){
		return comparators;
	}
	public String[] getImplementedComparatorsList(){
		return impl_comparators;
	}
	public int getComparatorIndex(String comparator){
		
		int index = -1;
		
		for (int i = 0;i<impl_comparators.length;i++){
			
			if(impl_comparators[i].equals(comparator)){
				index = i;
				break;
			}
			
		}
		
		return index;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public double getThreshold(){
		return threshold;
	}
	public double getPorcCommon(){
		return porcCommon;
	}
	public int getBlock(){
		return block;
	}	
	public void setNoSimilarityThreshold(double noSimilarityThreshold) {
		this.noSimilarityThreshold = noSimilarityThreshold;
	}
	public double getNoSimilarityThreshold(){
		return noSimilarityThreshold;
	}
	public void setSimilarityGrade(double similarityGrade) {
		this.similarityGrade = similarityGrade;
	}
	public double getSimilarityGrade(){
		return similarityGrade;
	}
	//public void setPorcCommon(double maxAuthors){
	public void setPorcCommon(){
		double maxAuthors = (double)this.rows;
		this.porcCommon = this.assertions / maxAuthors;
	}
	public void setPartialSimilarityGrade(double partialSimilarity) {
		this.partialSimilarity = partialSimilarity;
	}
	public double getPartialSimilarityGrade(){
		return partialSimilarity;
	}
	public void updateSimMatrix(int i, int j, double similarityValue) {
		this.similarities[i][j] = similarityValue;
	}
	public double getSimMatrixValue(int i, int j) {
		return similarities[i][j];
	}
	public double[][] getSimMatrix() {
		return similarities;
	}
	public void setSimiMatrix(int i, int j){
		this.rows = i;
		this.columns = j;
		this.similarities = new double [i][j];
	}
//	public void setSource(Entity[] source){
//		this.source = source;
//	}
	public void setSource(ArrayList<Entity> source){
		this.source = source;
	}
	public void setTarget(ArrayList<Entity> target){
		this.target = target;
	}
	public ArrayList<Entity> getTarget(){
		return this.target;
	}
//	public Entity[] getSource(){
//		return this.source;
//	}
	public ArrayList<Entity> getSource(){
		return this.source;
	}	
	public int getRows(){
		return rows;
	}
	public int getColumns(){
		return columns;
	}
	public void setComparisons(int comparison) {
		this.comparisons = comparison;
	}
	public void increaseComparisons() {
		this.comparisons++ ;
	}
	public int getComparisons() {
		return comparisons;
	}
	public void setAssertions(int assertion) {
		this.assertions = assertion;
	}
	public int getAssertions() {
		return assertions;
	}
	public void increaseAssertions() {
		this.assertions++ ;
	}
	public void setEfficency(int efficency) {
		this.efficency = efficency;
	}
	public void setBlock() {
		if (this.porcCommon == 0){ //none of the authors are equal
			this.block = 1;
		} else if (this.porcCommon == 1){//all the authors are equal
			this.block = 3;
		} else {
			this.block = 2;//some authors are equal
		}
	}	
	public double getEfficency() {
		return efficency;
	}
	
	public void calculate_partial_similarity_grade(int row, String mode){
		
		double highest = 0;
		double verticalSum = 0;
		double horizontalSum = 0;
		int i = 0;
		int j = 0;

		if(mode.equals("STRICT")){
			
			// find the highest values of each row						
			for (i = 0;i <= row; i++ ){
				for (j = 0; j < this.getColumns();j++){
					if (this.similarities[i][j] > highest){
						highest = this.similarities[i][j];
					}
				}
				verticalSum = verticalSum + highest;
				highest = 0;
			}
			
			this.partialSimilarity = verticalSum / (row+1);
			
		}else if(mode.equals("SUBSET")){
			
			// find the highest values of each column
			highest = 0;
			for (j = 0;j < this.getColumns(); j++ ){
				for (i = 0; i <= row;i++){
					if (this.getSimMatrixValue(i,j) > highest){
						highest = this.getSimMatrixValue(i,j);
					}
				}
				horizontalSum = horizontalSum + highest;
				highest = 0;
			}
			
			this.partialSimilarity = horizontalSum / this.getColumns();
		}
		
		
		
//		// find the highest values of each row						
//		for (i = 0;i <= row; i++ ){
//			for (j = 0; j < this.getColumns();j++){
//				if (this.similarities[i][j] > highest){
//					highest = this.similarities[i][j];
//				}
//			}
//			verticalSum = verticalSum + highest;
//			highest = 0;
//		}
//		
//		this.partialSimilarity = verticalSum / (row+1);
		
		//logger_log.info("partialSimilarity: " + this.partialSimilarity);
		
	}
	
	public static Similarity initialization_siip(String[] args){
		
		String authorList1 = args[0];
		String authorList2 = args[1];
					
		double threshold = Double.parseDouble(args[2]);
		double threshold_noSimil = Double.parseDouble(args[3]);
		
		Entity author2;
		Entity author1;
		String[] author_1_temp;
		String[] author_2_temp;
		ArrayList<Entity> author_1;
		//Entity [] author_2;
		ArrayList<Entity> author_2;
		
		int rows = 0, columns = 0;
		
		Sources source1 = new Sources();		
		
		author_1_temp = source1.process_string_semicolon(authorList1);
		author_2_temp = source1.process_string_semicolon(authorList2);
		
		int length_temp1 = author_1_temp.length-1;
		int length_temp2 = author_2_temp.length-1;
		
		author_1 = new ArrayList<Entity>(length_temp1);
		//author_2 = new Entity[length_temp2];
		author_2 = new ArrayList<Entity>(length_temp2);
		//Create a new object for each author from the source 1, and load it in an array					
		for(int i=0; i < length_temp1; i++){
			author1 = new Entity();
			author1.setPubId(author_1_temp[0]);
			author1.setRealName(author_1_temp[i+1]);
			author_1.add(author1);
		}
		//Create a new object for each author from the source 2, and load it in an array
		for(int i=0; i < length_temp2; i++){
			author2 = new Entity();
			author2.setPubId(author_2_temp[0]);
			author2.setRealName(author_2_temp[i+1]);
			//author_2[i] = author2;
			author_2.add(author2);
		}
		
		Similarity simil = new Heuristics();
		
		//the biggest array's length will be used as quantity of ROWS 
		//if(author_1.size() < author_2.length){
		if(author_1.size() < author_2.size()){
			//rows = author_2.length;
			rows = author_2.size();
			columns = author_1.size();
			simil.setSource(author_2);
			simil.setTarget(author_1);
		} else {
			rows = author_1.size();
			//columns = author_2.length;
			columns = author_2.size();
			simil.setSource(author_1);
			simil.setTarget(author_2);
		}
						
		// Initialize the similarity attributes object
			
		simil.setSimiMatrix(rows, columns);
		simil.setAssertions(0);
		simil.setComparisons(0);
		simil.setEfficency(0);
		simil.setSimilarityGrade(0);
		simil.setThreshold(threshold); // Set the threshold to determine if the authors are similar or not 0.9
		simil.setNoSimilarityThreshold(threshold_noSimil); // Set the threshold to determine when to stop comparing 0.8
		
		return simil;
	}
	
	
	public void initialization_oyster(String[] args, int splitSeparator){
		
		String authorList1 = args[0];
		String authorList2 = args[1];
					
		double threshold = Double.parseDouble(args[2]);
		double threshold_noSimil = Double.parseDouble(args[3]);
		
		String comparators = args[4];
		
		String aggrMode = args[5];
		
//		Entity author2;
//		Entity author1;
//		String[] author_1_temp;
//		String[] author_2_temp;
		String[] comparators_temp;
//		Entity [] author_1;
//		Entity [] author_2;
		ArrayList<Entity> author_1 = null;
		ArrayList<Entity> author_2 = null;
		
		int rows = 0, columns = 0;
		
		Sources source1 = new Sources();		
		
		switch (splitSeparator){
			case 1: //semicolon
				author_1 = source1.process_authors_semicolon(authorList1);
				author_2 = source1.process_authors_semicolon(authorList2);
				break;
			case 2://space
				author_1 = source1.process_string_space(authorList1);
				author_2 = source1.process_string_space(authorList2);
				break;
		}

		comparators_temp = source1.process_string_semicolon(comparators);
			
////		author_1 = new Entity[length_temp1];
////		author_2 = new Entity[length_temp2];
//		
//		author_1 = new ArrayList<Entity>(length_temp1);
//		author_2 = new ArrayList<Entity>(length_temp2);
//		
//		//Create a new object for each author from the source 1, and load it in an array					
//		for(int i=0; i < length_temp1; i++){
//			author1 = new Entity();
//			author1.setRealName(author_1_temp[i]);
////			author_1[i] = author1;
//			author_1.add(author1);
//		}
//		//Create a new object for each author from the source 2, and load it in an array
//		for(int i=0; i < length_temp2; i++){
//			author2 = new Entity();
//			author2.setRealName(author_2_temp[i]);
////			author_2[i] = author2;
//			author_2.add(author2);
//		}
		
		//Heuristics simil = new Heuristics();
		
		//the biggest array's length will be used as quantity of ROWS 
//		if(author_1.length < author_2.length){
//			rows = author_2.length;
//			columns = author_1.length;
		if(author_1.size() < author_2.size()){
			rows = author_2.size();
			columns = author_1.size();
			this.setSource(author_2);
			this.setTarget(author_1);
		} else {
//			rows = author_1.length;
//			columns = author_2.length;
			rows = author_1.size();
			columns = author_2.size();
			this.setSource(author_1);
			this.setTarget(author_2);
		}
						
		// Initialize the similarity attributes object
		
		this.aggregation.setMode(aggrMode);
		this.setComparators(comparators_temp);			
		this.setSimiMatrix(rows, columns);
		this.setAssertions(0);
		this.setComparisons(0);
		this.setEfficency(0);
		this.setSimilarityGrade(0);
		this.setThreshold(threshold); // Set the threshold to determine if the authors are similar or not 0.9
		this.setNoSimilarityThreshold(threshold_noSimil); // Set the threshold to determine when to stop comparing 0.8
		
		//return simil;
	}
	
//	public void scan(){
//		
//		Entity next_source;
//		Entity next_target;
//		
////		for(int i=0;i<this.source.length;i++){
////			this.source[i].formatRealName();
////		}
//		
//		Iterator<Entity> itr_src = this.source.iterator();
//		
//		while(itr_src.hasNext()){
//			next_source = itr_src.next();
//			next_source.formatRealName();
//		}		
//		
//		Iterator<Entity> itr_target = this.target.iterator();
//		
//		while(itr_target.hasNext()){
//			next_target = itr_target.next();
//			next_target.formatRealName();
//		}
//	}

	public void initialization_h8(String[] args, ArrayList<String> al1, ArrayList<String> al2){
		
		ArrayList<Entity> author_1;
		ArrayList<Entity> author_2;
		int rows = 0, columns = 0;
		
		double threshold = Double.parseDouble(args[2]);
		double threshold_noSimil = Double.parseDouble(args[3]);
		String comparators = args[4];
		String aggrMode = args[5];
		String[] comparators_temp;

		Sources source1 = new Sources();		
		
		author_1 = source1.process_keywords(al1);
		author_2 = source1.process_keywords(al2);
		comparators_temp = source1.process_string_semicolon(comparators);
			
		if(author_1.size() < author_2.size()){
			rows = author_2.size();
			columns = author_1.size();
			this.setSource(author_2);
			this.setTarget(author_1);
		} else {
			rows = author_1.size();
			columns = author_2.size();
			this.setSource(author_1);
			this.setTarget(author_2);
		}
						
		// Initialize the similarity attributes object
		
		this.aggregation.setMode(aggrMode);
		this.setComparators(comparators_temp);			
		this.setSimiMatrix(rows, columns);
		this.setAssertions(0);
		this.setComparisons(0);
		this.setEfficency(0);
		this.setSimilarityGrade(0);
		this.setThreshold(threshold); // Set the threshold to determine if the authors are similar or not 0.9
		this.setNoSimilarityThreshold(threshold_noSimil); // Set the threshold to determine when to stop comparing 0.8
	}
}


