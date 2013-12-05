package algorithm.fomin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import model.Graph;

import algorithm.AbstractMSCAlgorithm;
import algorithm.RepresentedSet;

public class AlgorithmMSCFNaive implements AbstractMSCAlgorithm {

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
			return g.isMDS(chosen) ? chosen : null;
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
			LinkedHashSet<Long> result = msc(sets, chosen, g);
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
		sets.remove(theRightSet);
		LinkedHashSet<Long> result1 = msc(sets, chosen, g);

		LinkedHashSet<Long> chosen2 = new LinkedHashSet<>(chosen);
		chosen2.add(theRightSet.getRepresentant());
		LinkedHashSet<Long> result2 = msc(getDel(theRightSet, sets), chosen2, g);
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
