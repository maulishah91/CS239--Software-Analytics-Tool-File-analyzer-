import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Creating a model from training data and added Ngrams and multiplicative weights to it
 */
public class Keywords_Ngrams {

	public static void main(String[] args) {
		// train data
		HashMap<String, HashMap<String, Double>> word_topic_matrix = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashSet<String>> topic_word_matrix = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> word_frequency = new HashMap<String, Integer>();

		// test data

		try {
			Keywords train_data = new Keywords();
			BufferedReader br = train_data.loadFile("/home/ms/Desktop/train_final.txt");

			Keywords predicted_topics = new Keywords();
			File file = new File(
					"/home/ms/Desktop/Quarter 2/CS289/project/QuoraChallenges-Labeler-master/Solution.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw_train = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw_predict = new BufferedWriter(fw_train);

			if (br != null) {

				String currLine = "";
				while ((currLine = br.readLine()) != null) {
					// System.out.println(currLine);
					String[] columns = currLine.split(",");

					// find frequency of each word
					String[] words = columns[2].split(" ");
					String prev = "";
					for (String w : words) {
						int count = 0;
						int count_ngram = 0;
						if (word_frequency.containsKey(w)) {
							count = word_frequency.get(w);
						}
						String gram2 = prev + " " + w;
						gram2 = gram2.trim();
						if (word_frequency.containsKey(gram2.trim())) {
							count_ngram = word_frequency.get(gram2.trim());
						}
						word_frequency.put(w, count + 1);
						if (!prev.equals("") && gram2.contains(" ")) {
							word_frequency.put(gram2.trim(), count_ngram + 1);
							// System.out.println("gram2: "+gram2.trim()+"
							// :"+(count_ngram+1));
						}
						prev = w;
					}

					// relate words to topics
					String[] labels = columns[1].split(" ");
					prev = "";
					for (String w : words) {
						// for ngrams
						String gram2 = prev + " " + w;
						gram2 = gram2.trim();
						if (word_topic_matrix.containsKey(gram2)) {
							HashMap<String, Double> label_curr = word_topic_matrix.get(gram2);
							for (String l : labels) {
								double count_gram2 = 0;
								if (label_curr.containsKey(l)) {
									count_gram2 = label_curr.get(l);
								}
								label_curr.put(l, count_gram2 + 1);
								if (!prev.equals("") && gram2.contains(" "))
									word_topic_matrix.put(gram2, label_curr);
							}
						} else {
							HashMap<String, Double> label_curr = new HashMap<String, Double>();
							for (String l : labels) {
								label_curr.put(l, 1.0);
							}
							if (!prev.equals("") && gram2.contains(" "))
								word_topic_matrix.put(gram2, label_curr);
						}

						prev = w;

						if (word_topic_matrix.containsKey(w)) {
							HashMap<String, Double> label_curr = word_topic_matrix.get(w);
							for (String l : labels) {
								double count = 0;
								if (label_curr.containsKey(l)) {
									count = label_curr.get(l);
								}
								label_curr.put(l, count + 1);
								word_topic_matrix.put(w, label_curr);
							}
						} else {
							HashMap<String, Double> label_curr = new HashMap<String, Double>();
							for (String l : labels) {
								label_curr.put(l, 1.0);
							}
							word_topic_matrix.put(w, label_curr);
						}
					}

					// relate topics to words

					for (String l : labels) {
						HashSet<String> all_words = new HashSet<String>();
						if (topic_word_matrix.containsKey(l)) {
							all_words = topic_word_matrix.get(l);
						}

						for (String w : words) {
							all_words.add(w);
						}
						topic_word_matrix.put(l, all_words);

						prev = "";

						if (topic_word_matrix.containsKey(l)) {
							all_words = topic_word_matrix.get(l);
						}
						for (String w : words) {
							String gram2 = prev + " " + w;
							gram2 = gram2.trim();
							if (!prev.equals("") && gram2.contains(" "))
								all_words.add(gram2);
							prev = w;
						}
						topic_word_matrix.put(l, all_words);

					}

				}

			}

			// print HashMap<String,HashSet<String>> topic_word_matrix
			int max = 0;
			int avg = 0;
			for (Map.Entry<String, HashSet<String>> entry : topic_word_matrix.entrySet()) {
				String key = entry.getKey().toString();
				Integer value = entry.getValue().size();
				if (value > max)
					max = value;
				avg += value;

			}
			// System.out.println("max val "+max);
			// System.out.println("avg: "+avg/(double)topic_word_matrix.size());

			// find categories for test data
			Keywords test_data = new Keywords();
			BufferedReader br_test = test_data.loadFile("/home/ms/Desktop/test_final.txt");

			if (br_test != null) {
				String currLine = "";
				int question_count = 0;
				while ((currLine = br_test.readLine()) != null) {
					// find frequency of each word
					boolean hasCat = false;
					String[] words = currLine.split(" ");

					HashMap<String, Double> finalTop = new HashMap<String, Double>();
					String prev = "";
					for (String w : words) {

						int freq = -1;
						if (word_frequency.containsKey(w))
							freq = word_frequency.get(w);
						// else System.out.println("This word has no occurance
						// in train data: "+w);
						if (freq > 0) {

							HashMap<String, Double> topics_curr = word_topic_matrix.get(w);
							for (String key : topics_curr.keySet()) {
								if (key.trim().equals(""))
									continue;
								// System.out.println(key + " " +
								// topics_curr.get(key));
								double value = topics_curr.get(key);
								int noOfWordsInTopic = topic_word_matrix.get(key).size();
								if (freq <= 1) {
									// exact anchor word

									if (noOfWordsInTopic == 1) {
										// word occurs only in 1 question
										// System.out.println(topics_curr.get(key)*10.0/(double)noOfWordsInTopic);
										finalTop.put(key, topics_curr.get(key) / (double) noOfWordsInTopic);
									}

									else {

										finalTop.put(key, topics_curr.get(key) / (double) (noOfWordsInTopic * 10));
									}

								}

								else if (!finalTop.containsKey(key)) {

									finalTop.put(key,
											(topics_curr.get(key) / (double) (noOfWordsInTopic * freq * 100)));

								} else {
									double val = finalTop.get(key);

									finalTop.put(key,
											val + (topics_curr.get(key) * 2 / (double) (noOfWordsInTopic * freq)));

								}
							}
							hasCat = true;

						}

						String gram2 = prev + " " + w;
						gram2 = gram2.trim();
						int freq_ngrams = -1;
						if (word_frequency.containsKey(gram2))
							freq_ngrams = word_frequency.get(gram2);
						prev = w;
						// code for ngrams
						if (freq_ngrams > 0) {
							// check for anchor words

							HashMap<String, Double> topics_curr = word_topic_matrix.get(gram2);
							for (String key : topics_curr.keySet()) {
								if (key.trim().equals(""))
									continue;
								// System.out.println(key + " " +
								// topics_curr.get(key));
								double value = topics_curr.get(key);
								int noOfWordsInTopic = topic_word_matrix.get(key).size();
								if (freq_ngrams <= 1) {
									// exact anchor word

									if (noOfWordsInTopic <= 1250) {
										// word occurs only in 1 question
										// System.out.println(topics_curr.get(key)*10.0/(double)noOfWordsInTopic);
										finalTop.put(key, topics_curr.get(key) / (double) (noOfWordsInTopic * 1000));
									}

									else {
										finalTop.put(key, topics_curr.get(key) * 4 / (double) noOfWordsInTopic);
									}

								}

								else if (!finalTop.containsKey(key)) {
									finalTop.put(key,
											(topics_curr.get(key) / (double) (noOfWordsInTopic * freq_ngrams)));

								} else {
									double val = finalTop.get(key);
									finalTop.put(key, val
											+ (topics_curr.get(key) / (double) (noOfWordsInTopic * freq_ngrams * 2)));

								}
							}
							hasCat = true;

						}

					}
					if (hasCat) {
						question_count++;

						// sort the finalTop
						String cat = "";
						Map<String, Double> final_tops = sortByComparator(finalTop);

						for (String key : final_tops.keySet()) {
							// System.out.println("Key: "+key+" :
							// "+final_tops.get(key));
							cat += key + " ";

						}
						// System.out.println(currLine);
						// System.out.println(cat.trim());
						bw_predict.write("1 " + cat.trim() + "\n");
					}
				}
				System.out.println("No of questions resolved : " + question_count);
			}

			bw_predict.close();
			br_test.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedReader loadFile(String name) {

		BufferedReader br = null;

		try {

			String sCurrentLine = "";

			br = new BufferedReader(new FileReader(name));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return br;
	}

	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return -1 * (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

}
