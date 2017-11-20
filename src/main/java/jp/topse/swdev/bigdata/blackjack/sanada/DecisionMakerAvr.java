package jp.topse.swdev.bigdata.blackjack.sanada;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.*;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class DecisionMakerAvr implements DecisionMaker {

	// モデル
    private Classifier classModel;
	// モデルパス
	static final String MODEL_PATH = "./models/sanada/deco_avr.model";
	// ARFFファイルパス（Instances用）
	static final String ARFF_PATH = "./models/sanada/result_new_train_avr.arff";
	// HIT,STAND閾値
	static final double THRESHOLD = 0;
	// Attribute
	Attribute atrAverage = new Attribute("AVRAGE", 0);
	Attribute dealer1 = new Attribute("Dealer1", 1);
	Attribute playerSum = new Attribute("PlayerSum", 2);
	// Instances
	Instances instances;
	// ファイル系
	ArffLoader rawatf = new ArffLoader();
	File dataLoader = new File(ARFF_PATH);
	
	// カード枚数Map
	Map<Card, Integer> map = new HashMap<Card, Integer>();
	// カード平均値
	double average = 0;
	
	// コンストラクタ
	public DecisionMakerAvr(int index) {
		try {
			// カード平均値取得
			this.average = getAverage();
			// モデル呼び出し
			importModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Action decide(Player player, Game game) {

		//System.out.println("Let's Go Created Logic");
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand hand = playerHands.get(player);
		Card dealerCard = game.getUpCard();
		

		try {
			// Instances
			rawatf.setFile(dataLoader);
			Instances instances = rawatf.getDataSet();
			instances.setClassIndex(3);
			// インスタンス作成
			Instance instance = new Instance(3);
			instance.setValue(atrAverage, this.average);
			instance.setValue(dealer1, dealerCard.getValue());
			instance.setValue(playerSum, hand.eval());			
			instance.setDataset(instances);

			// 評価結果を取得
			double result = classModel.classifyInstance(instance);
			// HIT,STANDの設定およびリターン
			if (result > THRESHOLD) {
				return Action.HIT;
			} else {
				return Action.STAND;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Force STAND");
		return Action.STAND;
	}
	
    public void importModel() throws Exception {
        classModel = (Classifier) weka.core.SerializationHelper.read(MODEL_PATH);
    }
    
    private double getAverage() {
    	
    	double retVal = 0;
    	double sumValue = 0;
    	double countCard = 0;
    	
    	// Deck生成
    	Deck dammyDeck = Deck.createDefault();
//      Deck dammyDeck = Deck.createTest1Deck();
//      Deck dammyDeck = Deck.createTest2Deck();
//      Deck dammyDeck = Deck.createTest3Deck();
    	
    	// カードがなくなるまでnextCard()をする
    	try {
    		Card card;
            do {
            	card = dammyDeck.nextCard();
            	sumValue += card.getValue();
            	countCard += 1;
            } while (true);
    	} catch (RuntimeException e) {
    		// カードが空っぽになったらRuntimeExceptionになるためループを抜ける
    	}
    	retVal = sumValue / countCard;
    	return retVal;
    }
}
