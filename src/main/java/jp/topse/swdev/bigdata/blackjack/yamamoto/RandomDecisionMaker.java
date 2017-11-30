package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Deck;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.Result;

public class RandomDecisionMaker implements DecisionMaker {

	@Override
	public Action decide(Player player, Game game) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand myhand = playerHands.get(player);
		int total = myhand.eval();
		if(total >= 21 || myhand.getCount() == 5) {
			return Action.STAND;
		}
		if(total < 11) {
			return Action.HIT;
		}
		double value = Math.random();
		return value < 0.5 ? Action.STAND : Action.HIT;
	}

	
	public static void main(String[] args) throws IOException {
		RandomDecisionMaker maker = new RandomDecisionMaker();
		maker.createTrainDataByMyself(1);
		maker.createTrainDataByMyself(2);
		maker.createTrainDataByMyself(3);
	}
	
	void createTrainDataByMyself(int deckNum) throws IOException {
        Player player = new Player("RANDOM" , new RandomDecisionMaker());

		try(FileWriter fw = new FileWriter("./models/yamamoto/mytrain"+deckNum+".csv");) {
            for (int i = 0; i < 50000; ++i) {
                createTrainDataByMyself(deckNum, fw, player);
            }
    	}
	}
	
	private void createTrainDataByMyself(int deckNum, FileWriter fw, Player player) throws IOException {
        Deck deck = null;
        if (deckNum == 1) {
        	deck = Deck.createTest1Deck();
        } else if (deckNum == 2) {
        	deck = Deck.createTest2Deck();
        } else if (deckNum == 3){
        	deck = Deck.createTest3Deck();
        } else {
    		deck = Deck.createDefault();
        }
        Game game = new Game(deck);
        game.join(player);
        game.setup();
        game.start();

        Result result = game.result();

        System.out.println(result.toString());
        fw.write(result.toString()+"\r\n");
        Result.Type t = result.getStandings().get(player);
	}
}
