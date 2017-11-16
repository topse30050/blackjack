package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Deck;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.Classifier;
import weka.core.*;

public class J48SampleDecisionMaker implements DecisionMaker {
	Classifier model;
	
    Attribute number;
    Attribute small;
    Attribute large;
    Attribute total;
    Attribute dealer;
	FastVector bool;
    Attribute take;
    
    Instances instances;
   
   
    
	public J48SampleDecisionMaker(boolean trainMyself) {
	    J48Sample app = new J48Sample();

		try {
			CSVReader reader = new CSVReader();
			reader.read(new File("data¥¥train-20171102-3.txt"));
			reader.writeCSV("mymodel.csv");
			
			instances = app.readCSV("mymodel.csv");
			//instances = app.readCSV("H:¥¥work¥¥bigdata¥¥model.csv");
	        this.model = app.createModel(instances);
	        
	        if(trainMyself) {
	        	createTrainDataByMyself();
	        	reader = new CSVReader();
				reader.read(new File("myresult.csv"));
				instances = app.readCSV("mymodel.csv");
		        this.model = app.createModel(instances);
	        }
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	    number = new Attribute("number", 0);
	    small = new Attribute("small", 1);
	    large = new Attribute("large", 2);
	    total = new Attribute("total", 3);
	    dealer = new Attribute("dealer", 4);
		bool = new FastVector(2);
	    bool.addElement("true");
	    bool.addElement("false");
	    take = new Attribute("take", bool, 5);
	}

	@Override
	public Action decide(Player player, Game game) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand myhand = playerHands.get(player);
		if(myhand.eval() >= 21) {
			return Action.STAND;
		}
		
		Card firstCard = myhand.get(0);
		Card secondCard = myhand.get(1);
		int first = firstCard.getValue();
		int second = secondCard.getValue();
		int small;
		int large;
		int total;
		if(first < second) {
			small = first;
			large = second;
		} else {
			small = second;
			large = first;
		}
		total = myhand.eval();//small + large;// 2回目に引くことを考えると、合計はevalで出したほうがよいか。ただ、学習方法と違ってしまうけど。
		int dealer = game.getUpCard().getValue();
		Instance take = instance(small, large, total, dealer, "true");
		Instance not_take = instance(small, large, total, dealer, "false");
		
    	take.setDataset(instances);
    	not_take.setDataset(instances);
    	try {
    		double classifyTake = model.classifyInstance(take);
    		double classifyNotTake = model.classifyInstance(not_take);
    		
    		if(classifyTake != classifyNotTake) {
    			if(classifyTake == 0.0) {//引く
    				//System.out.println("HIT!");
    				return Action.HIT;
    			} else {//引かない
    				return Action.STAND; 
    			}
    		}
    		
			double[] distTake = model.distributionForInstance(take);
			double[] distNotTake = model.distributionForInstance(not_take);
			
			if(distTake[1] > distNotTake[1]) {
				if(classifyTake == 0.0) {//引く
    				//System.out.println("HIT!");

					return Action.HIT;
    			} else {//引かない
    				return Action.STAND; 
    			}
			} else if (distNotTake[1] > distTake[1]) {
				if(classifyNotTake == 0.0) {//引く
    				//System.out.println("HIT!");

					return Action.HIT;
    			} else {//引かない
    				return Action.STAND; 
    			}
				
			} 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return Action.STAND;
	}

	public Instance instance(int small, int large, int total, int dealer, String take) {
        Instance ins = new Instance(7);
    	ins.setValue(this.number, 1);
    	ins.setValue(this.small, small);
    	ins.setValue(this.large, large);
    	ins.setValue(this.total, total);
    	ins.setValue(this.dealer, dealer);
    	ins.setValue(this.take, take);
    	return ins;
	}

	void createTrainDataByMyself() throws IOException {
        Player player = new Player("TSUBASA" , new J48SampleDecisionMaker(false));

		try(FileWriter fw = new FileWriter("myresult.csv");) {
            for (int i = 0; i < 1000000; ++i) {
                createTrainDataByMyself(fw, player);
            }
    	}
	}
	
	private void createTrainDataByMyself(FileWriter fw, Player player) throws IOException {
        Deck deck = Deck.createDefault();
        Game game = new Game(deck);
        game.join(player);
        game.setup();
        game.start();

        Result result = game.result();

        System.out.println(result.toString());
        fw.write(result.toString()+"¥r¥n");
        Result.Type t = result.getStandings().get(player);
	}
}
