package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.Edge;
import model.Graph;
import model.UndirectedGraph;
import algorithm.NaiveAlgorithm;

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
		String filename = args[0];
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			ArrayList<Edge> edgeList = new ArrayList<>();
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
		System.out.println(g.getMDS(new NaiveAlgorithm()));
	}

}
