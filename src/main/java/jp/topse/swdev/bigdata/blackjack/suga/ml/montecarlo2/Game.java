package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.suga.ml.Deck;

/**
 * Created by doi on 2017/09/27.
 */
public class Game {

	private Deck deck;

	private Hand dealerHand = new Hand();
	private List<Player> players = new LinkedList<Player>();
	private Map<Player, Hand> playerHands = new HashMap<Player, Hand>();

	private boolean isInitialized;
	private boolean isFinished;

	public Game(Deck deck) {
		isInitialized = false;
		isFinished = false;
		this.deck = deck;
	}

	public void join(Player player) {
		if (isInitialized) {
			throw new RuntimeException("Game is already setup.");
		}

		players.add(player);
		playerHands.put(player, new Hand());
	}

	public int getNumberOfPlayers() {
		return playerHands.size();
	}

	public void setup() {
		if (isInitialized) {
			throw new RuntimeException("Game is already setup.");
		}

		dealerHand.add(deck.nextCard());
		dealerHand.add(deck.nextCard());

		playerHands.entrySet().forEach(entry -> {
			entry.getValue().add(deck.nextCard());
			entry.getValue().add(deck.nextCard());
		});

		isInitialized = true;
	}

	public Hand getDealerHand() {
		if (!isFinished) {
			throw new RuntimeException("Game is not finished.");
		}
		return dealerHand;
	}

	public Card getUpCard() {
		if (!isInitialized) {
			throw new RuntimeException("Game is not setup.");
		}

		return dealerHand.get(0);
	}

	public Map<Player, Hand> getPlayerHands() {
		if (!isInitialized) {
			throw new RuntimeException("Game is not setup.");
		}
		return playerHands;
	}

	public MonteCarlo2 start(MonteCarlo2 learn) {
		MonteCarlo2 ret = learn;
		List<Qarg> qargs0 = new LinkedList<Qarg>();
		List<Qarg> qargs1 = new LinkedList<Qarg>();
		List<Qarg> qargs2 = new LinkedList<Qarg>();
		List<Qarg> qargs3 = new LinkedList<Qarg>();
		int[] player = { 0, 0, 0, 0 };
		int[] hand = { 0, 0, 0, 0 };
		int[] drawnum = { 0, 0, 0, 0 };
		Action[] action = {null, null, null, null};
		for (int i = 0; i < players.size(); i++) {
			while (true) {
				player[i] = playerHands.get(players.get(i)).eval();
				hand[i] = LearningDecisionMaker.getIntHandType(playerHands
						.get(players.get(i)));
				drawnum[i] = 2; // dealer•ª
				for (Hand h : playerHands.values()) {
					drawnum[i] += h.getCount();
				}
				action[i] = players.get(i).action(this, ret);
				if (action[i] == Action.STAND) {
					break;
				} else if (action[i] == Action.HIT) {
					playerHands.get(players.get(i)).add(deck.nextCard());
				}

				int nextplayer = playerHands.get(players.get(i)).eval();
				if (nextplayer == 21) {
					break;
				} else if (nextplayer > 21) {
					break;
				}
				if (i == 0)
					qargs0.add(new Qarg(this.getUpCard().getValue(), player[i],
							hand[i], drawnum[i], action[i].ordinal()));
				else if (i == 1)
					qargs1.add(new Qarg(this.getUpCard().getValue(), player[i],
							hand[i], drawnum[i], action[i].ordinal()));
				else if (i == 2)
					qargs2.add(new Qarg(this.getUpCard().getValue(), player[i],
							hand[i], drawnum[i], action[i].ordinal()));
				else if (i == 3)
					qargs3.add(new Qarg(this.getUpCard().getValue(), player[i],
							hand[i], drawnum[i], action[i].ordinal()));
			}
		}

		while (dealerHand.eval() < 17) {
			dealerHand.add(deck.nextCard());
		}

		for (int i = 0; i < players.size(); i++) {
			int nextplayer = playerHands.get(players.get(i)).eval();
			if (i == 0)
				qargs0.add(new Qarg(this.getUpCard().getValue(), player[i],
						hand[i], drawnum[i], action[i].ordinal()));
			else if (i == 1)
				qargs1.add(new Qarg(this.getUpCard().getValue(), player[i],
						hand[i], drawnum[i], action[i].ordinal()));
			else if (i == 2)
				qargs2.add(new Qarg(this.getUpCard().getValue(), player[i],
						hand[i], drawnum[i], action[i].ordinal()));
			else if (i == 3)
				qargs3.add(new Qarg(this.getUpCard().getValue(), player[i],
						hand[i], drawnum[i], action[i].ordinal()));

			Result.Type result = Result.checkResult(dealerHand,
					playerHands.get(players.get(i)));
			int reward = 0;
			switch (result) {
			case WIN:
				reward = MonteCarlo2.WIN_REWARD;
				break;
			case DRAW:
				reward = MonteCarlo2.DRAW_REWARD;
				break;
			case LOSE:
				reward = MonteCarlo2.LOSE_REWARD;
				break;
			}
			if (reward == MonteCarlo2.LOSE_REWARD && nextplayer == 22)
				reward = MonteCarlo2.BURST_REWARD;
			if (i == 0)
				learn.updateQ(qargs0, reward);
			else if (i == 1)
				learn.updateQ(qargs1, reward);
			else if (i == 2)
				learn.updateQ(qargs2, reward);
			else if (i == 3)
				learn.updateQ(qargs3, reward);
		}

		isFinished = true;
		return ret;
	}

	public Result result() {
		if (!isFinished) {
			throw new RuntimeException("Game is not finished.");
		}

		return new Result(dealerHand, players, playerHands);
	}
}
