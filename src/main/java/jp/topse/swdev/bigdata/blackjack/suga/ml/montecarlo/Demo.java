package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jp.topse.swdev.bigdata.blackjack.suga.ml.Deck;

/**
 * Created by doi on 2017/09/28.
 */
public class Demo {
	private MonteCarlo learn;
	private Player[] players = null;

	public Demo(Player[] players) {
		this.players = players;
		learn = new MonteCarlo();
	}

	private void eval() {
		learn.initQ();
		Result result;
		int win = 0;
		int draw = 0;
		int lose = 0;
		double winrate;
		for (int i = 0; i < 1000000000; ++i) {
			result = doOneGame(players, learn);
			switch (result.getStandings().get(players[0])) {
			case WIN:
				win++;
				break;
			case DRAW:
				draw++;
				break;
			case LOSE:
				lose++;
				break;
			}
			if (i % 100000 == 0 && i != 0) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String ctime = sdf.format(timestamp);
				FileWriter fw;
				try {
					// fw = new FileWriter("models/suga/monte_" + ctime +
					// ".csv");
					fw = new FileWriter("models/suga/monte2.csv");
					fw.write(learn.printQ());
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				winrate = (double) win / (win + lose);
				System.out.println("win : " + win);
				System.out.println("draw: " + draw);
				System.out.println("lose: " + lose);
				System.out.println("winrate: " + winrate);
				win = 0;
				draw = 0;
				lose = 0;
			}
		}
	}

	private Result doOneGame(Player[] players, MonteCarlo learn) {
		Deck deck = Deck.createQLearningDeck();

		Game game = new Game(deck);

		for (Player player : players) {
			game.join(player);
		}

		game.setup();

		learn = game.start(learn);
		Result result = game.result();
		// System.out.println(result.toString());
		return result;
	}

	public static void main(String[] args) {
		Player[] players = new Player[] {
				new Player("learn1", new LearningDecisionMaker()),
				new Player("learn2", new LearningDecisionMaker()),
//				new Player("learn3", new LearningDecisionMaker()),
//				new Player("learn4", new LearningDecisionMaker()),
				};
		Demo demo = new Demo(players);
		demo.eval();
	}
}
