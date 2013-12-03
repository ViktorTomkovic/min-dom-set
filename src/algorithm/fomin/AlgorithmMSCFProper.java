package algorithm.fomin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import main.Utils;
import model.DirectedGraph;
import model.Edge;

import algorithm.AbstractMSCAlgorithm;
import algorithm.RepresentedSet;

public class AlgorithmMSCFProper implements AbstractMSCAlgorithm {

	private ArrayList<Long> polyMSC(ArrayList<RepresentedSet> sets) {
		DirectedGraph graph = Utils.getDirectedGraphFromRepresentedSets(sets);
		LinkedHashSet<Edge> pickedEdges = Utils.fordFulkerson(graph);
		HashSet<Long> pickedVertices = new HashSet<>();
		for (Edge e : pickedEdges) {
			pickedVertices.add(e.from);
			pickedVertices.add(e.to);
		}
		HashSet<Long> twoSetVertices = new HashSet<>();
		for (RepresentedSet rs : sets) {
			Long[] set = (Long[]) rs.getSet().toArray();
			if (set.length == 2) {
				twoSetVertices.add(set[0]);
				twoSetVertices.add(set[1]);
			}
			if (set.length == 0) {
				System.out.println("ERROR " + set);
			}
		}
		ArrayList<Long> result = new ArrayList<>();
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

	private ArrayList<Long> msc(ArrayList<RepresentedSet> sets) {
		if (sets.size() == 0) {
			return new ArrayList<>();
		}
		
		// one include another
		RepresentedSet included = null;
		// RepresentedSet including = null;
		for (RepresentedSet r : sets) {
			for (RepresentedSet s : sets) {
				if (r.getSet().containsAll(s.getSet()) && r != s) {
					included = s;
					// including = r;
				}
				if (s.getSet().containsAll(r.getSet()) && s != r) {
					included = r;
					// including = s;
				}
			}
		}
		if (included != null) {
			sets.remove(included);
			return msc(sets);
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
				ArrayList<Long> resultPlus = msc(newSets);
				resultPlus.add(theRightSet.getRepresentant());
				return resultPlus;
			}
		}
		
		//max cardinality
		int maxCardinality = 0;
		RepresentedSet r = null;
		for (RepresentedSet s : sets) {
			if (s.getSet().size() > maxCardinality) {
				maxCardinality = s.getSet().size();
				r = s;
			}
		}
		
		if (maxCardinality == 2L) {
			return polyMSC(sets);
		}
		
		sets.remove(r);
		ArrayList<Long> result1 = msc(sets);

		ArrayList<Long> result2 = msc(getDel(r, sets));
		
		return result1.size() < result2.size() ? result1 : result2;
	}

	@Override
	public ArrayList<Long> getMSCforMDS(LinkedHashSet<Long> universum,
			ArrayList<RepresentedSet> sets) {
		return msc(sets);
	}

}