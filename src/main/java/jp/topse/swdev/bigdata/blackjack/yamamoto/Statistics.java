package jp.topse.swdev.bigdata.blackjack.yamamoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Statistics {
	List<Person> win = new ArrayList<>();
	List<Person> lose = new ArrayList<>();
	List<Person> draw = new ArrayList<>();

	
	private static Map<String, Statistics> myNumMap = new HashMap<>();
	private static Map<String, Statistics> dealerMap = new HashMap<>();
	
	public static Statistics getMyNumMapInstance(String name) {
		Statistics s = myNumMap.get(name);
		if(s == null) {
			s = new Statistics();
			myNumMap.put(name, s);
		}
		return s;
	}


	public static Statistics getDealerMapInstance(String name) {
		Statistics s = dealerMap.get(name);
		if(s == null) {
			s = new Statistics();
			dealerMap.put(name, s);
		}
		return s;
	}
	
	public void add(Person p) {
		if(p.isWin()) {
			win.add(p);
		}
		if(p.isLose()) {
			lose.add(p);
		}
		if(p.isDraw()) {
			draw.add(p);
		}
		
	}

	public static void print() {
		for(Entry<String, Statistics> entry : myNumMap.entrySet()) {
			String key = entry.getKey();
			Statistics value = entry.getValue();
			System.out.println(key+": win="+value.getNumOfWin()+" lose="+value.getNumOfLose()+ " draw="+value.getNumOfDraw());
		}
	}

	private int getNumOfWin() {
		return win.size();
	}
	private int getNumOfLose() {
		return lose.size();
	}
	private int getNumOfDraw() {
		return draw.size();
	}


	public void addForDealer(Person p) {
		for(Person player : p.personForDealer) {
			if(player.isWin()) {
				win.add(player);
			}
			if(player.isLose()) {
				lose.add(player);
			}
			if(player.isDraw()) {
				draw.add(player);
			}
		}
	}

	public static void printDealer() {
		for(Entry<String, Statistics> entry : dealerMap.entrySet()) {
			String key = entry.getKey();
			Statistics value = entry.getValue();
			System.out.println(key+": win="+value.getNumOfWin()+" lose="+value.getNumOfLose()+ " draw="+value.getNumOfDraw());
		}
	}

}
