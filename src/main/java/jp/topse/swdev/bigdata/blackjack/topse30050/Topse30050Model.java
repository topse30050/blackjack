package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Hand;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Topse30050Model {
    private static final String TRAIN_DATA = "data/training-data.csv";
//    private static final String TRAIN_DATA = "data/training-deck3.csv";
//    private static final String TRAIN_ARFF = "data/train.arff";
//    private static final String EVAL_ARFF = "data/eval.arff";
    private static final String CLASSIFIER_MODEL_RESULT = "models/topse30050_result.model";
    private static final String CLASSIFIER_MODEL_BUST = "models/topse30050_bust.model";

    private static final Result.Type[] CLASS_VALUES_RESULT = new Result.Type[]{
            Result.Type.WIN,
            Result.Type.DRAW,
            Result.Type.LOSE,
    };
    private static final String[] CLASS_VALUES_BUST = new String[]{
            "NON BUST",
            "BUST",
    };
    private static Classifier aiModelResult;
    private static Classifier aiModelBust;

    private Topse30050Model() {
    }

    private static class DataModelResult {
        private int dealerUpCard;   // ディーラーの1枚目
        private int hand1;          // 最初に配られた1枚目
        private int hand2;          // 最初に配られた2枚目
        private int hand3;          // HIT1枚目(引いてなければ0)
        private int hand4;          // HIT2枚目(引いてなければ0)
        private int hand5;          // HIT3枚目(引いてなければ0)
        private Result.Type result; // 結果

        DataModelResult(int dealerUpCard, int hand1, int hand2, int hand3, int hand4, int hand5, Result.Type result) {
            this.dealerUpCard = dealerUpCard;
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.hand3 = hand3;
            this.hand4 = hand4;
            this.hand5 = hand5;
            this.result = result;
        }
    }
    private static class DataModelBust {
        private int hand1;          // 最初に配られた1枚目
        private int hand2;          // 最初に配られた2枚目
        private int hand3;          // HIT1枚目(引いてなければ0)
        private int hand4;          // HIT2枚目(引いてなければ0)
        private int hand5;          // HIT3枚目(引いてなければ0)
        private String isBustStr;   // BUSTしたかどうか(文字列)

        DataModelBust(int hand1, int hand2, int hand3, int hand4, int hand5, String isBustStr) {
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.hand3 = hand3;
            this.hand4 = hand4;
            this.hand5 = hand5;
            this.isBustStr = isBustStr;
        }
    }

    private static int getHandEval(Card[] cards) {
        Hand hand = new Hand();
        Arrays.stream(cards).filter(Objects::nonNull).forEach(hand::add);
        return hand.eval();
    }

    private static void makeClassifierModel() {
        List<DataModelResult> dataResult= new ArrayList<>();
        List<DataModelBust> dataBust = new ArrayList<>();
        try (Stream<String> line = Files.lines(Paths.get(TRAIN_DATA))) {
            line.map(str -> str.split(",")).forEach(elements -> {
                Card[] dealerCards = {Card.valueOf(elements[1]), Card.valueOf(elements[2]), (elements[3].length() == 0) ? null : Card.valueOf(elements[3]), (elements[4].length() == 0) ? null : Card.valueOf(elements[4]), (elements[5].length() == 0) ? null : Card.valueOf(elements[5])};
                Card[] p1Cards = {Card.valueOf(elements[7]), Card.valueOf(elements[8]), (elements[9].length() == 0) ? null : Card.valueOf(elements[9]), (elements[10].length() == 0) ? null : Card.valueOf(elements[10]), (elements[11].length() == 0) ? null : Card.valueOf(elements[11])};
                Card[] p2Cards = {Card.valueOf(elements[14]), Card.valueOf(elements[15]), (elements[16].length() == 0) ? null : Card.valueOf(elements[16]), (elements[17].length() == 0) ? null : Card.valueOf(elements[17]), (elements[18].length() == 0) ? null : Card.valueOf(elements[18])};
                Card[] p3Cards = {Card.valueOf(elements[21]), Card.valueOf(elements[22]), (elements[23].length() == 0) ? null : Card.valueOf(elements[23]), (elements[24].length() == 0) ? null : Card.valueOf(elements[24]), (elements[25].length() == 0) ? null : Card.valueOf(elements[25])};
                Card[] p4Cards = {Card.valueOf(elements[28]), Card.valueOf(elements[29]), (elements[30].length() == 0) ? null : Card.valueOf(elements[30]), (elements[31].length() == 0) ? null : Card.valueOf(elements[31]), (elements[32].length() == 0) ? null : Card.valueOf(elements[32])};

                dataResult.add(new DataModelResult(dealerCards[0].getIndex(), p1Cards[0].getIndex(), p1Cards[1].getIndex(), (p1Cards[2] == null) ? 0 : p1Cards[2].getIndex(), (p1Cards[3] == null) ? 0 : p1Cards[3].getIndex(), (p1Cards[4] == null) ? 0 : p1Cards[4].getIndex(), Result.Type.valueOf(elements[12])));
                dataResult.add(new DataModelResult(dealerCards[0].getIndex(), p2Cards[0].getIndex(), p2Cards[1].getIndex(), (p2Cards[2] == null) ? 0 : p2Cards[2].getIndex(), (p2Cards[3] == null) ? 0 : p2Cards[3].getIndex(), (p2Cards[4] == null) ? 0 : p2Cards[4].getIndex(), Result.Type.valueOf(elements[19])));
                dataResult.add(new DataModelResult(dealerCards[0].getIndex(), p3Cards[0].getIndex(), p3Cards[1].getIndex(), (p3Cards[2] == null) ? 0 : p3Cards[2].getIndex(), (p3Cards[3] == null) ? 0 : p3Cards[3].getIndex(), (p3Cards[4] == null) ? 0 : p3Cards[4].getIndex(), Result.Type.valueOf(elements[26])));
                dataResult.add(new DataModelResult(dealerCards[0].getIndex(), p4Cards[0].getIndex(), p4Cards[1].getIndex(), (p4Cards[2] == null) ? 0 : p4Cards[2].getIndex(), (p4Cards[3] == null) ? 0 : p4Cards[3].getIndex(), (p4Cards[4] == null) ? 0 : p4Cards[4].getIndex(), Result.Type.valueOf(elements[33])));

                dataBust.add(new DataModelBust(p1Cards[0].getIndex(), p1Cards[1].getIndex(), (p1Cards[2] == null || p1Cards[3] == null) ? 0 : p1Cards[2].getIndex(), (p1Cards[3] == null || p1Cards[4] == null) ? 0 : p1Cards[3].getIndex(), 0, (getHandEval(p1Cards) > 21) ? CLASS_VALUES_BUST[1] : CLASS_VALUES_BUST[0]));
                dataBust.add(new DataModelBust(p2Cards[0].getIndex(), p2Cards[1].getIndex(), (p2Cards[2] == null || p2Cards[3] == null) ? 0 : p2Cards[2].getIndex(), (p2Cards[3] == null || p2Cards[4] == null) ? 0 : p2Cards[3].getIndex(), 0, (getHandEval(p2Cards) > 21) ? CLASS_VALUES_BUST[1] : CLASS_VALUES_BUST[0]));
                dataBust.add(new DataModelBust(p3Cards[0].getIndex(), p3Cards[1].getIndex(), (p3Cards[2] == null || p3Cards[3] == null) ? 0 : p3Cards[2].getIndex(), (p3Cards[3] == null || p3Cards[4] == null) ? 0 : p3Cards[3].getIndex(), 0, (getHandEval(p3Cards) > 21) ? CLASS_VALUES_BUST[1] : CLASS_VALUES_BUST[0]));
                dataBust.add(new DataModelBust(p4Cards[0].getIndex(), p4Cards[1].getIndex(), (p4Cards[2] == null || p4Cards[3] == null) ? 0 : p4Cards[2].getIndex(), (p4Cards[3] == null || p4Cards[4] == null) ? 0 : p4Cards[3].getIndex(), 0, (getHandEval(p4Cards) > 21) ? CLASS_VALUES_BUST[1] : CLASS_VALUES_BUST[0]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        int trainRatio = (int) (dataResult.size() * 0.7);
        List<DataModelResult> trainData = dataResult.subList(0, trainRatio);
        List<DataModelResult> evalData = dataResult.subList(trainRatio, dataResult.size());

        Instances trainArff = dataResult2arff(trainData);
        Instances evalArff = dataResult2arff(evalData);
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
//
//        System.out.println("***** RandomForest *****");
//        Classifier modelRf = getBuiltClassifier(trainArff, new RandomForest());
//        evalResult(modelRf, evalArff);
//        System.out.println();
//
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

        System.out.println("***** SVM *****");
        Classifier modelSVM = getBuiltClassifier(trainArff, new LibSVM());
        evalResult(modelSVM, evalArff);
        System.out.println();


        aiModelResult = modelSVM;
        try {
            SerializationHelper.write(CLASSIFIER_MODEL_RESULT, aiModelResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<DataModelBust> trainDataBust = dataBust.subList(0, trainRatio);
        List<DataModelBust> evalDataBust = dataBust.subList(trainRatio, dataBust.size());

        trainArff = dataBust2arff(trainDataBust);
        evalArff = dataBust2arff(evalDataBust);
//        writeArff(trainArff, TRAIN_ARFF);
//        writeArff(evalArff, EVAL_ARFF);

//        System.out.println("***** NaiveBayes *****");
//        Classifier modelNb = getBuiltClassifier(trainArff, new NaiveBayes());
//        evalResult(modelNb, evalArff);
//        System.out.println();
//
//        System.out.println("***** J48 *****");
//        modelJ48 =getBuiltClassifier(trainArff, new J48(), "-U");
//        evalResult(modelJ48, evalArff);
//        System.out.println();
//
//        System.out.println("***** RandomForest *****");
//        modelRf = getBuiltClassifier(trainArff, new RandomForest());
//        evalResult(modelRf, evalArff);
//        System.out.println();
//
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

        System.out.println("***** SVM *****");
        modelSVM = getBuiltClassifier(trainArff, new LibSVM());
        evalResult(modelSVM, evalArff);
        System.out.println();


        aiModelBust = modelSVM;
        try {
            SerializationHelper.write(CLASSIFIER_MODEL_BUST, aiModelBust);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Instances dataResult2arff(List<DataModelResult> data) {
        List<String> classValues = new ArrayList<>();
        for (Result.Type classValue : CLASS_VALUES_RESULT) {
            classValues.add(classValue.name());
        }

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Dealer Up Card"));
        attributes.add(new Attribute("Hand 1"));
        attributes.add(new Attribute("Hand 2"));
        attributes.add(new Attribute("Hand 3"));
        attributes.add(new Attribute("Hand 4"));
        attributes.add(new Attribute("Hand 5"));
        attributes.add(new Attribute("Predict Result", classValues));

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
            arff.add(new SparseInstance(1.0, values));
        });

        return arff;
    }
    private static Instances dataBust2arff(List<DataModelBust> data) {
        List<String> classValues = new ArrayList<>();
        Collections.addAll(classValues, CLASS_VALUES_BUST);

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Hand 1"));
        attributes.add(new Attribute("Hand 2"));
        attributes.add(new Attribute("Hand 3"));
        attributes.add(new Attribute("Hand 4"));
        attributes.add(new Attribute("Hand 5"));
        attributes.add(new Attribute("IS BUST", classValues));

        Instances arff = new Instances("Data", attributes, 0);
        arff.setClassIndex(arff.numAttributes() - 1);

        data.forEach(d -> {
            double[] values = new double[arff.numAttributes()];
            values[0] = d.hand1;
            values[1] = d.hand2;
            values[2] = d.hand3;
            values[3] = d.hand4;
            values[4] = d.hand5;
            values[5] = arff.attribute(5).indexOfValue(d.isBustStr);
            arff.add(new SparseInstance(1.0, values));
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

    private static Classifier getBuiltClassifier(Instances arff, AbstractClassifier classifier, String... options) {
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
        if (!new File(CLASSIFIER_MODEL_RESULT).exists()) {
            makeClassifierModel();
        }

        int predictedValue = Integer.MAX_VALUE;
        try {
            if (aiModelResult == null) {
                aiModelResult = (Classifier) SerializationHelper.read(CLASSIFIER_MODEL_RESULT);
            }
            List<DataModelResult> predictData = new ArrayList<>();
            predictData.add(new DataModelResult(dealerUpCard, hand1, hand2, hand3, hand4, hand5, Result.Type.WIN)); // 結果の引数はダミー
            Instances predictArff = dataResult2arff(predictData);
            predictedValue = (int) new Evaluation(predictArff).evaluateModelOnce(aiModelResult, predictArff.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (predictedValue == Integer.MAX_VALUE) ? null : CLASS_VALUES_RESULT[predictedValue];
    }
    static Boolean isBust(int hand1, int hand2, int hand3, int hand4, int hand5) {
        if (!new File(CLASSIFIER_MODEL_BUST).exists()) {
            makeClassifierModel();
        }

        int predictedValue = Integer.MAX_VALUE;
        try {
            if (aiModelBust == null) {
                aiModelBust = (Classifier) SerializationHelper.read(CLASSIFIER_MODEL_BUST);
            }
            List<DataModelBust> predictData = new ArrayList<>();
            predictData.add(new DataModelBust(hand1, hand2, hand3, hand4, hand5, CLASS_VALUES_BUST[0])); // 結果の引数はダミー
            Instances predictArff = dataBust2arff(predictData);
            predictedValue = (int) new Evaluation(predictArff).evaluateModelOnce(aiModelBust, predictArff.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (predictedValue == Integer.MAX_VALUE) ? null : predictedValue == 1;
    }


    public static void main(String[] args) {
        makeClassifierModel();
    }
}
