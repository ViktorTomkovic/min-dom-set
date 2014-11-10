package testgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import main.Utils;

public class CorectnessTestGenerator {
	private static final String TESTS_PATH = "src/test";
	private static final String TOKENS_FILENAME = "src/testgenerator/correctness.input.txt";
	private static final String TEMPLATE_FILENAME = "src/testgenerator/correctness.template.txt";
	private static final String SUITE_TEMPLATE_INPUT_FILENAME = "src/testgenerator/correctness.suiteTemplate.txt";
	private static final String SUITE_TEMPLATE_OUTPUT_FILENAME = "src/test/AllCorrectnessTests.java";
	private static final ArrayList<ArrayList<String>> tokenMap = new ArrayList<>();
	private static final StringBuilder allClasses = new StringBuilder();

	private CorectnessTestGenerator() {
	}

	// private static String addBraces(String input) {
	// return "${" + input + "}";
	// }

	public static void readTokensFromFile(String filename) {
		tokenMap.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			if (line == null) {
				throw new RuntimeException("Bad format of input file.");
			}
			while (line != null) {
				if (line.equals("")) {
					line = br.readLine();
					continue;
				}
				ArrayList<String> oneRun = new ArrayList<String>(4);
				String[] tokens = line.split(" ");
				if (tokens.length != 2) {
					throw new RuntimeException(
							"Line contains unexpected number of tokens.");
				}
				oneRun.add(0, tokens[0]);
				oneRun.add(1, tokens[1]);
				oneRun.add(2, tokens[0].substring(0, tokens[0].length() - 4)
						.toUpperCase());
				oneRun.add(3, Utils.getAlgorithmFullName(tokens[1]));
				tokenMap.add(oneRun);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteOldTests() {
		File testDir = new File(TESTS_PATH);
		if (testDir.isDirectory()) {
			String[] tests = testDir.list();
			for (String name : tests) {
				File file = new File(testDir, name);
				if (name.contains("CorrectnessTest")) {
					boolean wasDeleted = file.delete();
					if (!wasDeleted) {
						System.out.println("No need to delete files.");
					}
				}
			}
		}
	}

	public static void generateTests() {
		for (ArrayList<String> oneLine : tokenMap) {
			generateOneTest(oneLine);
		}
	}

	private static void generateOneTest(ArrayList<String> tokens) {
		String datasetName = tokens.get(0);
		String algorithmName = tokens.get(1);
		String datasetFullName = tokens.get(2);
		String algorithmFullName = tokens.get(3);
		StringBuilder out = new StringBuilder();
		out.append(TESTS_PATH);
		out.append("/");
		out.append(algorithmFullName);
		out.append(datasetFullName);
		out.append("CorrectnessTest.java");
		String outputFilename = out.toString();
		allClasses.append(",\n\t");
		allClasses.append(algorithmFullName);
		allClasses.append(datasetFullName);
		allClasses.append("CorrectnessTest.class");

		Utils.createAndCheckDirectory(TESTS_PATH);

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFilename)));
				BufferedReader reader = new BufferedReader(new FileReader(
						TEMPLATE_FILENAME))) {
			String line = reader.readLine();
			while (line != null) {
				line = line.replace("${algorithmName}", algorithmName);
				line = line.replace("${datasetName}", datasetName);
				line = line.replace("${algorithmFullName}", algorithmFullName);
				line = line.replace("${datasetFullName}", datasetFullName);
				writer.write(line);
				writer.newLine();
				line = reader.readLine();
			}
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void generateAllCorrectnessSuite() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(SUITE_TEMPLATE_OUTPUT_FILENAME)));
				BufferedReader reader = new BufferedReader(new FileReader(
						SUITE_TEMPLATE_INPUT_FILENAME))) {
			String line = reader.readLine();
			while (line != null) {
				line = line.replace("${allClasses}", allClasses.toString()
						.substring(1));
				writer.write(line);
				writer.newLine();
				line = reader.readLine();
			}
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		deleteOldTests();
		readTokensFromFile(TOKENS_FILENAME);
		generateTests();
		generateAllCorrectnessSuite();
	}

}
