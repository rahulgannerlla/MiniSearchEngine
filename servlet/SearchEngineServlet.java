package edu.asu.irs14.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;

import edu.asu.irs14.MyLibrary;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.asu.irs14.LinkAnalysis;

public class SearchEngineServlet extends HttpServlet{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LinkAnalysis linkAnalysis=null;
	static MyLibrary ref;
	static IndexReader r;
	
	public SearchEngineServlet(){
		System.out.println("In Constructor");
		linkAnalysis=new LinkAnalysis();
		ref=new MyLibrary();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//Map<Integer, Double> resultMap=new HashMap<Integer,Double>();
		r = IndexReader.open(FSDirectory.open(new File("/Users/rgannerl/Documents/workspace/SearchEngineWebApp/index")));

		long time=System.currentTimeMillis();
		String query=req.getParameter("query");
		String optionSelected=req.getParameter("ranking");

		HashMap<Integer, Double> resultMap=linkAnalysis.tfIdf(query, "1");
		HashMap<Integer, String> resultMapInDetail=new HashMap<Integer,String>();
		System.out.println("option selected"+optionSelected);

		if(optionSelected.equals("2"))
			resultMap=linkAnalysis.calculateAuthHubs("2");
		else if(optionSelected.equals("3"))
			resultMap=linkAnalysis.calculateAuthHubs("3");
		else if (optionSelected.equals("4"))	
			resultMap=linkAnalysis.pageRankNew();
		else
			System.out.print("");
		
		resultMapInDetail.clear();
		
		int tfidfCount=0;
		resultMap=(HashMap<Integer, Double>) ref.comparehashmapvaluesandsort(resultMap);
		for (Entry<Integer, Double> entry : resultMap.entrySet()){
			 Document d = r.document(entry.getKey());
			 String url = d.getFieldable("path").stringValue();
			 resultMapInDetail.put(entry.getKey(), url.replace("%%", "/"));
			 System.out.println(entry.getKey());
			if(tfidfCount>11)
				break;						
			tfidfCount++;
		}
		
		//linkAnalysis.tfIdf(query);
		//searchFiles.tfIdf(query);

		long totalTime=ref.currentdatetimestamp(time);
		
		String target="Result.jsp";
		req.setAttribute("results",resultMapInDetail);
		req.setAttribute("query",query);
		req.setAttribute("ranking", optionSelected);
		req.setAttribute("timetaken", String.valueOf(totalTime));

		RequestDispatcher dispatcher=req.getRequestDispatcher(target);
		dispatcher.forward(req, resp);

	}
}
