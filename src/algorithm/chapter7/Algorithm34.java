package algorithm.chapter7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import model.Graph;
import algorithm.AbstractAlgorithm;

public class Algorithm34 implements AbstractAlgorithm {
	private LinkedHashMap<Long, Algorithm34State> allVertices;
	private LinkedList<Algorithm34State> unfinishedVertices;
	private LinkedHashSet<Long> S;
	public Object joinLock = new Object();

	public Algorithm34() {
		unfinishedVertices = new LinkedList<>();
		allVertices = new LinkedHashMap<>();
		S = new LinkedHashSet<>();
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		Long nv = g.getNumberOfVertices();
		for (Long v : g.getVertices()) {
			Algorithm34State state = new Algorithm34State(v, g);
			unfinishedVertices.addLast(state);
			allVertices.put(v, state);
		}
		Long nt = Math.min(nv, 50);
		ArrayList<Thread> pool = new ArrayList<>(50);
		for (int i = 0; i < nt; i++) {
			Thread t = new Thread(new Algorithm34Task(this));
			pool.add(i, t);
		}
		for (int i = 0; i < nt; i++) {
			pool.get(i).start();
		}
		for (int i = 0; i < nt; i++) {
			try {
				pool.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return S;
	}

	public LinkedHashMap<Long, Algorithm34State> getAllVertices() {
		return this.allVertices;
	}

	public Algorithm34State chooseNextVertex() {
		Algorithm34State state;
		synchronized (unfinishedVertices) {
			state = unfinishedVertices.pollFirst();
		}
		return state;
	}

	public void saveState(Algorithm34State state) {
		synchronized (unfinishedVertices) {
			unfinishedVertices.addLast(state);
		}
		return;
	}

	public void joinS(Long v) {
		synchronized (S) {
			S.add(v);
		}
		return;
	}

}
