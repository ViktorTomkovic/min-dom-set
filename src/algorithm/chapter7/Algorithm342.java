package algorithm.chapter7;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import model.Graph;
import algorithm.AbstractAlgorithm;

public class Algorithm342 implements AbstractAlgorithm {
	public volatile static LinkedHashSet<Long> S = new LinkedHashSet<>();
	public volatile static LinkedHashSet<Long> G = new LinkedHashSet<>();
	public volatile static LinkedHashMap<Long, Algorithm34Vertex> instances = new LinkedHashMap<>();
	public volatile static Object lock = new Object();

	@Override
	public LinkedHashSet<Long> mdsAlg(Graph g) {

		for (Long v : g.getVertices()) {
			Algorithm34Vertex instance = new Algorithm34Vertex(g, v, S, G,
					instances, lock);
			instances.put(v, instance);
		}

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

		System.out.println();
		System.out.println("Koniec.");
		return S;
	}

}
