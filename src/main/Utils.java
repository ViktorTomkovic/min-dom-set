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

import datastructure.Dataset;

public class Utils {
	public static final int LONG_OFFSET = 32;
	public static final Integer NANOS_IN_MILI = 1000000;
	private static final int TEXT_SPLITTING_CONSTANT = 100;

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

	public static LinkedHashSet<Integer> importResult(String filename) {
		LinkedHashSet<Integer> result = new LinkedHashSet<Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			if (line == null) {
				throw new RuntimeException("Bad format of input file.");
			}
			int size = Integer.parseInt(line);
			result = new LinkedHashSet<Integer>((int) Math.ceil(size / 0.75));
			for (int i = 0; i < size; i++) {
				line = br.readLine();
				if (line == null) {
					throw new RuntimeException("Bad format of input file.");
				}
				result.add(Integer.valueOf(line));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void exportResult(String filename, LinkedHashSet<Integer> algResult) {
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
	
	public static void createAndCheckDirectory(String dirname) {
		File resultDir = new File(dirname);
		if (!resultDir.exists()) {
			boolean wasDirCreated = resultDir.mkdirs();
			if (!wasDirCreated) {
				throw new RuntimeException("Directory was not created.");
			}
		}
	}
}
