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

public class Keywords {

	public static void main(String[] args) {
		//train data
		HashMap<String,HashMap<String,Integer>> word_topic_matrix=new HashMap<String,HashMap<String,Integer>>();
		HashMap<String,HashSet<String>> topic_word_matrix=new HashMap<String,HashSet<String>>();
		HashMap<String,Integer> word_frequency=new HashMap<String,Integer>();
		
		
		//test data
		
		try {
			Keywords train_data=new Keywords();
			BufferedReader br=train_data.loadFile("/home/ms/Desktop/train_final.txt");
			
			Keywords predicted_topics=new Keywords();
			File file=new File("/home/ms/Desktop/Solution.txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw_train = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw_predict = new BufferedWriter(fw_train);
			
			
			
			
			if(br!=null){
				
				String currLine="";
				while((currLine=br.readLine())!=null)
				{
					//System.out.println(currLine);
					String[] columns=currLine.split(",");
					
					//find frequency of each word
					String[] words=columns[2].split(" ");
					for(String w:words){
						int count=0;
						if(word_frequency.containsKey(w)){
							count=word_frequency.get(w);
						}
						word_frequency.put(w, count+1);
					}
					
					
					//relate words to topics
					String[] labels=columns[1].split(" ");
					for(String w:words){
						if(word_topic_matrix.containsKey(w)){
							HashMap<String,Integer> label_curr=word_topic_matrix.get(w);
							for(String l:labels){
								int count=0;
								if(label_curr.containsKey(l)){
									count=label_curr.get(l);
								}
								label_curr.put(l, count+1);
								word_topic_matrix.put(w, label_curr);
							}
						}
						else{
							HashMap<String,Integer> label_curr=new HashMap<String,Integer>();
							for(String l:labels){
								label_curr.put(l, 1);
							}
							word_topic_matrix.put(w, label_curr);
						}
					}
					
					//relate topics to words
					for(String l:labels){
						HashSet<String> all_words=new HashSet<String>();
						if(topic_word_matrix.containsKey(l)){
							all_words=topic_word_matrix.get(l);
						}
							for(String w:words){
								all_words.add(w);
							}
							topic_word_matrix.put(l, all_words);
						
					
					
				}
					
				}
				
			}
			
			
			 // printing word frequency
			/* for (Map.Entry<String, Integer> entry : word_frequency.entrySet()) {
			    String key = entry.getKey().toString();
			    Integer value = entry.getValue();
			    System.out.println("key, " + key + " value " + value);
			}*/
			
			/*//find anchor and almost anchor words == frequency for now is 1
			for (Map.Entry<String, HashSet<String>> entry : topic_word_matrix.entrySet()) {
			    String key = entry.getKey().toString();
			    Integer value = entry.getValue().size();
			    System.out.println("key, " + key + " value " + value);
			}*/
			 
			 //find categories for test data
			 Keywords test_data=new Keywords();
			 BufferedReader br_test=test_data.loadFile("/home/ms/Desktop/test_final.txt");
			 
			 if(br_test!=null){
				 String currLine="";
				 int question_count=0;
			 while((currLine=br_test.readLine())!=null){
				//find frequency of each word
				 boolean hasCat=false;
				String[] words=currLine.split(" ");
				
				HashMap<String,Integer> finalTop=new HashMap<String,Integer>();
				for(String w: words){
					int freq=-1;
					if(word_frequency.containsKey(w))
				        freq= word_frequency.get(w);
					//else System.out.println("This word has no occurance in train data: "+w);
				if(freq>0){
					//exact anchor word
					
					
					HashMap<String,Integer> topics_curr=word_topic_matrix.get(w);
					for (String key : topics_curr.keySet()) {
					    //System.out.println(key + " " + topics_curr.get(key));
						int value=topics_curr.get(key);
						if(freq<=1){
							finalTop.put(key, 800);
						}
						
						else if(!finalTop.containsKey(key)){
					    	finalTop.put(key, topics_curr.get(key)/word_frequency.get(w));
					    }
					    else{
					    	int val=finalTop.get(key);
					    	finalTop.put(key, val+(topics_curr.get(key)/word_frequency.get(w)));
					    }
					}
					hasCat=true;
				}
				
				}
				if(hasCat){
				question_count++;
				
				//sort the finalTop
				String cat="";
				Map<String,Integer> final_tops=sortByComparator(finalTop);
				
				for (String key : final_tops.keySet()) {
					System.out.println("Key: "+key+" : "+final_tops.get(key));
					cat+=key+" ";
					
				}
				System.out.println(currLine);
				System.out.println(cat.trim());
				bw_predict.write("187 "+cat.trim()+"\n");
				}
			 }
			 System.out.println("No of questions resolved : "+question_count);
			 }
			 
			 bw_predict.close();
			 br_test.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public BufferedReader loadFile(String name){


		BufferedReader br = null;

		try {

			String sCurrentLine="";

			br = new BufferedReader(new FileReader(name));	
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return br;
}

	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return -1*(o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	
	
}

