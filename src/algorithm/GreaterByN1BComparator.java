package algorithm;

import java.util.Comparator;

import datastructure.graph.Graph;

public class GreaterByN1BComparator implements Comparator<Integer> {
	private Graph g;

	@Override
	public int compare(Integer arg0, Integer arg1) {
		int is = g.getN1(arg0).size();
		int js = g.getN1(arg1).size();
		return  ((js == is) ? (arg1 - arg0) : js - is);
	}

	public GreaterByN1BComparator(Graph g) {
		this.g = g;
	}

}
