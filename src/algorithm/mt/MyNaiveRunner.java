package algorithm.mt;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import datastructure.graph.Graph;

public class MyNaiveRunner implements Runnable {
	private MyNaiveAlgorithm alg;
	private ArrayList<Integer> unchosenVertices;
	private LinkedHashSet<Integer> chosenVertices;
	private Graph g;

	public MyNaiveRunner(MyNaiveAlgorithm alg,
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
			if (g.isMDS(chosenVertices)) {
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
			Thread t1 = alg.tryToCreateNewThread(unch1, ch1, g);
			if (t1 == null) {
				gms(unch1, ch1, g);
			} else {
//				System.out.println("Starting " + alg.getCurrentCores() + " "
//						+ unch1.size() + " " + unch1);
				t1.start();
			}
			Thread t2 = alg.tryToCreateNewThread(unch2, ch2, g);
			if (t2 == null) {
				gms(unch2, ch2, g);
			} else {
//				System.out.println("Starting " + alg.getCurrentCores() + " "
//						+ unch2.size() + " " + unch2);
				t2.start();
			}
			
			  if (t2 != null) { try { t2.join(); } catch (InterruptedException
			  e) { e.printStackTrace(); } } if (t1 != null) { try { t1.join();
			  } catch (InterruptedException e) { e.printStackTrace(); } }
			 
//			System.out.println("Ending " + alg.getCurrentCores() + " "
//					+ unchosenVertices.size() + " " + unchosenVertices);
		}
	}

}
