package algorithm;

import java.util.Comparator;

import datastructure.graph.Graph;

public class LessByN1BComparator implements Comparator<Integer> {
	private Graph g;

	@Override
	public int compare(Integer arg0, Integer arg1) {
		int is = g.getN1(arg0).size();
		int js = g.getN1(arg1).size();
		return  ((is == js) ? (arg0 - arg1) : is - js);
	}

	public LessByN1BComparator(Graph g) {
		this.g = g;
	}

}
