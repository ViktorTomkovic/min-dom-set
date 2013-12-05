package model;

public class Edge {
	public Long from;
	public Long to;

	public Edge() {
		this.from = 0L;
		this.to = 0L;
	}

	public Edge(Long from, Long to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "[" + from + "->" + to + "]";
	}
}
