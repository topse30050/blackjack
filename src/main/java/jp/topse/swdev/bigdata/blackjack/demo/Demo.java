package jp.topse.swdev.bigdata.blackjack.demo;

import jp.topse.swdev.bigdata.blackjack.*;

/**
 * Created by doi on 2017/09/28.
 */
public class Demo {

    public static void main(String[] args) {

        Player[] players = new Player[] {
                new Player("Aice"), new Player("Bob"), new Player("Charlie"),
                new Player("Dave"), new Player("Ellen"), new Player("Frank"),
        };
        Demo demo = new Demo(players);
        demo.eval();
    }

    private Player[] players = null;

    public Demo(Player[] players) {
        this.players = players;
    }

    private void eval() {
        Permutations<Player> permutations = new Permutations<Player>(players);
        while (permutations.hasNext()) {
            Player[] list = permutations.next();
            for (int i = 0; i < 100; ++i) {
                doOneGame(list);
            }
        }
    }

    private void doOneGame(Player[] players) {
//        Deck deck = Deck.createDefault();
//        Deck deck = Deck.createTest1Deck();
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
