// http://nlp.stanford.edu/software/tmt/0.3/

// tells Scala where to find the TMT classes
import scalanlp.io._;
import scalanlp.stage._;
import scalanlp.stage.text._;
import scalanlp.text.tokenize._;
import scalanlp.pipes.Pipes.global._;

import edu.stanford.nlp.tmt.stage._;
import edu.stanford.nlp.tmt.model.lda._;
import edu.stanford.nlp.tmt.model.llda._;

object Main {
  def main(args: Array[String]) {
    
    val modelPath = file("../resources/model/trained_labeler_model");
    
    System.err.println("Loading model ...");
    
    val lldaModel = LoadCVB0LabeledLDA(modelPath);
    val model = lldaModel.asCVB0LDA;
    val source = CSVFile("../resources/dataset/test/labeler_test_in.csv");
    val text = {
      source ~>                              // read from the source file
      Column(1) ~>                           // select column containing text
      TokenizeWith(model.tokenizer.get)      // tokenize with existing model's tokenizer
    }
    
    val output = file(modelPath, source.meta[java.io.File].getName.replaceAll(".csv",""));
    
    val dataset = LDADataset(text, model.termIndex);
    
    System.err.println("Generating output ...");
    val perDocTopicDistributions =
      InferCVB0DocumentTopicDistributions(model, dataset);
    
    val perDocTermTopicDistributions =
      EstimatePerWordTopicDistributions(model, dataset, perDocTopicDistributions);
    
    CSVFile(output+"-document-topic-distributions.csv").write({
      for ((terms,(dId,dists)) <- text.iterator zip perDocTopicDistributions.iterator) yield {
         
          dists.zipWithIndex.map({
             case (prob,topic) => lldaModel.topicIndex.get.get(topic) + ":" + prob
           }).mkString(" ");
        
      } // for ends
    }); //write ends
  }
}



