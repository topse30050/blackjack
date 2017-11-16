package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class J48Sample {
    
    //private static final String ARFF_PATH= "src/main/resources/winequality-red2.arff";
	private static final String ARFF_PATH= "train22.arff";
	private static final String TEST_PATH= "test22.arff";
    
    public static void main(String[] args) throws Exception {
        J48Sample app = new J48Sample();
        
        //app.analyze(ARFF_PATH);
        Instances data = app.readCSV("H:\\work\\bigdata\\myresult.csv");
        Classifier tree = app.createModel(data);
        app.applyModel(tree, data);
    }
    
    public Classifier createModel(Instances data) throws Exception {
    	Classifier tree = null;
        try {
            //Instances data = loadData(arffPath);
        	
            data.setClassIndex(6);
            
            filter(data);
          
            // TODO
            tree = new AdaBoostM1();
            //tree = new AdaBoostM1();//new J48();//new RandomForest();//new J48();
            //AdaBoostM1 not effective
            //String[] options = {"-I", "100"};
            //tree.setOptions(options);
            
            // J48
            //String[] options = new String[1];
            //options[0] = "-U";
            //tree.setOptions(options);
            tree.buildClassifier(data);
        } catch (Exception e) {
            throw e;
        }
        return tree;    	
    }
    
    public void applyModel(Classifier tree, Instances instances) throws Exception {
    	//System.out.println("-------------------------");
        Attribute number = new Attribute("number", 0);
        Attribute small = new Attribute("small", 1);
        Attribute large = new Attribute("large", 2);
        Attribute total = new Attribute("total", 3);
        Attribute dealer = new Attribute("dealer", 4);
    	FastVector bool = new FastVector(2);
        bool.addElement("true");
        bool.addElement("false");
        Attribute take = new Attribute("take", bool, 5);

        Instance ins = new Instance(7);
    	ins.setValue(number, 1);
    	ins.setValue(small, 10);
    	ins.setValue(large,11);
    	ins.setValue(total, 21);
    	ins.setValue(dealer, 2);
    	ins.setValue(take, "true");

    	ins.setDataset(instances);
    	double[] res = tree.distributionForInstance(ins);
    	//for(int i=0;i<res.length;i++) {
    	//	System.out.println(res[i]);
    	//}
    	//System.out.println("classifyInstance: "+tree.classifyInstance(ins));
    	//System.out.println("-------------------------");
    	
    	
    	ins = new Instance(7);
    	ins.setValue(number, 1);
    	ins.setValue(small, 10);
    	ins.setValue(large,11);
    	ins.setValue(total, 21);
    	ins.setValue(dealer, 2);
    	ins.setValue(take, "false");

    	ins.setDataset(instances);
    	res = tree.distributionForInstance(ins);
/*    	for(int i=0;i<res.length;i++) {
    		System.out.println(res[i]);
    	}
    	System.out.println("classifyInstance: "+tree.classifyInstance(ins));
    	System.out.println("-------------------------");*/

    	
//    	String label = CSVReader.CSV_LABEL;
//    	String firstLine = "1,7,8,15,11,TRUE,NOT";
//    	String secondLine = "2,7,8,15,11,FALSE,NOT";
//    	try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File("H:\\work\\bigdata\\test.csv")))){
//    		bw.write(label+"\r\n");
//    		bw.write(firstLine+"\r\n");
//    		bw.write(secondLine+"\r\n");
//    	}
//    	Instances data = readCSV("H:\\work\\bigdata\\test.csv");
//    	data.setClassIndex(6);
//    	System.out.println(tree.classifyInstance(data.instance(0)));
//    	System.out.println(tree.classifyInstance(data.instance(1)));
    }
    
    private void analyze(String arffPath) {
        try {
            //Instances data = loadData(arffPath);
            Instances data = readCSV("H:\\work\\bigdata\\model.csv");
        	
            if (data == null) {
                return;
            }
            data.setClassIndex(6);
            
            //Instances data2 = loadData(TEST_PATH);
            Instances data2 = readCSV("H:\\work\\bigdata\\model.csv");
            if (data2 == null) {
                return;
            }
            data2.setClassIndex(6);
            
            filter(data);
            
            J48 tree = new J48();
            String[] options = new String[1];
            options[0] = "-U";
            tree.setOptions(options);
            tree.buildClassifier(data);
            
            evalResult(tree, data2);
            
            showResult(tree);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Instances readCSV(String fileName) throws Exception {
    	CSVLoader loader = new CSVLoader();
    	String[] options = new String[] {"-N", "1,6,7"};
    	loader.setOptions(options);
    	loader.setFile(new File(fileName));
    	Instances instances = loader.getDataSet();
    	//System.out.println(instances);
    	return instances;
    }
    
    public Instances filter(Instances data) throws Exception {

    	
    	NumericToNominal filter = new NumericToNominal ();
    	filter.setAttributeIndices("last");
    	filter.setInputFormat(data);
    	Instances filteredData = Filter.useFilter(data, filter);
    	//System.out.println(filteredData);
    	return filteredData;
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
    
    private void evalResult(J48 tree, Instances data) {
        try {
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree,  data, 10, new Random(1));
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
