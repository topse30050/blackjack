package jp.topse.swdev.bigdata.blackjack.topse30050;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import jp.topse.swdev.bigdata.blackjack.Card;
import jp.topse.swdev.bigdata.blackjack.Result;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.*;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.core.converters.ArffSaver;

public class MakerForWekaModel {
//    private static final String TRAIN_DATA = "data/training-data.csv";
    private static final String TRAIN_DATA = "data/training-deck3.csv";
//    private static final String TRAIN_ARFF = "data/train.arff";
//    private static final String EVAL_ARFF = "data/eval.arff";
    private static final String CLASSIFIER_MODEL = "models/topse30050.model";

    private static final Result.Type[] CLASS_VALUES = new Result.Type[]{Result.Type.WIN, Result.Type.DRAW, Result.Type.LOSE};
    private static Classifier aiModel;

    private MakerForWekaModel() {
    }

    private static class DataModel {
        private int dealerUpCard;   // ディーラーの1枚目
//        private int playOrder;      // プレイ順序
        private int hand1;          // 最初に配られた1枚目
        private int hand2;          // 最初に配られた2枚目
        private int valBeforeHit;   // 最後の1枚を引く前の合計点(初期配布2枚で終わった場合は0とする)
        private int cardsCount;     // ゲーム終了時の所持枚数
        private Result.Type result; // ゲーム終了時の結果

//        private DataModel(int dealerUpCard, int playOrder, int hand1, int hand2, int cardsCount, Result.Type result) {
        private DataModel(int dealerUpCard, int hand1, int hand2, int valBeforeHit, int cardsCount, Result.Type result) {
            this.dealerUpCard = dealerUpCard;
//            this.playOrder = playOrder;
            this.hand1 = hand1;
            this.hand2 = hand2;
            this.valBeforeHit = valBeforeHit;
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
//                    .append(String.valueOf(valBeforeHit))
//                    .append(",")
//                    .append(String.valueOf(cardsCount))
//                    .append(",")
//                    .append(String.valueOf(result.name()))
//                    .toString();
//        }
    }


    private static void makeClassifierModel() {
        List<DataModel> data= new ArrayList<>();
        try (Stream<String> line = Files.lines(Paths.get(TRAIN_DATA))) {
            line.map(str -> str.split(",")).forEach(elements -> {
//                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 1, Card.valueOf(elements[7]).getIndex(), Card.valueOf(elements[8]).getIndex(), 2 + getCardCountFromCsvString(elements[9], elements[10], elements[11]), Result.Type.valueOf(elements[12])));
//                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 2, Card.valueOf(elements[14]).getIndex(), Card.valueOf(elements[15]).getIndex(), 2 + getCardCountFromCsvString(elements[16], elements[17], elements[18]), Result.Type.valueOf(elements[19])));
//                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 3, Card.valueOf(elements[21]).getIndex(), Card.valueOf(elements[22]).getIndex(), 2 + getCardCountFromCsvString(elements[23], elements[24], elements[25]), Result.Type.valueOf(elements[26])));
//                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), 4, Card.valueOf(elements[28]).getIndex(), Card.valueOf(elements[29]).getIndex(), 2 + getCardCountFromCsvString(elements[30], elements[31], elements[32]), Result.Type.valueOf(elements[33])));

                String[] p1Hands = { elements[7], elements[8], elements[9], elements[10], elements[11] };
                String[] p2Hands = { elements[14], elements[15], elements[16], elements[17], elements[18] };
                String[] p3Hands = { elements[21], elements[22], elements[23], elements[24], elements[25] };
                String[] p4Hands = { elements[28], elements[29], elements[30], elements[31], elements[32] };
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), Card.valueOf(p1Hands[0]).getValue(), Card.valueOf(p1Hands[1]).getValue(), getTotalBeforeLastHit(p1Hands), getCardCountFromCsvString(p1Hands), Result.Type.valueOf(elements[12])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), Card.valueOf(p2Hands[0]).getValue(), Card.valueOf(p2Hands[1]).getValue(), getTotalBeforeLastHit(p2Hands), getCardCountFromCsvString(p2Hands), Result.Type.valueOf(elements[19])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), Card.valueOf(p3Hands[0]).getValue(), Card.valueOf(p3Hands[1]).getValue(), getTotalBeforeLastHit(p3Hands), getCardCountFromCsvString(p3Hands), Result.Type.valueOf(elements[26])));
                data.add(new DataModel(Card.valueOf(elements[1]).getIndex(), Card.valueOf(p4Hands[0]).getValue(), Card.valueOf(p4Hands[1]).getValue(), getTotalBeforeLastHit(p4Hands), getCardCountFromCsvString(p4Hands), Result.Type.valueOf(elements[33])));
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
//
//        System.out.println("***** Logistic *****");
//        Classifier c4 = getBuiltClassifier(trainArff, new Logistic());
//        evalResult(c4, evalArff);
//        System.out.println();
//
//        System.out.println("***** SMO *****");
//        Classifier c5 = getBuiltClassifier(trainArff, new SMO());
//        evalResult(c5, evalArff);
//        System.out.println();
//
//        System.out.println("***** Multilayer Perceptron *****");
//        Classifier c6 = getBuiltClassifier(trainArff, new MultilayerPerceptron(), "-L", "0.5", "-M", "0.1");
//        evalResult(c6, evalArff);
//        System.out.println();
//
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

