package algorithm.mt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;

import main.Utils;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import datastructure.graph.Graph;

public class MyNaiveAlgorithm implements AbstractMDSAlgorithm {
	private long runTime = -1L;
	private int currentCores;
	private int maxCores;
	private LinkedHashSet<Integer> result;
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

	public void tryToSetResult(LinkedHashSet<Integer> newBest) {
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

	public Thread tryToCreateNewThread(ArrayList<Integer> unchosenVertices,
			LinkedHashSet<Integer> chosenVertices, Graph g) {
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
	public AbstractMDSResult mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		ArrayList<Integer> input = new ArrayList<Integer>(g.getVertices());
		System.out.println(Utils.largeCollectionToString(input));
		Collections.sort(input, new algorithm.GreaterByN1BComparator(g));
		System.out.println(Utils.largeCollectionToString(input));
		MyNaiveRunner r = new MyNaiveRunner(this, input,
				new LinkedHashSet<Integer>(), g);
		currentCores++;
		Thread t = new Thread(r);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		runTime = System.currentTimeMillis() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet(this.result.size());
		for (Integer i : this.result) {
			resultData.add(i);
		}
		result.setResult(resultData);
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
