package mindomset.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class BatchRun {
	private static final String INPUT_FILENAME = "batchInput.txt";

	private BatchRun() {
	}

	public static void main(String[] args) {
		try {
			Files.deleteIfExists(FileSystems.getDefault().getPath("temp.txt"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILENAME))) {
			String line = br.readLine();
			if (line == null || line.equals("")) {
				throw new RuntimeException("Bad format of input file.");
			}
			while (line != null) {
				if (line.equals("")) {
					line = br.readLine();
					continue;
				}
				MDS_Run.main(line.split(" "));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
