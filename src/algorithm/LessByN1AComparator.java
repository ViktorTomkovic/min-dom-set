package algorithm;

import java.util.Comparator;

import datastructure.graph.Graph;

public class LessByN1AComparator implements Comparator<Integer> {
	private Graph g;

	@Override
	public int compare(Integer arg0, Integer arg1) {
		int is = g.getN1(arg0).size();
		int js = g.getN1(arg1).size();
		return is - js;
	}
	
	public LessByN1AComparator(Graph g) {
		this.g = g;
	}

}
