package algorithm;

import java.util.Comparator;

import model.Graph;

public class LessByN1AComparator implements Comparator<Long> {
	private Graph g;

	@Override
	public int compare(Long arg0, Long arg1) {
		int is = g.getN1(arg0).size();
		int js = g.getN1(arg1).size();
		return is - js;
	}
	
	public LessByN1AComparator(Graph g) {
		this.g = g;
	}

}
