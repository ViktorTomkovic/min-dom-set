package mindomset.algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class LessByN1NComparator implements Comparator<Integer> {
	private HashMap<Integer, LinkedHashSet<Integer>> neig;
	private static final LinkedHashSet<Integer> DEFAULT_VALUE = new LinkedHashSet<>(0);

	@Override
	public int compare(Integer arg0, Integer arg1) {
		int is = neig.getOrDefault(arg0, DEFAULT_VALUE).size();
		int js = neig.getOrDefault(arg1, DEFAULT_VALUE).size();
		return  ((js == is) ? (arg0 - arg1) : is - js);
	}

	public LessByN1NComparator(HashMap<Integer, LinkedHashSet<Integer>> neig) {
		this.neig = neig;
	}

}
