package model;

public class Edge {
	public long from;
	public long to;

	public Edge() {
		this.from = 0;
		this.to = 0;
	}

	public Edge(long from, long to) {
		this.from = from;
		this.to = to;
	}
}
