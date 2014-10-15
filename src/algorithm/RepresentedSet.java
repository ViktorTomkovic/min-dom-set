package algorithm;

import java.util.LinkedHashSet;

public class RepresentedSet {
	private Long representant;

	private LinkedHashSet<Long> set;

	public Long getRepresentant() {
		return representant;
	}

	public LinkedHashSet<Long> getSet() {
		return set;
	}

	public void removeFromSet(LinkedHashSet<Long> set) {
		this.set.removeAll(set);
	}

	public RepresentedSet(Long representant, LinkedHashSet<Long> set) {
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
