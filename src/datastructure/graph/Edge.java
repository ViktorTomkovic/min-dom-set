package datastructure.graph;

public class Edge {
	public int from;
	public int to;

	public Edge() {
		this.from = 0;
		this.to = 0;
	}

	public Edge(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "[" + from + "->" + to + "]";
	}
}
