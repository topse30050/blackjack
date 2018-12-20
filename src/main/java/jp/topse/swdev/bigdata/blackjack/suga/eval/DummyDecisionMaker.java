package jp.topse.swdev.bigdata.blackjack.suga.eval;

import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;

public class DummyDecisionMaker implements DecisionMaker {
	@Override
	public Action decide(Player player, Game game) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand hand = playerHands.get(player);
		if (hand.eval() < 18) {
			return Action.HIT;
		} else {
			return Action.STAND;
		}
	}
}
