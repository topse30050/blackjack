package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Topse30050Model {
//    private static final String TRAIN_DATA = "data/training-data.csv";
    private static final String TRAIN_DATA = "data/training-deck3.csv";
//    private static final String TRAIN_ARFF = "data/train.arff";
//    private static final String EVAL_ARFF = "data/eval.arff";
    private static final String CLASSIFIER_MODEL = "models/topse30050.model";

    private static final Result.Type[] CLASS_VALUES = new Result.Type[]{
            Result.Type.WIN,
            Result.Type.DRAW,
            Result.Type.LOSE,
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
        private Result.Type result; // 結果

        DataModel(int dealerUpCard, int hand1, int hand2, int hand3, int hand4, int hand5, Result.Type result) {
            this.dealerUpCard = dealerUpCard;
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.hand3 = hand3;
            this.hand4 = hand4;
            this.hand5 = hand5;
            this.result = result;
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

                data.add(new DataModel(dealerHands[0].getIndex(), p1Hands[0].getIndex(), p1Hands[1].getIndex(), (p1Hands[2] == null) ? 0 : p1Hands[2].getIndex(), (p1Hands[3] == null) ? 0 : p1Hands[3].getIndex(), (p1Hands[4] == null) ? 0 : p1Hands[4].getIndex(), Result.Type.valueOf(elements[12])));
                data.add(new DataModel(dealerHands[0].getIndex(), p2Hands[0].getIndex(), p2Hands[1].getIndex(), (p2Hands[2] == null) ? 0 : p2Hands[2].getIndex(), (p2Hands[3] == null) ? 0 : p2Hands[3].getIndex(), (p2Hands[4] == null) ? 0 : p2Hands[4].getIndex(), Result.Type.valueOf(elements[19])));
                data.add(new DataModel(dealerHands[0].getIndex(), p3Hands[0].getIndex(), p3Hands[1].getIndex(), (p3Hands[2] == null) ? 0 : p3Hands[2].getIndex(), (p3Hands[3] == null) ? 0 : p3Hands[3].getIndex(), (p3Hands[4] == null) ? 0 : p3Hands[4].getIndex(), Result.Type.valueOf(elements[26])));
                data.add(new DataModel(dealerHands[0].getIndex(), p4Hands[0].getIndex(), p4Hands[1].getIndex(), (p4Hands[2] == null) ? 0 : p4Hands[2].getIndex(), (p4Hands[3] == null) ? 0 : p4Hands[3].getIndex(), (p4Hands[4] == null) ? 0 : p4Hands[4].getIndex(), Result.Type.valueOf(elements[33])));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        int trainRatio = (int) (data.size() * 0.7);
        List<DataModel> trainData = data.subList(0, trainRatio);
        List<DataModel> evalData = data.subList(trainRatio, data.size());

        Instances trainArff = data2arff(trainData);
        Instances evalArff = data2arff(evalData);
//        writeArff(trainArff, TRAIN_ARFF);
//        writeArff(evalArff, EVAL_ARFF);

//        System.out.println("***** NaiveBayes *****");
//        Classifier modelNb = getBuiltClassifier(trainArff, new NaiveBayes());
//        evalResult(modelNb, evalArff);
//        System.out.println();
//
//        System.out.println("***** J48 *****");
//        Classifier modelJ48 =getBuiltClassifier(trainArff, new J48(), "-U");
//        evalResult(modelJ48, evalArff);
//        System.out.println();

        System.out.println("***** RandomForest *****");
        Classifier modelRf = getBuiltClassifier(trainArff, new RandomForest());
        evalResult(modelRf, evalArff);
        System.out.println();

//        System.out.println("***** Logistic *****");
//        Classifier modelLogi = getBuiltClassifier(trainArff, new Logistic());
//        evalResult(modelLogi, evalArff);
//        System.out.println();
//
//        System.out.println("***** SMO *****");
//        Classifier modelSMO = getBuiltClassifier(trainArff, new SMO());
//        evalResult(modelSMO, evalArff);
//        System.out.println();
//
//        System.out.println("***** Multilayer Perceptron *****");
//        Classifier modelMP = getBuiltClassifier(trainArff, new MultilayerPerceptron(), "-L", "0.5", "-M", "0.1");
//        evalResult(modelMP, evalArff);
//        System.out.println();
//
//        System.out.println("***** SVM *****");
//        Classifier modelSVM = getBuiltClassifier(trainArff, new LibSVM());
//        evalResult(modelSVM, evalArff);
//        System.out.println();


        aiModel = modelRf;
        try {
            SerializationHelper.write(CLASSIFIER_MODEL, aiModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Instances data2arff(List<DataModel> data) {
        FastVector classValues = new FastVector();
        for (Result.Type classValue : CLASS_VALUES) {
            classValues.addElement(classValue.name());
        }

        FastVector attributes = new FastVector();
        attributes.addElement(new Attribute("Dealer Up Card"));
        attributes.addElement(new Attribute("Hand 1"));
        attributes.addElement(new Attribute("Hand 2"));
        attributes.addElement(new Attribute("Hand 3"));
        attributes.addElement(new Attribute("Hand 4"));
        attributes.addElement(new Attribute("Hand 5"));
        attributes.addElement(new Attribute("Predict Result", classValues));

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
            values[6] = arff.attribute(6).indexOfValue(d.result.name());
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
    static Result.Type getPredictedResult(int dealerUpCard, int hand1, int hand2, int hand3, int hand4, int hand5) {
        if (!new File(CLASSIFIER_MODEL).exists()) {
            makeClassifierModel();
        }

        int predictedValue = Integer.MAX_VALUE;
        try {
            if (aiModel == null) {
                aiModel = (Classifier) SerializationHelper.read(CLASSIFIER_MODEL);
            }
            List<DataModel> predictData = new ArrayList<>();
            predictData.add(new DataModel(dealerUpCard, hand1, hand2, hand3, hand4, hand5, Result.Type.WIN)); // 結果の引数はダミー
            Instances predictArff = data2arff(predictData);
            predictedValue = (int) new Evaluation(predictArff).evaluateModelOnce(aiModel, predictArff.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (predictedValue == Integer.MAX_VALUE) ? null : CLASS_VALUES[predictedValue];
    }


    public static void main(String[] args) {
        makeClassifierModel();
//        Card nextCard = Topse30050Model.getPredictedResult(7, 1, 2, 0, 0, 0, 3);
//        System.out.println(nextCard);
    }
}
