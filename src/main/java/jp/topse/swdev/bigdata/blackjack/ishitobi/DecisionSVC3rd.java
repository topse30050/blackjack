package jp.topse.swdev.bigdata.blackjack.ishitobi;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;


public class DecisionSVC3rd {
		public static void main(String[] args) {
			try {
		      DataSource source = new DataSource("tr_SVC3rd_testdeck3_17.arff");
		      Instances instances = source.getDataSet();
		      instances.setClassIndex(13);
		      Classifier classifier = new SMO();
		      classifier.buildClassifier(instances);

		      Evaluation eval = new Evaluation(instances);
		      eval.evaluateModel(classifier, instances);
		      System.out.println(eval.toSummaryString());
		      
		      SerializationHelper.write("./ishitobi_SVC3rd_test3.model", classifier);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	  public double main(String td1,String tp11,String tp12,String tp21,String tp22,String tp31,String tp32,String tp41,String tp42,String tp51,String tp52,String tp61,String tp62) {
	    try {
	    	
	    	Classifier classifier = (Classifier)SerializationHelper.read("./models/ishitobi/ishitobi_SVC3rd_test3.model");

	      FastVector card = new FastVector(13);
	       card.addElement("ACE");
	       card.addElement("TWO");
	       card.addElement("THREE");
	       card.addElement("FOUR");
	       card.addElement("FIVE");
	       card.addElement("SIX");
	       card.addElement("SEVEN");
	       card.addElement("EIGHT");
	       card.addElement("NINE");
	       card.addElement("TEN");
	       card.addElement("JACK");
	       card.addElement("QUEEN");
	       card.addElement("KING");     
	      FastVector ac = new FastVector(2);
	       ac.addElement("STAND");
	       ac.addElement("HIT");        
	       
	      Attribute d1 = new Attribute("d1", card); 
	      Attribute p11 = new Attribute("p11", card);
	      Attribute p12 = new Attribute("p12", card); 
	      Attribute p13 = new Attribute("p13", card); 
	      Attribute p14 = new Attribute("p14", card); 
	      Attribute p15 = new Attribute("p15", card); 
	      Attribute p21 = new Attribute("p21", card);
	      Attribute p22 = new Attribute("p22", card); 
	      Attribute p23 = new Attribute("p23", card); 
	      Attribute p24 = new Attribute("p24", card); 
	      Attribute p25 = new Attribute("p25", card); 
	      Attribute p31 = new Attribute("p31", card);
	      Attribute p32 = new Attribute("p32", card);    
	      Attribute p33 = new Attribute("p33", card);
	      Attribute p34 = new Attribute("p34", card);
	      Attribute p35 = new Attribute("p35", card);
	      Attribute p41 = new Attribute("p41", card);
	      Attribute p42 = new Attribute("p42", card);    
	      Attribute p43 = new Attribute("p43", card);
	      Attribute p44 = new Attribute("p44", card);
	      Attribute p45 = new Attribute("p45", card);
	      Attribute p51 = new Attribute("p51", card);
	      Attribute p52 = new Attribute("p52", card);    
	      Attribute p53 = new Attribute("p53", card);
	      Attribute p54 = new Attribute("p54", card);
	      Attribute p55 = new Attribute("p55", card);
	      Attribute p61 = new Attribute("p61", card);
	      Attribute p62 = new Attribute("p62", card);    
	      Attribute p63 = new Attribute("p63", card);
	      Attribute p64 = new Attribute("p64", card);
	      Attribute p65 = new Attribute("p65", card);     
	      Attribute action3 = new Attribute("action3", ac);
	      Attribute action4 = new Attribute("action4", ac);
	      Attribute action5 = new Attribute("action5", ac);
	      
	      
	      FastVector attributes = new FastVector();
	      attributes.addElement(d1);
	      attributes.addElement(p11);
	      attributes.addElement(p12);
//	      attributes.addElement(p13);
//	      attributes.addElement(p14);
//	      attributes.addElement(p15);
	      attributes.addElement(p21);
	      attributes.addElement(p22);
//	      attributes.addElement(p23);
//	      attributes.addElement(p24);
//	      attributes.addElement(p25);
	      attributes.addElement(p31);
	      attributes.addElement(p32);
//	      attributes.addElement(p33);
//	      attributes.addElement(p34);
//	      attributes.addElement(p35);
	      attributes.addElement(p41);
	      attributes.addElement(p42);
//	      attributes.addElement(p43);
//	      attributes.addElement(p44);
//	      attributes.addElement(p45);
	      attributes.addElement(p51);
	      attributes.addElement(p52);
//	      attributes.addElement(p53);
//	      attributes.addElement(p54);
//	      attributes.addElement(p55);
	      attributes.addElement(p61);
	      attributes.addElement(p62);
//	      attributes.addElement(p63);
//	      attributes.addElement(p64);
//	      attributes.addElement(p65);
	      attributes.addElement(action3);
//	      attributes.addElement(action4);
//	      attributes.addElement(action5);
	          
	      Instances data = new Instances("SVC3rdInstances", attributes, 0);
	      data.setClassIndex(13);

	      Instance instance = new Instance(data.numAttributes());
	      instance.setDataset(data);

	      instance.setValue(d1, card.indexOf(td1));
	      instance.setValue(p11, card.indexOf(tp11));
	      instance.setValue(p12, card.indexOf(tp12));
	      instance.setValue(p21, card.indexOf(tp21));      
	      instance.setValue(p22, card.indexOf(tp22));      
	      instance.setValue(p31, card.indexOf(tp31));
	      instance.setValue(p32, card.indexOf(tp32));   
	      instance.setValue(p41, card.indexOf(tp41));
	      instance.setValue(p42, card.indexOf(tp42));
	      instance.setValue(p51, card.indexOf(tp51));
	      instance.setValue(p52, card.indexOf(tp52));
	      instance.setValue(p61, card.indexOf(tp61));
	      instance.setValue(p62, card.indexOf(tp62));	      
	      
	      double result = classifier.classifyInstance(instance);
//	      System.out.println(result);
	      return result;
	    } catch (Exception e) {
	      e.printStackTrace();
	      return 0;
	    }

	  }

	}
