/**
 * 
 */
package test;

import java.util.Collections;
import java.util.LinkedHashSet;

import main.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import algorithm.AbstractMDSAlgorithm;
import algorithm.basic.GreedyAlgorithm;
import algorithm.basic.GreedyQuickAlgorithm;
import algorithm.flower.FlowerUniqueAlgorithm;
import datastructure.Dataset;
import datastructure.graph.CompactUndirectedGraph;
import datastructure.graph.Graph;

/**
 * <p>
 * This test is generated to check correctness of specific algorithm on a
 * specific dataset. It is generated with parameters:
 * </p>
 * <p>
 * <ul>
 * <li>Class of algorithm: GreedyAlgorithm</li>
 * <li>Path to dataset: data/ca-1.txt</li>
 * <li>Path to file with correct result: results/greedy.mdsres</li>
 * </ul>
 * </p>
 * 
 * 
 * @author viktort
 *
 */
public class GreedyAlgorithmCorrectnessTest {
	private Graph g;
	private AbstractMDSAlgorithm algorithm;
	private LinkedHashSet<Integer> result;
	private final String resultFilename = "results/greedyq/result-ca-1.txt";
	private LinkedHashSet<Integer> readResult;

	@Before
	public void setUp() throws Exception {
		String filename = "data/ca-1.txt";
		Dataset dataset = Utils.readEdgeListFromFile(filename);
		g = new CompactUndirectedGraph(dataset);
		algorithm = new GreedyQuickAlgorithm();
		readResult = Utils.importResult(resultFilename);
	}

	@After
	public void tearDown() throws Exception {
		g = null;
		result = null;
		algorithm = null;
	}

	/**
	 * Test method for
	 * {@link algorithm.basic.GreedyAlgorithm#mdsAlg(datastructure.graph.Graph)}
	 * .
	 */
	@Test
	public final void testMdsAlg() {
		result = g.getMDS(algorithm);
		Assert.assertNotNull(result);
		Assert.assertTrue(g.isMDS(result));
		Assert.assertEquals(1582, result.size());
		Assert.assertEquals(result, readResult);
		System.out.println(readResult.size());
	}

}
