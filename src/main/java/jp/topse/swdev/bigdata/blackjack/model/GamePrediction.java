package jp.topse.swdev.bigdata.blackjack.model;

import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Hashtable;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author nagi
 *
 */
@SuppressWarnings("deprecation")
public class GamePrediction {

//	private static final String ARFF_PATH= "src/main/resources/iris.arff";
	private Classifier classModel;
    private String classModelFile = "/home/nagi/work/git/blackjack/model/j48_deck3.model";


	/**
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("test");
		GamePrediction prediction;
		 try {
			 prediction = new GamePrediction();
			 Hashtable<String, String> testValues = new Hashtable<>();
			 testValues.put("dealer_card", "10");
			 testValues.put("player1_card_sum", "10");
			 testValues.put("stand_hit", "10");
			 testValues.put("player2_card_1", "10");
			 testValues.put("player2_card_2", "10");
			 testValues.put("player3_card_1", "10");
			 testValues.put("player3_card_2", "10");
			 testValues.put("player4_card_1", "10");
			 testValues.put("player4_card_2", "10");
			 testValues.put("player5_card_1", "10");
			 testValues.put("player5_card_2", "10");
			 testValues.put("player6_card_1", "10");
			 testValues.put("player6_card_2", "10");
			 System.out.println(prediction.classifySpecies(testValues));
			 System.out.println("test");
		 } catch (Exception e){
			 System.out.println(e);
		 }
	
		
	}
	public GamePrediction() throws Exception{
		 classModel = (Classifier) weka.core.SerializationHelper.read(classModelFile);
	}
	public  String classifySpecies(Hashtable<String, String> measures) throws Exception {
        FastVector dataClasses = new FastVector();
        FastVector dataAttribs = new FastVector();
        Attribute species;
        double values[] = new double[measures.size() + 1];
        int i = 0, maxIndex = 0;

        //  "dataClasses"に想定しうる勝負の種別をセット
        dataClasses.addElement("WIN");
        dataClasses.addElement("LOSE");
        dataClasses.addElement("DRAW");
        species = new Attribute("class", dataClasses);

        //  Create the object to classify on.
        for (Enumeration<String> keys = measures.keys(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            double val = Double.parseDouble(measures.get(key));
            dataAttribs.addElement(new Attribute(key));
            values[i++] = val;
        }
        dataAttribs.addElement(species);
        Instances dataModel = new Instances("classify", dataAttribs, 0);
        dataModel.setClass(species);
        dataModel.add(new Instance(1, values));
        dataModel.instance(0).setClassMissing();

        //  Find the class with the highest estimated likelihood

        double cl[] = classModel.distributionForInstance(dataModel.instance(0));
        for(i = 0; i < cl.length; i++)
            if(cl[i] > cl[maxIndex])
                maxIndex = i;

        return dataModel.classAttribute().value(maxIndex);
    }
}