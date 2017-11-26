package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
	List<Person> players = new ArrayList<>();
	List<Person> dealers = new ArrayList<>();

	public void read(File file) {
		try(BufferedReader br = new BufferedReader(new FileReader(file));) {
			for(String line; (line = br.readLine()) != null;) {
				String[] strs = line.split(",");

				//0から5まではディーラの入力値
				Person dealer = new Person(strs[0], true, Arrays.copyOfRange(strs, 1, 6), null, null);
				dealers.add(dealer);
				for(int i=6;i<strs.length;i+=7) {
					Person person = new Person(strs[i], false, Arrays.copyOfRange(strs, i+1, i+6), strs[i+6], dealer);
					players.add(person);
					dealer.addPlayerForDealer(person);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String CSV_LABEL2 = "first,second,numOfAce,total,dealer,took,result";
	public static final String CSV_LABEL3 = "first,second,third,numOfAce,total,dealer,took,result";
	public static final String CSV_LABEL4 = "first,second,third,fourth,numOfAce,total,dealer,took,result";

	public static int getNumOfAce(int[] numArray) {
		int value = 0;
		for(int i=0;i<numArray.length;i++) {
			if(numArray[i] == 1 || numArray[i] == 11) {
				value ++;
			}
		}
		return value;
	}

	public static int[] getNumArray(int[] numbers, int num) {
		int[] a = Arrays.copyOf(numbers, num);
		Arrays.sort(a);

		int value = 0;
		for(int i=0;i<num;i++) {
			value += a[i];
		}

		while(value > 21 && a[num-1] == 11) {
			value = 0;
			int[] b = a;
			a = new int[num];
			a[0] = 1;
			value = a[0];
			for(int i=0;i<num-1;i++) {
				a[i+1] = b[i];
				value+=a[i+1];
			}
		}
		return a;
	}

	public void writeCSV(String path) {
		// 2枚目の判断用
		String path2=path+"-2.csv";
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path2)))){
			String line = CSV_LABEL2;
			bw.write(line+"\r\n");
			for(Person p : players) {
				int[] numArray = getNumArray(p.getNumbers(), 2);
				StringBuilder sb = new StringBuilder();
				sb.append(numArray[0]).append(",")
				.append(numArray[1]).append(",")
				.append(getNumOfAce(numArray)).append(",")
				.append(numArray[0] + numArray[1]).append(",")
				.append(p.getDealer().getFirstValue()).append(",")
				.append(p.tookCard(2)).append(",")
				.append(p.getResult());
				bw.write(sb.toString()+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3枚目の判断用
		String path3=path+"-3.csv";
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path3)))){
			String line = CSV_LABEL3;
			bw.write(line+"\r\n");
			for(Person p : players) {
				int[] numArray = getNumArray(p.getNumbers(), 3);
				if(numArray[0] == 0) continue;
				StringBuilder sb = new StringBuilder();
				sb.append(numArray[0]).append(",")
				.append(numArray[1]).append(",")
				.append(numArray[2]).append(",")
				.append(getNumOfAce(numArray)).append(",")
				.append(numArray[0] + numArray[1] + numArray[2]).append(",")
				.append(p.getDealer().getFirstValue()).append(",")
				.append(p.tookCard(3)).append(",")
				.append(p.getResult());
				bw.write(sb.toString()+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 4枚目の判断用
		String path4=path+"-4.csv";
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path4)))){
			String line = CSV_LABEL4;
			bw.write(line+"\r\n");
			for(Person p : players) {
				int[] numArray = getNumArray(p.getNumbers(), 4);
				if(numArray[0] == 0) continue;
				StringBuilder sb = new StringBuilder();
				sb.append(numArray[0]).append(",")
				.append(numArray[1]).append(",")
				.append(numArray[2]).append(",")
				.append(numArray[3]).append(",")
				.append(getNumOfAce(numArray)).append(",")
				.append(numArray[0] + numArray[1] + numArray[2] + numArray[3]).append(",")
				.append(p.getDealer().getFirstValue()).append(",")
				.append(p.tookCard(4)).append(",")
				.append(p.getResult());
				bw.write(sb.toString()+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calcDealerNum() {
		for(int i=0;i<dealers.size();i++) {
			Person p = dealers.get(i);

			Statistics s = Statistics.getDealerMapInstance(p.getFirstValue()+"");
			s.addForDealer(p);
		}
	}
}