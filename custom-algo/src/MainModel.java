import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainModel {

	public static void main(String[] args) {

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("/home/ms/Desktop/labeler_sample.in"));
			
			
			File file_train = new File("/home/ms/Desktop/train.txt");
			File file_test = new File("/home/ms/Desktop/test.txt");

			// if file doesnt exists, then create it
			if (!file_train.exists()) {
				file_train.createNewFile();
			}
			
			if (!file_test.exists()) {
				file_test.createNewFile();
			}

			FileWriter fw_train = new FileWriter(file_train.getAbsoluteFile());
			BufferedWriter bw_train = new BufferedWriter(fw_train);
			
			FileWriter fw_test = new FileWriter(file_test.getAbsoluteFile());
			BufferedWriter bw_test = new BufferedWriter(fw_test);
			
			
			
			
			if ((sCurrentLine = br.readLine()) != null) {
				
				String input_size[]= sCurrentLine.split(" ");
				int train_size=Integer.parseInt(input_size[0]);
				int test_size=Integer.parseInt(input_size[1]);
				for(int i=0;i<train_size;i++){
				String labels=(br.readLine()).substring(2);
				String line=br.readLine();
				
				line=line.replaceAll(";", "");
				//line=line.replaceAll("?", "");
				line=line.replaceAll(",", "");
				//line=line.replaceAll(".", "");
				
				String[] createLabels=labels.split(" ");
				String appendLabel="";
				for(String s:createLabels){
					appendLabel+="char"+s+" ";
				}
				
				String output_line=i+","+appendLabel.trim()+","+line+"\n";
				System.out.println(output_line);
				bw_train.write(output_line);
				}
				System.out.println("Done");
				bw_train.close();
				//System.exit(0);
				for(int i=0;i<test_size;i++){
					String questions=i+",,"+br.readLine()+"\n";
					System.out.println(questions);
					bw_test.write(questions);
					
				}
				System.out.println("Done");
				bw_test.close();
				//System.out.println(sCurrentLine);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}