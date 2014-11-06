package testgenerator;

import main.Utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.DummyAlgorithm;
import algorithm.MDSResultBackedByIntOpenHashSet;
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
 * <li>Name of dataset: ca-1.txt</li>
 * <li>Name of algorithm: greedyq</li>
 * </ul>
 * </p>
 * 
 * 
 * @author Friker
 * 
 */
public class DummyAlgorithmCorrectnessTest {
	private static final String DATASET_NAME = "ca-1.txt";
	private static final String ALGORITHM_NAME = "greedyq";
	private Dataset usableDataset = new Dataset();
	private Graph g = new CompactUndirectedGraph(usableDataset);
	private AbstractMDSAlgorithm algorithm = new DummyAlgorithm();
	private AbstractMDSResult result = new MDSResultBackedByIntOpenHashSet();
	private String resultFilename;
	private String datasetFilename;
	private AbstractMDSResult readResult = new MDSResultBackedByIntOpenHashSet();
	private Dataset readDataset;

	@Before
	public void setUp() throws Exception {
		datasetFilename = Utils.getDatasetFilename(DATASET_NAME);
		resultFilename = Utils.getResultFilename(ALGORITHM_NAME, DATASET_NAME);
		algorithm = Utils.getAlgorithm(ALGORITHM_NAME);
		readResult = Utils.importResult(resultFilename);
		readDataset = Utils.readEdgeListFromFile(datasetFilename);
		usableDataset = readDataset.deepCopy();
		g = new CompactUndirectedGraph(usableDataset);
	}

	@After
	public void tearDown() throws Exception {
		usableDataset = null;
		g = null;
		algorithm = null;
		result = null;
		resultFilename = null;
		datasetFilename = null;
		readResult = null;
		readDataset = null;
	}

	@Test
	public final void testMdsAlg() {
		result = algorithm.mdsAlg(g);
		Assert.assertNotNull(result);
		Assert.assertTrue(g.isMDS(result));
		Assert.assertEquals(readResult.size(), result.size());
		Assert.assertEquals(readResult, result);
	}

}
