package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractMDSAlgorithm;

public class Algorithm342 implements AbstractMDSAlgorithm {
	private long prepTime = -1L;
	private long runTime = -1L;
	public volatile static LinkedHashSet<Long> S = new LinkedHashSet<>();
	public volatile static LinkedHashSet<Long> G = new LinkedHashSet<>();
	public volatile static LinkedHashMap<Long, Algorithm34Vertex> instances = new LinkedHashMap<>();
	public volatile static Object lock = new Object();

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {
		long start = System.currentTimeMillis();
		for (Long v : g.getVertices()) {
			Algorithm34Vertex instance = new Algorithm34Vertex(g, v, S, G,
					instances, lock);
			instances.put(v, instance);
		}
		prepTime = System.currentTimeMillis() - start;
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
		runTime = System.currentTimeMillis() - start;
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
