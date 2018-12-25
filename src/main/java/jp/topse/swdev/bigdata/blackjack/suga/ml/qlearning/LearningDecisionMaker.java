package jp.topse.swdev.bigdata.blackjack.suga.ml.qlearning;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class LearningDecisionMaker implements DecisionMaker {
	//	private static Classifier cls;
	//	static {
	//		try {
	//			cls = (Classifier) weka.core.SerializationHelper.read("tree.model");
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	@Override
	public Action decide(Player _player, Game _game, QLearning learn) {
		int dealer = _game.getUpCard().getValue();
		Hand playerhand = _game.getPlayerHands().get(_player);
		int player = playerhand.eval();
		int hand = getIntHandType(playerhand);
		
		int a = learn.eGreedy(dealer, player, hand); // É√-greedyñ@Ç≈çsìÆÇëIë
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
