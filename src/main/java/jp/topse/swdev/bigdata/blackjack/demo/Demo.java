package jp.topse.swdev.bigdata.blackjack.demo;

import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Deck;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.KusanagiDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.Result;

/**
 * Created by doi on 2017/09/28.
 */
public class Demo {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; ++i) {
            doOneGame();
        }
    }

    private static void doOneGame() {
        Deck deck = Deck.createDefault();
        
        Game game = new Game(deck);
        game.join(new Player("Alice"));
        game.join(new Player("Bob"));
 	    game.join(new Player("Charlie"));
        game.join(new Player("Nagi",new KusanagiDecisionMaker()));
        game.setup();	
        game.start();

        Result result = game.result();

        System.out.println(result.toString());
    }
}
