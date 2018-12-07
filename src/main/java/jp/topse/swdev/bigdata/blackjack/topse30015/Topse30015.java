package jp.topse.swdev.bigdata.blackjack.topse30015;

import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

@SuppressWarnings("deprecation")
public class Topse30015 implements DecisionMaker {
  private Instances dataset;
  private Classifier tree;

  public Topse30015() {
    // 学習に使用したデータの読み込み、学習モデルの読み込み
    try {
      DataSource source = new DataSource("models/topse30015.arff");
      dataset = source.getDataSet();
      dataset.setClassIndex(dataset.numAttributes() - 1);
      tree = (Classifier)SerializationHelper.read("models/topse30015.model");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Override
  public Action decide(Player player, Game game) {
    Map<Player, Hand> playerHands = game.getPlayerHands();
    Hand myHand = playerHands.get(player);

    FastVector cardKind = new FastVector(14);
    cardKind.addElement("ACE");
    cardKind.addElement("TWO");
    cardKind.addElement("THREE");
    cardKind.addElement("FOUR");
    cardKind.addElement("FIVE");
    cardKind.addElement("SIX");
    cardKind.addElement("SEVEN");
    cardKind.addElement("EIGHT");
    cardKind.addElement("NINE");
    cardKind.addElement("TEN");
    cardKind.addElement("JACK");
    cardKind.addElement("QUEEN");
    cardKind.addElement("KING");
    cardKind.addElement("NONE");

    FastVector result = new FastVector(2);
    result.addElement("burst");
    result.addElement("non-burst");

    Instance instance = new DenseInstance(13);
    Attribute dealer = new Attribute("dealer", cardKind, 0);
    Attribute[] players = new Attribute[8];
    players[0] = new Attribute("player1-1", cardKind, 1);
    players[1] = new Attribute("player1-2", cardKind, 2);
    players[2] = new Attribute("player2-1", cardKind, 3);
    players[3] = new Attribute("player2-2", cardKind, 4);
    players[4] = new Attribute("player3-1", cardKind, 5);
    players[5] = new Attribute("player3-2", cardKind, 6);
    players[6] = new Attribute("player4-1", cardKind, 7);
    players[7] = new Attribute("player4-2", cardKind, 8);
    Attribute[] addCards = new Attribute[2];
    addCards[0] = new Attribute("card-1", cardKind, 9);
    addCards[1] = new Attribute("card-2", cardKind, 10);
    Attribute total = new Attribute("total", 11);
    Attribute take = new Attribute("result", result, 12);

    instance.setValue(dealer, game.getUpCard().toString());
    int i = 0;
    for (Map.Entry<Player, Hand> entry: playerHands.entrySet()) {
      Hand hand = entry.getValue();
      instance.setValue(players[i], hand.get(0).toString());
      instance.setValue(players[i], hand.get(1).toString());
      ++i;
    }

    String[] cards = new String[2];
    cards[0] = "NONE";
    cards[1] = "NONE";
    for (int j = 2; j < myHand.getCount(); ++j) {
    	if (j == 4) break;
    	cards[j - 2] = myHand.get(j).toString();
    }
    for (int j = 0; j < 2; ++j) {
    	instance.setValue(addCards[j], cards[j]);
    }

    instance.setValue(total, myHand.eval());

    instance.setDataset(dataset);
    double ret = -1;
    try {
      ret = tree.classifyInstance(instance);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (ret > 0) {
      return Action.HIT;
    } else {
      return Action.STAND;
    }
  }
}
