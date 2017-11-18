package jp.topse.swdev.bigdata.blackjack.ishitobi;

import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.ishitobi.DecisionSVC3rd;
import jp.topse.swdev.bigdata.blackjack.ishitobi.DecisionSVC4th;
import jp.topse.swdev.bigdata.blackjack.ishitobi.DecisionSVC5th;


public class IshitobiDecisionMaker implements DecisionMaker {

	@Override
	public Action decide(Player player, Game game) {
		// TODO Auto-generated method stub
		Hand hand = getPlayerHandByName(game, player.getName());
//		ディーラーと各プレイヤーの手札を得る
		Hand alice_hand = getPlayerHandByIndex(game, 0);
        Hand bob_hand = getPlayerHandByIndex(game, 1);
        Hand charlie_hand = getPlayerHandByIndex(game, 2);
        Hand dave_hand = getPlayerHandByIndex(game, 3);
        Hand ellen_hand = getPlayerHandByIndex(game, 4);
        Hand frank_hand = getPlayerHandByIndex(game, 5);
//		Hand alice_hand = getPlayerHandByName(game, "Alice");
//		Hand bob_hand = getPlayerHandByName(game, "Bob");
//		Hand charlie_hand = getPlayerHandByName(game, "Charlie");
//		Hand dave_hand = getPlayerHandByName(game, "Dave");
//		Hand ellen_hand = getPlayerHandByName(game, "Ellen");
//		Hand frank_hand = getPlayerHandByName(game, "Frank");

		String td1 = game.getUpCard().name();
	
        String tp11 = alice_hand.get(0).name();
		String tp12 = alice_hand.get(1).name();
		String tp13 = "";
		String tp14 = "";
		String tp21 = bob_hand.get(0).name();
		String tp22 = bob_hand.get(1).name();
		String tp23 = "";
		String tp24 = "";
		String tp31 = charlie_hand.get(0).name();
		String tp32 = charlie_hand.get(1).name();           
		String tp33 = "";
		String tp34 = "";		
		String tp41 = dave_hand.get(0).name();
		String tp42 = dave_hand.get(1).name();           
		String tp43 = "";
		String tp44 = ""; 
		String tp51 = ellen_hand.get(0).name();
		String tp52 = ellen_hand.get(1).name();           
		String tp53 = "";
		String tp54 = "";
		String tp61 = frank_hand.get(0).name();
		String tp62 = frank_hand.get(1).name();           
		String tp63 = "";
		String tp64 = "";
		
//ここから実際の処理		
        DecisionSVC3rd svc3rd = new DecisionSVC3rd();
        DecisionSVC4th svc4th = new DecisionSVC4th();
        DecisionSVC5th svc5th = new DecisionSVC5th();

    	if(hand.getCount() == 0){
    		return Action.HIT;
    	}
    	//二枚目を持っていなければ引く
    	else if(hand.getCount() == 1){
    		return Action.HIT;
    	}
    	//三枚目の判定
    	else if(hand.getCount() == 2){
    		double result = svc3rd.main(td1,tp11,tp12,tp21,tp22,tp31,tp32,tp41,tp42,tp51,tp52,tp61,tp62);
			if(result == 0){
    			return Action.STAND;
    		}else{
    			return Action.HIT;
    		}
       	}
    	//四枚目の判定
    	else if(hand.getCount() == 3){
    		if(alice_hand.getCount() >= 3){
    		 tp13 = alice_hand.get(2).name();
    		}
    		if(bob_hand.getCount() >= 3){
    		 tp23 = bob_hand.get(2).name();   		
    		}
    		if(charlie_hand.getCount() >= 3){
    		 tp33 = charlie_hand.get(2).name();  		
    		}
    		if(dave_hand.getCount() >= 3){
    		 tp43 = dave_hand.get(2).name();
    		}
    		if(ellen_hand.getCount() >= 3){
    		 tp53 = ellen_hand.get(2).name();    		
    		}
    		if(frank_hand.getCount() >= 3){
    		 tp63 = frank_hand.get(2).name();    		
    		}
    		double result = svc4th.main(td1,tp11,tp12,tp13,tp21,tp22,tp23,tp31,tp32,tp33,tp41,tp42,tp43,tp51,tp52,tp53,tp61,tp62,tp63);
			if(result == 0){
    			return Action.STAND;
    		}else{
    			return Action.HIT;
    		}
    	}
    	//五枚目の判定
    	else {
		
    		if(alice_hand.getCount() >= 4){
       		 tp14 = alice_hand.get(3).name();
       		}
       		if(bob_hand.getCount() >= 4){
       		 tp24 = bob_hand.get(3).name();   		
       		}
       		if(charlie_hand.getCount() >= 4){
       		 tp34 = charlie_hand.get(3).name();  		
       		}
       		if(dave_hand.getCount() >= 4){
       		 tp44 = dave_hand.get(3).name();
       		}
       		if(ellen_hand.getCount() >= 4){
       		 tp54 = ellen_hand.get(3).name();    		
       		}
       		if(frank_hand.getCount() >= 4){
       		 tp64 = frank_hand.get(3).name();    		
       		}		
    		
    		double result = svc5th.main(td1,tp11,tp12,tp13,tp14,tp21,tp22,tp23,tp24,tp31,tp32,tp33,tp34,tp41,tp42,tp43,tp44,tp51,tp52,tp53,tp54,tp61,tp62,tp63,tp64);
			if(result == 0){
    			return Action.STAND;
    		}else{
    			return Action.HIT;
    		}
    	}
	}
	
	private Hand getPlayerHandByName(Game game, String name) {
        Map<Player, Hand> playerHands = game.getPlayerHands();
        for (Player player: playerHands.keySet()) {
        	if (player.getName().equals(name)) {
        		return playerHands.get(player);
        	}
        }
        return null;
	}

	private Hand getPlayerHandByIndex(Game game, int index) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		int i = 0;
		for (Player player: playerHands.keySet()) {
			if (i == index) {
				return playerHands.get(player);
			}
			++i;
		}
		return null;
	}

}
