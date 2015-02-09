package edu.ualr.oyster.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.core.Functions;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.ualr.oyster.ErrorFormatter;
import edu.ualr.oyster.core.ReferenceItem;
import edu.ualr.oyster.data.ClusterRecordSet;
import edu.ualr.oyster.data.CoDoSAOIR;
import edu.ualr.oyster.data.OysterIdentityRecord;
import edu.ualr.oyster.data.OysterIdentityRecordMap;
import edu.ualr.oyster.data.RecordTypes;

/**
 * This class is used to read a RDF STORE.
 * @author Pablo Mazzucchi
 */
public class OysterRDFReader extends OysterSourceReader {
	
	/** The name of the class that categorizes the data */
	private String className = null;
	
	/** The address of the remote endpoint */
	private String endPoint = null;
	
	 /** The custom SPARQL string to run */
	private String overRideSPARQL = null;
	
	 /** The path of the prefix to be loaded for complete the sparql query */
	private String prefix = null;
	
	/** */
	private ResultSet rs = null;

	/**
     * Creates a new instance of <code>OysterRDFReader</code>.
     */
	public OysterRDFReader(Logger log) {
		super(log);
	}
	
	/**
     * Creates a new instance of <code>OysterRDFReader</code>.
     * @param className the name of the class that categorizes the data.
     * @param endPoint the address of the remote endpoint.
     * @param prefix the path of the prefix to be loaded for complete the sparql query. 
     * if required otherwise refereceItem must be uri´s  
     * @param ri the ArrayList of <code>ReferenceItems</code> that will be used to
     * store the parsed data.
     */	
	public OysterRDFReader (String className, String endPoint, String prefix, ArrayList<ReferenceItem> ri, Logger log){
		super(log);
		
		this.className = className;
		this.endPoint = endPoint;
		this.prefix = prefix;
		
		setReferenceItems(ri);
	}
	
	/**
     * Returns the <code>ClassName</code> for this <code>OysterRDFReader</code>.
     * @return the <code>ClassName</code>.
     */	
	public String getClassName() {
		return className;
	}
	
	/**
     * Sets the <code>ClassName</code> for this <code>OysterRDFReader</code>.
     * @param className the <code>ClassName</code> to be set.
     */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
     * Returns the <code>EndPoint</code> for this <code>OysterRDFReader</code>.
     * @return the <code>EndPoint</code>.
     */	
	public String getEndPoint() {
		return endPoint;
	}
	
	/**
     * Sets the <code>EndPoint</code> for this <code>OysterRDFReader</code>.
     * @param endPoint the <code>EndPoint</code> to be set.
     */
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
	/**
     * Returns the <code>OverRideSPARQL</code> for this <code>OysterRDFReader</code>.
     * @return the <code>OverRideSPARQL</code>.
     */	
	public String getOverRideSPARQL() {
		return overRideSPARQL;
	}

	/**
     * Sets the <code>OverRideSPARQL</code> for this <code>OysterRDFReader</code>.
     * @param overRideSPARQL the <code>OverRideSPARQL</code> to be set.
     */
	public void setOverRideSPARQL(String overRideSPARQL) {
		this.overRideSPARQL = overRideSPARQL;
	}
	
	/**
     * Returns the <code>Prefix</code> for this <code>OysterRDFReader</code>.
     * @return the <code>Prefix</code>.
     */	
	public String getPrefix (){
		return prefix;
	}
	
	/**
     * Sets the <code>Prefix</code> for this <code>OysterRDFReader</code>.
     * @param prefix the <code>Prefix</code> to be set.
     */
	public void setPrefix (String prefix){
		this.prefix = prefix;
	}
	
