package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Player;

public class Topse30050AI implements DecisionMaker {

	/**
	 * 今のところ即スタンド。
	 */
	@Override
	public Action decide(Player player, Game game) {
		Action decidedAction = Action.STAND;

		System.out.println();
		System.out.println("ディーラーのアップカード:" + game.getUpCard().getValue());
		System.out.println(player.getName() + ":" + game.getPlayerHands().get(player).eval());
		System.out.println();

		if (game.getPlayerHands().get(player).eval() < 17) {
			decidedAction = Action.HIT;
		}

		return decidedAction;
	}

}
