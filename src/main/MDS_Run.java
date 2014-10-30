package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import model.Edge;
import model.Graph;
import model.UndirectedGraph;
import algorithm.AbstractMDSAlgorithm;
import algorithm.basic.GreedyAlgorithm;
import algorithm.basic.GreedyQuickAlgorithm;
import algorithm.basic.NaiveAlgorithm;
import algorithm.chapter7.Algorithm33;
import algorithm.chapter7.Algorithm34;
import algorithm.chapter7.Algorithm34OneThread;
import algorithm.chapter7.Algorithm35;
import algorithm.chapter7.Algorithm35OneThread;
import algorithm.flower.FlowerUniqueAlgorithm;
import algorithm.fomin.AlgorithmFNaive;
import algorithm.fomin.AlgorithmFProper;
import algorithm.mt.MyNaive2Algorithm;
import algorithm.mt.MyNaive3Algorithm;
import algorithm.mt.MyNaiveAlgorithm;

public class MDS_Run {
	public static final String MY_ARGS = "data/ca-1.txt greedy";
	public static final Integer NANOS_IN_MILI = 1000000;

	/**
	 * @param args
	 *            filename of processed graph
	 */
	public static void main(String[] args) {
		if (MY_ARGS != null && !MY_ARGS.equals("")) {
			args = MY_ARGS.split(" ");
		}
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
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				Integer a = -1;
				Integer b = -1;
				int count = 0;
				if (st.hasMoreTokens()) {
					try {
						a = Integer.parseInt(st.nextToken());
						count++;
					} catch (NumberFormatException e) {
					}
				}
				if (st.hasMoreTokens()) {
					try {
						b = Integer.parseInt(st.nextToken());
						count++;
					} catch (NumberFormatException e) {
					}
				}
				if (count == 2) {
					Edge e = new Edge(a, b);
					edgeList.add(e);
				}
			}
			br.close();
			g = new UndirectedGraph(new LinkedHashSet<Integer>(), edgeList);
			System.out.println("Graph loaded - vertices: "
					+ g.getNumberOfVertices() + ", edges: "
					+ g.getEdges().size() + ".");
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
		LinkedHashSet<Integer> mds; // = new LinkedHashSet<>();
		AbstractMDSAlgorithm alg;
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long start2 = System.nanoTime();
		long start = bean.getCurrentThreadCpuTime();
		if (algorithm.compareTo("") == 0) {
			throw new IllegalArgumentException(
					"You should specify algorithm you want to use. Naive algorithm is used.");
		} else if (algorithm.compareTo("naive") == 0) {
			alg = new NaiveAlgorithm();
		} else if (algorithm.compareTo("mynaive") == 0) {
			alg = new MyNaiveAlgorithm();
		} else if (algorithm.compareTo("mynaive2") == 0) {
			alg = new MyNaive2Algorithm();
		} else if (algorithm.compareTo("mynaive3") == 0) {
			alg = new MyNaive3Algorithm();
		} else if (algorithm.compareTo("greedy") == 0) {
			alg = new GreedyAlgorithm();
		} else if (algorithm.compareTo("greedyq") == 0) {
			alg = new GreedyQuickAlgorithm();
		} else if (algorithm.compareTo("ch7alg33") == 0) {
			alg = new Algorithm33();
		} else if (algorithm.compareTo("ch7alg34") == 0) {
			alg = new Algorithm34();
		} else if (algorithm.compareTo("ch7alg34OT") == 0) {
			alg = new Algorithm34OneThread();
		} else if (algorithm.compareTo("ch7alg35") == 0) {
			alg = new Algorithm35();
		} else if (algorithm.compareTo("ch7alg35OT") == 0) {
			alg = new Algorithm35OneThread();
		} else if (algorithm.compareTo("fnaive") == 0) {
			alg = new AlgorithmFNaive();
		} else if (algorithm.compareTo("fproper") == 0) {
			alg = new AlgorithmFProper();
		} else if (algorithm.compareTo("floweru") == 0) {
			alg = new FlowerUniqueAlgorithm();
		} else {
			throw new IllegalArgumentException("Algorithm is not implemented.");
		}
		assert alg != null : "Algorith is null!";
		mds = g.getMDS(alg);
		long elapsed = (bean.getCurrentThreadCpuTime() - start) / NANOS_IN_MILI;
		long elapsed2 = (System.nanoTime() - start2) / NANOS_IN_MILI;
		System.out.println("Graph ...... - vertices: "
				+ g.getNumberOfVertices() + ", edges: " + g.getEdges().size()
				+ ".");
		System.out.println(mds.size() + " "
				+ Utils.largeCollectionToString(mds));
		System.out.println("The set is " + (g.isMDS(mds) ? "" : "not ")
				+ "a dominating set.");
		StringBuilder sb = new StringBuilder();
		sb.append("Time elapsed: ");
		sb.append(elapsed);
		sb.append("ms. \t(");
		sb.append(alg.getLastPrepTime() / NANOS_IN_MILI);
		sb.append("ms + ");
		sb.append((alg.getLastRunTime() - alg.getLastPrepTime())
				/ NANOS_IN_MILI);
		sb.append("ms)\t\t\t");
		sb.append("Wall time: ");
		sb.append(elapsed2);
		sb.append("ms.");
		System.out.println(sb.toString());
	}
}
