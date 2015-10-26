package edu.ualr.oyster.utilities.acma.core;

import java.util.ArrayList;

public class ScriptTestSynonyms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Heuristics simil = new Heuristics();
		Entity entity_1 = new Entity();
		Entity entity_2 = new Entity();
		Entity entity_3 = new Entity();
		
		entity_1.setRealName("entity resolution");
		entity_1.setPosition(1);
		entity_2.setRealName("record linkage");
		entity_2.setPosition(2);
		entity_3.setRealName("similarity Matching");
		entity_3.setPosition(3);
				
		ArrayList<Entity> source = new ArrayList<Entity>();
		
		source.add(entity_1);
		source.add(entity_2);
		source.add(entity_3);
		
		simil.setSource(source);
		
		Entity entity_4 = new Entity();
		Entity entity_5 = new Entity();
		
		entity_4.setRealName("duplicate detection");
		entity_4.setPosition(1);
		entity_5.setRealName("object matching");
		entity_5.setPosition(2);
		
		ArrayList<Entity> target = new ArrayList<Entity>();	
		
		target.add(entity_4);
		target.add(entity_5);
		
		simil.setTarget(target);		
		
		Synonym syn = new Synonym();
		
		//syn.getSynonyms(simil);
	}
}
