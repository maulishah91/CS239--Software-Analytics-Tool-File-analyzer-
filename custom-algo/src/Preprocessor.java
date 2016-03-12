import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This file remove the special characters, stop words and stems
 */
public class Preprocessor {

	static HashSet<Integer> labels_set = new HashSet<Integer>();

	public Preprocessor() {
		for (int i = 0; i < 250; i++) {
			labels_set.add(i);
		}

	}

	public static void main(String[] args) {

		Preprocessor a = new Preprocessor();
		BufferedReader br = null;

		try {

			String sCurrentLine;
			loadStopWords();

			// PorterStemmer.stemWords("/home/ms/Desktop/labeler_sample.in");

			br = new BufferedReader(new FileReader("/home/ms/Desktop/labeler_sample.in"));

			if ((sCurrentLine = br.readLine()) != null) {

				String input_size[] = sCurrentLine.split(" ");
				int train_size = Integer.parseInt(input_size[0]);
				int test_size = Integer.parseInt(input_size[1]);
				StringBuilder content = new StringBuilder();

				for (int i = 0; i < train_size; i++) {
					String labels_op = br.readLine().substring(2);
					String[] labels_curr = (labels_op).split(" ");
					for (String s : labels_curr) {
						// System.out.println(s);
						if (!s.equals("") && labels_set.contains(Integer.parseInt(s))) {
							// System.out.println(i);
							labels_set.remove(Integer.parseInt(s));
						}
					}

					String line1 = br.readLine();
					// remove special characters
					String line = removeSpecialCharacters(line1);
					line = line.toLowerCase();
					// remove stop words
					String[] filterLine = line.split(" ");
					String actualLine = "";
					for (String s : filterLine) {
						if (!stopWords_set.contains(s)) {
							actualLine += s + " ";
						}
					}
					// System.out.println(actualLine);
					// if actualLine is blank
					if (actualLine.trim().equals("")) {
						content.append(i + "," + labels_op + "," + line + "\n");
					} else
						content.append(i + "," + labels_op + "," + actualLine + "\n");

				}
				// write to file
				writeToFile("/home/ms/Desktop/train_filter.txt", content);
				PorterStemmer.stemWords("/home/ms/Desktop/train_filter.txt", "/home/ms/Desktop/train_final.txt");

				

				// find anchor words
				content = new StringBuilder();
				// testing area
				for (int i = 0; i < test_size; i++) {
					String line = br.readLine();
					line = removeSpecialCharacters(line);
					line = line.toLowerCase();
					// remove stop words
					String[] filterLine = line.split(" ");
					String actualLine = "";
					for (String s : filterLine) {
						if (!stopWords_set.contains(s)) {
							actualLine += s + " ";
						}
					}
					System.out.println(actualLine);
					content.append(actualLine + "\n");
				}
				writeToFile("/home/ms/Desktop/test_filter.txt", content);
				PorterStemmer.stemWords("/home/ms/Desktop/test_filter.txt", "/home/ms/Desktop/test_final.txt");

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private static String removeSpecialCharacters(String line) {
		Pattern pt = Pattern.compile("[^a-zA-Z0-9\\s]");
		Matcher match = pt.matcher(line);
		while (match.find()) {

			String s = match.group();
			if (s.equals("'")) {
				line = line.replaceAll("'", "");
			} else
				line = line.replaceAll("\\" + s, " ");
		}
		return line;
	}

	static HashSet<String> stopWords_set = new HashSet<String>();

	public static void loadStopWords() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/home/ms/Desktop/stopwords.txt"));

		String sCurrentLine = "";

		while ((sCurrentLine = br.readLine()) != null) {
			// System.out.println(sCurrentLine);
			stopWords_set.add(sCurrentLine);
		}
		System.out.println("Initialised Stop Words");
		br.close();
	}

	public static void writeToFile(String name, StringBuilder content) throws IOException {
		File file_train = new File(name);

		// if file doesnt exists, then create it
		if (!file_train.exists()) {
			file_train.createNewFile();
		}

		FileWriter fw_train = new FileWriter(file_train.getAbsoluteFile());
		BufferedWriter bw_train = new BufferedWriter(fw_train);
		bw_train.write(content.toString());
		bw_train.close();

	}
}
