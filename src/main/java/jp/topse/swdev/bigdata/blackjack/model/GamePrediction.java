package jp.topse.swdev.bigdata.blackjack.model;

import java.util.Enumeration;
import java.util.Hashtable;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author nagi
 *
 */
public class GamePrediction {

//	private static final String ARFF_PATH= "src/main/resources/iris.arff";
	private Classifier classModel;
    private String classModelFile = "model/j48_deck3_weka3612.model";


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
			 Hashtable<String, Double> testValues = new Hashtable<>();
			 testValues.put("dealer_card", 10.0/10);
			 testValues.put("player1_card_sum", 10.0/10);
			 testValues.put("stand_hit",       0.0);
			 testValues.put("player2_card_1", (double) 10/10);
			 testValues.put("player2_card_2", (double) 10/10);
			 testValues.put("player3_card_1", (double) 10/10);
			 testValues.put("player3_card_2", (double) 10/10);
			 testValues.put("player4_card_1", (double) 10/10);
			 testValues.put("player4_card_2", (double) 10/10);
			 testValues.put("player5_card_1", (double) 10/10);
			 testValues.put("player5_card_2", (double) 10/10);
			 testValues.put("player6_card_1", (double) 10/10);
			 testValues.put("player6_card_2", (double) 10/10);
			 System.out.println(prediction.classifySpecies(testValues));
			 System.out.println("test");
		 } catch (Exception e){
			 System.out.println(e);
		 }
	
		
	}
	public GamePrediction() throws Exception{
		 classModel = (Classifier) weka.core.SerializationHelper.read(classModelFile);
	}
	
	public  String classifySpecies(Hashtable<String, Double> play_game) throws Exception {
        FastVector dataClasses = new FastVector();
        FastVector dataAttribs = new FastVector();
        Attribute species;
        double values[] = new double[play_game.size() + 1];
        int i = 0, maxIndex = 0;

        //  "dataClasses"に想定しうる勝負の種別をセット
        dataClasses.addElement("WIN");
        dataClasses.addElement("LOSE");
        dataClasses.addElement("DRAW");
        species = new Attribute("class", dataClasses);

        //  Create the object to classify on.
        for (Enumeration<String> keys = play_game.keys(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            dataAttribs.addElement(new Attribute(key));
            values[i++] = play_game.get(key);
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