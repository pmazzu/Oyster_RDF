package edu.ualr.oyster.utilities.acma.string_matching;

//import org.junit.Test;

public class LevenshteinDistance {

	public double Levenshtein_Distance (String s0, String s1) {
		
		int len0 = s0.length()+1;
		int len1 = s1.length()+1;
	 
		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];
	 
		// initial cost of skipping prefix in String s0
		for(int i=0;i<len0;i++) cost[i]=i;
	 
		// dynamically computing the array of distances
	 
		// transformation cost for each letter in s1
		for(int j=1;j<len1;j++) {
	 
			// initial cost of skipping prefix in String s1
			newcost[0]=j-1;
	 
			// transformation cost for each letter in s0
			for(int i=1;i<len0;i++) {
	 
				// matching current letters in both strings
				int match = (s0.charAt(i-1)==s1.charAt(j-1))?0:1;
	 
				// computing cost for each transformation
				int cost_replace = cost[i-1]+match;
				int cost_insert  = cost[i]+1;
				int cost_delete  = newcost[i-1]+1;
	 
				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete),cost_replace );
			}
	 
			// swap cost/newcost arrays
			int[] swap=cost; cost=newcost; newcost=swap;
		}
	 
		// the distance is the cost for transforming all letters in both strings
		return cost[len0-1];
	}

	public static void main(String[] args) {
		
		double distance = 0;
		double normalized_lev = 0;
		String author1 = "Brena-Pinero, R. F.";
		String author2 = "Brena, R.F.";
		
		LevenshteinDistance lev = new LevenshteinDistance();
		
		distance = lev.Levenshtein_Distance(author1, author2);
		
		System.out.println("distance: " + distance);
		
		System.out.println("division: " + distance / Math.max(author1.length(), author2.length()));
		
		normalized_lev = 1 - (distance / Math.max(author1.length(), author2.length()));
		
		System.out.println("normalized_lev: " + normalized_lev);
			
	}

	
}
