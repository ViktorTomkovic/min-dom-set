package algorithm;

import java.util.LinkedHashSet;

public class RepresentedSet {
	private Integer representant;

	private LinkedHashSet<Integer> set;

	public Integer getRepresentant() {
		return representant;
	}

	public LinkedHashSet<Integer> getSet() {
		return set;
	}

	public void removeFromSet(LinkedHashSet<Integer> set) {
		this.set.removeAll(set);
	}

	public RepresentedSet(Integer representant, LinkedHashSet<Integer> set) {
		this.representant = representant;
		this.set = new LinkedHashSet<>(set);
	}
	
	@Override
	public String toString() {
		String result = "";
		result = "[" + representant + " " + set + "]";
		return result;
	}

}
