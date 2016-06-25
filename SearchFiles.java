package edu.asu.irs14;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SearchFiles {

	static MyLibrary ref;
	static IndexReader r;
	TermEnum t;
	HashMap<Integer, Double> similarityMap;
	public HashMap<Integer, Integer> map;
	int count;
	long startTime, endTime;
	HashMap<Integer, Double> weightsMap;
	
	public SearchFiles(){
		
		try{
		ref = new MyLibrary();
		r = IndexReader.open(FSDirectory.open(new File("index")));
		map = new HashMap<Integer, Integer>();
		t=r.terms();
		count=1;
		
		similarityMap = new HashMap<Integer, Double>();
		weightsMap= new HashMap<Integer, Double>();
		
		double idf, tfidf;
			
		//calculating highest term frequency of a word in the document.
		while (t.next()) {
			Term te = new Term("contents", t.term().text());
			TermDocs td = r.termDocs(te);

			while (td.next())
			{
				if(map.containsKey(td.doc()))
				{
					if (map.get(td.doc()) < td.freq())
						map.put(td.doc(), td.freq());
				}
				else
					map.put(td.doc(), td.freq());
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
				m = map.get(td.doc());
				idf=(float)  ((float) (Math.log((float)r.maxDoc() / r.docFreq(t.term()))) / Math.log(2));
				tfidf = (k/m) * idf;	
				if(weightsMap.containsKey(td.doc()))
					weightsMap.put(td.doc(), weightsMap.get(td.doc()) + tfidf* tfidf);
				else
					weightsMap.put(td.doc(), tfidf* tfidf);
			}
		}
		
		
		}
		catch(Exception e){
			
		}
	}

	public static void main(String[] args) throws Exception {
//		ref = new MyLibrary();
//		r = IndexReader.open(FSDirectory.open(new File("index")));
//		
//		// initializing all the map values to zero for calculating the norm factor in tf and the highest frequency of a word in tfidf
//		SearchFiles sf = new SearchFiles();
//		sf.map = new HashMap<Integer, Integer>();
//	
//		sf.t = r.terms();
//		sf.count=1;
//		
//		//remove comments for executing one among the ranking concepts and comment the other.
//		//sf.tf();
//		sf.tfIdf();
	}
	
	public void tfIdf( String query) throws CorruptIndexException, IOException {
		double idf, tfidf,m, k, similarity;
		
		//System.out.println("timestamp   " + ref.currentdatetimestamp());

		//calculating similarity for the given query and insert into similarityMap
		Scanner sc = new Scanner(System.in);
		String str = "\\s*\\s*";
		//System.out.print("query> ");
				

		//while (!(str = sc.nextLine()).equals("quit")) {
			String[] terms = query.split("\\s");

			for (String word : terms) {
				Term term = new Term("contents", word);

				TermDocs tdocs = r.termDocs(term);
				while (tdocs.next()) {
					if (map.get(tdocs.doc()) != 0) {	
						k = tdocs.freq();
						m = map.get(tdocs.doc());
						idf = ( (Math.log(r.maxDoc() / r.docFreq(term))) / Math.log(2));
						tfidf = (k/m) * idf;
						similarity = (1 * tfidf) / (Math.sqrt(terms.length) * Math.sqrt(weightsMap.get(tdocs.doc())));
						similarityMap.put(tdocs.doc(), similarity);
					}

				}
			}
			
			//function for sorting similarityMap
			similarityMap = (HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(similarityMap);
			
			System.out.println("\n\n\nDocument Numbers");
			
			for (Entry<Integer, Double> entry : similarityMap.entrySet()){
				if(count>15)
					break;
				System.out.println(entry.getKey());								
				count++;
			}
		
			similarityMap.clear();
			count=1;			
			//System.out.print("query> ");
		//}
		sc.close();
	}



	public void tf() throws CorruptIndexException, IOException {
		
		//square of the frequency of each word in the document for calculating the magnitude
		while (t.next()) {

				Term te = new Term("contents", t.term().text());
				TermDocs td = r.termDocs(te);
			
					while (td.next())
					{
						//if(td.doc()<25000){
							if(map.containsKey(td.doc()))
								map.put(td.doc(), map.get(td.doc()) + td.freq() * td.freq());
							else
								map.put(td.doc(), td.freq() * td.freq());
						//}
				
					}

		}
		
		
		double similarity;

		//System.out.println("timestamp   " + ref.currentdatetimestamp());
		
		//calculating similarity for the given query and insert into similarityMap
		Scanner sc = new Scanner(System.in);
		String str = "\\s*\\s*";
		System.out.print("query> ");
		while (!(str = sc.nextLine()).equals("quit")) {
			String[] terms = str.split("\\s");
			for (String word : terms) {
				
				Term term = new Term("contents", word);
				TermDocs tdocs = r.termDocs(term);
				while (tdocs.next()) {
					//if(tdocs.doc()<25000){
						if (map.get(tdocs.doc()) != 0) {					
							similarity = (1 * tdocs.freq()) / (Math.sqrt(terms.length) * Math.sqrt(map.get(tdocs.doc())));
							similarityMap.put(tdocs.doc(), similarity);	
						
					}
				
					//}

				}
			}
	
			//function for sorting similarityMap
		similarityMap = (HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(similarityMap);

		System.out.println("\n\n\nDocument Numbers");

			for (Entry<Integer, Double> entry : similarityMap.entrySet()){
				if(count>10)
					break;
				System.out.println(entry.getKey());								
				count++;
			}
		

			similarityMap.clear();
			count=1;
			System.out.print("query> ");
		}
		sc.close();

	}

}


//while(t.next())
//{
//		idf=(float)  ((float) (Math.log((float)r.maxDoc() / r.docFreq(t.term()))) / Math.log(2));
//		idfMap.put(t.term().text(), idf);
//	
//	i++;
//}
//idfMap=(HashMap<String, Double>)ref.comparehashmapvaluesandsortidf(idfMap);
//i=1;
//for (Entry<String, Double> entry : idfMap.entrySet()){
//	if(i>15)
//		break;
//	System.out.println(entry.getKey()+"  valuee   "+entry.getValue());								
//	i++;
//}



//startTime = System.nanoTime();
//endTime   = System.nanoTime();
//System.out.println("\n\n total sort time"+ (endTime - startTime));

//
//String d_url = r.document(tdocs.doc()).getFieldable("path").stringValue();
//similarity = (1 * tdocs.freq()) / (Math.sqrt(terms.length) * Math.sqrt(weightsMap.get(tdocs.doc())));
//similarityMap.put(tdocs.doc(), similarity);
//System.out.println(tdocs.doc() + d_url);

//String d_url = r.document(tdocs.doc()).getFieldable("path").stringValue();

//System.out.println("tdocs.doc()  " + tdocs.doc()
//+ "similarity  " + similarity + "url" + d_url
//+ "tdocsfreq" + tdocs.freq());

//
// Document d = r.document(22009);
// String url = d.getFieldable("path").stringValue(); // the 'path' field of the
// Document object holds the URL
// System.out.println(url.replace("%%", "/"));