package edu.asu.irs14;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MyLibrary {

	public long currentdatetimestamp(long startTime) {
        long endTime = System.currentTimeMillis( );
        long diff = endTime - startTime;
        return diff;
	}
	
	public long currentdatetimestampinNano(long startTime) {
        long endTime = System.nanoTime( );
        long diff = endTime - startTime;
        return diff;
	}

	public Map<Integer, Double> comparehashmapvaluesandsort(Map<Integer, Double> unsortMap) {
		List<Entry<Integer, Double>> mylist = new LinkedList<Entry<Integer, Double>>(unsortMap.entrySet());

		Collections.sort(mylist, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> object1,Entry<Integer, Double> object2) {
				return object2.getValue().compareTo(object1.getValue());
			}
		});

		Map<Integer, Double> hashmapsorted = new LinkedHashMap<Integer, Double>();
		for (Entry<Integer, Double> entry : mylist)
			hashmapsorted.put(entry.getKey(), entry.getValue());

		return hashmapsorted;
	}
	
	public double[][] multiplyTwoMatrices(double matrixOne[][], double matrixTwo[][]){
		
		double multipliedMatrix[][]=new double[matrixOne[0].length][matrixTwo[1].length];
		
		for(int i=0;i<matrixOne[0].length;i++)
			for(int j=0;j<matrixTwo[1].length;j++)
				multipliedMatrix[i][j]=0.0;
		
		for(int i=0;i<matrixOne[0].length;i++){
			for(int j=0;j<matrixTwo[1].length;j++){
				 for (int k = 0; k < matrixOne[1].length; k++){
					 multipliedMatrix[i][j]+= matrixOne[i][k] * matrixTwo[k][j];
				 }
			}
		}
		return multipliedMatrix;
	}
	
	
	public boolean checkTheResult (double pr1[], double pr2[]) {
		boolean flag = true;
		double[] change = new double[25053];
			for (int i = 0; i <= 25053; i++)
				{
				change[i] = Math.abs(pr2[i]-pr1[i]);
					if(change[i] > 0.00002) 
					{ 
						flag=false;
					      break; 
					}
				}
			return flag;
		}

}


//public Map<String, Double> comparehashmapvaluesandsortidf(Map<String, Double> unsortMap) {
//	List<Entry<String, Double>> mylist = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
//
//	Collections.sort(mylist, new Comparator<Entry<String, Double>>() {
//		public int compare(Entry<String, Double> object1,Entry<String, Double> object2) {
//			return object1.getValue().compareTo(object2.getValue());
//		}
//	});
//
//	Map<String, Double> hashmapsorted = new LinkedHashMap<String, Double>();
//	for (Entry<String, Double> entry : mylist)
//		hashmapsorted.put(entry.getKey(), entry.getValue());
//
//	return hashmapsorted;
//}

