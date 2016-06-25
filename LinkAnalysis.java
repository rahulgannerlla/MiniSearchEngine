package edu.asu.irs14;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.File;
import java.io.IOException;


public class LinkAnalysis {

	public static final String linksFile = "IntLinks.txt";
	public static final String citationsFile = "IntCitations.txt";
	public static int numDocs = 25054;

	private int[][] links;
	private int[][] citations;
	
	static MyLibrary ref;
	static IndexReader r;
	TermEnum t;
	HashMap<Integer, Double> similarityMap = new HashMap<Integer, Double>();
	HashMap<Integer, Double> pageRankMap = new HashMap<Integer, Double>();
	HashMap<Integer, Integer> tfMap = new HashMap<Integer, Integer>();
	HashMap<Integer, Double> weightsMap = new HashMap<Integer, Double>();
	HashMap<Integer, String> resultsMap = new HashMap<Integer, String>();
	double pageRank2[]=new double[25054];
	int docNumbers[]=new int[12];
	int tfidfCount;
	long startTime, endTime;
	double similarity, idf, tfidf;
	
	
	public LinkAnalysis()
	{
		try
		{
			
			System.out.println("In LA Constructor");
			ref = new MyLibrary();
			
			// Read in the links file
			links = new int[numDocs][];
			BufferedReader br = new BufferedReader(new FileReader("/Users/rgannerl/Documents/workspace/SearchEngineWebApp/src/IntLinks.txt"));
			String s = "";
			while ((s = br.readLine())!=null)
			{
				String[] words = s.split("->"); // split the src->dest1,dest2,dest3 string
				int src = Integer.parseInt(words[0]);
				if (words.length > 1 && words[1].length() > 0)
				{
					String[] dest = words[1].split(",");
					links[src] = new int[dest.length];
					for (int i=0; i<dest.length; i++)
					{
						links[src][i] = Integer.parseInt(dest[i]);
					}
				}
				else
				{
					links[src] = new int[0];
				}
			}
			br.close();
			
			// Read in the citations file
			citations = new int[numDocs][];
			br = new BufferedReader(new FileReader("/Users/rgannerl/Documents/workspace/SearchEngineWebApp/src/IntCitations.txt"));
			s = "";
			while ((s = br.readLine())!=null)
			{
				String[] words = s.split("->"); // split the src->dest1,dest2,dest3 string
				int src = Integer.parseInt(words[0]);
				if (words.length > 1 && words[1].length() > 0)
				{
					String[] dest = words[1].split(",");
					citations[src] = new int[dest.length];
					for (int i=0; i<dest.length; i++)
					{
						citations[src][i] = Integer.parseInt(dest[i]);
					}
				}
				else
				{
					citations[src] = new int[0];
				}

			}
			br.close();
			
			//computing tf  
			r = IndexReader.open(FSDirectory.open(new File("/Users/rgannerl/Documents/workspace/SearchEngineWebApp/index")));
			t = r.terms();
			//calculating highest term frequency of a word in the document.
			while (t.next()) {
				Term te = new Term("contents", t.term().text());
				TermDocs td = r.termDocs(te);

				while (td.next())
				{
					if(tfMap.containsKey(td.doc()))
					{
						if (tfMap.get(td.doc()) < td.freq())
							tfMap.put(td.doc(), td.freq());
					}
					else
						tfMap.put(td.doc(), td.freq());
				}				

			}

			t = r.terms();

			double m, k;

			//calculating tfidf factor for each term, adding the factor for all terms and storing them in the document number key
			while (t.next()) {
				Term te = new Term("contents", t.term().text());
				TermDocs td = r.termDocs(te);
				
				while (td.next()) {
					k = td.freq();
					m = tfMap.get(td.doc());
					idf=(float)  ((float) (Math.log((float)r.maxDoc() / r.docFreq(t.term()))) / Math.log(2));
					tfidf = (k/m) * idf;	
					if(weightsMap.containsKey(td.doc()))
						weightsMap.put(td.doc(), weightsMap.get(td.doc()) + tfidf* tfidf);
					else
						weightsMap.put(td.doc(), tfidf* tfidf);
				}
			}
			
			//computing page rank
			double pageRank[]=new double[25054];
			double c=0.9;
			long startTime=System.currentTimeMillis();
			for(int a=0; a<19; a++){
				for(int i=0; i <=25053; i++ ){
					double total=0.0;
					ArrayList<Integer> docIdList = new ArrayList<Integer>();
					for(int cit:getCitations(i))
						docIdList.add(cit);
					for(int j=0; j<=25053; j++){
						double elementValue;
						int columnCount=getLinks(j).length;
						if(docIdList.contains(j))
							elementValue=(1.0/columnCount)*c + ((1-c)/25054);
						else{
							if(columnCount==0)
								elementValue=1.0/25054;
							else
								elementValue=(1-c)/25054;
						}
						if(a==0)
							total=total+elementValue;
						else
							total=total+elementValue*pageRank[j];
					}
					pageRank2[i]=total;
				}
				for(int i=0; i <=25053; i++ )
					pageRank[i]=pageRank2[i];
			}
			
			double nFactor=0.0;
			for(int i=0; i <=25053; i++ )
				nFactor=nFactor+pageRank2[i]*pageRank2[i];
			
			
			for(int i=0; i <=25053; i++ )
				pageRank2[i]=pageRank2[i]/(Math.sqrt(nFactor));
			
			System.out.println("timestamp  pr " + ref.currentdatetimestamp(startTime));
			
		}
		catch(NumberFormatException e)
		{
			System.err.println("links file is corrupt: ");
			e.printStackTrace();			
		}
		catch(IOException e)
		{
			System.err.println("Failed to open links file: ");
			e.printStackTrace();
		}
	}
	
