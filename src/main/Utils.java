package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import algorithm.AbstractMDSAlgorithm;
import algorithm.AbstractMDSResult;
import algorithm.MDSResultBackedByIntOpenHashSet;
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

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import datastructure.Dataset;

public class Utils {
	public static final int LONG_OFFSET = 32;
	public static final Integer NANOS_IN_MILI = 1000000;
	private static final int TEXT_SPLITTING_CONSTANT = 100;
	public static final String DATASET_DIRECTORY = "data";
	public static final String RESULTS_DIRECTORY = "results";

	private Utils() {
	}

	public static String largeIntArrayToString(int[] array) {
		StringBuilder sb = new StringBuilder();
		if (array.length < TEXT_SPLITTING_CONSTANT) {
			sb.append(Arrays.toString(array));
		} else {
			sb.append("[");
			int a = 0;
			for (int value : array) {
				a++;
				if (a < TEXT_SPLITTING_CONSTANT
						|| a > array.length - TEXT_SPLITTING_CONSTANT / 3) {
					sb.append(value);
					sb.append(", ");
				} else if (a == TEXT_SPLITTING_CONSTANT) {
					sb.append("    .    .    .    ");
				}
			}
			sb.append("]");
		}
		return sb.toString();
	}

	public static <T> String largeCollectionToString(Collection<T> collection) {
		StringBuilder sb = new StringBuilder();
		if (collection.size() < TEXT_SPLITTING_CONSTANT) {
			sb.append(collection.toString());
		} else {
			sb.append("[");
			int a = 0;
			for (T value : collection) {
				a++;
				if (a < TEXT_SPLITTING_CONSTANT
						|| a > collection.size() - TEXT_SPLITTING_CONSTANT / 3) {
					sb.append(value);
					sb.append(", ");
				} else if (a == TEXT_SPLITTING_CONSTANT) {
					sb.append("    .    .    .    ");
				}
			}
			sb.append("]");
		}
		return sb.toString();
	}

	public static <T> String largeCollectionToString(IntOpenHashSet collection) {
		StringBuilder sb = new StringBuilder();
		if (collection.size() < TEXT_SPLITTING_CONSTANT) {
			sb.append(collection.toString());
		} else {
			sb.append("[");
			int a = 0;
			for (IntCursor cursor : collection) {
				a++;
				if (a < TEXT_SPLITTING_CONSTANT
						|| a > collection.size() - TEXT_SPLITTING_CONSTANT / 3) {
					sb.append(cursor.value);
					sb.append(", ");
				} else if (a == TEXT_SPLITTING_CONSTANT) {
					sb.append("    .    .    .    ");
				}
			}
			sb.append("]");
		}
		return sb.toString();
	}

