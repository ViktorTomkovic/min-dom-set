package mindomset.algorithm;

import mindomset.main.Utils;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

public class MDSResultBackedByIntOpenHashSet implements AbstractMDSResult {
	private IntOpenHashSet internalData;

	public MDSResultBackedByIntOpenHashSet() {
		internalData = new IntOpenHashSet();
	}
	
	public void setResult(IntOpenHashSet result) {
		internalData = new IntOpenHashSet(result);
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
		if (!(obj instanceof MDSResultBackedByIntOpenHashSet)) {
			return false;
		}
		MDSResultBackedByIntOpenHashSet other = (MDSResultBackedByIntOpenHashSet) obj;
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
		return internalData;
	}

}
