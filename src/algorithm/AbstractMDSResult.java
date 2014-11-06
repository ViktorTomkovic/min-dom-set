package algorithm;

import com.carrotsearch.hppc.cursors.IntCursor;

public interface AbstractMDSResult {

	public int size();

	public String toString();
	
	public Iterable<IntCursor> getIterableStructure();
}
