package jp.topse.swdev.bigdata.blackjack.topse30050;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.core.converters.ArffSaver;

public class MakerForWekaModel {
    private static final String TRAIN_DATA = "data/training-data.csv";
//    private static final String TRAIN_ARFF = "data/train.arff";
//    private static final String EVAL_ARFF = "data/eval.arff";

    private MakerForWekaModel() {
    }

    private static class DataModel {
        private int dealerUpCard;   // ディーラーの1枚目
        private int playOrder;      // プレイ順序
        private int hand1;          // 最初に配られた1枚目
        private int hand2;          // 最初に配られた2枚目
        private int cardsCount;     // ゲーム終了時の所持枚数
        private Result.Type result; // ゲーム終了時の結果

        private DataModel(int dealerUpCard, int playOrder, int hand1, int hand2, int cardsCount, Result.Type result) {
            this.dealerUpCard = dealerUpCard;
            this.playOrder = playOrder;
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.cardsCount = cardsCount;
            this.result = result;
        }
//
//        public String toString() {
//            return new StringBuilder()
//                    .append(String.valueOf(dealerUpCard))
//                    .append(",")
//                    .append(String.valueOf(playOrder))
//                    .append(",")
//                    .append(String.valueOf(hand1))
//                    .append(",")
//                    .append(String.valueOf(hand2))
//                    .append(",")
//                    .append(String.valueOf(cardsCount))
//                    .append(",")
//                    .append(String.valueOf(result.name()))
//                    .toString();
//        }
    }

    public static void main(String[] args) {
        List<DataModel> data= new ArrayList<>();
        try (Stream<String> line = Files.lines(Paths.get(TRAIN_DATA))) {
            line.map(str -> str.split(",")).forEach(elements -> {
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 1, Card.valueOf(elements[7]).getIndex(), Card.valueOf(elements[8]).getIndex(), 2 + getCardCountFromCsvString(elements[9], elements[10], elements[11]), Result.Type.valueOf(elements[12])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 2, Card.valueOf(elements[14]).getIndex(), Card.valueOf(elements[15]).getIndex(), 2 + getCardCountFromCsvString(elements[16], elements[17], elements[18]), Result.Type.valueOf(elements[19])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 3, Card.valueOf(elements[21]).getIndex(), Card.valueOf(elements[22]).getIndex(), 2 + getCardCountFromCsvString(elements[23], elements[24], elements[25]), Result.Type.valueOf(elements[26])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 4, Card.valueOf(elements[28]).getIndex(), Card.valueOf(elements[29]).getIndex(), 2 + getCardCountFromCsvString(elements[30], elements[31], elements[32]), Result.Type.valueOf(elements[33])));
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

        System.out.println("***** NaiveBayes *****");
        Classifier c = analyzeByNaiveBayes(trainArff);
        evalResult(c, evalArff);
        System.out.println();

        System.out.println("***** J48 *****");
        Classifier c2 = analyzeByJ48(trainArff);
        evalResult(c2, evalArff);
        System.out.println();

        System.out.println("***** RandomForest *****");
        Classifier c3 = analyzeByRandomForest(trainArff);
        evalResult(c3, evalArff);
        System.out.println();

        Classifier c4 = analyzeBySVM(trainArff);
        System.out.println("***** SVM *****");
        evalResult(c4, evalArff);
        System.out.println();




        try {
            SerializationHelper.write("data/topse30050.model", c4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Classifier preModel = (Classifier) SerializationHelper.read("data/topse30050.model");
            System.out.println("***** Load Model *****");
            evalResult(preModel, evalArff);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static int getCardCountFromCsvString(String... hands) {
        return hands.length - (int) Arrays.stream(hands).filter(String::isEmpty).count();
    }

    private static Instances data2arff(List<DataModel> data) {
        FastVector classValues = new FastVector();
        classValues.addElement(Result.Type.WIN.name());
        classValues.addElement(Result.Type.DRAW.name());
        classValues.addElement(Result.Type.LOSE.name());

        FastVector attributes = new FastVector();
        attributes.addElement(new Attribute("Dealer Up Card"));
        attributes.addElement(new Attribute("Play Order"));
        attributes.addElement(new Attribute("Hand1"));
        attributes.addElement(new Attribute("Hand2"));
        attributes.addElement(new Attribute("Cards Count"));
        attributes.addElement(new Attribute("Result", classValues));

        Instances arff = new Instances("Data", attributes, 0);
        arff.setClassIndex(arff.numAttributes() - 1);

        data.forEach(d -> {
            double[] values = new double[arff.numAttributes()];
            values[0] = d.dealerUpCard;
            values[1] = d.playOrder;
            values[2] = d.hand1;
            values[3] = d.hand2;
            values[4] = d.cardsCount;
            values[5] = arff.attribute(5).indexOfValue(d.result.name());
            arff.add(new Instance(1.0, values));
        });

        return arff;
    }

    private static void writeArff(Instances arff, String arffPath) {
        try {
            ArffSaver arffSaver = new ArffSaver();
            arffSaver.setInstances(arff);
            arffSaver.setFile(new File(arffPath));
            arffSaver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Classifier analyzeByNaiveBayes(Instances arff) {
        NaiveBayes nb = new NaiveBayes();
        try {
            nb.buildClassifier(arff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nb;
    }

    private static Classifier analyzeByJ48(Instances arff) {
        J48 tree = new J48();
        try {
            String[] options = new String[1];
            options[0] = "-U";
            tree.setOptions(options);
            tree.buildClassifier(arff);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tree;
    }

    private static Classifier analyzeByRandomForest(Instances arff) {
        RandomForest rf = new RandomForest();
        try {
            rf.buildClassifier(arff);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rf;
    }

    private static Classifier analyzeBySVM(Instances arff) {
        LibSVM svm = new LibSVM();
        try {
            svm.buildClassifier(arff);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return svm;
    }

    private static void evalResult(Classifier c, Instances arff) {
        try {
            Evaluation eval = new Evaluation(arff);
            eval.evaluateModel(c, arff);
            System.out.println(eval.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
