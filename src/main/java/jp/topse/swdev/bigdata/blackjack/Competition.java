package jp.topse.swdev.bigdata.blackjack;

import jp.topse.swdev.bigdata.blackjack.hyogo.HyogoDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.ishitobi.IshitobiDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.kusanagi.KusanagiDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.sanada.DecisionMakerAvr;
import jp.topse.swdev.bigdata.blackjack.yamamoto.MyDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.yoshimura.yoshi_DecisionMaker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doi on 2017/11/16.
 */
public class Competition {

    public static void main(String[] args) {
        Competition competition = new Competition();
//        competition.game1();
//        competition.game2();
        competition.game3();
    }

    public Competition() {
    }

    public void game1() {
        Deck deck = Deck.createTest1Deck();
        Player[] players = new Player[] {
                new Player("Yamamoto", new MyDecisionMaker()),
                new Player("Ishitobi", new IshitobiDecisionMaker()),
                new Player("Yoshimura", new yoshi_DecisionMaker()),
                new Player("Hyogo", new HyogoDecisionMaker()),
                new Player("Sanada", new DecisionMakerAvr()),
                new Player("Kusanagi", new KusanagiDecisionMaker()),
        };
        Map<Player, Integer> pointsMap = eval(players, deck);
        showResult("Game1", pointsMap);
    }

    public void game2() {
        Deck deck = Deck.createTest2Deck();
        Player[] players = new Player[] {
                new Player("Yamamoto", new MyDecisionMaker()),
                new Player("Ishitobi", new IshitobiDecisionMaker()),
                new Player("Yoshimura", new yoshi_DecisionMaker()),
                new Player("Hyogo", new HyogoDecisionMaker()),
                new Player("Sanada", new DecisionMakerAvr()),
                new Player("Kusanagi", new KusanagiDecisionMaker()),
        };
        Map<Player, Integer> pointsMap = eval(players, deck);
        showResult("Game2", pointsMap);
    }

    public void game3() {
        Deck deck = Deck.createTest3Deck();
        Player[] players = new Player[] {
                new Player("Yamamoto", new MyDecisionMaker()),
                new Player("Ishitobi", new IshitobiDecisionMaker()),
                new Player("Yoshimura", new yoshi_DecisionMaker()),
                new Player("Hyogo", new HyogoDecisionMaker()),
                new Player("Sanada", new DecisionMakerAvr()),
                new Player("Kusanagi", new KusanagiDecisionMaker()),
        };
        Map<Player, Integer> pointsMap = eval(players, deck);
        showResult("Game3", pointsMap);
    }

    private Map<Player, Integer> eval(Player[] players, Deck deck) {
        Map<Player, Integer> pointsMap = new HashMap<Player, Integer>();
        for (Player player : players) {
            pointsMap.put(player, 0);
        }

        Permutations<Player> permutations = new Permutations<Player>(players);
        while (permutations.hasNext()) {
            Player[] list = permutations.next();
            for (int i = 0; i < 1; ++i) {
                Map<Player, Result.Type> standings = doOneGame(list, deck);
                for (Player player : players) {
                    if (standings.get(player) == Result.Type.WIN) {
                        int point = pointsMap.get(player);
                        pointsMap.put(player, point + 1);
                    }
                }

            }
        }

        return pointsMap;
    }

    private Map<Player, Result.Type> doOneGame(Player[] players, Deck deck) {
        deck.reset();
        Game game = new Game(deck);

        for (Player player : players) {
            game.join(player);
        }
        game.setup();
        game.start();

        return game.result().getStandings();
    }

    private void showResult(String name, Map<Player, Integer> pointsMap) {
        System.out.println("Results of " + name);
        pointsMap.entrySet().stream().forEach((entry) -> {
            System.out.println(entry.getKey().getName() + " : " + entry.getValue());
        });
    }
}
