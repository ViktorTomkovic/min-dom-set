/**
 * 
 */
package testgenerator;

import main.Utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
import algorithm.basic.GreedyAlgorithm;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import datastructure.Dataset;
import datastructure.graph.CompactUndirectedGraph;
import datastructure.graph.Graph;

/**
 * @author viktort
 *
 */
public class GreedyAlgorithmBenchmarkTest {
	private Graph g = new CompactUndirectedGraph(new Dataset());
	private boolean isMDS = false;
	private GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
	private AbstractMDSResult result = new MDSResultBackedByIntOpenHashSet();

	// @Rule
	// public TestRule benchmarkRun = new BenchmarkRule();

	/*
	 * new IResultsConsumer() {
	 * 
	 * @Override public void accept(Result result) throws IOException { String s
	 * = String.format("Time average: %.3fs (+- %.3fs)",
	 * result.roundAverage.location, result.roundAverage.dispersion);
	 * System.out.println(s); } }
	 */

	@Before
	public void setUp() throws Exception {
		String filename = "data/ca-2.txt";
		Dataset dataset = Utils.readEdgeListFromFile(filename);
		g = new CompactUndirectedGraph(dataset);
		greedyAlgorithm = new GreedyAlgorithm();
	}

	@After
	public void tearDown() throws Exception {
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
