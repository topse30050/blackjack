package jp.topse.swdev.bigdata.blackjack;

import java.util.Hashtable;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.model.GamePrediction;

//import jp.topse.swdev.bigdata.blackjack.Player.SimpleDecisionMaker;

public class KusanagiDecisionMaker implements DecisionMaker {

	GamePrediction prediction;
    private Integer threshold_value;
    public KusanagiDecisionMaker(Integer  value) {
    	threshold_value = value;
		 try {
			 prediction = new GamePrediction();
		 } catch (Exception e){
			 System.out.println(e);
		 }
    
    }
    public KusanagiDecisionMaker() {
    	threshold_value = 17;
		 try {
			 prediction = new GamePrediction();
		 } catch (Exception e){
			 System.out.println(e);
		 }

    }

	
	private int predictionGame(int stand_hit,Player player, Game game){
        Map<Player, Hand> playerHands = game.getPlayerHands();
        Hand hand = playerHands.get(player);
        
        Hashtable<String, Double> testValues = new Hashtable<>();

		 testValues.put("dealer_card", (double)game.getUpCard().getValue());		 

	   	for(Player p : game.getPlayerHands().keySet()) {
			String name = p.getName();
	        
			if(player.getName().equals(name)){
	           	Hand my_hand = playerHands.get(p);
	   		 	testValues.put("player1_card_sum",(double)my_hand.eval());
	   		 	testValues.put("stand_hit", (double)stand_hit);
	         }
	   	}
	   	int other_player_count=0;
	   	for(Player p : game.getPlayerHands().keySet()) {
			String name = p.getName();
	
			if(!player.getName().equals(name)){
	           	Hand other_hand = playerHands.get(p);
	           	double card1=(double)other_hand.get(0).getValue();
	           	double card2=(double)other_hand.get(1).getValue();
	           	if(other_player_count == 0){
		   			testValues.put("player2_card_1", card1);
	   		 		testValues.put("player2_card_2", card2);
	           	}else if(other_player_count == 1){
		   			testValues.put("player3_card_1", card1);
	   		 		testValues.put("player3_card_2", card2);
	           	}else if(other_player_count == 2){
		   			testValues.put("player4_card_1", card1);
	   		 		testValues.put("player4_card_2", card2);
	           	}else if(other_player_count == 3){
		   			testValues.put("player5_card_1", card1);
	   		 		testValues.put("player5_card_2", card2);
	           	}else if(other_player_count == 4){
		   			testValues.put("player6_card_1", card1);
	   		 		testValues.put("player6_card_2", card2);
	           	}
	         	other_player_count++;
	         }
	   	}
		String result;
		try {
			result = prediction.classifySpecies(testValues);
			if(result.equals("WIN")){
		        return 1;			
			}else{
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

        return 0;

	}	
	@Override
    public Action decide(Player player, Game game){
    	if(predictionGame(1,player,game)==1)
    		return Action.HIT;
    	else
    		return Action.STAND;
    }
    
}

