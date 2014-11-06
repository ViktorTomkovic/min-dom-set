/**
 * 
 */
package testgenerator;

import java.util.LinkedHashSet;

import main.Utils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import algorithm.basic.GreedyAlgorithm;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import datastructure.Dataset;
import datastructure.graph.CompactUndirectedGraph;
import datastructure.graph.Graph;

/**
 * @author viktort
 *
 */
public class GreedyAlgorithmBenchmarkTest {
	private static Graph g;
	private static boolean isMDS;
	private static GreedyAlgorithm greedyAlgorithm;
	private static LinkedHashSet<Integer> result;

	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();

	/*
	 * new IResultsConsumer() {
	 * 
	 * @Override public void accept(Result result) throws IOException { // TODO
	 * Auto-generated method stub String s =
	 * String.format("Time average: %.3fs (+- %.3fs)",
	 * result.roundAverage.location, result.roundAverage.dispersion);
	 * System.out.println(s); } }
	 */

	@BeforeClass
	public static void setUp() throws Exception {
		String filename = "data/ca-2.txt";
		Dataset dataset = Utils.readEdgeListFromFile(filename);
		g = new CompactUndirectedGraph(dataset);
		greedyAlgorithm = new GreedyAlgorithm();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		isMDS = g.isMDS(result);
		Assert.assertTrue(isMDS);
		g = null;
		result = null;
		greedyAlgorithm = null;
	}

	/**
	 * Test method for
	 * {@link algorithm.basic.GreedyAlgorithm#mdsAlg(datastructure.graph.Graph)}
	 * .
	 */
	@BenchmarkOptions(benchmarkRounds = 15, warmupRounds = 5)
	@Test
	public final void testMdsAlg() {
		result = g.getMDS(greedyAlgorithm);
		Assert.assertNotNull(result);
	}

}
