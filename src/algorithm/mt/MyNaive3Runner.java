package algorithm.mt;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;

import datastructure.graph.Graph;

public class MyNaive3Runner implements Runnable {
	private MyNaive3Algorithm alg;
	private ArrayList<Integer> unchosenVertices;
	private LinkedHashSet<Integer> chosenVertices;
	private Graph g;

	public MyNaive3Runner(MyNaive3Algorithm alg,
			ArrayList<Integer> unchosenVertices,
			LinkedHashSet<Integer> chosenVertices, Graph g) {
		this.alg = alg;
		this.unchosenVertices = new ArrayList<>(unchosenVertices);
		this.chosenVertices = new LinkedHashSet<>(chosenVertices);
		this.g = g;
		System.out.print("+");
	}

	@Override
	public void run() {
		gms(unchosenVertices, chosenVertices, g);
		alg.decCC();
		System.out.print(alg.getCurrentCores() + " ");
	}

	private void gms(ArrayList<Integer> unchosenVertices,
			LinkedHashSet<Integer> chosenVertices, Graph g) {
		// System.out.println("u  "+Utils.LargeCollectionToString(unchosenVertices));
		// System.out.println("ch "+Utils.LargeCollectionToString(chosenVertices));

		if (chosenVertices.size() >= alg.getCurrentBest())
			return;

		if (unchosenVertices.size() == 0) {
			IntOpenHashSet chosenVertices2 = new IntOpenHashSet(chosenVertices.size());
			for (Integer i : chosenVertices) {
				chosenVertices2.add(i);
			}
			if (g.isMDS(chosenVertices2)) {
				alg.tryToSetResult(chosenVertices);
			}
		} else {
			Integer v = unchosenVertices.get(unchosenVertices.size() - 1);
			ArrayList<Integer> unch1 = new ArrayList<>(unchosenVertices);
			ArrayList<Integer> unch2 = new ArrayList<>(unchosenVertices);
			unch1.remove(unch1.size() - 1);
			unch2.remove(unch2.size() - 1);
			// System.out.println("unch1 "+Utils.LargeCollectionToString(unch1));
			// System.out.println("unch2 "+Utils.LargeCollectionToString(unch2));

			LinkedHashSet<Integer> ch1 = new LinkedHashSet<>(chosenVertices);
			LinkedHashSet<Integer> ch2 = new LinkedHashSet<>(chosenVertices);
			ch2.add(v);
			// System.out.println("ch1 "+Utils.LargeCollectionToString(ch1));
			// System.out.println("ch2 "+Utils.LargeCollectionToString(ch2));
				gms(unch1, ch1, g);
				gms(unch2, ch2, g);
//			System.out.println("Ending " + alg.getCurrentCores() + " "
//					+ unchosenVertices.size() + " " + unchosenVertices);
		}
	}

}
