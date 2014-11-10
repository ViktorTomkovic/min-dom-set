package mindomset.algorithm.fomin;

import java.util.Arrays;

import mindomset.algorithm.AbstractMSCAlgorithm;
import mindomset.algorithm.RepresentedSet;
import mindomset.algorithm.Utils;
import mindomset.datastructure.graph.DirectedGraph;
import mindomset.datastructure.graph.Edge;
import mindomset.datastructure.graph.Graph;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

public class AlgorithmMSCFProper implements AbstractMSCAlgorithm {

	private IntOpenHashSet polyMSC(ObjectArrayList<RepresentedSet> sets) {
		DirectedGraph graph = Utils.getDirectedGraphFromRepresentedSets(sets);
		ObjectOpenHashSet<Edge> pickedEdges = Utils.fordFulkerson(graph);
		IntOpenHashSet pickedVertices = new IntOpenHashSet(pickedEdges.size());
		for (ObjectCursor<Edge> ecur : pickedEdges) {
			pickedVertices.add(ecur.value.from);
			pickedVertices.add(ecur.value.to);
		}
		IntOpenHashSet twoSetVertices = new IntOpenHashSet(sets.size());
		for (ObjectCursor<RepresentedSet> rscur : sets) {
			if (rscur.value.getSet().size() == 2) {
				int[] set = rscur.value.getSet().toArray();
				twoSetVertices.add(set[0]);
				twoSetVertices.add(set[1]);
			}
			if (rscur.value.getSet().size() == 0) {
				System.out.println("ERROR "
						+ Arrays.toString(rscur.value.getSet().toArray()));
			}
		}
		IntOpenHashSet result = new IntOpenHashSet();
		for (ObjectCursor<RepresentedSet> rscur : sets) {
			if (rscur.value.getSet().size() == 1) {
				int v = rscur.value.getSet().toArray()[0];
				if (!twoSetVertices.contains(v)) {
					result.add(rscur.value.getRepresentant());
				}
			}
		}
		for (ObjectCursor<Edge> ecur : pickedEdges) {
			for (ObjectCursor<RepresentedSet> rscur : sets) {
				IntOpenHashSet set = rscur.value.getSet();
				if (set.contains(ecur.value.from)
						&& set.contains(ecur.value.to)) {
					result.add(rscur.value.getRepresentant());
				}
			}
		}
		// System.out.println(result);
		return result;
	}

	private IntOpenHashSet getUniversum(ObjectArrayList<RepresentedSet> sets) {
		IntOpenHashSet result = new IntOpenHashSet(sets.size());
		for (ObjectCursor<RepresentedSet> scur : sets) {
			result.addAll(scur.value.getSet());
		}
		return result;
	}

	private ObjectArrayList<RepresentedSet> getDel(RepresentedSet set,
			ObjectArrayList<RepresentedSet> sets) {
		ObjectArrayList<RepresentedSet> result = new ObjectArrayList<>();
		for (ObjectCursor<RepresentedSet> rscur : sets) {
			IntOpenHashSet ss = new IntOpenHashSet(rscur.value.getSet());
			ss.removeAll(set.getSet());
			if (ss.size() > 0) {
				RepresentedSet ns = new RepresentedSet(
						rscur.value.getRepresentant(), ss);
				result.add(ns);
			}
		}
		return result;
	}

	private IntOpenHashSet msc(ObjectArrayList<RepresentedSet> sets,
			IntOpenHashSet chosen, Graph g) {
		if (sets.size() == 0) {
			IntOpenHashSet chosenDeepCopy = new IntOpenHashSet(chosen);
			return g.isMDS(chosenDeepCopy) ? chosenDeepCopy : null;
		}

		// one include another
		RepresentedSet included = null;
		boolean found = false;
		// RepresentedSet including = null;
		for (ObjectCursor<RepresentedSet> rcur : sets) {
			if (found) {
				break;
			}
			for (ObjectCursor<RepresentedSet> scur : sets) {
				if (Utils.containsAll(rcur.value.getSet(), scur.value.getSet())
						&& !rcur.value.equals(scur.value)) {
					included = scur.value;
					found = true;
					break;
				}
				if (Utils.containsAll(scur.value.getSet(), rcur.value.getSet())
						&& !scur.value.equals(rcur.value)) {
					included = rcur.value;
					found = true;
					break;
				}
			}
		}
		if (included != null) {
			sets.removeLastOccurrence(included);
			IntOpenHashSet result = msc(sets, chosen, g);
			sets.add(included);
			return result;
		}

		// is unique
		IntOpenHashSet universum = getUniversum(sets);
		for (IntCursor lcur : universum) {
			int counter = 0;
			RepresentedSet theRightSet = null;
			for (ObjectCursor<RepresentedSet> scur : sets) {
				if (scur.value.getSet().contains(lcur.value)) {
					counter++;
					theRightSet = scur.value;
				}
			}
			if (counter == 1) {
				ObjectArrayList<RepresentedSet> newSets = getDel(theRightSet,
						sets);
				IntOpenHashSet chosen2 = new IntOpenHashSet(chosen);
				chosen2.add(theRightSet.getRepresentant());
				IntOpenHashSet result = msc(newSets, chosen2, g);
				return result;
			}
		}

		// max cardinality
		int maxCardinality = 0;
		RepresentedSet theRightSet = null;
		for (ObjectCursor<RepresentedSet> scur : sets) {
			if (scur.value.getSet().size() > maxCardinality) {
				maxCardinality = scur.value.getSet().size();
				theRightSet = scur.value;
			}
		}

		if (maxCardinality == 2) {
			IntOpenHashSet finalChoice = polyMSC(sets);
			chosen.addAll(finalChoice);
			IntOpenHashSet chosenDeepCopy = new IntOpenHashSet(chosen);
			return g.isMDS(chosenDeepCopy) ? chosenDeepCopy : null;
		}
		
		sets.removeLastOccurrence(theRightSet);
		IntOpenHashSet result1 = msc(sets, chosen, g);

		chosen.add(theRightSet.getRepresentant());
		IntOpenHashSet result2 = msc(getDel(theRightSet, sets), chosen, g);
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
	public IntOpenHashSet getMSCforMDS(IntOpenHashSet universum,
			ObjectArrayList<RepresentedSet> sets, Graph g) {
		return msc(sets, IntOpenHashSet.newInstance(), g);
	}

}
