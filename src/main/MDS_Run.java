package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import model.Edge;
import model.Graph;
import model.UndirectedGraph;
import algorithm.NaiveAlgorithm;
import algorithm.chapter7.Algorithm33;
import algorithm.chapter7.Algorithm34;
import algorithm.chapter7.Algorithm35;
import algorithm.fomin.AlgorithmFNaive;

public class MDS_Run {

	/**
	 * @param args
	 *            filename of processed graph
	 * 
	 *            TODO make optional arguments
	 */
	public static void main(String[] args) {
		for (String s : args)
			System.out.println(s);
		Graph g = new UndirectedGraph();
		if (args.length < 1) {
			System.out
					.println("Please use the first argument as input filename.");
			return;
		}
		String filename = args[0];
		try {
			ArrayList<Edge> edgeList = new ArrayList<>();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = new String();
			while ((line = br.readLine()) != null) {
				String[] nos = line.split(" ");
				/*
				 * for (String ns : nos) System.out.print(ns + " ");
				 * System.out.println("");
				 */
				if (nos.length == 2) {
					Edge e = new Edge(Long.parseLong(nos[0]),
							Long.parseLong(nos[1]));
					edgeList.add(e);
				}
			}
			br.close();
			g = new UndirectedGraph(edgeList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((g.getEdges().size() == 0) && (g.getNumberOfVertices() == 0)) {
			System.out
					.println("The graph has no vertices or does not load correctly.");
		}
		String algorithm = "";
		if (args.length < 2) {
			System.out
					.println("You can use the second argument to choose an algorithm.");
		} else {
			algorithm = args[1];
		}
		LinkedHashSet<Long> mds = new LinkedHashSet<>();
		long start = System.currentTimeMillis();
		if (algorithm.compareTo("") == 0) {
			System.out
					.println("You should specify algorithm you want to use. Naive algorithm is used.");
			mds = g.getMDS(new NaiveAlgorithm());
		} else if (algorithm.compareTo("naive") == 0) {
			mds = g.getMDS(new NaiveAlgorithm());
		} else if (algorithm.compareTo("ch7alg33") == 0) {
			mds = g.getMDS(new Algorithm33());
		} else if (algorithm.compareTo("ch7alg34") == 0) {
			mds = g.getMDS(new Algorithm34());
		} else if (algorithm.compareTo("ch7alg35") == 0) {
			mds = g.getMDS(new Algorithm35());
		} else if (algorithm.compareTo("fnaive") == 0) {
			mds = g.getMDS(new AlgorithmFNaive());
		}
		System.out.println(mds.size() + " " + mds);
		System.out.println("The set is " + (g.isMDS(mds) ? "" : "not ")
				+ "a dominating set.");
		System.out.println("Time elapsed: "
				+ (System.currentTimeMillis() - start) + "ms");
	}

}
