package jp.topse.swdev.bigdata.blackjack.yoshimura;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class yoshi_DecisionMaker implements DecisionMaker{

	//private static final String MODEL_PATH= "./src/main/resources/blackjack.model";
	private static final String MODEL_PATH= "./models/yoshimura/blackjack-yoshi-1.model";
	//private static final String MODEL_PATH_T1= "./src/main/resources/blackjack-t1.model";
	//private static final String MODEL_PATH_T2= "./src/main/resources/blackjack-t2.model";
	//private static final String MODEL_PATH_T3= "./src/main/resources/blackjack-t3.model";

    @Override
    public Action decide(Player player, Game game) {
        return analyze(player, game);
    }
    
    private Action analyze(Player player, Game game) {
        try {

            FastVector attributes = new FastVector();
            attributes.addElement(new Attribute("Dealer"));
            attributes.addElement(new Attribute("p1_num"));
            attributes.addElement(new Attribute("p1_hs"));

            FastVector kindValues = new FastVector();
            kindValues.addElement("0");
            kindValues.addElement("1");
            kindValues.addElement("2");
            attributes.addElement(new Attribute("result", kindValues));

            Instances testdata = new Instances("Data1", attributes, 0);
            Instances stand_testdata = new Instances("Data1", attributes, 0);

            double[] hitvalues = new double[testdata.numAttributes()];
            double[] standtvalues = new double[stand_testdata.numAttributes()];

            Map<Player, Hand> playerHands = game.getPlayerHands();
            Hand hand = playerHands.get(player);
            Card dealerCard = game.getUpCard();
            // ディーラーの手札
            hitvalues[0] = dealerCard.getValue();
            standtvalues[0] = dealerCard.getValue();
            // 自手札
            hitvalues[1] = hand.eval();
            standtvalues[1] = dealerCard.getValue();

            // 手札の枚数が5枚以上ならスタンド（ゲームの都合上の判定）
            if (5 <= hand.getCount()){
            	return Action.STAND;
            }
            // HITと仮定
            hitvalues[2] = 1;
            // STANDと仮定
            standtvalues[2] = 0;

            //hitvalues[3] = 0;
            //standtvalues[3] = 0;

            testdata.add(new Instance(1.0, hitvalues));
            testdata.setClassIndex(testdata.numAttributes() - 1);

            stand_testdata.add(new Instance(1.0, standtvalues));
            stand_testdata.setClassIndex(stand_testdata.numAttributes() - 1);

            // モデル読み込み
            Classifier cls_1 = (Classifier) weka.core.SerializationHelper.read(MODEL_PATH);
            //Classifier cls_2 = (Classifier) weka.core.SerializationHelper.read(MODEL_PATH_T2);
            //Classifier cls_3 = (Classifier) weka.core.SerializationHelper.read(MODEL_PATH_T3);

            double label_ht = cls_1.classifyInstance(testdata.instance(0));
            double[] probabilities_ht = cls_1.distributionForInstance(testdata.instance(0));

            double label_st = cls_1.classifyInstance(stand_testdata.instance(0));
            double[] probabilities_st = cls_1.distributionForInstance(stand_testdata.instance(0));

            //System.out.println(label);
            //System.out.println(probabilities_ht[0]);
            //System.out.println(probabilities_ht[1]);
            //System.out.println(probabilities_ht[2]);
            //System.out.println(probabilities_st[0]);
            //System.out.println(probabilities_st[1]);
            //System.out.println(probabilities_st[2]);

            // HITの勝率が高ければ
            if ( probabilities_st[0] <= probabilities_ht[0]){
            	return Action.HIT;
            }else{
            	return Action.STAND;
            }

            //showResult(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Action.STAND;
    }
    
    private Instances loadData(String arffPath) {
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(arffPath)));
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void evalResult(J48 tree, Instances data) {
        try {
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree,  data, 100, new Random(1));
            //System.out.println(eval.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

