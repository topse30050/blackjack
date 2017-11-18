package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.*;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class MyClassifier {
    public Classifier createModel(Instances data, String classfiername, String[] constructorArgs, String[] options, int classIndex) throws Exception {
    	Classifier tree = null;
        try {
        	
            data.setClassIndex(classIndex);
            
            filter(data);
          
            Class<Classifier> clazz = (Class<Classifier>) Class.forName(classfiername);
            if(constructorArgs == null) {
				tree = clazz.newInstance();
            } else {
            	// TODO
            }
            
            if(options != null) {
            	tree.setOptions(options);
            }

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
    
    
    public Instances filter(Instances data) throws Exception {

    	
    	NumericToNominal filter = new NumericToNominal ();
    	filter.setAttributeIndices("last");
    	filter.setInputFormat(data);
    	Instances filteredData = Filter.useFilter(data, filter);
    	//System.out.println(filteredData);
    	return filteredData;
    }

    
    public Instances readCSV(String fileName, int classIndex) throws Exception {
    	CSVLoader loader = new CSVLoader();
    	String[] options = new String[] {"-N", "1,"+(classIndex-1)+","+classIndex};
    	loader.setOptions(options);
    	loader.setFile(new File(fileName));
    	Instances instances = loader.getDataSet();
    	instances.setClassIndex(classIndex);
    	//System.out.println(instances);
    	return instances;
    }
}
