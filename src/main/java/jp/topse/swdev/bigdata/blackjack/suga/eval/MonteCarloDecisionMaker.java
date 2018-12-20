package jp.topse.swdev.bigdata.blackjack.suga.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class MonteCarloDecisionMaker implements DecisionMaker {
	/*
	 * Q値 [dealer(2-11)] [player(12-21,over] [handtype(soft/hard:0/1)]
	 * [tactics(stand/hit:0/1)]
	 */
	private double q[][][][];
	private final int[] Q_LEN = { 12, 23, HandType.values().length,
			Action.values().length };

	public MonteCarloDecisionMaker() {
		this("models/suga/monte_suga.csv");
	}

	public MonteCarloDecisionMaker(String filename) {
		q = new double[Q_LEN[0]][Q_LEN[1]][Q_LEN[2]][Q_LEN[3]];
		// closeメソッドは自動呼び出しの対象とする。
		try (BufferedReader in = new BufferedReader(new FileReader(new File(
				filename)))) {
			boolean isheader = true;
			String line;

			while ((line = in.readLine()) != null) {
				if (isheader) {
					isheader = false;
				} else {
					String[] vals = line.split(",");
					if (vals.length == 5) {
						int dealer = Integer.valueOf(vals[0].trim());
						int player = Integer.valueOf(vals[1].trim());
						int handtype = Integer.valueOf(vals[2].trim());
						int action = Integer.valueOf(vals[3].trim());
						double qvalue = Double.valueOf(vals[4].trim());
						q[dealer][player][handtype][action] = qvalue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public Action decide(Player _player, Game _game) {
		int dealer = _game.getUpCard().getValue();
		Hand playerhand = _game.getPlayerHands().get(_player);
		int player = playerhand.eval();
		int hand = getIntHandType(playerhand);

		if (player >= 21) {
			return Action.STAND;
		} else if (player < 12) {
			return Action.HIT;
		} else {
			int a = action(dealer, player, hand);
			if (a == Action.STAND.ordinal()) {
				return Action.STAND;
			} else {
				return Action.HIT;
			}
		}
	}

	private int action(int dealer, int player, int type) {
		int select = 0;

		for (int a = 0; a < Q_LEN[3]; a++) {
			if (q[dealer][player][type][select] < q[dealer][player][type][a]) {
				select = a;
			}
		}
		return select;
	}

	private static int getIntHandType(Hand hand) {
		int nextplayer = hand.eval();
		int nextsum = 0;
		for (int j = 0; j < hand.getCount(); j++) {
			Card card = hand.get(j);
			if (card == Card.ACE)
				nextsum = nextsum - 10;
			nextsum = nextsum + card.getValue();
		}
		return (nextsum == nextplayer) ? HandType.HARD.ordinal()
				: HandType.SOFT.ordinal();
	}
}
