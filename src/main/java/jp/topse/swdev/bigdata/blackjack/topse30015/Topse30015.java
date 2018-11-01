package main.java.jp.topse.swdev.bigdata.blackjack.topse30015;

import java.util.*;
import jp.topse.swdev.bigdata.blackjack.*;

import weka.core.*;
import weka.classifiers.Classifier;

public class Topse30015 implements DecisionMaker {
  private Classifier tree;

  public Topse30015() {
    try {
      tree = (Classifier)SerializationHelper.read("models/test.model");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Override
  public Action decide(Player player, Game game) {
    Map<Player, Hand> playerHands = game.getPlayerHands();

    Instance instance = new DenseInstance(6);
    Attribute host = new Attribute("host", 0);
    Attribute[] players = new Attribute[5];
    players[0] = new Attribute("player1", 1);
    players[1] = new Attribute("player2", 2);
    players[2] = new Attribute("player3", 3);
    players[3] = new Attribute("player4-1", 4);
    players[4] = new Attribute("player4-2", 5);
    FastVector handClass = new FastVector(2);
    handClass.addElement("STAND");
    handClass.addElement("HIT");
    Attribute take = new Attribute("class", handClass);
    
    instance.setValue(host, game.getUpCard().getValue());
    int i = 0;
    for (Map.Entry<Player, Hand> entry: playerHands.entrySet()) {
      Hand hand = entry.getValue();
      instance.setValue(players[i], hand.get(0).getValue());
      if (i == 3) {
        instance.setValue(players[4], hand.get(1).getValue());
      }
      ++i;
    }
    try {
      //System.out.println(instance);
      double ret = tree.classifyInstance(instance);
      System.out.println(ret);  
    } catch (Exception e) {
      e.printStackTrace();
    }

//    if (hand.eval()< 17) {
//        return Action.HIT;
//    } else {
        return Action.STAND;
//    }
  }
}
