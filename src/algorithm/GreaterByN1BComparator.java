package algorithm;

import java.util.Comparator;

import model.Graph;

public class GreaterByN1BComparator implements Comparator<Long> {
	private Graph g;

	@Override
	public int compare(Long arg0, Long arg1) {
		int is = g.getN1(arg0).size();
		int js = g.getN1(arg1).size();
		return  ((js == is) ? (int)(arg1 - arg0) : js - is);
	}

	public GreaterByN1BComparator(Graph g) {
		this.g = g;
	}

}
