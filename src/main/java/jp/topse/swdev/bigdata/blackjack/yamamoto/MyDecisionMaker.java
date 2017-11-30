package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.*;
import java.util.Map;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Deck;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Player;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.*;

public class MyDecisionMaker implements DecisionMaker {
	Classifier model;
	Classifier model3;
	Classifier model4;

	static FastVector bool;

	static Attribute number2;
	static Attribute first2;
	static Attribute second2;
	static Attribute third2;
	static Attribute fourth2;
	static Attribute numOfAce2;
	static Attribute total2;
	static Attribute dealer2;
	static Attribute take2;

	static Attribute number3;
	static Attribute first3;
	static Attribute second3;
	static Attribute third3;
	static Attribute fourth3;
	static Attribute numOfAce3;
	static Attribute total3;
	static Attribute dealer3;
	static Attribute take3;

	static Attribute number4;
	static Attribute first4;
	static Attribute second4;
	static Attribute third4;
	static Attribute fourth4;
	static Attribute numOfAce4;
	static Attribute total4;
	static Attribute dealer4;
	static Attribute take4;


	Instances instances;
	Instances instances3;
	Instances instances4;

	static {
		bool = new FastVector(2);
		bool.addElement("true");
		bool.addElement("false");


		first2 = new Attribute("first", 0);
		second2 = new Attribute("second", 1);
		numOfAce2 = new Attribute("numOfAce", 2);
		total2 = new Attribute("total", 3);
		dealer2 = new Attribute("dealer", 4);
		take2 = new Attribute("take", bool, 5);

		first3 = new Attribute("first", 0);
		second3 = new Attribute("second", 1);
		third3 = new Attribute("third", 2);
		numOfAce3 = new Attribute("numOfAce", 3);
		total3 = new Attribute("total", 4);
		dealer3 = new Attribute("dealer", 5);
		take3 = new Attribute("take", bool, 6);

		first4 = new Attribute("first", 0);
		second4 = new Attribute("second", 1);
		third4 = new Attribute("third", 2);
		fourth4 = new Attribute("fourth", 3);
		numOfAce4 = new Attribute("numOfAce", 4);
		total4 = new Attribute("total", 5);
		dealer4 = new Attribute("dealer", 6);
		take4 = new Attribute("take", bool, 7);
	}
	static Class c = RandomForest.class;
	public static void main(String[] args) throws Exception {

		//learn("1", false, c.getName(), null, null, "./models/yamamoto/mytrain1.csv");
		//learn("2", false, c.getName(), null, null, "./models/yamamoto/mytrain2.csv");
		//learn("3", false, c.getName(), null, null, "./models/yamamoto/mytrain3.csv");
		learn("1", false, c.getName(), null, null, "./data/train-20171104-1.csv");
		learn("2", false, c.getName(), null, null, "./data/train-20171104-2.csv");
		learn("3", false, c.getName(), null, null, "./data/train-20171104-3.csv");
	}

