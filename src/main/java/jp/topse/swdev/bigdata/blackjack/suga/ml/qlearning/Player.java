package jp.topse.swdev.bigdata.blackjack.suga.ml.qlearning;

import jp.topse.swdev.bigdata.blackjack.Action;

/**
 * Created by doi on 2017/09/27.
 */
public class Player {

    private String name;
    private DecisionMaker decisionMaker;

    public Player(String name, DecisionMaker decisionMaker) {
        this.name = name;
        this.decisionMaker = decisionMaker;
        //System.out.println("Player " + name + " was created.");
    }

    public String getName() {
        return name;
    }

    public Action action(Game game, QLearning learn) {
        return decisionMaker.decide(this, game, learn);
    }

}
