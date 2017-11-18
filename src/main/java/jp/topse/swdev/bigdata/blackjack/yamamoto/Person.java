package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Person {
	String name;
	boolean isDealer;
	String[] data;
	String result;
	Person mydealer;
	
	int[] numbers;
	int num;
	
	int twoEval;
	int threeEval;
	int fourEval;
	int fiveEval;
	
	int numOfAce;
	
	List<Person> personForDealer = new ArrayList<>();

/*	
	public int getSmallValue() {
		return numbers[0] < numbers[1] ? numbers[0] : numbers[1];
	}
	
	public int getLargeValue() {
		return numbers[0] < numbers[1] ? numbers[1] : numbers[0];
	}*/
	
	public Person(String name, boolean isDealer, String[] data, String result, Person mydealer) {
		this.name = name;
		this.isDealer = isDealer;
		this.data = data;
		this.result = result;
		this.mydealer = mydealer;
		toNum();
		this.twoEval = calEvals(2);
		this.threeEval = calEvals(3);
		this.fourEval = calEvals(4);
		this.fiveEval = calEvals(5);
	}
	
	public boolean isWin() {
		return "WIN".equals(result);
	}
	public boolean isLose() {
		return "LOSE".equals(result);
	}
	public boolean isDraw() {
		return "DRAW".equals(result);
	}

	public int[] getNumbers() {
		return numbers;
	}
	
	public void toNum() {
		numbers = new int[data.length];
		for(int i=0;i<data.length;i++) {
			if("ACE".equals(data[i])) {numbers[i] = 11;numOfAce++;}
			else if("TWO".equals(data[i])) numbers[i] = 2;
			else if("THREE".equals(data[i])) numbers[i] = 3;
			else if("FOUR".equals(data[i])) numbers[i] = 4;
			else if("FIVE".equals(data[i])) numbers[i] = 5;
			else if("SIX".equals(data[i])) numbers[i] = 6;
			else if("SEVEN".equals(data[i])) numbers[i] = 7;
			else if("EIGHT".equals(data[i])) numbers[i] = 8;
			else if("NINE".equals(data[i])) numbers[i] = 9;
			else if("TEN".equals(data[i])) numbers[i] = 10;
			else if("JACK".equals(data[i])) numbers[i] = 10;
			else if("QUEEN".equals(data[i])) numbers[i] = 10;
			else if("KING".equals(data[i])) numbers[i] = 10;
			else {num = i;break;}
			if(i+1 == data.length) {
				num = data.length;
			}
		}
	}
	
	public int calEvals(int num) {
		List<Integer> list = new LinkedList<Integer>();
		for(int i=0;i<num;i++) {
			list.add(numbers[i]);
		}
		return eval(list);
	}

	public void addPlayerForDealer(Person person) {
		personForDealer.add(person);
	}

	public int getFirstValue() {
		return numbers[0];
	}

	public Person getDealer() {
		return mydealer;
	}

	public boolean tookCard(int num) {
		return this.num > num;
	}

	public String getResult() {
		return result;
	}
	

    public int eval(List<Integer> cards) {
        int score = cards.stream()
                .map(card -> card.intValue())
                .reduce(0, (sum, n) -> sum + n);
        int numOfAce = (int)cards.stream()
                .filter(card -> card == 11)
                .count();
        for (int i = 0; i < numOfAce; ++i) {
            if (score > 21) {
                score -= 10;
            }
        }

        return score;
    }
}
