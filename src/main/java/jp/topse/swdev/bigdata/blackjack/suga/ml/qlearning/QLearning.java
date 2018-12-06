package jp.topse.swdev.bigdata.blackjack.suga.ml.qlearning;

import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class QLearning {
	/* ε-greedy法の探策確率ε */
	private static final double	EPSILON			= 0.20;
	/* 学習率α */
	private static final double	ALPHA			= 0.001;
	/* 割引率γ */
	private static final double	GAMMA			= 0.90;
	//	/* 学習回数 */
	//	private static final int	LEARNING_TIMES	= 1000000;
	//	/* Qの初期値の最大値(乱数の最大値) */
	//	private static final int	INIT_Q_MAX	= 30;

	/* WIN時の報酬 */
	public static final int		WIN_REWARD		= 700;
	/* DRAW時の報酬 */
	public static final int		DRAW_REWARD		= 0;
	/* LOSE時の報酬 */
	public static final int		LOSE_REWARD		= -100;
	/* BURST時の報酬 */
	public static final int		BURST_REWARD	= -90;
	/* HITしてBurstしなかったときの報酬 */
	public static final int		REMAIN_REWARD	= 1;

	/*
	 * Q値
	 * [dealer(2-11)]
	 * [player(12-21,over]
	 * [handtype(soft/hard:0/1)]
	 * [tactics(stand/hit:0/1)]
	 */
	private double				q[][][][];
	private static final int[]	Q_LEN			= { 12, 23, HandType.values().length,
			Action.values().length };

	/**
	 * コンストラクタ
	 */
	public QLearning() {
		// Q値の初期化
		q = new double[Q_LEN[0]][Q_LEN[1]][Q_LEN[2]][Q_LEN[3]];
	}

	/**
	 * Q値の初期化
	 */
	public void initQ() {
		for (int x = 0; x < Q_LEN[0]; x++) {
			for (int y = 0; y < Q_LEN[1]; y++) {
				for (int z = 0; z < Q_LEN[2]; z++) {
					for (int a = 0; a < Q_LEN[3]; a++) {
						//						q[x][y][z][a] = rand.nextInt(INIT_Q_MAX + 1);
						if (y == 21) {
							q[x][y][z][Action.STAND.ordinal()] = WIN_REWARD;
						} else if (y == 22) {
							q[x][y][z][a] = LOSE_REWARD;
						} else {
							q[x][y][z][a] = 0;
						}
					}
				}
			}
		}
	}

	/**
	 * 行動の選択 ε-greedy法
	 * 
	 * @return
	 */
	public int eGreedy(int dealer, int player, int type) {
		int select = 0;
		Random rand = new Random();
		int randNum = rand.nextInt(100 + 1);

		// 確率(1-ε)でQ値採用
		if (randNum > EPSILON * 100.0) {
			// εの確率 Q値が最大となるようなaを選択
			for (int a = 0; a < Q_LEN[3]; a++) {
				if (q[dealer][player][type][select] < q[dealer][player][type][a]) {
					select = a;
				}
			}
		// 確率εで探索
		} else {
			select = rand.nextInt(Q_LEN[3]);
		}
		return select;
	}

	/**
	 * Q値の更新
	 * 
	 * @param reward
	 * @param a
	 */
	public void updateQ(int dealer, int player, int handtype, int action, int nextplayer, int nexthandtype,
			int reward) {

		// 状態s'で行った時にQ値が最大となるような行動
		int maxA = 0;
		for (int i = 0; i < Q_LEN[3]; i++) {
			if (q[dealer][nextplayer][nexthandtype][maxA] < q[dealer][nextplayer][nexthandtype][i]) {
				maxA = i;
			}
		}

		// Q値の更新
		q[dealer][player][handtype][action] = (1.0 - ALPHA) * q[dealer][player][handtype][action] + ALPHA
				* (reward + GAMMA * q[dealer][nextplayer][nexthandtype][maxA]);
		//		System.out.println(
		//				"d:" + dealer + ",p:" + player + ",h:" + handtype + ",a:" + action + " updated from np:" + nextplayer
		//						+ ",nh:" + nexthandtype + ",r:" + reward + ",q:" + q[dealer][nextplayer][nexthandtype][maxA]);
	}

	/**
	 * すべてのQ値を出力
	 */
	public String printQ() {
		StringBuilder sb = new StringBuilder();
		sb.append("dealer,player,handtype,action,Q-value" + "\n");
		for (int x = 0; x < Q_LEN[0]; x++) {
			for (int y = 0; y < Q_LEN[1]; y++) {
				for (int z = 0; z < Q_LEN[2]; z++) {
					for (int a = 0; a < Q_LEN[3]; a++) {
						sb.append(x + ", " + y + ", " + z + ", " + a + ", " + q[x][y][z][a] + "\n");
					}
				}
			}
		}
		return new String(sb);
	}
}