package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.Card;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Topse30050Model {
//    private static final String TRAIN_DATA = "data/training-data.csv";
    private static final String TRAIN_DATA = "data/training-deck3.csv";
//    private static final String TRAIN_ARFF = "data/train.arff";
//    private static final String EVAL_ARFF = "data/eval.arff";
    private static final String CLASSIFIER_MODEL = "models/topse30050.model";

    private static final Card[] CLASS_VALUES = new Card[]{
            Card.ACE,
            Card.TWO,
            Card.THREE,
            Card.FOUR,
            Card.FIVE,
            Card.SIX,
            Card.SEVEN,
            Card.EIGHT,
            Card.NINE,
            Card.TEN,
//            Card.JACK,
//            Card.QUEEN,
//            Card.KING,
    };
    private static Classifier aiModel;

    private Topse30050Model() {
    }

    private static class DataModel {
        private int dealerUpCard;   // ディーラーの1枚目
        private int hand1;          // 最初に配られた1枚目
        private int hand2;          // 最初に配られた2枚目
        private int hand3;          // HIT1枚目(引いてなければ0)
        private int hand4;          // HIT2枚目(引いてなければ0)
        private int hand5;          // HIT3枚目(引いてなければ0)
        private int predictIndex;   // 予測したいカードの位置
        private Card predictCard;    // 予測したいカードの実際の値

        public DataModel(int dealerUpCard, int hand1, int hand2, int hand3, int hand4, int hand5, int predictIndex, Card predictCard) {
            this.dealerUpCard = dealerUpCard;
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.hand3 = hand3;
            this.hand4 = hand4;
            this.hand5 = hand5;
            this.predictIndex = predictIndex;
            this.predictCard = predictCard;
        }
    }

    private static void makeClassifierModel() {
        List<DataModel> data= new ArrayList<>();
        try (Stream<String> line = Files.lines(Paths.get(TRAIN_DATA))) {
            line.map(str -> str.split(",")).forEach(elements -> {
                Card[] dealerHands = {Card.valueOf(elements[1]), Card.valueOf(elements[2]), (elements[3].length() == 0) ? null : Card.valueOf(elements[3]), (elements[4].length() == 0) ? null : Card.valueOf(elements[4]), (elements[5].length() == 0) ? null : Card.valueOf(elements[5])};
                Card[] p1Hands = {Card.valueOf(elements[7]), Card.valueOf(elements[8]), (elements[9].length() == 0) ? null : Card.valueOf(elements[9]), (elements[10].length() == 0) ? null : Card.valueOf(elements[10]), (elements[11].length() == 0) ? null : Card.valueOf(elements[11])};
                Card[] p2Hands = {Card.valueOf(elements[14]), Card.valueOf(elements[15]), (elements[16].length() == 0) ? null : Card.valueOf(elements[16]), (elements[17].length() == 0) ? null : Card.valueOf(elements[17]), (elements[18].length() == 0) ? null : Card.valueOf(elements[18])};
                Card[] p3Hands = {Card.valueOf(elements[21]), Card.valueOf(elements[22]), (elements[23].length() == 0) ? null : Card.valueOf(elements[23]), (elements[24].length() == 0) ? null : Card.valueOf(elements[24]), (elements[25].length() == 0) ? null : Card.valueOf(elements[25])};
                Card[] p4Hands = {Card.valueOf(elements[28]), Card.valueOf(elements[29]), (elements[30].length() == 0) ? null : Card.valueOf(elements[30]), (elements[31].length() == 0) ? null : Card.valueOf(elements[31]), (elements[32].length() == 0) ? null : Card.valueOf(elements[32])};

                data.add(new DataModel(dealerHands[0].getValue(), p1Hands[0].getValue(), p1Hands[1].getValue(), (p1Hands[2] == null) ? 0 : p1Hands[2].getValue(), (p1Hands[3] == null) ? 0 : p1Hands[3].getValue(), (p1Hands[4] == null) ? 0 : p1Hands[4].getValue(), 3, p1Hands[2]));
                data.add(new DataModel(dealerHands[0].getValue(), p1Hands[0].getValue(), p1Hands[1].getValue(), (p1Hands[2] == null) ? 0 : p1Hands[2].getValue(), (p1Hands[3] == null) ? 0 : p1Hands[3].getValue(), (p1Hands[4] == null) ? 0 : p1Hands[4].getValue(), 4, p1Hands[3]));
                data.add(new DataModel(dealerHands[0].getValue(), p1Hands[0].getValue(), p1Hands[1].getValue(), (p1Hands[2] == null) ? 0 : p1Hands[2].getValue(), (p1Hands[3] == null) ? 0 : p1Hands[3].getValue(), (p1Hands[4] == null) ? 0 : p1Hands[4].getValue(), 5, p1Hands[4]));

                data.add(new DataModel(dealerHands[0].getValue(), p2Hands[0].getValue(), p2Hands[1].getValue(), (p2Hands[2] == null) ? 0 : p2Hands[2].getValue(), (p2Hands[3] == null) ? 0 : p2Hands[3].getValue(), (p2Hands[4] == null) ? 0 : p2Hands[4].getValue(), 3, p2Hands[2]));
                data.add(new DataModel(dealerHands[0].getValue(), p2Hands[0].getValue(), p2Hands[1].getValue(), (p2Hands[2] == null) ? 0 : p2Hands[2].getValue(), (p2Hands[3] == null) ? 0 : p2Hands[3].getValue(), (p2Hands[4] == null) ? 0 : p2Hands[4].getValue(), 4, p2Hands[3]));
                data.add(new DataModel(dealerHands[0].getValue(), p2Hands[0].getValue(), p2Hands[1].getValue(), (p2Hands[2] == null) ? 0 : p2Hands[2].getValue(), (p2Hands[3] == null) ? 0 : p2Hands[3].getValue(), (p2Hands[4] == null) ? 0 : p2Hands[4].getValue(), 5, p2Hands[4]));

                data.add(new DataModel(dealerHands[0].getValue(), p3Hands[0].getValue(), p3Hands[1].getValue(), (p3Hands[2] == null) ? 0 : p3Hands[2].getValue(), (p3Hands[3] == null) ? 0 : p3Hands[3].getValue(), (p3Hands[4] == null) ? 0 : p3Hands[4].getValue(), 3, p3Hands[2]));
                data.add(new DataModel(dealerHands[0].getValue(), p3Hands[0].getValue(), p3Hands[1].getValue(), (p3Hands[2] == null) ? 0 : p3Hands[2].getValue(), (p3Hands[3] == null) ? 0 : p3Hands[3].getValue(), (p3Hands[4] == null) ? 0 : p3Hands[4].getValue(), 4, p3Hands[3]));
                data.add(new DataModel(dealerHands[0].getValue(), p3Hands[0].getValue(), p3Hands[1].getValue(), (p3Hands[2] == null) ? 0 : p3Hands[2].getValue(), (p3Hands[3] == null) ? 0 : p3Hands[3].getValue(), (p3Hands[4] == null) ? 0 : p3Hands[4].getValue(), 5, p3Hands[4]));

                data.add(new DataModel(dealerHands[0].getValue(), p4Hands[0].getValue(), p4Hands[1].getValue(), (p4Hands[2] == null) ? 0 : p4Hands[2].getValue(), (p4Hands[3] == null) ? 0 : p4Hands[3].getValue(), (p4Hands[4] == null) ? 0 : p4Hands[4].getValue(), 3, p4Hands[2]));
                data.add(new DataModel(dealerHands[0].getValue(), p4Hands[0].getValue(), p4Hands[1].getValue(), (p4Hands[2] == null) ? 0 : p4Hands[2].getValue(), (p4Hands[3] == null) ? 0 : p4Hands[3].getValue(), (p4Hands[4] == null) ? 0 : p4Hands[4].getValue(), 4, p4Hands[3]));
                data.add(new DataModel(dealerHands[0].getValue(), p4Hands[0].getValue(), p4Hands[1].getValue(), (p4Hands[2] == null) ? 0 : p4Hands[2].getValue(), (p4Hands[3] == null) ? 0 : p4Hands[3].getValue(), (p4Hands[4] == null) ? 0 : p4Hands[4].getValue(), 5, p4Hands[4]));

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 予想したいカードは引かれてないパターンはデータとして除き絵札は10点なので10のカードに寄せる
        List<DataModel> training = data.stream()
                .filter(d -> d.predictCard != null)
                .peek(d -> {
                    if (d.predictCard.equals(Card.JACK) || d.predictCard.equals(Card.QUEEN) || d.predictCard.equals(Card.KING)) {
                        d.predictCard = Card.TEN;
                    }
                })
                .collect(Collectors.toList());

        int trainRatio = (int) (training.size() * 0.7);
        List<DataModel> trainData = training.subList(0, trainRatio);
        List<DataModel> evalData = training.subList(trainRatio, training.size());
System.out.printf("全パターン%d件 -> 有効データ%d, トレーニング%d件:テスト%d件\n", data.size(), training.size(), trainData.size(), evalData.size());

        Instances trainArff = data2arff(trainData);
        Instances evalArff = data2arff(evalData);
//        writeArff(trainArff, TRAIN_ARFF);
//        writeArff(evalArff, EVAL_ARFF);

//        System.out.println("***** NaiveBayes *****");
//        Classifier c1 = getBuiltClassifier(trainArff, new NaiveBayes());
//        evalResult(c1, evalArff);
//        System.out.println();

        System.out.println("***** J48 *****");
        Classifier c2 =getBuiltClassifier(trainArff, new J48(), "-U");
        evalResult(c2, evalArff);
        System.out.println();

//        System.out.println("***** RandomForest *****");
//        Classifier c3 = getBuiltClassifier(trainArff, new RandomForest());
//        evalResult(c3, evalArff);
//        System.out.println();

//        System.out.println("***** Logistic *****");
//        Classifier c4 = getBuiltClassifier(trainArff, new Logistic());
//        evalResult(c4, evalArff);
//        System.out.println();

//        System.out.println("***** SMO *****");
//        Classifier c5 = getBuiltClassifier(trainArff, new SMO());
//        evalResult(c5, evalArff);
//        System.out.println();

//        System.out.println("***** Multilayer Perceptron *****");
//        Classifier c6 = getBuiltClassifier(trainArff, new MultilayerPerceptron(), "-L", "0.5", "-M", "0.1");
//        evalResult(c6, evalArff);
//        System.out.println();

//        System.out.println("***** SVM *****");
//        Classifier c7 = getBuiltClassifier(trainArff, new LibSVM());
//        evalResult(c7, evalArff);
//        System.out.println();


        aiModel = c2;
        try {
            SerializationHelper.write(CLASSIFIER_MODEL, aiModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Instances data2arff(List<DataModel> data) {
        FastVector classValues = new FastVector();
        for (Card classValue : CLASS_VALUES) {
            classValues.addElement(classValue.name());
        }

        FastVector attributes = new FastVector();
        attributes.addElement(new Attribute("Dealer Up Card"));
        attributes.addElement(new Attribute("Hand 1"));
        attributes.addElement(new Attribute("Hand 2"));
        attributes.addElement(new Attribute("Hand 3"));
        attributes.addElement(new Attribute("Hand 4"));
        attributes.addElement(new Attribute("Hand 5"));
        attributes.addElement(new Attribute("Predict Index"));
        attributes.addElement(new Attribute("Predict Card", classValues));

        Instances arff = new Instances("Data", attributes, 0);
        arff.setClassIndex(arff.numAttributes() - 1);

        data.forEach(d -> {
            double[] values = new double[arff.numAttributes()];
            values[0] = d.dealerUpCard;
            values[1] = d.hand1;
            values[2] = d.hand2;
            values[3] = d.hand3;
            values[4] = d.hand4;
            values[5] = d.hand5;
            values[6] = d.predictIndex;
            values[7] = arff.attribute(7).indexOfValue(d.predictCard.name());
            arff.add(new Instance(1.0, values));
        });

        return arff;
    }

//    private static void writeArff(Instances arff, String arffPath) {
//        try {
//            ArffSaver arffSaver = new ArffSaver();
//            arffSaver.setInstances(arff);
//            arffSaver.setFile(new File(arffPath));
//            arffSaver.writeBatch();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static Classifier getBuiltClassifier(Instances arff, Classifier classifier, String... options) {
        try {
            if (options != null) {
                classifier.setOptions(options);
            }
            classifier.buildClassifier(arff);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classifier;
    }


    private static void evalResult(Classifier c, Instances arff) {
        try {
            Evaluation eval = new Evaluation(arff);
            eval.evaluateModel(c, arff);
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toMatrixString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static Card getPredictedCard(int dealerUpCard, int hand1, int hand2, int hand3, int hand4, int hand5, int predictIndex) {
        if (!new File(CLASSIFIER_MODEL).exists()) {
            makeClassifierModel();
        }

        int predictedValue = Integer.MAX_VALUE;
        try {
            if (aiModel == null) {
                aiModel = (Classifier) SerializationHelper.read(CLASSIFIER_MODEL);
            }
            List<DataModel> predictData = new ArrayList<>();
            predictData.add(new DataModel(dealerUpCard, hand1, hand2, hand3, hand4, hand5, predictIndex, Card.ACE)); // 結果の引数はダミー
            Instances predictArff = data2arff(predictData);
            predictedValue = (int) new Evaluation(predictArff).evaluateModelOnce(aiModel, predictArff.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (predictedValue == Integer.MAX_VALUE) ? null : CLASS_VALUES[predictedValue];
    }


    public static void main(String[] args) {
//        makeClassifierModel();
        Card nextCard = Topse30050Model.getPredictedCard(7, 1, 2, 0, 0, 0, 3);
        System.out.println(nextCard);
    }
}
