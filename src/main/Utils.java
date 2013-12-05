package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import model.DirectedGraph;
import model.Edge;
import algorithm.RepresentedSet;

public class Utils {
	public static LinkedHashSet<Edge> fordFulkerson (DirectedGraph graph) {
		LinkedHashSet<Edge> result = new LinkedHashSet<>();
		
		LinkedHashSet<Edge> edges = new LinkedHashSet<>(graph.getEdges());

		HashMap<Long, Long> match = new HashMap<>();
		
		LinkedHashSet<Long> whiteSet = new LinkedHashSet<>();
		LinkedHashSet<Long> blackSet = new LinkedHashSet<>();
		for (Edge e : edges) {
			whiteSet.add(e.from);
			blackSet.add(e.to);
			match.put(e.to, -1L);
		}
		
		for (Long v : whiteSet) {
			HashSet<Long> visitedBlack = new HashSet<>();
			aug(edges, v, visitedBlack, match, blackSet);
		}
		for (Long to : match.keySet()) {
			result.add(new Edge(match.get(to), to));
		}
		return result;
	}

	private static boolean aug(LinkedHashSet<Edge> edges, Long white,
			HashSet<Long> visitedBlack, HashMap<Long, Long> match,
			LinkedHashSet<Long> blackSet) {
		for (Long black : blackSet) {
			boolean contains = false;
			for (Edge e : edges) {
				if ((e.from == white) && (e.to == black)) {
					contains = true;
				}
			}
			if (contains && !visitedBlack.contains(black)) {
				visitedBlack.add(black);
				if (match.get(black) == -1L || aug(edges, match.get(black), visitedBlack, match, blackSet)) {
					match.put(black, white);
					return true;
				}
			}
		}
		return false;
	}

	public static DirectedGraph getDirectedGraphFromRepresentedSets(
			ArrayList<RepresentedSet> sets) {
		ArrayList<Edge> edges = new ArrayList<>();
		LinkedList<RepresentedSet> twoSets = new LinkedList<>(sets);
		for (RepresentedSet s : sets) {
			if (s.getSet().size() == 1) {
				twoSets.remove(s);
			}
		}
		LinkedHashSet<Long> coloredBlack = new LinkedHashSet<>();
		LinkedHashSet<Long> coloredWhite = new LinkedHashSet<>();
		TreeSet<Long> unfinishedWhite = new TreeSet<>();
		TreeSet<Long> unfinishedBlack = new TreeSet<>();
		while (!twoSets.isEmpty()) {
			RepresentedSet s = twoSets.pollFirst();
			Object[] ab = s.getSet().toArray();
			Long a = (Long) ab[0];
			Long b = (Long) ab[1];
			coloredWhite.add(a);
			coloredBlack.add(b);
			unfinishedWhite.add(a);
			unfinishedBlack.add(b);
			edges.add(new Edge(a, b));

			while (!unfinishedBlack.isEmpty() || !unfinishedWhite.isEmpty()) {
				while (!unfinishedWhite.isEmpty()) {
					Long l = unfinishedWhite.pollFirst();
					ArrayList<RepresentedSet> toDelete = new ArrayList<>();
					for (RepresentedSet rs : twoSets) {
						if (rs.getSet().contains(l)) {
							Object[] ab2 = rs.getSet().toArray();
							Long a2 = (Long) ab2[0];
							Long b2 = (Long) ab2[1];
							if (a2 == l) {
								unfinishedBlack.add(b2);
								edges.add(new Edge(a2, b2));
								coloredWhite.add(a2);
								coloredBlack.add(b2);
							} else {
								unfinishedBlack.add(a2);
								edges.add(new Edge(b2, a2));
								coloredWhite.add(b2);
								coloredBlack.add(a2);
							}
							toDelete.add(rs);
						}
					}
					twoSets.removeAll(toDelete);
				}

				while (!unfinishedBlack.isEmpty()) {
					Long l = unfinishedBlack.pollFirst();
					ArrayList<RepresentedSet> toDelete = new ArrayList<>();
					for (RepresentedSet rs : twoSets) {
						if (rs.getSet().contains(l)) {
							Object[] ab2 = rs.getSet().toArray();
							Long a2 = (Long) ab2[0];
							Long b2 = (Long) ab2[1];
							if (a2 == l) {
								unfinishedWhite.add(b2);
								edges.add(new Edge(b2, a2));
								coloredWhite.add(b2);
								coloredBlack.add(a2);
							} else {
								unfinishedWhite.add(a2);
								edges.add(new Edge(a2, b2));
								coloredWhite.add(a2);
								coloredBlack.add(b2);
							}
							toDelete.add(rs);
						}
					}
					twoSets.removeAll(toDelete);
				}
			}
			/*
			 * for (Long v : coloredWhite) { edges.add(new Edge(-1L, v)); } for
			 * (Long v : coloredBlack) { edges.add(new Edge(v, -2L)); }
			 */
		}
		return new DirectedGraph(edges);
	}
}