	public int[] getLinks(int docNumber)
	{
		return links[docNumber];
	}
	
	public int[] getCitations(int docNumber)
	{
		return citations[docNumber];
	}
	
	
	public HashMap<Integer, Double> tfIdf(String query, String optionSelected) throws CorruptIndexException, IOException {	
		System.out.println("In TFIDF");
		similarityMap.clear();
		pageRankMap.clear();
		double m, k;
		
		//vector similarity
		String[] terms = query.split("\\s");

			for (String word : terms) {
				Term term = new Term("contents", word);

				TermDocs tdocs = r.termDocs(term);
				while (tdocs.next()) {
					if (tfMap.get(tdocs.doc()) != 0) {	
						k = tdocs.freq();
						m = tfMap.get(tdocs.doc());
						
						idf =  Math.log((double)r.maxDoc()/r.docFreq(term))/(Math.log(2));
						tfidf = (k/m) * idf;
						similarity = (1 * tfidf) / (Math.sqrt(terms.length) * Math.sqrt(weightsMap.get(tdocs.doc())));
						similarityMap.put(tdocs.doc(), similarity);
						pageRankMap.put(tdocs.doc(), similarity);
					}

				}
			}
			

				similarityMap=(HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(similarityMap);
				
				tfidfCount=0;
				for (Entry<Integer, Double> entry : similarityMap.entrySet()){
					if(tfidfCount>10)
						break;
					//docNumbers[tfidfCount]=entry.getKey();;
					System.out.println("Documenttfidf - "+entry.getKey()+"||  value - "+entry.getValue());							
					tfidfCount++;
			}

				
			return similarityMap;
	}

	public static void main(String[] args) throws Exception
	{
	}
	
	
	public HashMap<Integer, Double> pageRankNew() throws CorruptIndexException, IOException{
		
		//pagerank+vector similarity
		startTime=System.currentTimeMillis();	
		double w=0.4;
		for (Entry<Integer, Double> entry : pageRankMap.entrySet()){
			pageRankMap.put(entry.getKey(), (pageRank2[entry.getKey()]*w)+((entry.getValue())*(1-w)) );
	}
		pageRankMap=(HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(pageRankMap);
		System.out.println("timestamp   " + ref.currentdatetimestamp(startTime));
		return pageRankMap;
	}
	
	public HashMap<Integer, Double> calculateAuthHubs(String authHubFlag){

		//root set
		tfidfCount=0;
		for (Entry<Integer, Double> entry : similarityMap.entrySet()){
			if(tfidfCount>10)
				break;
			docNumbers[tfidfCount]=entry.getKey();
			tfidfCount++;
	}
		
		startTime=System.currentTimeMillis( );
		
		HashMap<Integer, Double> indexHashMap=new  HashMap<Integer, Double>();
		ArrayList<Integer> docIdList = new ArrayList<Integer>();
		
		for(int i:docNumbers){
			System.out.println(i);
		}
		
		//base set
		for(int i:docNumbers){		
			if(!docIdList.contains(i))
			docIdList.add(i);
			
			for(int links:getLinks(i)){
				if(!docIdList.contains(links))
					docIdList.add(links);
			}
			
			for(int citations:getCitations(i)){
				if(!docIdList.contains(citations))
					docIdList.add(citations);
			}
				
		}
		Collections.sort(docIdList);
		System.out.println("base set" + ref.currentdatetimestamp(startTime));
		
		
		double adjMatrix[][]=new double[docIdList.size()][docIdList.size()];
		double authMatrix[][]=new double[docIdList.size()][1];
		double hubMatrix[][]=new double[docIdList.size()][1];
		
		//adj matrix
		for(int i:docNumbers){
			for(int links:getLinks(i))
				adjMatrix[docIdList.indexOf(i)][docIdList.indexOf(links)]=1.0;
			for(int cit:getCitations(i))
				adjMatrix[docIdList.indexOf(cit)][docIdList.indexOf(i)]=1.0;
				
		}
		
		double nFactor;
		
		//multiplication with initial auth matrix
		for(int i=0;i<docIdList.size();i++)
			for(int j=0;j<1;j++)
				 for (int k = 0; k < docIdList.size(); k++)
						 authMatrix[i][j]+= adjMatrix[k][i] * 1;
				
		
		//compute aauth hubs
		for(int a=0;a<20;a++){
			startTime=System.currentTimeMillis( );
			
			//auth and hub matrix
			if(a>0)
				for(int i=0;i<docIdList.size();i++)
					for(int j=0;j<1;j++)
						for (int k = 0; k < docIdList.size(); k++)
							 authMatrix[i][j]+= adjMatrix[k][i] * hubMatrix[k][j];
					 
			
			for(int i=0;i<docIdList.size();i++)
				for(int j=0;j<1;j++)
					 for (int k = 0; k < docIdList.size(); k++)
						 hubMatrix[i][j]+= adjMatrix[i][k] * authMatrix[k][j];
			
			
			//normalizing auth and hub matrix
			nFactor=0.0;
			for(int i=0;i<docIdList.size();i++)
				nFactor=nFactor+ authMatrix[i][0] * authMatrix[i][0];
			
			
			for(int i=0;i<docIdList.size();i++)
				 authMatrix[i][0]= authMatrix[i][0]/Math.sqrt(nFactor);
			
			
			nFactor=0.0;
			for(int i=0;i<docIdList.size();i++)
				nFactor=nFactor+ hubMatrix[i][0] * hubMatrix[i][0];
			
			
			for(int i=0;i<docIdList.size();i++)
				hubMatrix[i][0]= hubMatrix[i][0]/Math.sqrt(nFactor);
			
			
			
		}
			
		//sorting
		indexHashMap.clear();
		if(authHubFlag.equals("2")){
			for(int i=0;i<docIdList.size();i++)
				indexHashMap.put(docIdList.get(i), authMatrix[i][0]);
			indexHashMap=(HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(indexHashMap);
		}	
		System.out.println("\n\n\n");
		
		
		if(authHubFlag.equals("3")){
			for(int i=0;i<docIdList.size();i++)
				indexHashMap.put(docIdList.get(i), hubMatrix[i][0]);
			indexHashMap=(HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(indexHashMap);
		}
	
		return indexHashMap;
	}
	
}
