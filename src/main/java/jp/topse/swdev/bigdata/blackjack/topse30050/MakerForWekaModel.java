package jp.topse.swdev.bigdata.blackjack.topse30050;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class MakerForWekaModel {
	private static final String TRAIN_DATA = "data/train.csv";
	private static final String TRAIN_ARFF = "data/train.arff";
//	private static final String EVAL_DATA = "data/test.csv";

	public static void main(String[] args) {
//		Instances trainArff = csv2arff(TRAIN_DATA);
//		writeArff(trainArff, TRAIN_ARFF);
//		Instances evalArff = csv2arff(EVAL_DATA);

//		System.out.println("***** Red by NaiveBayes *****");
//		Classifier c = analyzeByNaiveBayes(trainArff);
//		evalResult(c, evalArff);
//		System.out.println();
//
//		System.out.println("***** Red by J48 *****");
//		Classifier c2 = analyzeByJ48(trainArff);
//		evalResult(c2, evalArff);
//		System.out.println();

/*
SerializationHelper.write("/path/to/test.model", clf); // ファイルに保存
MultiClassClassifier model = (MultiClassClassifier) SerializationHelper.read("/path/to/test.model"); // ファイルから読み込み

// byte配列に変換
ByteArrayOutputStream stream = new ByteArrayOutputStream();
SerializationHelper.write(stream, clf);
byte[] modelBinary = stream.toByteArray();
// byte配列から読み込み
ByteArrayInputStream inputStream = new ByteArrayInputStream(modelBinary, 0, modelBinary.length);
MultiClassClassifier model = (MultiClassClassifier) SerializationHelper.read(inputStream);
 */
	}

//    private static Instances csv2arff(String csvPath) {
//		FastVector classValues = new FastVector();
//		classValues.addElement("0");
//		classValues.addElement("1");
//		classValues.addElement("2");
//		classValues.addElement("3");
//		classValues.addElement("4");
//		classValues.addElement("5");
//		classValues.addElement("6");
//		classValues.addElement("7");
//
//		FastVector attributes = new FastVector();
//		attributes.addElement(new Attribute("p1"));
//		attributes.addElement(new Attribute("p2"));
//		attributes.addElement(new Attribute("p3"));
//		attributes.addElement(new Attribute("p4"));
//		attributes.addElement(new Attribute("p5"));
//		attributes.addElement(new Attribute("p6"));
//		attributes.addElement(new Attribute("p7"));
//		attributes.addElement(new Attribute("p8"));
//		attributes.addElement(new Attribute("p9"));
//		attributes.addElement(new Attribute("p10"));
//		attributes.addElement(new Attribute("class", classValues));

//		Instances arff = new Instances("Data", attributes, 0);
//		arff.setClassIndex(arff.numAttributes() - 1);

//		try (Stream<String> line = Files.lines(Paths.get(csvPath))) {
//			line.skip(1).map(str -> str.split(",")).forEach(elements -> {
//				double[] values = new double[arff.numAttributes()];
//				// elements[0]はidなので使用しない
//				values[0] = Double.parseDouble(elements[1]);	// p1
//				values[1] = Double.parseDouble(elements[2]);	// p2
//				values[2] = Double.parseDouble(elements[3]);	// p3
//				values[3] = Double.parseDouble(elements[4]);	// p4
//				values[4] = Double.parseDouble(elements[5]);	// p5
//				values[5] = Double.parseDouble(elements[6]);	// p6
//				values[6] = Double.parseDouble(elements[7]);	// p7
//				values[7] = Double.parseDouble(elements[8]);	// p8
//				values[8] = Double.parseDouble(elements[9]);	// p9
//				values[9] = Double.parseDouble(elements[10]);	// p10
//				values[10] = Double.parseDouble(elements[11]);	// class
//				arff.add(new Instance(1.0, values));
//			});
//		} catch (IOException | NumberFormatException e) {
//			e.printStackTrace();
//		}

//		return arff;
//	}

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
//		try {
//			nb.buildClassifier(arff);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return nb;
	}

    private static Classifier analyzeByJ48(Instances arff) {
		J48 tree = new J48();
//		try {
//			String[] options = new String[1];
//			options[0] = "-U";
//			tree.setOptions(options);
//			tree.buildClassifier(arff);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

    	return tree;
	}

	private static void evalResult(Classifier c, Instances arff) {
//		try {
//			Evaluation eval = new Evaluation(arff);
//			eval.evaluateModel(c, arff);
//			System.out.println(eval.toSummaryString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
