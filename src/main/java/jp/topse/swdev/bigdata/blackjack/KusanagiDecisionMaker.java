package jp.topse.swdev.bigdata.blackjack;

import java.util.Map;

public class KusanagiDecisionMaker implements DecisionMaker {
    @Override
    public Action decide(Player player, Game game) {
        Map<Player, Hand> playerHands = game.getPlayerHands();
        Hand hand = playerHands.get(player);
        
    	for(Player p : game.getPlayerHands().keySet()) {
            String name = p.getName();
            if(!player.getName().equals(name)){
            	Hand other_hand = playerHands.get(p);
            	System.out.println(name);
            	System.out.println(other_hand.eval());            	
            	System.out.println(other_hand.get(0).getValue());
	            System.out.println(other_hand.get(1).getValue());
            }
    	}


        
        if (hand.eval()< 17) {
            return Action.HIT;
        } else {
            return Action.STAND;
        }
    }
    
}

class ModelImport {
    private Classifier classModel;
    private String classModelFile = "src\\main\\resources\\iris.model";

    public ModelImport() throws Exception {
        classModel = (Classifier) weka.core.SerializationHelper.read(classModelFile);
    }

    public String classifySpecies(Hashtable<String, String> measures) throws Exception {
        FastVector dataClasses = new FastVector();
        FastVector dataAttribs = new FastVector();
        Attribute species;
        double values[] = new double[measures.size() + 1];
        int i = 0, maxIndex = 0;

        //  "dataClasses"に想定しうるアヤメの種別をセット
        dataClasses.addElement("Iris-setosa");
        dataClasses.addElement("Iris-versicolor");
        dataClasses.addElement("Iris-virginica");
        species = new Attribute("species", dataClasses);

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