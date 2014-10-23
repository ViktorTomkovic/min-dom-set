package algorithm.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import main.Utils;
import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class MyNaive2Algorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;
	private int currentCores;
	private int maxCores;
	private LinkedHashSet<Long> result;
	private int currentBest;
	private Object isWriting = new Object();
	private Object isCreatingNewThread = new Object();
	private ArrayList<Thread> pool;

	public int getCurrentCores() {
		int result;
		synchronized (isCreatingNewThread) {
			result = currentCores;
		}
		return result;
	}

	public int getMaxCores() {
		return maxCores;
	}

	public int getCurrentBest() {
		int result;
		synchronized (isWriting) {
			result = currentBest;
		}
		return result;
	}

	public void tryToSetResult(LinkedHashSet<Long> newBest) {
		synchronized (isWriting) {
			if (newBest.size() < this.currentBest) {
				this.result = new LinkedHashSet<>(newBest);
				this.currentBest = newBest.size();
				/*
				 * System.out.println("b  " +
				 * Utils.LargeCollectionToString(newBest));
				 */
			}
		}
	}

	public Thread tryToCreateNewThread(ArrayList<Long> unchosenVertices,
			LinkedHashSet<Long> chosenVertices, Graph g) {
		Thread result = null;
		synchronized (isCreatingNewThread) {
			if (currentCores < maxCores) {
				currentCores++;
				Thread t = new Thread(new MyNaive2Runner(this,
						unchosenVertices, chosenVertices, g));
				result = t;
			} else {
				result = null;
			}
		}
		return result;
	}

	public void decCC() {
		synchronized (isCreatingNewThread) {
			currentCores--;
		}
	}

	public void fillPool(ArrayList<Thread> pool, ArrayList<Long> unch,
			LinkedHashSet<Long> ch, Graph g, ArrayList<Long> unchP) {
		if (unch.size() == 0) {
			Thread t = new Thread(new MyNaive2Runner(this, unchP, ch, g));
			pool.add(t);
			currentCores++;
			return;
		}
		
		Long v = unch.get(unch.size() - 1);
		unch.remove(unch.size() - 1);

		LinkedHashSet<Long> nch2 = new LinkedHashSet<>(ch);
		nch2.add(v);

		fillPool(pool, unch, ch, g, unchP);
		fillPool(pool, unch, nch2, g, unchP);

		unch.add(v);
		return;
	}

	public MyNaive2Algorithm() {
		currentCores = 0;
		maxCores = Runtime.getRuntime().availableProcessors();
		result = new LinkedHashSet<>();
		currentBest = Integer.MAX_VALUE;
		pool = new ArrayList<>();
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		ArrayList<Long> vertices = new ArrayList<Long>(g.getVertices());
		System.out.println(Utils.largeCollectionToString(vertices));
		Collections.sort(vertices, new algorithm.GreaterByN1BComparator(g));
		System.out.println(Utils.largeCollectionToString(vertices));

		long height = Math.round(Math.log(maxCores) / Math.log(2));
		LinkedList<Long> ver2 = new LinkedList<>();
		for (int i = 0; i < height; i++) {
			ver2.addFirst(vertices.get(vertices.size() - 1));
			vertices.remove(vertices.size() - 1);
		}
		ArrayList<Long> ver3 = new ArrayList<>(ver2);
		System.out.println(Utils.largeCollectionToString(ver3));

		fillPool(pool, ver3, new LinkedHashSet<Long>(), g, vertices);

		for (Thread t : pool) {
			t.start();
		}

		for (Thread t : pool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		runTime = System.currentTimeMillis() - start;
		return result;
	}

	@Override
	public long getLastPrepTime() {
		return 0;
	}

	@Override
	public long getLastRunTime() {
		return runTime;
	}
}
