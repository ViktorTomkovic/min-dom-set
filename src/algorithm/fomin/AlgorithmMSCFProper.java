package algorithm.fomin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import main.Utils;
import model.DirectedGraph;
import model.Edge;
import model.Graph;
import algorithm.AbstractMSCAlgorithm;
import algorithm.RepresentedSet;

public class AlgorithmMSCFProper implements AbstractMSCAlgorithm {

	private LinkedHashSet<Long> polyMSC(ArrayList<RepresentedSet> sets) {
		DirectedGraph graph = Utils.getDirectedGraphFromRepresentedSets(sets);
		LinkedHashSet<Edge> pickedEdges = Utils.fordFulkerson(graph);
		HashSet<Long> pickedVertices = new HashSet<>();
		for (Edge e : pickedEdges) {
			pickedVertices.add(e.from);
			pickedVertices.add(e.to);
		}
		HashSet<Long> twoSetVertices = new HashSet<>();
		for (RepresentedSet rs : sets) {
			Object[] set = rs.getSet().toArray();
			Long a = (Long) set[0];
			Long b = (Long) set[1];
			if (set.length == 2) {
				twoSetVertices.add(a);
				twoSetVertices.add(b);
			}
			if (set.length == 0) {
				System.out.println("ERROR " + Arrays.toString(set));
			}
		}
		LinkedHashSet<Long> result = new LinkedHashSet<>();
		for (RepresentedSet rs : sets) {
			LinkedHashSet<Long> set = rs.getSet();
			if (set.size() == 1) {
				Long v = (Long) set.toArray()[0];
				if (!twoSetVertices.contains(v)) {
					result.add(rs.getRepresentant());
				}
			}
		}
		for (Edge e : pickedEdges) {
			for (RepresentedSet rs : sets) {
				LinkedHashSet<Long> set = rs.getSet();
				if (set.contains(e.from) && set.contains(e.to)) {
					result.add(rs.getRepresentant());
				}
			}
		}
		// System.out.println(result);
		return result;
	}

	private ArrayList<Long> getUniversum(ArrayList<RepresentedSet> sets) {
		HashSet<Long> result = new HashSet<>();
		for (RepresentedSet s : sets) {
			result.addAll(s.getSet());
		}
		return new ArrayList<>(result);
	}

	private ArrayList<RepresentedSet> getDel(RepresentedSet set,
			ArrayList<RepresentedSet> sets) {
		ArrayList<RepresentedSet> result = new ArrayList<>();
		for (RepresentedSet rs : sets) {
			LinkedHashSet<Long> ss = new LinkedHashSet<>(rs.getSet());
			ss.removeAll(set.getSet());
			if (ss.size() > 0) {
				RepresentedSet ns = new RepresentedSet(rs.getRepresentant(), ss);
				result.add(ns);
			}
		}
		return result;
	}

	private LinkedHashSet<Long> msc(ArrayList<RepresentedSet> sets,
			LinkedHashSet<Long> chosen, Graph g) {
		if (sets.size() == 0) {
			return g.isMDS(chosen) ? new LinkedHashSet<>(chosen) : null;
		}

		// one include another
		RepresentedSet included = null;
		boolean found = false;
		// RepresentedSet including = null;
		for (RepresentedSet r : sets) {
			if (found)
				break;
			for (RepresentedSet s : sets) {
				if (r.getSet().containsAll(s.getSet()) && r != s) {
					included = s;
					found = true;
					break;
				}
				if (s.getSet().containsAll(r.getSet()) && s != r) {
					included = r;
					found = true;
					break;
				}
			}
		}
		if (included != null) {
			sets.remove(included);
			LinkedHashSet<Long> result = msc(sets, chosen, g);
			sets.add(included);
			return result;
		}

		// is unique
		ArrayList<Long> universum = getUniversum(sets);
		for (Long l : universum) {
			Long counter = 0L;
			RepresentedSet theRightSet = null;
			for (RepresentedSet s : sets) {
				if (s.getSet().contains(l)) {
					counter++;
					theRightSet = s;
				}
			}
			if (counter == 1L) {
				ArrayList<RepresentedSet> newSets = getDel(theRightSet, sets);
				LinkedHashSet<Long> chosen2 = new LinkedHashSet<>(chosen);
				chosen2.add(theRightSet.getRepresentant());
				LinkedHashSet<Long> result = msc(newSets, chosen2, g);
				return result;
			}
		}

		// max cardinality
		int maxCardinality = 0;
		RepresentedSet theRightSet = null;
		for (RepresentedSet s : sets) {
			if (s.getSet().size() > maxCardinality) {
				maxCardinality = s.getSet().size();
				theRightSet = s;
			}
		}

		if (maxCardinality == 2L) {
			LinkedHashSet<Long> finalChoice = polyMSC(sets);
			chosen.addAll(finalChoice);
			return g.isMDS(chosen) ? new LinkedHashSet<>(chosen) : null;
		}
				
		sets.remove(theRightSet);
		LinkedHashSet<Long> result1 = msc(sets, chosen, g);

		chosen.add(theRightSet.getRepresentant());
		LinkedHashSet<Long> result2 = msc(getDel(theRightSet, sets), chosen, g);
		chosen.remove(theRightSet.getRepresentant());

		sets.add(theRightSet);
		if (result1 == null) {
			return result2;
		} else if (result2 == null) {
			return result1;
		}
		return result1.size() < result2.size() ? result1 : result2;
	}


	@Override
	public LinkedHashSet<Long> getMSCforMDS(LinkedHashSet<Long> universum,
			ArrayList<RepresentedSet> sets, Graph g) {
		return msc(sets, new LinkedHashSet<Long>(), g);
	}

}
