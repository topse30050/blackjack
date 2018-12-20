package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo;

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

	private Deck				deck;

	private Hand				dealerHand	= new Hand();
	private List<Player>		players		= new LinkedList<Player>();
	private Map<Player, Hand>	playerHands	= new HashMap<Player, Hand>();

	private boolean				isInitialized;
	private boolean				isFinished;

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

		playerHands.entrySet()
				.forEach(entry -> {
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

	public MonteCarlo start(MonteCarlo learn) {
		MonteCarlo ret = learn;
		List<Qarg> qargs = new LinkedList<Qarg>();
		int player = 0;
		int hand = 0;
		Action action = null;
		for (int i = 0; i < players.size(); i++) {
			while (true) {
				player = playerHands.get(players.get(i)).eval();
				hand = LearningDecisionMaker.getIntHandType(playerHands.get(players.get(i)));
				action = players.get(i).action(this, ret);
				if (action == Action.STAND) {
					break;
				} else if (action == Action.HIT) {
					playerHands.get(players.get(i)).add(deck.nextCard());
				}

				int nextplayer = playerHands.get(players.get(i)).eval();
				if (nextplayer == 21) {
					break;
				} else if (nextplayer > 21) {
					break;
				}
				qargs.add(new Qarg(this.getUpCard().getValue(), player, hand, action.ordinal()));
			}
		}

		while (dealerHand.eval() < 17) {
			dealerHand.add(deck.nextCard());
		}

		int nextplayer = playerHands.get(players.get(0)).eval();
		qargs.add(new Qarg(this.getUpCard().getValue(), player, hand, action.ordinal()));
		
		Result.Type result = Result.checkResult(dealerHand, playerHands.get(players.get(0)));
		int reward = 0;
		switch (result) {
		case WIN:
			reward = MonteCarlo.WIN_REWARD;
			break;
		case DRAW:
			reward = MonteCarlo.DRAW_REWARD;
			break;
		case LOSE:
			reward = MonteCarlo.LOSE_REWARD;
			break;
		}
		if (reward == MonteCarlo.LOSE_REWARD && nextplayer == 22)
			reward = MonteCarlo.BURST_REWARD;
		learn.updateQ(qargs, reward);

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
