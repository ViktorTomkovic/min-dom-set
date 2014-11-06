package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BatchRun {
	private static final String INPUT_FILENAME = "src/main/batchInput.txt";

	private BatchRun() {
	}

	public static void main(String[] args) {
		try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILENAME))) {
			String line = br.readLine();
			if (line == null) {
				throw new RuntimeException("Bad format of input file.");
			}
			while (line != null) {
				MDS_Run.main(line.split(" "));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
