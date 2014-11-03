/**
 * 
 */
package test;

import java.io.IOException;
import java.util.LinkedHashSet;

import main.Utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import algorithm.AbstractMDSAlgorithm;
import algorithm.basic.GreedyAlgorithm;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.IResultsConsumer;
import com.carrotsearch.junitbenchmarks.Result;

import datastructure.Dataset;
import datastructure.graph.CompactUndirectedGraph;
import datastructure.graph.Graph;

/**
 * @author viktort
 *
 */
public class GreedyAlgorithmCorrectnessTest {
	private static Graph g;
	private static boolean isMDS;
	private static AbstractMDSAlgorithm greedyAlgorithm;
	private static LinkedHashSet<Integer> result;

	@Before
	public static void setUp() throws Exception {
		String filename = "data/ca-2.txt";
		Dataset dataset = Utils.readEdgeListFromFile(filename);
		g = new CompactUndirectedGraph(dataset);
		greedyAlgorithm = new GreedyAlgorithm();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		isMDS = g.isMDS(result);
		g = null;
		result = null;
		greedyAlgorithm = null;
	}

	/**
	 * Test method for
	 * {@link algorithm.basic.GreedyAlgorithm#mdsAlg(datastructure.graph.Graph)}
	 * .
	 */
	@Test
	public final void testMdsAlg() {
		result = g.getMDS(greedyAlgorithm);
		Assert.assertNotNull(result);
		Assert.assertTrue(g.isMDS(result));
		Assert.assertEquals(result.size(), 345);
	}

}
