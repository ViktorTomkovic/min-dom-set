package algorithm.chapter7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm34 implements AbstractMDSAlgorithm {
	private LinkedHashMap<Long, Algorithm34State> allVertices = new LinkedHashMap<>();
	private LinkedList<Algorithm34State> unfinishedVertices = new LinkedList<>();
	private LinkedHashSet<Long> S = new LinkedHashSet<>();
	public Object joinLock = new Object();
	public Object waitForStart = new Object();

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		Long start = System.currentTimeMillis();
		LinkedList<Long> times = new LinkedList<>();
		int bla = (int) Math.ceil(g.getNumberOfVertices() * 1.5);
		allVertices = new LinkedHashMap<>(bla);
		Long nv = g.getNumberOfVertices();
		for (Long v : g.getVertices()) {
			Algorithm34State state = new Algorithm34State(v, g);
			unfinishedVertices.add(state);
			allVertices.put(v, state);
		}
		times.addLast(System.currentTimeMillis() - start);
		Long nt = Math.min(nv, 1000);
		System.out.println(nv);

		// variant 1
		ArrayList<Thread> pool = new ArrayList<>();
		for (int i = 0; i < nt; i++) {
			Thread t = new Thread(new Algorithm34Task(this));
			pool.add(t);
		}
		times.addLast(System.currentTimeMillis() - start);
		for (Thread t : pool) {
			t.start();
		}
		/*
		 * times.addLast(System.currentTimeMillis() - start); try {
		 * Thread.sleep(50); } catch (InterruptedException e1) {
		 * e1.printStackTrace(); } synchronized (waitForStart) {
		 * waitForStart.notifyAll(); }
		 */
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
		System.out.println("Time elapsed: " + times);
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
