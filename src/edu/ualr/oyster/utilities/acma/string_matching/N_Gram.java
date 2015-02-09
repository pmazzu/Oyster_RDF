/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universit�t Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package edu.ualr.oyster.utilities.acma.string_matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Character n-gram measure as described in
 * 
 * A. Barr�n-Cede�o, P. Rosso, E. Agirre, and G. Labaka: Plagiarism
 * Detection across Distant Language Pairs, in: Proceedings of COLING,
 * pages 37-45, 2010.
 * 
 * Originally, they used n-grams of length 3.
 */

public class N_Gram {
	
        protected boolean distanceMeasure = true;
        
        private static final String alphabet = "abcdefjhijklmnopqrstuvwxyz0123456789";
        
        int n;
        Set<String> ngrams;
        Map<String, Double> idf;
               
        public void CharacterNGramMeasure(int n, Map<String, Double> idfValues)
        {
                this.n = n;
                this.idf = idfValues;
        }
              
        public double getSimilarity(String text1, String text2)
        {
                Set<String> ngrams1 = getNGrams(text1);
                Set<String> ngrams2 = getNGrams(text2);
                
                ngrams = new HashSet<String>();
                ngrams.addAll(ngrams1);
                ngrams.addAll(ngrams2);         
                 
                Map<String, Double> tf1 = getTF(ngrams1);
                Map<String, Double> tf2 = getTF(ngrams2);
                
                // Build up the vectors
                double[] tfidf1 = new double[ngrams.size()];
                double[] tfidf2 = new double[ngrams.size()];
                
                int idx = 0;
                for (String ngram : ngrams)
                {
                        if (tf1.containsKey(ngram) && idf.containsKey(ngram))
                                tfidf1[idx] = tf1.get(ngram) * idf.get(ngram);
                        else
                                tfidf1[idx] = 0.0;
                        
                        if (tf2.containsKey(ngram) && idf.containsKey(ngram))
                                tfidf2[idx] = tf2.get(ngram) * idf.get(ngram);
                        else
                                tfidf2[idx] = 0.0;
                               
                        idx++;
                }
                
                // Compute Cosine
                double dotprod = 0.0;
                for (int i = 0; i < ngrams.size(); i++)
                        dotprod += tfidf1[i] * tfidf2[i];
                
                double mag1 = 0.0;
                for (int i = 0; i < ngrams.size(); i++)
                        mag1 += Math.pow(tfidf1[i], 2);
                
                double mag2 = 0.0;
                for (int i = 0; i < ngrams.size(); i++)
                        mag2 += Math.pow(tfidf2[i], 2);
                
                return dotprod / (Math.sqrt(mag1) * Math.sqrt(mag2));
        }
               
        private Map<String, Double> getTF(Set<String> ngrams)
        {
                Map<String, Double> tf = new HashMap<String, Double>();
                
                for (String ngram : ngrams)
                {
                        double count = 0;
                        if (tf.containsKey(ngram))
                                count = tf.get(ngram);
                        
                        count++;
                        tf.put(ngram, count);
                }
                
                return tf;
        }
        
        public Set<String> getNGrams(String text)
        {
                Set<String> ngrams = new HashSet<String>();
                
                text = encode(text);
                
                for (int i = 0; i < text.length() - (n - 1); i++)
                {
                        // Generate n-gram at index i                   
                        String ngram = text.substring(i, i + n).toLowerCase();
                        
                        // Add
                        ngrams.add(ngram);
                }
                
                return ngrams;
        }
        
        private String encode(String text)
        {
                StringBuilder sb = new StringBuilder();
                
                text = text.toLowerCase();
                char[] chars = text.toCharArray();
                
                for (char c : chars)
                {
                        if (alphabet.indexOf(c) > -1)
                                sb.append(c);
                }
                
                return sb.toString();
        }
        
        public String getName()
        {
        	return this.getClass().getSimpleName() + "_" + n + "grams";
	     }

}
