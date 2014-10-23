package algorithm.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import main.Utils;
import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class MyNaiveAlgorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;
	private int currentCores;
	private int maxCores;
	private LinkedHashSet<Long> result;
	private int currentBest;
	private Object isWriting = new Object();
	private Object isCreatingNewThread = new Object();

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
				Thread t = new Thread(new MyNaiveRunner(this, unchosenVertices,
						chosenVertices, g));
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

	public MyNaiveAlgorithm() {
		currentCores = 0;
		maxCores = Runtime.getRuntime().availableProcessors();
		result = new LinkedHashSet<>();
		currentBest = Integer.MAX_VALUE;
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		ArrayList<Long> input = new ArrayList<Long>(g.getVertices());
		System.out.println(Utils.largeCollectionToString(input));
		Collections.sort(input, new algorithm.GreaterByN1BComparator(g));
		System.out.println(Utils.largeCollectionToString(input));
		MyNaiveRunner r = new MyNaiveRunner(this, input,
				new LinkedHashSet<Long>(), g);
		currentCores++;
		Thread t = new Thread(r);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
