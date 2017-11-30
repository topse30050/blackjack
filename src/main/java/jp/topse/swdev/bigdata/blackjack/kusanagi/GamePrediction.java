package jp.topse.swdev.bigdata.blackjack.kusanagi;

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
	private int model_type = 1;
	//private String classModelFile = "models/kusanagi/svm_deck3_kusanagi.model";
	private String[] classModelFile = {
			"models/kusanagi/svm_default_kusanagi.model",
			"models/kusanagi/nn_h1_8_n10000_deck1_type1_kusanagi.model",
			//"models/kusanagi/nn_h2_a_a_n1000_deck1_type2_kusanagi.model",
			//"models/kusanagi/nn_h1_a_n1000_deck1_type2_kusanagi.model",
			//"models/kusanagi/nn_h2_8_8_n10000_deck1_kusanagi.model",
			//"models/kusanagi/j48_deck1_kusanagi.model",
			//"models/kusanagi/j48_deck1_type2_kusanagi.model",
			//"models/kusanagi/j48_deck1_type3_kusanagi.model",
			//"models/kusanagi/naivebayes_deck1_kusanagi.model",
			//"models/kusanagi/svm_deck1_kusanagi.model",
			//"models/kusanagi/rf_deck1_kusanagi.model",
			//"models/kusanagi/rf_deck1_type2_kusanagi.model",
			"models/kusanagi/nn_h1_8_n10000_deck2_type1_kusanagi.model",
			//"models/kusanagi/nn_h1_a_n1000_deck2_type2_kusanagi.model",
			//"models/kusanagi/nn_h2_a_a_n1000_deck2_type2_kusanagi.model",
			//"models/kusanagi/nn_h2_8_8_n10000_deck2_type1_kusanagi.model",
//			"models/kusanagi/j48_deck2_type2_kusanagi.model",
			//"models/kusanagi/naivebayes_deck2_kusanagi.model",
			//"models/kusanagi/svm_deck2_kusanagi.model",
			//"models/kusanagi/rf_deck2_type1_kusanagi.model",
			//"models/kusanagi/rf_deck2_type2_kusanagi.model",
			//"models/kusanagi/nn_h1_8_n10000_deck3_type1_kusanagi.model",
			"models/kusanagi/nn_h2_8_8_n10000_deck3_type1_kusanagi.model",
			//"models/kusanagi/j48_deck3_kusanagi.model",
			//"models/kusanagi/naivebayes_deck3_kusanagi.model",
			//"models/kusanagi/svm_deck3_kusanagi.model",
			//"models/kusanagi/rf_deck3_kusanagi.model",

			
	};
	
    


	/**
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("test");
		GamePrediction prediction;
		 try {
			 prediction = new GamePrediction(2);
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
	public GamePrediction(int index) throws Exception{

		classModel = (Classifier) weka.core.SerializationHelper.read(classModelFile[index]);
		if(index==1) model_type = 1;
		else if(index==2) model_type = 1;
		else 		 model_type = 1;
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
        /*
        for (Enumeration<String> keys = play_game.keys(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            dataAttribs.addElement(new Attribute(key));
            values[i++] = play_game.get(key);
        }
        */
        if(model_type== 1) {
        	dataAttribs.addElement(new Attribute("dealer_card"));
        	values[i++] = play_game.get("dealer_card");
        	dataAttribs.addElement(new Attribute("player1_card_sum"));
        	values[i++] = play_game.get("player1_card_sum");
        	dataAttribs.addElement(new Attribute("stand_hit"));
        	values[i++] = play_game.get("stand_hit");
        	dataAttribs.addElement(new Attribute("player2_card_1"));
        	values[i++] = play_game.get("player2_card_1");
        	dataAttribs.addElement(new Attribute("player2_card_2"));
        	values[i++] = play_game.get("player2_card_2");
        	dataAttribs.addElement(new Attribute("player3_card_1"));
        	values[i++] = play_game.get("player3_card_1");
        	dataAttribs.addElement(new Attribute("player3_card_2"));
        	values[i++] = play_game.get("player3_card_2");
        	dataAttribs.addElement(new Attribute("player4_card_1"));
        	values[i++] = play_game.get("player4_card_1");
        	dataAttribs.addElement(new Attribute("player4_card_2"));
        	values[i++] = play_game.get("player4_card_2");
        	dataAttribs.addElement(new Attribute("player5_card_1"));
        	values[i++] = play_game.get("player5_card_1");
        	dataAttribs.addElement(new Attribute("player5_card_2"));
        	values[i++] = play_game.get("player5_card_2");
        	dataAttribs.addElement(new Attribute("player6_card_1"));
        	values[i++] = play_game.get("player6_card_1");
        	dataAttribs.addElement(new Attribute("player6_card_2"));
        	values[i++] = play_game.get("player6_card_2");
        }else if(model_type== 2) {
        	dataAttribs.addElement(new Attribute("dealer_card"));
        	values[i++] = play_game.get("dealer_card");
        	dataAttribs.addElement(new Attribute("player1_card_sum"));
        	values[i++] = play_game.get("player1_card_sum");
        	dataAttribs.addElement(new Attribute("stand_hit"));
        	values[i++] = play_game.get("stand_hit");
        } else if(model_type== 3) {
        	dataAttribs.addElement(new Attribute("dealer_card"));
        	values[i++] = play_game.get("dealer_card");
        	dataAttribs.addElement(new Attribute("player1_card_1"));
        	values[i++] = play_game.get("player1_card_1");
        	dataAttribs.addElement(new Attribute("player1_card_2"));
        	values[i++] = play_game.get("player1_card_2");
        	
        	dataAttribs.addElement(new Attribute("stand_hit"));
        	values[i++] = play_game.get("stand_hit");
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