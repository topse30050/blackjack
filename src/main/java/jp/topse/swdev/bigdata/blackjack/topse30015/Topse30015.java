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
      DataSource source = new DataSource("models/test.arff");
      dataset = source.getDataSet();
      dataset.setClassIndex(dataset.numAttributes() - 1);
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
    instance.setDataset(dataset);
    double ret = -1;
    try {
      ret = tree.classifyInstance(instance);
//      System.out.println(ret);
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
