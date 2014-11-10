package mindomset.datastructure.graph;

import mindomset.algorithm.AbstractMDSAlgorithm;
import mindomset.algorithm.AbstractMDSResult;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

public interface Graph {
	public boolean isDirected();

	public ObjectArrayList<Edge> getEdges();

	public IntOpenHashSet getVertices();

	public int getNumberOfVertices();

	public IntOpenHashSet getN1(int vertex);

	public IntOpenHashSet getN2(int vertex);

	public AbstractMDSResult getMDS(AbstractMDSAlgorithm algorithm);

	public boolean isMDS(AbstractMDSResult mds);

	public boolean isMDS(IntOpenHashSet mds);
}
