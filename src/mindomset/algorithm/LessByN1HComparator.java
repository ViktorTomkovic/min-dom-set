package mindomset.algorithm;

import java.util.Comparator;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;

public class LessByN1HComparator implements Comparator<Integer> {
	private IntObjectOpenHashMap<IntOpenHashSet> neig;
	private static final IntOpenHashSet DEFAULT_VALUE = IntOpenHashSet.newInstance();

	@Override
	public int compare(Integer arg0, Integer arg1) {
		int is = neig.getOrDefault(arg0, DEFAULT_VALUE).size();
		int js = neig.getOrDefault(arg1, DEFAULT_VALUE).size();
		return  ((js == is) ? (arg0 - arg1) : is - js);
	}

	public LessByN1HComparator(IntObjectOpenHashMap<IntOpenHashSet> neig) {
		this.neig = neig;
	}

}
