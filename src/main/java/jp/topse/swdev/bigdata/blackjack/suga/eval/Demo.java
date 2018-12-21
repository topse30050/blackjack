package jp.topse.swdev.bigdata.blackjack.suga.eval;

import java.text.DecimalFormat;

import jp.topse.swdev.bigdata.blackjack.Deck;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.Result;
import jp.topse.swdev.bigdata.blackjack.suga.SugaDecisionMaker;

/**
 * Created by doi on 2017/09/28.
 */
public class Demo {

	public static void main(String[] args) {
		Player[] players = new Player[] {
				new Player("suga", new SugaDecisionMaker()),
				new Player("suga", new SugaDecisionMaker()),
				new Player("suga", new SugaDecisionMaker()),
				new Player("suga", new SugaDecisionMaker()),
		// new Player("monte2", new MonteCarlo2DecisionMaker(
		// "models/suga/monte2.csv")),
		// new Player("monte2", new MonteCarlo2DecisionMaker(
		// "models/suga/monte2.csv")),
		// new Player("monte2", new MonteCarlo2DecisionMaker(
		// "models/suga/monte2.csv")),
		// new Player("monte2", new MonteCarlo2DecisionMaker(
		// "models/suga/monte2.csv")),
		};
		Demo demo = new Demo(players);
		demo.eval();
	}

	private Player[] players = null;

	public Demo(Player[] players) {
		this.players = players;
	}

	private void eval() {
		Result result;
		int[] win = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] draw = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] lose = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int winall = 0;
		int drawall = 0;
		int loseall = 0;
		double winrate;
		int defaultw = 0;
		int defaultd = 0;
		int defaultl = 0;
		double defaultwr = 0.0;
		int wdiffs = 0;
		double wrdiffs = 0.0;
		for (int i = 0; i < 1000000000; ++i) {
			result = doOneGame(players);
			// System.out.println(result.toString());
			for (int j = 0; j < players.length; j++) {
				Result.Type rt = result.getStandings().get(players[j]);
				if (rt == Result.Type.WIN) {
					win[j]++;
					winall++;
				} else if (rt == Result.Type.DRAW) {
					draw[j]++;
					drawall++;
				} else if (rt == Result.Type.LOSE) {
					lose[j]++;
					loseall++;
				}
				if (i % 100000 == 0 && i != 0) {
					winrate = (double) win[j] / (win[j] + lose[j]);
					if (j == 0) {
						defaultw = win[j];
						defaultd = draw[j];
						defaultl = lose[j];
						defaultwr = winrate;
					}
					DecimalFormat dfnum = new DecimalFormat(
							"+##########0;-###########0");
					DecimalFormat dfdbl1 = new DecimalFormat("#.00000");
					DecimalFormat dfdbl2 = new DecimalFormat(
							"+#.00000;-#.00000");
					System.out.println("[" + players[j].getName() + "] win : "
							+ win[j] + " (" + dfnum.format(win[j] - defaultw)
							+ ")");
					System.out.println("[" + players[j].getName() + "] draw: "
							+ draw[j] + " (" + dfnum.format(draw[j] - defaultd)
							+ ")");
					System.out.println("[" + players[j].getName() + "] lose: "
							+ lose[j] + " (" + dfnum.format(lose[j] - defaultl)
							+ ")");
					System.out.println("[" + players[j].getName()
							+ "] winrate: " + dfdbl1.format(winrate) + " ("
							+ dfdbl2.format(winrate - defaultwr) + ")");
					// if (j != 0) {
					// wdiffs = wdiffs + win[j] - defaultw;
					// wrdiffs = wrdiffs + winrate - defaultwr;
					// System.out.println("wdiffs:  " + wdiffs);
					// System.out.println("wrdiffs: " + wrdiffs);
					// }
					win[j] = 0;
					draw[j] = 0;
					lose[j] = 0;
				}
			}
			if (i % 100000 == 0 && i != 0) {
				System.out.println("winrate(all): " + (double) winall
						/ (winall + loseall));
			}
		}
		// learn.printQ();
	}

	private Result doOneGame(Player[] players) {
		return null;
//		Deck deck = Deck.createQLearningDeck();
//
//		Game game = new Game(deck);
//
//		for (Player player : players) {
//			game.join(player);
//		}
//
//		game.setup();
//
//		game.start();
//
//		Result result = game.result();
//
//		// System.out.println(result.toString());
//
//		return result;
	}
}
