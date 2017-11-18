package jp.topse.swdev.bigdata.blackjack.yoshimura;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Result.Type;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class MakeARFFdata {
	static final String FILE_PATH = "./models/yoshimura/blackjack-yoshi.arff";
	static final String FILE_PATH_T1 = "./models/yoshimura/blackjack-yoshi-1.arff";
	static final String FILE_PATH_T2 = "./models/yoshimura/blackjack-yoshi-2.arff";
	static final String FILE_PATH_T3 = "./models/yoshimura/blackjack-yoshi-3.arff";
	//static final String FILE_PATH= "./src/main/resources/blackjack.arff";
	
	static final String TRAIN_PATH = "./data/train-20171104-3.csv";
	static final String TRAIN_PATH_T1 = "./data/train-20171104-1.csv";
	static final String TRAIN_PATH_T2 = "./data/train-20171104-2.csv";
	static final String TRAIN_PATH_T3 = "./data/train-20171104-3.csv";


	public static void main(String[] args) {
		MakeARFFdata app = new MakeARFFdata();
//		app.create(FILE_PATH, TRAIN_PATH);
        app.create(FILE_PATH_T1, TRAIN_PATH_T1);
        app.create(FILE_PATH_T2, TRAIN_PATH_T2);
        app.create(FILE_PATH_T3, TRAIN_PATH_T3);
    }
    
    private void create(String filepath, String csvpath) {
    	FastVector attributes = new FastVector();
    	//ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        
        attributes.addElement(new Attribute("Dealer"));
        attributes.addElement(new Attribute("p1_num"));
        attributes.addElement(new Attribute("p1_hs"));
        
        FastVector kindValues = new FastVector();
        kindValues.addElement("0");
        kindValues.addElement("1");
        kindValues.addElement("2");
        attributes.addElement(new Attribute("result", kindValues));
        
        Instances data = new Instances("Data1", attributes, 0);
    	
        try {
		      File f = new File(csvpath);
		      BufferedReader br = new BufferedReader(new FileReader(f));
		 
		      String line;
		      // 1行ずつCSVファイルを読み込む
		      //int cnt=0;
		      while ((line = br.readLine()) != null) {
		          String[] csvdata = line.split(",", 0); // 行をカンマ区切りで配列に変換
		          
		          int prayernum;
		          // プレイヤ数チェック(ディーラのデータを除いて、プレイヤのデータ数で割る)
		          prayernum = (csvdata.length -6)/7;
		          
		          // ディーラのカード（見えている一枚）取得
		          // [0]名称
		          // [1-5]ディーラのカード
		          Card delcard = Card.valueOf(csvdata[1]);
		          // ディーラの最終結果をカウントしておく
		          int delresult=0;
		          Card deltempcard;
		          for (int j = 0; j < 5; j++) {
        			  if (csvdata[1+j].isEmpty()){
        				  break;
        			  }
        			  deltempcard = Card.valueOf(csvdata[1+j]);
        			  delresult = delresult + deltempcard.getValue();
                  }
		          
		          // プレイヤ数ごとにデータ読み込み
		          for (int lp= 0; lp < prayernum; lp++){
		        	  
		        	  double[] values = new double[data.numAttributes()];

		        	  // [6 + prayernum*7]名称
			          // [7-11 + prayernum*7]]プレイヤのカード
			          // プレイヤのカード枚数カウント
			          int handsnum=0;
			          int plresult=0;
			          int prevalue = 0;
			          Card pltempcard;
			          for (int j = 0; j < 5; j++) {
	        			  if (csvdata[7+j+(lp*7)].isEmpty()){
	        				  break;
	        			  }
	        			  // カード枚数カウント
	        			  handsnum++;
	        			  // カードの合計値算出
	        			  pltempcard = Card.valueOf(csvdata[7+j+(lp*7)]);
	        			  plresult = plresult + pltempcard.getValue();
	        			  // 今回のカードの値保持
	        			  prevalue = pltempcard.getValue();
	                  }
			          // [12 + prayernum*7]結果
			          int gameresult;
	        		  if (csvdata[12+(lp*7)].equals("WIN")){
	        			  gameresult = 0;
	        		  }else if  (csvdata[12+(lp*7)].equals("DRAW")){
	        			  gameresult = 1;
	        		  }else{
	        			  gameresult = 2;
	        		  }
	        		  
			          // １枚もヒットしなかった場合
			          if (2 >= handsnum){
			        	  // ディーラのカード
			        	  values[0] = delcard.getValue();
			        	  // 自分の手札の合計値
			        	  values[1] = plresult;
			        	  // スタンド
			        	  values[2] = 0;
			        	  // 結果
			        	  values[3] = gameresult;	
		        		  // セット
			        	  data.add(new Instance(1.0, values));
		        	  // １枚以上ヒットした場合
			          }else{
			        	  // ディーラのカード
			        	  values[0] = delcard.getValue();
			        	  // 自分の手札の合計値(ヒットする前)
			        	  int decvalue = plresult - prevalue;
			        	  values[1] = decvalue;
			        	  // ヒット
			        	  values[2] = 1;
			        	  // 結果
			        	  values[3] = gameresult;		        		  
		        		  // セット
			        	  data.add(new Instance(1.0, values));
			        	  
			        	  // 最後の一枚をヒットしなかった場合のデータを作成
			        	  double[] ifvalues = new double[data.numAttributes()];
			        	  // ディーラのカード
			        	  ifvalues[0] = delcard.getValue();
			        	  // 自分の手札の合計値			        	  
			        	  ifvalues[1] = decvalue;
			        	  // スタンド
			        	  ifvalues[2] = 0;
			        	  // 結果判定
			        	  if (delresult > 21) {
			                  if (decvalue > 21) {
			                	  // 負け
				        		  ifvalues[3] = 2;
			                  } else {
			                	  // 勝ち
				        		  ifvalues[3] = 0;
			                  }
			              } else {
			                  if (decvalue > 21 || delresult > decvalue) {
			                	  // 負け
				        		  ifvalues[3] = 2;
			                  } else if (delresult < decvalue) {
			                	  // 勝ち
				        		  ifvalues[3] = 0;
			                  } else {
			                	  // 引き分け
				        		  ifvalues[3] = 1;
			                  }
			              }
			        	  
			        	  // セット
			        	  data.add(new Instance(1.0, ifvalues));
			          }
		        	  
		          }
		         
		      }
		      br.close();
	 
	    } catch (IOException e) {
	      System.out.println(e);
	    }
        
        try {
            ArffSaver arffSaver = new ArffSaver();
            arffSaver.setInstances(data);
            arffSaver.setFile(new File(filepath));
            arffSaver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
