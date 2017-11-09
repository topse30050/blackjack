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

/*
    	for (int thr_val = 10; thr_val < 21 ; thr_val ++) {
    		Player[] players = new Player[] {
    				new Player("Aice",new KusanagiDecisionMaker(thr_val)), 
    				new Player("Bob",new KusanagiDecisionMaker(thr_val)), 
    				new Player("Charlie",new KusanagiDecisionMaker(thr_val)),
    				new Player("Dave",new KusanagiDecisionMaker(thr_val)), 
    				new Player("Ellen",new KusanagiDecisionMaker(thr_val)), 
    				new Player("Frank")
    		};
    		Demo demo = new Demo(players);
    		demo.eval();
    	}    	
*/
  

	Player[] players = new Player[] {
				new Player("Aice",new KusanagiDecisionMaker()), 
				new Player("Bob",new KusanagiDecisionMaker()), 
				new Player("Charlie",new KusanagiDecisionMaker()),
				new Player("Dave",new KusanagiDecisionMaker()), 
				new Player("Ellen",new KusanagiDecisionMaker()), 
				new Player("Frank")
		};
		Demo demo = new Demo(players);
		demo.eval();
    }
    private Player[] players = null;

    public Demo(Player[] players) {
        this.players = players;
    }

    private void eval() {
/*
    	Permutations<Player> permutations = new Permutations<Player>(players);
        while (permutations.hasNext()) {
           Player[] list = permutations.next();
            for (int i = 0; i < 1;++i) {
                doOneGame(list);
            }
        }
*/  
    	
    	for (int i = 0; i < 1000;++i) {
    		doOneGame(players);
    	}

    }
    

    private void doOneGame(Player[] players) {
//        Deck deck = Deck.createDefault();
        //Deck deck = Deck.createTest1Deck();
//        Deck deck = Deck.createTest2Deck();
        Deck deck = Deck.createTest3Deck();

        Game game = new Game(deck);

        for (Player player : players) {
            game.join(player);
      }
        
        game.setup();

        game.start();

        Result result = game.result();

        System.out.println(result.toString());
    }
}
