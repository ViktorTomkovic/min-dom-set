package algorithm.chapter7;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import datastructure.graph.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm342 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	public volatile static LinkedHashSet<Integer> S = new LinkedHashSet<>();
	public volatile static LinkedHashSet<Integer> G = new LinkedHashSet<>();
	public volatile static LinkedHashMap<Integer, Algorithm34Vertex> instances = new LinkedHashMap<>();
	public volatile static Object lock = new Object();

	@Override
	public LinkedHashSet<Integer> mdsAlg(Graph g) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start = bean.getCurrentThreadCpuTime();
		for (Integer v : g.getVertices()) {
			Algorithm34Vertex instance = new Algorithm34Vertex(g, v, S, G,
					instances, lock);
			instances.put(v, instance);
		}
		prepTime = bean.getCurrentThreadCpuTime() - start;
		for (Algorithm34Vertex a : instances.values()) {
			a.start();
		}

		for (Algorithm34Vertex a : instances.values()) {
			try {
				a.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		runTime = bean.getCurrentThreadCpuTime() - start;
		return S;
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
