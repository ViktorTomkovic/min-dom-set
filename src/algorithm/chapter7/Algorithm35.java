package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;

// TODO prepisat na HPPC

public class Algorithm35 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	private LinkedHashMap<Integer, Algorithm35State> allVertices = new LinkedHashMap<>();
	private LinkedList<Algorithm35State> unfinishedVertices = new LinkedList<>();
	private LinkedHashSet<Integer> S = new LinkedHashSet<>();
	public final Object joinLock = new Object();

	public Algorithm35() {
	}

	@Override
	public AbstractMDSResult mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		LinkedList<Long> times = new LinkedList<>();
		int bla = (int) Math.ceil(g.getNumberOfVertices() * 1.5);
		allVertices = new LinkedHashMap<>(bla);
		Integer nv = g.getNumberOfVertices();
		for (IntCursor v : g.getVertices()) {
			Algorithm35State state = new Algorithm35State(v.value, g);
			unfinishedVertices.add(state);
			allVertices.put(v.value, state);
		}
		times.addLast(bean.getCurrentThreadCpuTime() - start);
		Integer nt = Math.min(nv, 1000);
		System.out.println(nv);

		// variant 1
		ArrayList<Thread> pool = new ArrayList<>();
		for (int i = 0; i < nt; i++) {
			Thread t = new Thread(new Algorithm35Task(this));
			pool.add(t);
		}
		times.addLast(bean.getCurrentThreadCpuTime() - start);
		prepTime = bean.getCurrentThreadCpuTime() - start;
		for (Thread t : pool) {
			t.start();
		}
		times.addLast(bean.getCurrentThreadCpuTime() - start);
		for (Thread t : pool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		times.addLast(bean.getCurrentThreadCpuTime() - start);

		/*
		 * // variant 2 Thread[] pool = new Thread[nv.intValue()]; for (int i =
		 * 0; i < nt; i++) { Thread t = new Thread(new Algorithm34Task(this));
		 * pool[i] = t; } times.addLast(bean.getCurrentThreadCpuTime() - start); for
		 * (int i = 0; i < nt; i++) { pool[i].start(); }
		 * times.addLast(bean.getCurrentThreadCpuTime() - start);
		 * 
		 * for (int i = 0; i < nt; i++) { try { pool[i].join(); } catch
		 * (InterruptedException e) { e.printStackTrace(); } }
		 * times.addLast(bean.getCurrentThreadCpuTime() - start);
		 */
		// System.out.println("Time elapsed: " + times);
		runTime = bean.getCurrentThreadCpuTime() - start;
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet(S.size());
		for (Integer i : S) {
			resultData.add(i);
		}
		result.setResult(resultData);
		return result;
	}

	public LinkedHashMap<Integer, Algorithm35State> getAllVertices() {
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

	public void joinS(Integer v) {
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

	public Object getJoinLock() {
		return joinLock;
	}
}