	/**
     * This method creates a sparql statement for the this endPoint based on
     * the set reference items.
     * If the user specify a prefix then prefix must be loaded and added to the
     * sparql query string.
     * Otherwise assume that reference items are uri´s
     * @return an SPARQL query statement for the current reference items.
     */
	private String createSPARQLConnectionString (){
		
		String prefixData="";
		
		if (!this.prefix.equals("")){
			BufferedReader prefixIn =null;
			
			try {
				prefixIn = new BufferedReader(new InputStreamReader(new FileInputStream(this.prefix), "UTF8"));
				
				String line="";
				while ((line=prefixIn.readLine()) != null){
					prefixData +=line+"\n";
				}
				
			} catch (FileNotFoundException ex) {
	            Logger.getLogger(OysterDelimitedReader.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
	        } catch (UnsupportedEncodingException ex) {
	            Logger.getLogger(OysterDelimitedReader.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
	        } catch (IOException ex) {
	        	 Logger.getLogger(OysterDelimitedReader.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
			}			
			
		}
				
		String sparql = "";
		
		if (getItemCount()!=0){
			String select ="";
			String where = "";
			
			if (prefixData.equals("")){

				select = "SELECT";
				where = " \nWHERE {?class a <" + this.className + ">.\n";

				for (int i=0; i<getItemCount(); i++){
					ReferenceItem item = referenceItems.get(i);

					if (item.getAttribute().contains("@")){
						select += " ?" + item.getAttribute().replace("@", "");
						where += "OPTIONAL {?class <"+ item.getName() + "> ?" + item.getAttribute().replace("@", "") + "}.\n";
					}else {
						select += " ?" + item.getAttribute();
						where += "OPTIONAL {?class <"+ item.getName() + "> ?" + item.getAttribute() + "}.\n";
					}				

				}

			}else {

				select = "SELECT";
				where = " \nWHERE {?" + this.className.replace(":", "") + " a " + this.className + ".\n";

				for (int i = 0; i < getItemCount(); i++) {
					ReferenceItem item = referenceItems.get(i);
					
					if (item.getAttribute().contains("@")){
						select += " ?" + item.getAttribute().replace("@", "");
						where += "OPTIONAL {?" + this.className.replace(":", "") + " " + item.getName() + " ?" + item.getAttribute().replace("@", "") + "}.\n";
					}else{
						select += " ?" + item.getAttribute();
						where += "OPTIONAL {?" + this.className.replace(":", "") + " " + item.getName() + " ?" + item.getAttribute() + "}.\n";
					}
					
				}

			}
									
			sparql = prefixData + select + where + "}";
			
		}
		
		return sparql;
	}
	
	/**
     * This method initializes the <code>ResultSet</code>
     * primes the data reader.
     */
	@Override
	public void open() {
		
		String sparql;
		
		if (overRideSPARQL == null){
			sparql = createSPARQLConnectionString();
		}else {
			sparql = overRideSPARQL;
		}
		
		StringBuilder sb = new StringBuilder(250);
        sb.append("SPARQL: ").append(sparql).append(System.getProperty("line.separator"));
        logger.severe(sb.toString());
        System.out.println(sb.toString());
        
        try{
        	
        	rs = Functions.getResultSetService(endPoint, sparql);        	
        	
        }catch (Exception ex){
            System.err.println(sparql);
            Logger.getLogger(OysterDatabaseReader.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
        }

	}
	
	 /**
     * This method not apply.
     */
	@Override
	public void close() {
		
	}

	/**
     * getNextReference() is an external method that reads a reference
     * from the Reference Source, parses the reference into individual items and
     * store the items in the string array itemList
     * @return the number of items it found, zero at EOF
     */
	@Override
	public int getNextReference() {				
		int count = 0;
		
		try{
			
			for (int i = 0; i < getItemCount(); i++) {
                ReferenceItem item = referenceItems.get(i);
                item.setData("");
            }
			
			 if (rs.hasNext()) {		 
				 QuerySolution row = rs.nextSolution();				 
				 for (int i = 0; i < getItemCount(); i++) {				 
					 ReferenceItem item = referenceItems.get(i);					 
					 String token = null;
					 
					 RDFNode node = row.get(item.getAttribute().replace("@", ""));	
					 
					 if (node!=null){
						 token = node.toString();
					 }				  
					 
					 item.setData(token);
					 
					 if (item.getAttribute().equals("@RefID")){ 
                         if (item.getData() != null && !item.getData().equals("")){
                         item.setData(source + "." + item.getData());
                     } else {
                             item.setData(source + ".D" + this.recordCount);
                         }
                     }
					 
					// ignore any records that have a blank or null id
                     if (item.getAttribute().equals("@RefID") && (item.getData() == null || item.getData().equals(""))){
                         count = 0;
                         break;
                     }
					
					 referenceItems.set(i, item);	                    
					 count++;					 
				 }				 
				 
				 OysterIdentityRecord oysterIdentityRecord;

				 switch (recordType) {
				 case RecordTypes.CODOSA:
					 oysterIdentityRecord = new CoDoSAOIR();
					 break;
				 case RecordTypes.MAP:
					 oysterIdentityRecord = new OysterIdentityRecordMap();
					 break;
				 default:
					 oysterIdentityRecord = new OysterIdentityRecordMap();
				 }


				 oysterIdentityRecord.convertToOIR(referenceItems);
				 if (clusterRecord == null) {
					 clusterRecord = new ClusterRecordSet(recordType);
				 }

				 clusterRecord.clear();
				 clusterRecord.insertRecord(oysterIdentityRecord);

				 if (recordCount % getCountPoint() == 0) {
					 System.out.println(recordCount + "...");
				 }

				 recordCount++;
				 
			 }
			
		}catch (Exception ex) {
            Logger.getLogger(OysterDatabaseReader.class.getName()).log(Level.SEVERE, ErrorFormatter.format(ex), ex);
            Logger.getLogger(OysterDatabaseReader.class.getName()).log(Level.SEVERE, clusterRecord.toString());
        }
		return count;
	}

	@Override
	public String getRecordImage() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
     * Returns a string representation of the current record with 0x07 field delimiter
     * @return the current record as a 0x07 delimited string
     */
    public String getRecordImageAsDelimitedString () {
        StringBuilder result = new StringBuilder(250);
        if (getItemCount() > 0){
            for (int i = 0; i < getItemCount(); i++){
                ReferenceItem item = referenceItems.get(i);
                result.append(item.getData()).append("\u0007");
            }
            
            result = result.delete(result.length()-1, result.length());
        }
        
        return result.toString();
    }
    
    /**
     * Returns the keyword value pairs of the current record. Each pair is delimited
     * by the bell (0x07) character
     * @return the current record as attribute/value pairs
     */
    public String getRecordImageAsKeyValuePair() {
        StringBuilder result = new StringBuilder(250);
        if (getItemCount() > 0){
            for (int i = 0; i < getItemCount(); i++){
                ReferenceItem item = referenceItems.get(i);
                result.append(item.getName()).append("=").append(item.getData()).append("\u0007");
            }
            
            result = result.delete(result.length()-1, result.length());
        }
        
        return result.toString();
    }
	

	

		

}
