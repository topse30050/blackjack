package main.java.jp.topse.swdev.bigdata.blackjack.topse30015;

import java.util.Map;
import jp.topse.swdev.bigdata.blackjack.*;

public class Topse30015 implements DecisionMaker {
  @Override
  public Action decide(Player player, Game game) {
    Map<Player, Hand> playerHands = game.getPlayerHands();
    Hand hand = playerHands.get(player);
    if (hand.eval()< 17) {
        return Action.HIT;
    } else {
        return Action.STAND;
    }
  }
}
