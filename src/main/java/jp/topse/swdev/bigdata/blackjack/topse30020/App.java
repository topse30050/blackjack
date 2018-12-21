package jp.topse.swdev.bigdata.blackjack.topse30020;

//package jp.topse.bigdata.wf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;

public class App {

	J48 tree = new J48();
	Evaluation eval;
	Classifier classifier;
	
    public void analyze(String arffPath) {
        try {
            Instances data = loadData(arffPath);
            if (data == null) {
                return;
            }
                    
            String[] options = new String[1];
            options[0] = "-U";
            tree.setOptions(options);
            tree.buildClassifier(data);

            eval = new Evaluation(data);
            classifier = new SMO();
            classifier.buildClassifier(data);
            
            evalResult(data);
            
            //showResult(tree);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Instances loadData(String arffPath) {
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(arffPath)));
            //data.deleteAttributeAt(0);
            //System.out.println( data.firstInstance());
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void evalResult(Instances data) {
        try {
            eval.evaluateModel( classifier, data);
            //eval.crossValidateModel(tree, data, 10, new Random(1));
            /*System.out.println(eval.toSummaryString());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double evalOnce(String filePath) {
        try {
        	Instances data = new Instances(new BufferedReader(new FileReader(filePath)));
            //data.deleteAttributeAt(0);
            //System.out.println( data.firstInstance());
            /*System.out.println(eval.toSummaryString());*/
            data.setClassIndex(data.numAttributes() - 1);
            
            return eval.evaluateModelOnce(classifier, data.firstInstance());

        	//return eval.evaluateModelOnce(tree, data.firstInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
		return 0;
    }
   
    private void showResult(J48 tree) {
        try {
            TreeVisualizer visualizer = new TreeVisualizer(null, tree.graph(), new PlaceNode2());
            
            JFrame frame = new JFrame("Results");
            frame.setSize(800, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.getContentPane().add(visualizer);
            frame.setVisible(true);
            visualizer.fitToScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}