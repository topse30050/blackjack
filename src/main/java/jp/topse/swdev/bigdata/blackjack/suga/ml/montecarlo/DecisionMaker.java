package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo;

import jp.topse.swdev.bigdata.blackjack.Action;

/**
 * Created by doi on 2017/09/28.
 */
public interface DecisionMaker {
    public Action decide(Player player, Game game, MonteCarlo learn);
}
