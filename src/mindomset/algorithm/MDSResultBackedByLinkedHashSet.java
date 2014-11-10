package mindomset.algorithm;

import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;

import mindomset.main.Utils;

public class MDSResultBackedByLinkedHashSet implements AbstractMDSResult {
	private LinkedHashSet<Integer> internalData;

	public MDSResultBackedByLinkedHashSet() {
		internalData = new LinkedHashSet<>();
	}
	
	public void setResult(LinkedHashSet<Integer> result) {
		internalData = new LinkedHashSet<>(result);
	}

	@Override
	public int size() {
		return internalData.size();
	}
	
	@Override
	public String toString() {
		return Utils.largeCollectionToString(internalData);
	}

	@Override
	public int hashCode() {
		final int prime = 47;
		int result = 1;
		result = prime * result
				+ ((internalData == null) ? 0 : internalData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MDSResultBackedByLinkedHashSet)) {
			return false;
		}
		MDSResultBackedByLinkedHashSet other = (MDSResultBackedByLinkedHashSet) obj;
		if (internalData == null) {
			if (other.internalData != null) {
				return false;
			}
		} else if (!internalData.equals(other.internalData)) {
			return false;
		}
		return true;
	}

	@Override
	public Iterable<IntCursor> getIterableStructure() {
		IntArrayList result = new IntArrayList(internalData.size());
		for (Integer i : internalData) {
			result.add(i);
		}
		return result;
	}

}
