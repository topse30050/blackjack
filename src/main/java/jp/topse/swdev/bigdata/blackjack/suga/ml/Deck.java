package jp.topse.swdev.bigdata.blackjack.suga.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Card;

public class Deck {
	private static int cards[][];
	private static int sum[];
	private int current;
	static {
		String filename = "models/suga/cards.csv";
		int[][] incards = new int[25][13];
		int[] insum = new int[25];
		try (BufferedReader in = new BufferedReader(new FileReader(new File(
				filename)))) {
			String line;
			int linenum = 0;

			while ((line = in.readLine()) != null) {
				String[] vals = line.split(",");
				for (int i = 0; i < 25; i++) {
					if (i < vals.length) {
						incards[i][linenum] = Integer.valueOf(vals[i].trim());
					} else {
						incards[i][linenum] = 1;
					}
				}
				linenum++;
			}
			for (int i = 0; i < 25; i++) {
				int tmpsum = 0;
				for (int j = 0; j < 13; j++) {
					tmpsum += incards[i][j];
				}
				insum[i] = tmpsum;
			}
			cards = incards;
			sum = insum;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Deck createDefault() {
		return new Deck();
	}

	public static Deck createTestDeck(int i) {
		return new Deck();
	}

	public static Deck createQLearningDeck() {
		return new Deck();
	}

	public static void reset() {
		;
	}

	public Deck() {
		current = 0;
	}

	public interface RandomGenerator {
		public int nextInt(int bound);
	}

	private static class DefaultRandomGenerator implements RandomGenerator {

		private final Random randomGenerator = new Random();

		@Override
		public int nextInt(int bound) {
			return randomGenerator.nextInt(bound);
		}
	}

	private RandomGenerator randomGenerator = new DefaultRandomGenerator();

	public Card nextCard() {
		Card card = null;
		// System.out.println("current = " + current);
		if (current < 25) {
			// System.out.println("sum = " + sum);
			int tmpindex = randomGenerator.nextInt(sum[current]) + 1;
			// System.out.println("tmpindex = " + tmpindex);
			int index = 0;
			int tmpsum = 0;
			for (int i = 0; i < 13; i++) {
				tmpsum += cards[current][i];
				if (tmpindex <= tmpsum) {
					index = i + 1;
					break;
				}
			}
			// System.out.println("index = " + index);
			card = Card.indexOf(index);
		} else {
			int index = randomGenerator.nextInt(Card.numberOfTypes()) + 1;
			card = Card.indexOf(index);
		}
		current++;
		return card;
	}
}