    private static int getCardCountFromCsvString(String... hands) {
        return hands.length - (int) Arrays.stream(hands).filter(String::isEmpty).count();
    }

    private static int getTotalBeforeLastHit(String... hands) {
        if (hands.length != 5) {
            return 0;
        }

        int hitCount = getCardCountFromCsvString(hands[2], hands[3], hands[4]);
        if (hitCount == 0) {
            return 0;
        }

        int ret = Card.valueOf(hands[0]).getValue();
        for (int i = 0; i < hitCount; i++) {
            ret += Card.valueOf(hands[i+1]).getValue();
        }

        return ret;
    }

    private static Instances data2arff(List<DataModel> data) {
        FastVector classValues = new FastVector();
        for (Result.Type classValue : CLASS_VALUES) {
            classValues.addElement(classValue.name());
        }

        FastVector attributes = new FastVector();
        attributes.addElement(new Attribute("Dealer Up Card"));
//        attributes.addElement(new Attribute("Play Order"));
        attributes.addElement(new Attribute("Hand1"));
        attributes.addElement(new Attribute("Hand2"));
        attributes.addElement(new Attribute("Value Before Final HIT"));
        attributes.addElement(new Attribute("Cards Count"));
        attributes.addElement(new Attribute("Result", classValues));

        Instances arff = new Instances("Data", attributes, 0);
        arff.setClassIndex(arff.numAttributes() - 1);

        data.forEach(d -> {
            double[] values = new double[arff.numAttributes()];
            values[0] = d.dealerUpCard;
//            values[1] = d.playOrder;
//            values[2] = d.hand1;
//            values[3] = d.hand2;
//            values[4] = d.cardsCount;
//            values[5] = arff.attribute(5).indexOfValue(d.result.name());
            values[1] = d.hand1;
            values[2] = d.hand2;
            values[3] = d.valBeforeHit;
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


//    public static Result.Type getPredictedResult(int dealerUpCard, int playOrder, int hand1, int hand2, int cardsCount) {
    static Result.Type getPredictedResult(int dealerUpCard, int hand1, int hand2, int valBeforeHit, int cardsCount) {
        if (!new File(CLASSIFIER_MODEL).exists()) {
            makeClassifierModel();
        }

        int predictedValue = Integer.MAX_VALUE;
        try {
            if (aiModel == null) {
                aiModel = (Classifier) SerializationHelper.read(CLASSIFIER_MODEL);
            }
//            System.out.println("***** Load Model *****");
//            evalResult(preModel, evalArff);
//            System.out.println();
            List<DataModel> predictData = new ArrayList<>();
//            predictData.add(new DataModel(dealerUpCard, playOrder, hand1, hand2, cardsCount, Result.Type.WIN)); // 結果の引数はダミー
            predictData.add(new DataModel(dealerUpCard, hand1, hand2, valBeforeHit, cardsCount, Result.Type.WIN)); // 結果の引数はダミー
            Instances predictArff = data2arff(predictData);
            predictedValue = (int) new Evaluation(predictArff).evaluateModelOnce(aiModel, predictArff.firstInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (predictedValue == Integer.MAX_VALUE) ? null : CLASS_VALUES[predictedValue];
    }



    public static void main(String[] args) {
//        makeClassifierModel();

//        Result.Type result = MakerForWekaModel.getPredictedResult(10, 1, 8, 3, 3);
//        Result.Type result = MakerForWekaModel.getPredictedResult(7, 4, 1, 2, 4);

        Result.Type result = MakerForWekaModel.getPredictedResult(7, 1, 2, 13, 3);
        System.out.println(result);
    }
}
