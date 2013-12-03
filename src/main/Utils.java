package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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
			Edge e = new Edge(white, black);
			if (edges.contains(e) && !visitedBlack.contains(black)) {
				visitedBlack.add(black);
				if (match.get(black) == null || aug(edges, match.get(black), visitedBlack, match, blackSet)) {
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
		LinkedList<Long> unfinishedWhite = new LinkedList<>();
		LinkedList<Long> unfinishedBlack = new LinkedList<>();
		while (!twoSets.isEmpty()) {
			RepresentedSet s = twoSets.pollFirst();
			Long[] ab = (Long[]) s.getSet().toArray();
			coloredWhite.add(ab[0]);
			coloredBlack.add(ab[1]);
			unfinishedWhite.add(ab[0]);
			unfinishedBlack.add(ab[1]);
			edges.add(new Edge(ab[0], ab[1]));

			while (!unfinishedBlack.isEmpty() || !unfinishedWhite.isEmpty()) {
				while (!unfinishedWhite.isEmpty()) {
					Long l = unfinishedWhite.pollFirst();
					ArrayList<RepresentedSet> toDelete = new ArrayList<>();
					for (RepresentedSet rs : twoSets) {
						if (rs.getSet().contains(l)) {
							Long[] ab2 = (Long[]) rs.getSet().toArray();
							if (ab2[0] == l) {
								unfinishedBlack.add(ab2[1]);
								edges.add(new Edge(ab2[0], ab2[1]));
								coloredWhite.add(ab2[0]);
								coloredBlack.add(ab2[1]);
							} else {
								unfinishedBlack.add(ab2[0]);
								edges.add(new Edge(ab2[1], ab2[0]));
								coloredWhite.add(ab2[1]);
								coloredBlack.add(ab2[0]);
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
							Long[] ab2 = (Long[]) rs.getSet().toArray();
							if (ab2[0] == l) {
								unfinishedWhite.add(ab2[1]);
								edges.add(new Edge(ab2[1], ab2[0]));
								coloredWhite.add(ab2[1]);
								coloredBlack.add(ab2[0]);
							} else {
								unfinishedWhite.add(ab2[0]);
								edges.add(new Edge(ab2[0], ab2[1]));
								coloredWhite.add(ab2[0]);
								coloredBlack.add(ab2[1]);
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