	private static void learn(String name, boolean trainMyself, String className, String[] constructorArgs, String[] options, String trainDataPath) {

		MyClassifier app = new MyClassifier();
		try {
			CSVReader reader = new CSVReader();
			reader.read(new File(trainDataPath));
			reader.writeCSV("./models/yamamoto/" + name + "-mymodel");

			File file = new File("./models/yamamoto/" + name + "-2.model");
			if (!file.exists()) {
				Instances instances = app.readCSV("./models/yamamoto/" + name + "-mymodel-2.csv", 6);
				Classifier model = app.createModel(instances, className, constructorArgs, options, 6);
				saveModel(model, file);
			}

			File file3 = new File("./models/yamamoto/" + name + "-3.model");
			if (!file3.exists()) {
				Instances instances = app.readCSV("./models/yamamoto/" + name + "-mymodel-3.csv", 7);
				Classifier model = app.createModel(instances, className, constructorArgs, options, 7);
				saveModel(model, file3);
			}

			File file4 = new File("./models/yamamoto/" + name + "-4.model");
			if (!file4.exists()) {
				Instances instances = app.readCSV("./models/yamamoto/" + name + "-mymodel-4.csv", 8);
				Classifier model = app.createModel(instances, className, constructorArgs, options, 8);
				saveModel(model, file4);
			}

			// TODO
			if(trainMyself) {
				createTrainDataByMyself(className, constructorArgs, options, trainDataPath);
				reader = new CSVReader();
				reader.read(new File("myresult.csv"));
				Instances instances = app.readCSV("mymodel.csv", 7);
				Classifier model = app.createModel(instances, className, constructorArgs, options, 6);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public MyDecisionMaker(int index) {
		String name = String.valueOf(index);
		MyClassifier app = new MyClassifier();

		try {
			File file = new File("./models/yamamoto/3-2.model");
			if (!file.exists()) {
				learn("1", false, c.getName(), null, null, "./data/train-20171104-1.csv");
			}
			this.instances = app.readCSV("./models/yamamoto/" + name + "-mymodel-2.csv", 6);
			this.model = loadModel(file);

			File file3 = new File("./models/yamamoto/3-3.model");
			if (!file3.exists()) {
				learn("2", false, c.getName(), null, null, "./data/train-20171104-2.csv");
			}
			this.instances3 = app.readCSV("./models/yamamoto/" + name + "-mymodel-3.csv", 7);
			this.model3 = loadModel(file3);

			File file4 = new File("./models/yamamoto/3-4.model");
			if (!file4.exists()) {
				learn("3", false, c.getName(), null, null, "./data/train-20171104-3.csv");
			}
			this.instances4 = app.readCSV("./models/yamamoto/" + name + "-mymodel-4.csv", 8);
			this.model4 = loadModel(file4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int[] getNumArray(Hand myhand) {
		int[] numArray = new int[myhand.getCount()];
		for(int i=0;i<myhand.getCount();i++) {
			numArray[i] = myhand.get(i).getValue();
		}

		return CSVReader.getNumArray(numArray, myhand.getCount());
	}

	@Override
	public Action decide(Player player, Game game) {
		Map<Player, Hand> playerHands = game.getPlayerHands();
		Hand myhand = playerHands.get(player);
		int total = myhand.eval();
		if(total >= 21 || myhand.getCount() == 5) {
			return Action.STAND;
		}
		int[] numArray = getNumArray(myhand);
		int numOfAce = CSVReader.getNumOfAce(numArray);
		int dealer = game.getUpCard().getValue();

		Instance take = instance(numArray, numOfAce, total, dealer, "true");
		Instance not_take = instance(numArray, numOfAce, total, dealer, "false");

		Classifier classifier = null;
		if(myhand.getCount() == 2) {
			classifier = model;
			take.setDataset(instances);
			not_take.setDataset(instances);
		} else if (myhand.getCount() == 3) {
			classifier = model3;
			take.setDataset(instances3);
			not_take.setDataset(instances3);
		} else {
			classifier = model4;
			take.setDataset(instances4);
			not_take.setDataset(instances4);
		}

		try {
			double[] distTake = classifier.distributionForInstance(take);
			double[] distNotTake =classifier.distributionForInstance(not_take);
			double difference = distTake[0] - distNotTake[0];

			if(distTake[0] > distNotTake[0] && distTake[0] >= 0.5 && Math.abs(difference) >= 0.2 ) {
				return Action.HIT;
			} else if (distNotTake[0] > distTake[0] && distNotTake[0] >= 0.5  && Math.abs(difference) >= 0.2 ) {
				return Action.STAND; 
			}
			if(total < 17) {
				return Action.HIT;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Action.STAND;
	}

	public Instance instance(int[] numArray, int numOfAce, int total, int dealer, String take) {
		int num = numArray.length;; 
		Instance ins = new Instance(4 + num); 
		if(num == 2) {
			ins.setValue(first2, numArray[0]);
			ins.setValue(second2, numArray[1]);
			ins.setValue(numOfAce2, numOfAce);
			ins.setValue(total2, total);
			ins.setValue(dealer2, dealer);
			ins.setValue(take2, take);
		} else if (num == 3) {
			ins.setValue(first3, numArray[0]);
			ins.setValue(second3, numArray[1]);
			ins.setValue(third3, numArray[2]);
			ins.setValue(numOfAce3, numOfAce);
			ins.setValue(total3, total);
			ins.setValue(dealer3, dealer);
			ins.setValue(take3, take);       	
		} else {
			ins.setValue(first4, numArray[0]);
			ins.setValue(second4, numArray[1]);
			ins.setValue(third4, numArray[2]);
			ins.setValue(fourth4, numArray[3]);
			ins.setValue(numOfAce4, numOfAce);
			ins.setValue(total4, total);
			ins.setValue(dealer4, dealer);
			ins.setValue(take4, take);     
		}
		return ins;
	}

	private static void createTrainDataByMyself(String className, String[] constructorArgs, String[] options, String trainDataPath) throws IOException {
		Player player = new Player("TSUBASA" , new MyDecisionMaker(0));

		try(FileWriter fw = new FileWriter("myresult.csv");) {
			for (int i = 0; i < 1000000; ++i) {
				createTrainDataByMyself(fw, player);
			}
		}
	}

	private static void createTrainDataByMyself(FileWriter fw, Player player) throws IOException {
		Deck deck = Deck.createDefault();
		Game game = new Game(deck);
		game.join(player);
		game.setup();
		game.start();

		Result result = game.result();

		System.out.println(result.toString());
		fw.write(result.toString()+"\r\n");
		Result.Type t = result.getStandings().get(player);
	}

	private static void saveModel(Classifier model, File file) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Classifier loadModel(File file) {
		Classifier model = null;
		ObjectInputStream  ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			model = (Classifier)ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return model;
	}
}
