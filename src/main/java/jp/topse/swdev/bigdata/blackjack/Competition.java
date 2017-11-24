package jp.topse.swdev.bigdata.blackjack;

import jp.topse.swdev.bigdata.blackjack.hyogo.HyogoDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.ishitobi.IshitobiDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.kusanagi.KusanagiDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.sanada.DecisionMakerAvr;
import jp.topse.swdev.bigdata.blackjack.yamamoto.MyDecisionMaker;
import jp.topse.swdev.bigdata.blackjack.yoshimura.yoshi_DecisionMaker;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by doi on 2017/11/16.
 */
public class Competition {

    public static void main(String[] args) {
        Competition competition = new Competition();
        competition.game();
    }

    public void game() {
        for (int i = 1; i <= 3; ++i) {
            game(i);
        }
    }

    private void game(int index) {
        PrintWriter logger = null;
        try {
            logger = new PrintWriter(new BufferedWriter(new FileWriter("./logs/" + index + ".csv", false)));

            Deck deck = Deck.createTestDeck(index);
            Player[] players = new Player[]{
                    new Player("Hyogo", new HyogoDecisionMaker(index)),
                    new Player("Ishitobi", new IshitobiDecisionMaker(index)),
                    new Player("Kusanagi", new KusanagiDecisionMaker(index)),
                    new Player("Sanada", new DecisionMakerAvr(index)),
                    new Player("Yamamoto", new MyDecisionMaker(index)),
                    new Player("Yoshimura", new yoshi_DecisionMaker(index)),
            };
            Map<Player, Integer> pointsMap = eval(players, deck, logger);
            showResult("Game " + index, pointsMap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.close();
        }
    }

    private Map<Player, Integer> eval(Player[] players, Deck deck, PrintWriter logger) {
        Map<Player, Integer> pointsMap = new HashMap<Player, Integer>();
        for (Player player : players) {
            pointsMap.put(player, 0);
        }

        Permutations<Player> permutations = new Permutations<Player>(players);
        while (permutations.hasNext()) {
            Player[] list = permutations.next();
            for (int i = 0; i < 1; ++i) {
                Map<Player, Result.Type> standings = doOneGame(list, deck, logger);
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

    private Map<Player, Result.Type> doOneGame(Player[] players, Deck deck, PrintWriter logger) {
        deck.reset();
        Game game = new Game(deck);

        for (Player player : players) {
            game.join(player);
        }
        game.setup();
        game.start();

        Result result = game.result();
        logger.println(result.toString());
        return result.getStandings();
    }

    private void showResult(String name, Map<Player, Integer> pointsMap) {
        System.out.println("Results of " + name);
        pointsMap.entrySet().stream().forEach((entry) -> {
            System.out.println(entry.getKey().getName() + " : " + entry.getValue());
        });
    }
}
