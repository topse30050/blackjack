package jp.topse.swdev.bigdata.blackjack.yoshimura;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class MakeModel {
	
	static final String ARFF_PATH= "./data/blackjack-yoshi.arff";
    static final String ARFF_PATH_T1 = "./models/yoshimura/blackjack-yoshi-1.arff";
    static final String ARFF_PATH_T2 = "./models/yoshimura/blackjack-yoshi-2.arff";
    static final String ARFF_PATH_T3 = "./models/yoshimura/blackjack-yoshi-3.arff";
    //static final String ARFF_PATH= "./data/blackjack-yoshi-kai.arff";
	//private static final String ARFF_PATH= "./src/main/resources/blackjack.arff";

	private static final String MODEL_PATH = "./models/yoshimura/blackjack-yoshi.model";
    private static final String MODEL_PATH_T1 = "./models/yoshimura/blackjack-yoshi-1.model";
    private static final String MODEL_PATH_T2 = "./models/yoshimura/blackjack-yoshi-2.model";
    private static final String MODEL_PATH_T3 = "./models/yoshimura/blackjack-yoshi-3.model";
	//private static final String MODEL_PATH= "./data/blackjack-yoshi-kai.model";
	//private static final String MODEL_PATH= "./src/main/resources/blackjack.model";

	public static void main(String[] args) {
		MakeModel app = new MakeModel();
//		app.analyze(ARFF_PATH, MODEL_PATH);
        app.analyze(ARFF_PATH_T1, MODEL_PATH_T1);
        app.analyze(ARFF_PATH_T2, MODEL_PATH_T2);
        app.analyze(ARFF_PATH_T3, MODEL_PATH_T3);
    }
	
	private void analyze(String arffPath, String modelPath) {
        try {
            Instances data = loadData(arffPath);
            if (data == null) {
                return;
            }
            
            // 二分岐
            //J48 tree = new J48();
            // ランダムツリー
            RandomTree tree = new RandomTree();
            //RandomForest tree = new RandomForest();
            String[] options = new String[1];
            options[0] = "-U";
            tree.setOptions(options);
            tree.buildClassifier(data);
            
            evalResult(tree, data);
            
            weka.core.SerializationHelper.write(modelPath, tree);
            
            //showResult(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private Instances loadData(String arffPath) {
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(arffPath)));
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void evalResult(RandomTree tree, Instances data) {
	//private void evalResult(RandomForest tree, Instances data) {
        try {
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree,  data, 4, new Random(1));
            //System.out.println(eval.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
