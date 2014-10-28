package model;

public class Edge {
	public Integer from;
	public Integer to;

	public Edge() {
		this.from = 0;
		this.to = 0;
	}

	public Edge(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "[" + from + "->" + to + "]";
	}
}
