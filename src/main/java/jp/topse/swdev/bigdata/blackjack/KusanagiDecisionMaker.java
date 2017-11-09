package jp.topse.swdev.bigdata.blackjack;

import java.util.Map;

//import jp.topse.swdev.bigdata.blackjack.Player.SimpleDecisionMaker;

public class KusanagiDecisionMaker implements DecisionMaker {

	
    private Integer threshold_value;
    public KusanagiDecisionMaker(Integer  value) {
    	threshold_value = value;
    
    }
    public KusanagiDecisionMaker() {
    	threshold_value = 17;
    }

	
	
	@Override
    public Action decide(Player player, Game game) {
        Map<Player, Hand> playerHands = game.getPlayerHands();
        Hand hand = playerHands.get(player);

        /*
    	for(Player p : game.getPlayerHands().keySet()) {
            String name = p.getName();
            if(!player.getName().equals(name)){
            	Hand other_hand = playerHands.get(p);
            	System.out.println(name);
            	System.out.println(other_hand.eval());            	
            	System.out.println(other_hand.get(0).getValue());
            	System.out.println(other_hand.get(1).getValue());
            	System.out.println(other_hand.getCount());
            }
    	}
         */

        
        if (hand.eval()< threshold_value) {
            return Action.HIT;
        } else {
            return Action.STAND;
        }
    }
    
}

