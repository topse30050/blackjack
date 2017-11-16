package jp.topse.swdev.bigdata.blackjack.hyogo;

import java.util.ArrayList;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.*;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class HyogoDecisionMaker implements DecisionMaker {

    static int count = 1;
    // TODO 以下の変数は環境に応じて変更する
    static final String MYNAME = "Hyogo";
    static final String MODEL3rd_FILE_PATH = "./models/hyogo/3rdCard_hyogo.model";
    static final String MODEL4th_FILE_PATH = "./models/hyogo/4thCard_hyogo.model";
    static final String MODEL5th_FILE_PATH = "./models/hyogo/5thCard_hyogo.model";


	@Override
	public Action decide(Player player, Game game) {
		// TODO Auto-generated method stub
        Map<Player, Hand> playerHands = game.getPlayerHands();
        Hand hand = playerHands.get(player);
        
        //ToDo：HIT or STANDの判断ロジック
		// 3枚目のカードを引くかどうかを判断する
		if (count ==1) {
	        try {
	        	Classifier classifier = (Classifier)SerializationHelper.read(MODEL3rd_FILE_PATH);
	        
	        	FastVector rl = new FastVector(3);
	            rl.addElement("WIN");
	            rl.addElement("DRAW");
	            rl.addElement("LOSE");
	            FastVector ac = new FastVector(2);
	            ac.addElement("HIT");
	            ac.addElement("STAND");
	            
	            //事例データの定義
	        	Attribute d1 = new Attribute("d1", 0);
	        	Attribute p11 = new Attribute("p11", 1);
	        	Attribute p12 = new Attribute("p12", 2);
	        	Attribute p13 = new Attribute("p13", 3);
	        	Attribute p21 = new Attribute("p21", 4);
	        	Attribute p22 = new Attribute("p22", 5);
	        	Attribute p23 = new Attribute("p23", 6);
	        	Attribute p31 = new Attribute("p31", 7);
	        	Attribute p32 = new Attribute("p32", 8);
	        	Attribute p33 = new Attribute("p33", 9);
	        	Attribute p41 = new Attribute("p41", 10);
	        	Attribute p42 = new Attribute("p42", 11);
	        	Attribute p43 = new Attribute("p43", 12);
	        	Attribute p51 = new Attribute("p51", 13);
	        	Attribute p52 = new Attribute("p52", 14);
	        	Attribute p53 = new Attribute("p53", 15);
	        	Attribute m1 = new Attribute("m1", 16);
	        	Attribute m2 = new Attribute("m2", 17);
	            Attribute result = new Attribute("result",rl,18);
	            Attribute action = new Attribute("action",ac,19);
	            
	            FastVector attributes = new FastVector();
	            attributes.addElement(d1);
	            attributes.addElement(p11);
	            attributes.addElement(p12);
	            attributes.addElement(p13);
	            attributes.addElement(p21);
	            attributes.addElement(p22);
	            attributes.addElement(p23);
	            attributes.addElement(p31);
	            attributes.addElement(p32);
	            attributes.addElement(p33);
	            attributes.addElement(p41);
	            attributes.addElement(p42);
	            attributes.addElement(p43);
	            attributes.addElement(p51);
	            attributes.addElement(p52);
	            attributes.addElement(p53);
	            attributes.addElement(m1);
	            attributes.addElement(m2);
	            attributes.addElement(result);
	            attributes.addElement(action);
	            
	            //事例データのインスタンス作成
	            Instances data = new Instances("TestInstances", attributes, 0);
	            data.setClassIndex(data.numAttributes() - 1);

	            Instance instance = new Instance(data.numAttributes());
	            instance.setDataset(data);
	         
	            //事例データに他プレイヤのカードの値を代入、カードがない場合は0
	            instance.setValue(d1, game.getUpCard().getValue());
	            ArrayList<Integer> pValues = new ArrayList<Integer>();                        
	            for(Player p:game.getPlayerHands().keySet()) {
		        	if(MYNAME.equals(p.getName())) {
		        		//Nothing
		        	} else {
		        		Hand otherHand = game.getPlayerHands().get(p);
		        		for (int i = 0; i < count +2;++i) {
		        			try {
		        				Card card =otherHand.get(i);
		        				pValues.add(card.getValue());
		        			} catch (IndexOutOfBoundsException e) {
		        				pValues.add(0);
		        			}
		        		}
		        	}
		        }
	            
	            //事例データに値をセット
	            instance.setValue(p11,pValues.get(0));
	            instance.setValue(p12,pValues.get(1));
	            instance.setValue(p13,pValues.get(2));
	            instance.setValue(p21,pValues.get(3));
	            instance.setValue(p22,pValues.get(4));
	            instance.setValue(p23,pValues.get(5));
	            instance.setValue(p31,pValues.get(6));
	            instance.setValue(p32,pValues.get(7));
	            instance.setValue(p33,pValues.get(8));
	            instance.setValue(p41,pValues.get(9));
	            instance.setValue(p42,pValues.get(10));
	            instance.setValue(p43,pValues.get(11));
	            instance.setValue(p51,pValues.get(12));
	            instance.setValue(p52,pValues.get(13));
	            instance.setValue(p53,pValues.get(14));
	            
	            //自分のカードの値をゲット
	            ArrayList<Integer> mValues = new ArrayList<Integer>(); 
	            for (int i = 0; i < count +1;++i) {
	        		Card card =hand.get(i);
	        		mValues.add(card.getValue());
	        	}
	            
	            //自分のカードの値をセット
	            instance.setValue(m1,mValues.get(0));
	            instance.setValue(m2,mValues.get(1));
	            
		        //WEKAで分類を実行 	
		        double judge = classifier.classifyInstance(instance);
		        //System.out.println(judge);
		        
		     	count++;		            
	        
		     	//HITかSTANDかの判断
	        	if (judge == 0) {
	        		return Action.HIT;
	        	} else {
	        		count = 1;
	        		return Action.STAND;
	        	}
		     	
	        }  catch (Exception e) {
	            e.printStackTrace();
	            return Action.STAND;
	        }
        }  else if (count == 2) {
        	// 4枚目のカードを引くかどうかを判断する
        	try {
	        	Classifier classifier = (Classifier)SerializationHelper.read(MODEL4th_FILE_PATH);
	        
	        	FastVector rl = new FastVector(3);
	            rl.addElement("WIN");
	            rl.addElement("DRAW");
	            rl.addElement("LOSE");
	            FastVector ac = new FastVector(2);
	            ac.addElement("HIT");
	            ac.addElement("STAND");
	            
	            //事例データの定義
	        	Attribute d1 = new Attribute("d1", 0);
	        	Attribute p11 = new Attribute("p11", 1);
	        	Attribute p12 = new Attribute("p12", 2);
	        	Attribute p13 = new Attribute("p13", 3);
	        	Attribute p14 = new Attribute("p14", 4);
	        	Attribute p21 = new Attribute("p21", 5);
	        	Attribute p22 = new Attribute("p22", 6);
	        	Attribute p23 = new Attribute("p23", 7);
	        	Attribute p24 = new Attribute("p24", 8);
	        	Attribute p31 = new Attribute("p31", 9);
	        	Attribute p32 = new Attribute("p32", 10);
	        	Attribute p33 = new Attribute("p33", 11);
	        	Attribute p34 = new Attribute("p34", 12);
	        	Attribute p41 = new Attribute("p41", 13);
	        	Attribute p42 = new Attribute("p42", 14);
	        	Attribute p43 = new Attribute("p43", 15);
	        	Attribute p44 = new Attribute("p44", 16);
	        	Attribute p51 = new Attribute("p51", 17);
	        	Attribute p52 = new Attribute("p52", 18);
	        	Attribute p53 = new Attribute("p53", 19);
	        	Attribute p54 = new Attribute("p54", 20);
	        	Attribute m1 = new Attribute("m1", 21);
	        	Attribute m2 = new Attribute("m2", 22);
	        	Attribute m3 = new Attribute("m3", 23);
	            Attribute result = new Attribute("result",rl,24);
	            Attribute action = new Attribute("action",ac,25);
	            
	            FastVector attributes = new FastVector();
	            attributes.addElement(d1);
	            attributes.addElement(p11);
	            attributes.addElement(p12);
	            attributes.addElement(p13);
	            attributes.addElement(p14);
	            attributes.addElement(p21);
	            attributes.addElement(p22);
	            attributes.addElement(p23);
	            attributes.addElement(p24);
	            attributes.addElement(p31);
	            attributes.addElement(p32);
	            attributes.addElement(p33);
	            attributes.addElement(p34);
	            attributes.addElement(p41);
	            attributes.addElement(p42);
	            attributes.addElement(p43);
	            attributes.addElement(p44);
	            attributes.addElement(p51);
	            attributes.addElement(p52);
	            attributes.addElement(p53);
	            attributes.addElement(p54);
	            attributes.addElement(m1);
	            attributes.addElement(m2);
	            attributes.addElement(m3);
	            attributes.addElement(result);
	            attributes.addElement(action);
	            
	            //事例データのインスタンス作成
	            Instances data = new Instances("TestInstances", attributes, 0);
	            data.setClassIndex(data.numAttributes() - 1);

	            Instance instance = new Instance(data.numAttributes());
	            instance.setDataset(data);
	         
	            //事例データに他プレイヤのカードの値を代入、カードがない場合は0
	            instance.setValue(d1, game.getUpCard().getValue());
	            ArrayList<Integer> pValues = new ArrayList<Integer>();                        
	            for(Player p:game.getPlayerHands().keySet()) {
		        	if(MYNAME.equals(p.getName())) {
		        		//Nothing
		        	} else {
		        		Hand otherHand = game.getPlayerHands().get(p);
		        		for (int i = 0; i < count +2;++i) {
		        			try {
		        				Card card =otherHand.get(i);
		        				pValues.add(card.getValue());
		        			} catch (IndexOutOfBoundsException e) {
		        				pValues.add(0);
		        			}
		        		}
		        	}
		        }
	            
	            //事例データに値をセット
	            instance.setValue(p11,pValues.get(0));
	            instance.setValue(p12,pValues.get(1));
	            instance.setValue(p13,pValues.get(2));
	            instance.setValue(p14,pValues.get(3));
	            instance.setValue(p21,pValues.get(4));
	            instance.setValue(p22,pValues.get(5));
	            instance.setValue(p23,pValues.get(6));
	            instance.setValue(p24,pValues.get(7));
	            instance.setValue(p31,pValues.get(8));
	            instance.setValue(p32,pValues.get(9));
	            instance.setValue(p33,pValues.get(10));
	            instance.setValue(p34,pValues.get(11));
	            instance.setValue(p41,pValues.get(12));
	            instance.setValue(p42,pValues.get(13));
	            instance.setValue(p43,pValues.get(14));
	            instance.setValue(p44,pValues.get(15));
	            instance.setValue(p51,pValues.get(16));
	            instance.setValue(p52,pValues.get(17));
	            instance.setValue(p53,pValues.get(18));
	            instance.setValue(p54,pValues.get(19));
	            
	            //自分のカードの値をゲット
	            ArrayList<Integer> mValues = new ArrayList<Integer>(); 
	            for (int i = 0; i < count +1;++i) {
	            	try {
		            	Card card =hand.get(i);
		        		mValues.add(card.getValue());
	            	} catch (IndexOutOfBoundsException e) {
	            		mValues.add(0);
	            	}
	        	}
	            
	            //自分のカードの値をセット
	            instance.setValue(m1,mValues.get(0));
	            instance.setValue(m2,mValues.get(1));
	            instance.setValue(m3,mValues.get(2));
	            
		        //WEKAで分類を実行 	
		        double judge = classifier.classifyInstance(instance);
		        //System.out.println(judge);
		        
		     	count++;		            
	        
		     	//HITかSTANDかの判断
	        	if (judge == 0) {
	        		return Action.HIT;
	        	} else {
	        		count = 1;
	        		return Action.STAND;
	        	}
		     	
	        }  catch (Exception e) {
	            e.printStackTrace();
	            return Action.STAND;
	        }
        } else if (count == 3) {
        	//5枚目のカードを引くかどうかを判断する
        	try {
	        	Classifier classifier = (Classifier)SerializationHelper.read(MODEL5th_FILE_PATH);
	        
	        	FastVector rl = new FastVector(3);
	            rl.addElement("WIN");
	            rl.addElement("DRAW");
	            rl.addElement("LOSE");
	            FastVector ac = new FastVector(2);
	            ac.addElement("HIT");
	            ac.addElement("STAND");
	            
	            //事例データの定義
	        	Attribute d1 = new Attribute("d1", 0);
	        	Attribute p11 = new Attribute("p11", 1);
	        	Attribute p12 = new Attribute("p12", 2);
	        	Attribute p13 = new Attribute("p13", 3);
	        	Attribute p14 = new Attribute("p14", 4);
	        	Attribute p15 = new Attribute("p15", 5);
	        	Attribute p21 = new Attribute("p21", 6);
	        	Attribute p22 = new Attribute("p22", 7);
	        	Attribute p23 = new Attribute("p23", 8);
	        	Attribute p24 = new Attribute("p24", 9);
	        	Attribute p25 = new Attribute("p25", 10);
	        	Attribute p31 = new Attribute("p31", 11);
	        	Attribute p32 = new Attribute("p32", 12);
	        	Attribute p33 = new Attribute("p33", 13);
	        	Attribute p34 = new Attribute("p34", 14);
	        	Attribute p35 = new Attribute("p35", 15);
	        	Attribute p41 = new Attribute("p41", 16);
	        	Attribute p42 = new Attribute("p42", 17);
	        	Attribute p43 = new Attribute("p43", 18);
	        	Attribute p44 = new Attribute("p44", 19);
	        	Attribute p45 = new Attribute("p45", 20);
	        	Attribute p51 = new Attribute("p51", 21);
	        	Attribute p52 = new Attribute("p52", 22);
	        	Attribute p53 = new Attribute("p53", 23);
	        	Attribute p54 = new Attribute("p54", 24);
	        	Attribute p55 = new Attribute("p55", 25);
	        	Attribute m1 = new Attribute("m1", 26);
	        	Attribute m2 = new Attribute("m2", 27);
	        	Attribute m3 = new Attribute("m3", 28);
	        	Attribute m4 = new Attribute("m4", 29);
	            Attribute result = new Attribute("result",rl,30);
	            Attribute action = new Attribute("action",ac,31);
	            
	            FastVector attributes = new FastVector();
	            attributes.addElement(d1);
	            attributes.addElement(p11);
	            attributes.addElement(p12);
	            attributes.addElement(p13);
	            attributes.addElement(p14);
	            attributes.addElement(p15);
	            attributes.addElement(p21);
	            attributes.addElement(p22);
	            attributes.addElement(p23);
	            attributes.addElement(p24);
	            attributes.addElement(p25);
	            attributes.addElement(p31);
	            attributes.addElement(p32);
	            attributes.addElement(p33);
	            attributes.addElement(p34);
	            attributes.addElement(p35);
	            attributes.addElement(p41);
	            attributes.addElement(p42);
	            attributes.addElement(p43);
	            attributes.addElement(p44);
	            attributes.addElement(p45);
	            attributes.addElement(p51);
	            attributes.addElement(p52);
	            attributes.addElement(p53);
	            attributes.addElement(p54);
	            attributes.addElement(p55);
	            attributes.addElement(m1);
	            attributes.addElement(m2);
	            attributes.addElement(m3);
	            attributes.addElement(m4);
	            attributes.addElement(result);
	            attributes.addElement(action);
	            
	            //事例データのインスタンス作成
	            Instances data = new Instances("TestInstances", attributes, 0);
	            data.setClassIndex(data.numAttributes() - 1);

	            Instance instance = new Instance(data.numAttributes());
	            instance.setDataset(data);
	         
	            //事例データに他プレイヤのカードの値を代入、カードがない場合は0
	            instance.setValue(d1, game.getUpCard().getValue());
	            ArrayList<Integer> pValues = new ArrayList<Integer>();                        
	            for(Player p:game.getPlayerHands().keySet()) {
		        	if(MYNAME.equals(p.getName())) {
		        		//Nothing
		        	} else {
		        		Hand otherHand = game.getPlayerHands().get(p);
		        		for (int i = 0; i < count +2;++i) {
		        			try {
		        				Card card =otherHand.get(i);
		        				pValues.add(card.getValue());
		        			} catch (IndexOutOfBoundsException e) {
		        				pValues.add(0);
		        			}
		        		}
		        	}
		        }
	            
	            //事例データに値をセット
	            instance.setValue(p11,pValues.get(0));
	            instance.setValue(p12,pValues.get(1));
	            instance.setValue(p13,pValues.get(2));
	            instance.setValue(p14,pValues.get(3));
	            instance.setValue(p15,pValues.get(4));
	            instance.setValue(p21,pValues.get(5));
	            instance.setValue(p22,pValues.get(6));
	            instance.setValue(p23,pValues.get(7));
	            instance.setValue(p24,pValues.get(8));
	            instance.setValue(p25,pValues.get(9));
	            instance.setValue(p31,pValues.get(10));
	            instance.setValue(p32,pValues.get(11));
	            instance.setValue(p33,pValues.get(12));
	            instance.setValue(p34,pValues.get(13));
	            instance.setValue(p35,pValues.get(14));
	            instance.setValue(p41,pValues.get(15));
	            instance.setValue(p42,pValues.get(16));
	            instance.setValue(p43,pValues.get(17));
	            instance.setValue(p44,pValues.get(18));
	            instance.setValue(p45,pValues.get(19));
	            instance.setValue(p51,pValues.get(20));
	            instance.setValue(p52,pValues.get(21));
	            instance.setValue(p53,pValues.get(22));
	            instance.setValue(p54,pValues.get(23));
	            instance.setValue(p55,pValues.get(24));
	            
	            //自分のカードの値をゲット
	            ArrayList<Integer> mValues = new ArrayList<Integer>(); 
	            for (int i = 0; i < count +1;++i) {
		        	try {
	            		Card card =hand.get(i);
		        		mValues.add(card.getValue());
		            } catch (IndexOutOfBoundsException e) {
	            		mValues.add(0);
	            	}
	        	}
	            
	            //自分のカードの値をセット
	            instance.setValue(m1,mValues.get(0));
	            instance.setValue(m2,mValues.get(1));
	            instance.setValue(m3,mValues.get(2));
	            instance.setValue(m4,mValues.get(3));
	            
		        //WEKAで分類を実行 	
		        double judge = classifier.classifyInstance(instance);
		        //System.out.println(judge);
		        
		     	count++;		            
	        
		     	//HITかSTANDかの判断
	        	if (judge == 0) {
	        		return Action.HIT;
	        	} else {
	        		count = 1;
	        		return Action.STAND;
	        	}
		     	
	        }  catch (Exception e) {
	            e.printStackTrace();
	            return Action.STAND;
	        }
        } else {
        	// 6枚目以降を引くことはほとんどないため、STANDで固定
        	count = 1;
        	return Action.STAND;
        }
    }

	public HyogoDecisionMaker() {
	}

}
