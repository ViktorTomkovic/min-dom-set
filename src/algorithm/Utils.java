package algorithm;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import datastructure.graph.DirectedGraph;
import datastructure.graph.Edge;

public class Utils {
	private Utils() {
	}

	public static ObjectOpenHashSet<Edge> fordFulkerson(DirectedGraph graph) {
		ObjectOpenHashSet<Edge> result = new ObjectOpenHashSet<>();

		ObjectOpenHashSet<Edge> edges = new ObjectOpenHashSet<>(
				graph.getEdges());

		IntIntOpenHashMap match = new IntIntOpenHashMap();

		IntOpenHashSet whiteSet = new IntOpenHashSet();
		IntOpenHashSet blackSet = new IntOpenHashSet();
		for (ObjectCursor<Edge> ecur : edges) {
			whiteSet.add(ecur.value.from);
			blackSet.add(ecur.value.to);
			match.put(ecur.value.to, -1);
		}

		for (IntCursor vcur : whiteSet) {
			IntOpenHashSet visitedBlack = new IntOpenHashSet();
			aug(edges, vcur.value, visitedBlack, match, blackSet);
		}
		for (IntIntCursor entrycur : match) {
			result.add(new Edge(entrycur.value, entrycur.key));
		}
		return result;
	}

	private static boolean aug(ObjectOpenHashSet<Edge> edges, int white,
			IntOpenHashSet visitedBlack, IntIntOpenHashMap match,
			IntOpenHashSet blackSet) {
		for (IntCursor blackcur : blackSet) {
			boolean contains = false;
			final int black = blackcur.value;
			for (ObjectCursor<Edge> ecur : edges) {
				if ((white == ecur.value.from)
						&& (black == ecur.value.to)) {
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
			ObjectArrayList<RepresentedSet> sets) {
		ObjectArrayList<Edge> edges = new ObjectArrayList<>();
		ObjectArrayList<RepresentedSet> twoSets = new ObjectArrayList<>(sets);
		for (ObjectCursor<RepresentedSet> scur : sets) {
			if (scur.value.getSet().size() == 1) {
				twoSets.removeLastOccurrence(scur.value);
			}
		}
		IntOpenHashSet coloredBlack = new IntOpenHashSet();
		IntOpenHashSet coloredWhite = new IntOpenHashSet();
		IntOpenHashSet unfinishedWhite = new IntOpenHashSet();
		IntOpenHashSet unfinishedBlack = new IntOpenHashSet();
		while (!twoSets.isEmpty()) {
			RepresentedSet s = twoSets.remove(twoSets.size()-1);
			int[] ab = s.getSet().toArray();
			int a = ab[0];
			int b = ab[1];
			coloredWhite.add(a);
			coloredBlack.add(b);
			unfinishedWhite.add(a);
			unfinishedBlack.add(b);
			edges.add(new Edge(a, b));

			while (!unfinishedBlack.isEmpty() || !unfinishedWhite.isEmpty()) {
				while (!unfinishedWhite.isEmpty()) {
					int picked = -1;
					for (int i = 0; i < unfinishedWhite.allocated.length; i++) {
						if (unfinishedWhite.allocated[i]) {
							picked = unfinishedWhite.keys[i];
							break;
						}
					}
					unfinishedWhite.remove(picked);

					ObjectOpenHashSet<RepresentedSet> toDelete = new ObjectOpenHashSet<>();
					for (ObjectCursor<RepresentedSet> rscur : twoSets) {
						if (rscur.value.getSet().contains(picked)) {
							int[] ab2 = rscur.value.getSet().toArray();
							int a2 = ab2[0];
							int b2 = ab2[1];
							if (a2 == picked) {
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
							toDelete.add(rscur.value);
						}
					}
					twoSets.removeAll(toDelete);
				}

				while (!unfinishedBlack.isEmpty()) {
					int picked = -1;
					for (int i = 0; i < unfinishedBlack.allocated.length; i++) {
						if (unfinishedBlack.allocated[i]) {
							picked = unfinishedBlack.keys[i];
							break;
						}
					}
					unfinishedBlack.remove(picked);
					
					ObjectOpenHashSet<RepresentedSet> toDelete = new ObjectOpenHashSet<>();
					for (ObjectCursor<RepresentedSet> rscur : twoSets) {
						if (rscur.value.getSet().contains(picked)) {
							int[] ab2 = rscur.value.getSet().toArray();
							int a2 = ab2[0];
							int b2 = ab2[1];
							if (a2 == picked) {
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
							toDelete.add(rscur.value);
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

	/** Is small set contained in big set? */
	public static boolean containsAll(IntOpenHashSet bigSet,
			IntOpenHashSet smallSet) {
		boolean isContained = true;
		for (IntCursor smallcur : smallSet) {
			if (!bigSet.contains(smallcur.value)) {
				isContained = false;
				break;
			}
		}
		return isContained;
	}
}
