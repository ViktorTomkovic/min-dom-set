package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.carrotsearch.hppc.cursors.ObjectCursor;

import datastructure.graph.DirectedGraph;
import datastructure.graph.Edge;

public class Utils {
	private Utils() {
	}
	
	// TODO preprobit na HPPC

	public static LinkedHashSet<Edge> fordFulkerson(DirectedGraph graph) {
		LinkedHashSet<Edge> result = new LinkedHashSet<>();

		LinkedHashSet<Edge> edges = new LinkedHashSet<>();
		for (ObjectCursor<Edge> edgeCur : graph.getEdges()) {
			edges.add(edgeCur.value);
		}

		HashMap<Integer, Integer> match = new HashMap<>();

		LinkedHashSet<Integer> whiteSet = new LinkedHashSet<>();
		LinkedHashSet<Integer> blackSet = new LinkedHashSet<>();
		for (Edge e : edges) {
			whiteSet.add(e.from);
			blackSet.add(e.to);
			match.put(e.to, -1);
		}

		for (Integer v : whiteSet) {
			HashSet<Integer> visitedBlack = new HashSet<>();
			aug(edges, v, visitedBlack, match, blackSet);
		}
		for (Entry<Integer, Integer> entry : match.entrySet()) {
			result.add(new Edge(entry.getValue(), entry.getKey()));
		}
		return result;
	}

	private static boolean aug(LinkedHashSet<Edge> edges, Integer white,
			HashSet<Integer> visitedBlack, HashMap<Integer, Integer> match,
			LinkedHashSet<Integer> blackSet) {
		for (Integer black : blackSet) {
			boolean contains = false;
			for (Edge e : edges) {
				if ((white.equals(e.from)) && (black.equals(e.to))) {
					contains = true;
				}
			}
			if (contains && !visitedBlack.contains(black)) {
				visitedBlack.add(black);
				if (match.get(black) == -1L
						|| aug(edges, match.get(black), visitedBlack, match,
								blackSet)) {
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
		LinkedHashSet<Integer> coloredBlack = new LinkedHashSet<>();
		LinkedHashSet<Integer> coloredWhite = new LinkedHashSet<>();
		TreeSet<Integer> unfinishedWhite = new TreeSet<>();
		TreeSet<Integer> unfinishedBlack = new TreeSet<>();
		while (!twoSets.isEmpty()) {
			RepresentedSet s = twoSets.pollFirst();
			Object[] ab = s.getSet().toArray();
			Integer a = (Integer) ab[0];
			Integer b = (Integer) ab[1];
			coloredWhite.add(a);
			coloredBlack.add(b);
			unfinishedWhite.add(a);
			unfinishedBlack.add(b);
			edges.add(new Edge(a, b));

			while (!unfinishedBlack.isEmpty() || !unfinishedWhite.isEmpty()) {
				while (!unfinishedWhite.isEmpty()) {
					Integer l = unfinishedWhite.pollFirst();
					ArrayList<RepresentedSet> toDelete = new ArrayList<>();
					for (RepresentedSet rs : twoSets) {
						if (rs.getSet().contains(l)) {
							Object[] ab2 = rs.getSet().toArray();
							Integer a2 = (Integer) ab2[0];
							Integer b2 = (Integer) ab2[1];
							if (a2.equals(l)) {
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
					Integer l = unfinishedBlack.pollFirst();
					ArrayList<RepresentedSet> toDelete = new ArrayList<>();
					for (RepresentedSet rs : twoSets) {
						if (rs.getSet().contains(l)) {
							Object[] ab2 = rs.getSet().toArray();
							Integer a2 = (Integer) ab2[0];
							Integer b2 = (Integer) ab2[1];
							if (a2.equals(l)) {
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
			 * for (Integer v : coloredWhite) { edges.add(new Edge(-1L, v)); }
			 * for (Integer v : coloredBlack) { edges.add(new Edge(v, -2L)); }
			 */
		}
		return new DirectedGraph(edges);
	}
}
