package algorithm.chapter7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm35 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private LinkedHashMap<Long, Algorithm35State> allVertices = new LinkedHashMap<>();
	private LinkedList<Algorithm35State> unfinishedVertices = new LinkedList<>();
	private LinkedHashSet<Long> S = new LinkedHashSet<>();
	public Object joinLock = new Object();

	public Algorithm35() {
	}

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		LinkedList<Long> times = new LinkedList<>();
		int bla = (int) Math.ceil(g.getNumberOfVertices() * 1.5);
		allVertices = new LinkedHashMap<>(bla);
		Long nv = g.getNumberOfVertices();
		for (Long v : g.getVertices()) {
			Algorithm35State state = new Algorithm35State(v, g);
			unfinishedVertices.add(state);
			allVertices.put(v, state);
		}
		times.addLast(System.currentTimeMillis() - start);
		Long nt = Math.min(nv, 1000);
		System.out.println(nv);

		// variant 1
		ArrayList<Thread> pool = new ArrayList<>();
		for (int i = 0; i < nt; i++) {
			Thread t = new Thread(new Algorithm35Task(this));
			pool.add(t);
		}
		times.addLast(System.currentTimeMillis() - start);
		prepTime = System.currentTimeMillis() - start;
		for (Thread t : pool) {
			t.start();
		}
		times.addLast(System.currentTimeMillis() - start);
		for (Thread t : pool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		times.addLast(System.currentTimeMillis() - start);

		/*
		 * // variant 2 Thread[] pool = new Thread[nv.intValue()]; for (int i =
		 * 0; i < nt; i++) { Thread t = new Thread(new Algorithm34Task(this));
		 * pool[i] = t; } times.addLast(System.currentTimeMillis() - start); for
		 * (int i = 0; i < nt; i++) { pool[i].start(); }
		 * times.addLast(System.currentTimeMillis() - start);
		 * 
		 * for (int i = 0; i < nt; i++) { try { pool[i].join(); } catch
		 * (InterruptedException e) { e.printStackTrace(); } }
		 * times.addLast(System.currentTimeMillis() - start);
		 */
		// System.out.println("Time elapsed: " + times);
		runTime = System.currentTimeMillis() - start;
		return S;
	}

	public LinkedHashMap<Long, Algorithm35State> getAllVertices() {
		return this.allVertices;
	}

	public Algorithm35State chooseNextVertex() {
		Algorithm35State state;
		synchronized (unfinishedVertices) {
			state = unfinishedVertices.pollFirst();
		}
		return state;
	}

	public void saveState(Algorithm35State state) {
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

	@Override
	public long getLastPrepTime() {
		return prepTime;
	}

	@Override
	public long getLastRunTime() {
		return runTime;
	}

}