	public static Dataset readEdgeListFromFile(String filename) {
		Dataset dataset = new Dataset();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			// ArrayList<Edge> edgeList = new ArrayList<>();
			int sizeOfList = 16;
			int firstFree = 0;
			int[] edgesFrom = new int[sizeOfList];
			int[] edgesTo = new int[sizeOfList];
			String line = br.readLine();
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				int a = -1;
				int b = -1;
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
					edgesFrom[firstFree] = a;
					edgesTo[firstFree] = b;
					firstFree = firstFree + 1;
					if (firstFree == sizeOfList) {
						sizeOfList = sizeOfList << 1;
						int[] tempEdgeList = new int[sizeOfList];
						System.arraycopy(edgesFrom, 0, tempEdgeList, 0,
								firstFree);
						edgesFrom = tempEdgeList;
						tempEdgeList = new int[sizeOfList];
						System.arraycopy(edgesTo, 0, tempEdgeList, 0, firstFree);
						edgesTo = tempEdgeList;
					}
				}
				line = br.readLine();
			}
			dataset.setRawEdges(edgesFrom, edgesTo, firstFree);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataset;
	}

	public static AbstractMDSResult importResult(String filename) {
		MDSResultBackedByIntOpenHashSet result = new MDSResultBackedByIntOpenHashSet();
		IntOpenHashSet resultData = new IntOpenHashSet();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			if (line == null) {
				throw new RuntimeException("Bad format of input file.");
			}
			int size = Integer.parseInt(line);
			resultData = new IntOpenHashSet((int) Math.ceil(size / 0.75));
			for (int i = 0; i < size; i++) {
				line = br.readLine();
				if (line == null) {
					throw new RuntimeException("Bad format of input file.");
				}
				resultData.add(Integer.parseInt(line));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result.setResult(resultData);
		return result;
	}

	public static void exportResult(String filename,
			LinkedHashSet<Integer> algResult) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename)))) {
			int size = algResult.size();
			writer.write(String.valueOf(size));
			writer.newLine();
			for (Integer i : algResult) {
				writer.write(i.toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportResult(String filename,
			AbstractMDSResult algResult) {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename)))) {
			int size = algResult.size();
			writer.write(String.valueOf(size));
			writer.newLine();
			for (IntCursor i : algResult.getIterableStructure()) {
				writer.write(i.value);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createAndCheckDirectory(String dirname) {
		File resultDir = new File(dirname);
		if (!resultDir.exists()) {
			boolean wasDirCreated = resultDir.mkdirs();
			if (!wasDirCreated) {
				throw new RuntimeException("Directory was not created.");
			}
		}
	}

	public static AbstractMDSAlgorithm getAlgorithm(String name) {
		AbstractMDSAlgorithm algorithm;
		if (name.compareTo("") == 0) {
			throw new IllegalArgumentException(
					"You should specify algorithm you want to use.");
		} else if (name.compareTo("naive") == 0) {
			algorithm = new NaiveAlgorithm();
		} else if (name.compareTo("mynaive") == 0) {
			algorithm = new MyNaiveAlgorithm();
		} else if (name.compareTo("mynaive2") == 0) {
			algorithm = new MyNaive2Algorithm();
		} else if (name.compareTo("mynaive3") == 0) {
			algorithm = new MyNaive3Algorithm();
		} else if (name.compareTo("greedy") == 0) {
			algorithm = new GreedyAlgorithm();
		} else if (name.compareTo("greedyq") == 0) {
			algorithm = new GreedyQuickAlgorithm();
		} else if (name.compareTo("ch7alg33") == 0) {
			algorithm = new Algorithm33();
		} else if (name.compareTo("ch7alg34") == 0) {
			algorithm = new Algorithm34();
		} else if (name.compareTo("ch7alg34OT") == 0) {
			algorithm = new Algorithm34OneThread();
		} else if (name.compareTo("ch7alg35") == 0) {
			algorithm = new Algorithm35();
		} else if (name.compareTo("ch7alg35OT") == 0) {
			algorithm = new Algorithm35OneThread();
		} else if (name.compareTo("fnaive") == 0) {
			algorithm = new AlgorithmFNaive();
		} else if (name.compareTo("fproper") == 0) {
			algorithm = new AlgorithmFProper();
		} else if (name.compareTo("floweru") == 0) {
			algorithm = new FlowerUniqueAlgorithm();
		} else {
			throw new IllegalArgumentException("Algorithm is not implemented.");
		}
		return algorithm;
	}

	public static String getAlgorithmFullName(String name) {
		String fullName;
		if (name.compareTo("naive") == 0) {
			fullName = "NaiveAlgorithm";
		} else if (name.compareTo("mynaive") == 0) {
			fullName = "MyNaiveAlgorithm";
		} else if (name.compareTo("mynaive2") == 0) {
			fullName = "MyNaive2Algorithm";
		} else if (name.compareTo("mynaive3") == 0) {
			fullName = "MyNaive3Algorithm";
		} else if (name.compareTo("greedy") == 0) {
			fullName = "GreedyAlgorithm";
		} else if (name.compareTo("greedyq") == 0) {
			fullName = "GreedyQuickAlgorithm";
		} else if (name.compareTo("ch7alg33") == 0) {
			fullName = "Algorithm33";
		} else if (name.compareTo("ch7alg34") == 0) {
			fullName = "Algorithm34";
		} else if (name.compareTo("ch7alg34OT") == 0) {
			fullName = "Algorithm34OneThread";
		} else if (name.compareTo("ch7alg35") == 0) {
			fullName = "Algorithm35";
		} else if (name.compareTo("ch7alg35OT") == 0) {
			fullName = "Algorithm35OneThread";
		} else if (name.compareTo("fnaive") == 0) {
			fullName = "AlgorithmFNaive";
		} else if (name.compareTo("fproper") == 0) {
			fullName = "AlgorithmFProper";
		} else if (name.compareTo("floweru") == 0) {
			fullName = "FlowerUniqueAlgorithm";
		} else {
			throw new IllegalArgumentException("Algorithm has not a full name.");
		}
		return fullName;
	}

	public static String getDatasetFilename(String datasetName) {
		return getDatasetFilename(DATASET_DIRECTORY, datasetName);
	}

	public static String getDatasetFilename(String datasetFolder,
			String datasetName) {
		StringBuilder datasetFilenameBuilder = new StringBuilder();
		datasetFilenameBuilder.append(datasetFolder);
		Utils.createAndCheckDirectory(datasetFilenameBuilder.toString());
		datasetFilenameBuilder.append('/');
		datasetFilenameBuilder.append(datasetName);
		return datasetFilenameBuilder.toString();
	}

	public static String getResultFilename(String algorithmName,
			String datasetName) {
		return getResultFilename(RESULTS_DIRECTORY, algorithmName, datasetName);
	}

	public static String getResultFilename(String resultFolder,
			String algorithmName, String datasetName) {
		StringBuilder resultFilenameBuilder = new StringBuilder();
		resultFilenameBuilder.append(Utils.RESULTS_DIRECTORY);
		Utils.createAndCheckDirectory(resultFilenameBuilder.toString());
		resultFilenameBuilder.append('/');
		resultFilenameBuilder.append(algorithmName);
		Utils.createAndCheckDirectory(resultFilenameBuilder.toString());
		resultFilenameBuilder.append("/result-");
		resultFilenameBuilder.append(datasetName);
		return resultFilenameBuilder.toString();
	}
}
