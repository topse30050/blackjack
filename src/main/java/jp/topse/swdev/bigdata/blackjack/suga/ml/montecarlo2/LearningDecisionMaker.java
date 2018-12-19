package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo2;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class LearningDecisionMaker implements DecisionMaker {
	@Override
	public Action decide(Player _player, Game _game, MonteCarlo2 learn) {
		int dealer = _game.getUpCard().getValue();
		Hand playerhand = _game.getPlayerHands().get(_player);
		int player = playerhand.eval();
		int hand = getIntHandType(playerhand);
		int drawnum = 2; // dealer•ª
		for (Hand h : _game.getPlayerHands().values()) {
			drawnum += h.getCount();
		}
		
		int a = learn.eGreedy(dealer, player, hand, drawnum); // ƒÃ-greedy–@‚Ås“®‚ğ‘I‘ğ
		if (a == Action.STAND.ordinal()) {
			return Action.STAND;
		} else {
			return Action.HIT;
		}
	}

	static int getIntHandType(Hand hand) {
		int nextplayer = hand.eval();
		int nextsum = 0;
		for (int j = 0; j < hand.getCount(); j++) {
			Card card = hand.get(j);
			if (card == Card.ACE)
				nextsum = nextsum - 10;
			nextsum = nextsum + card.getValue();
		}
		//		System.out.println("nextplayer:" + nextplayer);
		//		System.out.println("nextsum:" + nextsum);
		return (nextsum == nextplayer) ? HandType.HARD.ordinal() : HandType.SOFT.ordinal();
	}
}
