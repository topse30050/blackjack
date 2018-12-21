package jp.topse.swdev.bigdata.blackjack.topse30020;

import java.io.FileWriter;
import java.io.IOException;

import jp.topse.swdev.bigdata.blackjack.*;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import weka.core.*;
import weka.clusterers.*;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.core.converters.ConverterUtils.DataSource;

import weka.core.Instance;

public class Topse30020  implements DecisionMaker {

	private static final String ARFF_PATH= "models/topse30020.arff";
	//private static final String ARFF_PATH2= "H:\\date1.arff";
	
	private static final String ARFF_PATH_out= "models/topse30020.arff";
	//private static final String ARFF_PATH_out2= "H:\\date4.arff";
	
	App app;
	//App app2;
	
    public Topse30020() {
    	try {
	    	/*機械学習*/
	    	app = new App();
	    	app.analyze(ARFF_PATH);
	    	
	    	//app2 = new App();
	    	//app2.analyze(ARFF_PATH2);
	    	
	    	return;
    	}
    	finally{}
    }
	
    @Override
    public Action decide(Player player, Game game) {
    	try {
	    	String str = "@RELATION date\r\n" + 
	    			"\r\n" + 
	    			"@ATTRIBUTE Dealer REAL\r\n" + 
	    			"@ATTRIBUTE Player REAL\r\n" + 
	    			"@ATTRIBUTE class   {0,1,2}\r\n" +
	    			"\r\n" + 
	    			"@DATA" +
	    			"\r\n";
	    	
	    	/*String str2 = "@RELATION date\r\n" + 
	    			"\r\n" + 
	    			"@ATTRIBUTE Dealer REAL\r\n" + 
	    			"@ATTRIBUTE class   {0,1}\r\n" +
	    			"\r\n" + 
	    			"@DATA" +
	    			"\r\n";    	
	    	*/
	    	/*評価*/
	    	/*ディーラーの手札を取得*/
	    	Card dealerHand = game.getUpCard();
 	
	    	/*ファイルへ出力*/
	    	/*PrintWriter fw2 = new PrintWriter(ARFF_PATH_out2);
	    	fw2.write(str2);
	    	fw2.format("%d", dealerHand.getValue() );
	    	fw2.write( ",0\r\n" );
	    	fw2.close();
	    	
	    	// バーストチェック 
	    	double burst_check;
	    	burst_check = app2.evalOnce(ARFF_PATH_out2);
	    	*/
	    	
	    	/*自分の手札を取得*/
	    	Map<Player, Hand> playerHands = game.getPlayerHands();	    	
	    	Hand hand = playerHands.get(player);	
	    	/*System.out.println( hand.eval());*/
	    	/*ファイルへ出力*/
	    	PrintWriter fw = new PrintWriter(ARFF_PATH_out);
	    	fw.write(str);
	    	fw.format("%d", dealerHand.getValue() );
	    	fw.write( "," );
	    	fw.format("%d",hand.eval() );
	    	fw.write( ",0\r\n" );
	    	fw.close();
	    	
	    	/* 相手がバーストなら何もしない */
	    	
	    		/*勝ち負け判定*/
		    	double win_judge;
		    	win_judge = app.evalOnce(ARFF_PATH_out);
		    	/*System.out.println(win_judge);*/
		    	
		        /* ロジック */
		        /*勝ち予測の場合*/
		    	if (win_judge == 0) {
		    		return Action.STAND;
		        }
		    	else if(win_judge == 1 ) /*負け予測の場合 */ 
		        {
		    		return Action.HIT;
		        }    
		    	else /*引き分け予測の場合*/
		    	{
		    		return Action.STAND;
		    	}

	    }
    	catch (IOException ex) {
    		ex.printStackTrace();
    		return Action.STAND;
    	}
    }	
}
