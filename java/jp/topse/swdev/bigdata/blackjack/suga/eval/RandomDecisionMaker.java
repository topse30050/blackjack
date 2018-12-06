package jp.topse.swdev.bigdata.blackjack.suga.eval;

import java.util.Map;
import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;

public class RandomDecisionMaker implements DecisionMaker {
	private static Random rand = new Random();
	
	@Override
	public Action decide(Player player, Game game) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand hand = playerHands.get(player);
		if (hand.eval() > 11 && rand.nextInt(100) < 50) {
			return Action.STAND;
		} else {
			return Action.HIT;
		}
	}
}
