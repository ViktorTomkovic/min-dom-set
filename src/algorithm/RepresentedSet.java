package algorithm;

import com.carrotsearch.hppc.IntOpenHashSet;

public class RepresentedSet {
	private int representant;

	private IntOpenHashSet set;

	public int getRepresentant() {
		return representant;
	}

	public IntOpenHashSet getSet() {
		return set;
	}

	public void removeFromSet(IntOpenHashSet set) {
		this.set.removeAll(set);
	}

	public RepresentedSet(int representant, IntOpenHashSet set) {
		this.representant = representant;
		this.set = new IntOpenHashSet(set);
	}
	
	@Override
	public String toString() {
		String result = "";
		result = "[" + representant + " " + set + "]";
		return result;
	}

}
