package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo;

import java.util.List;
import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class MonteCarlo {
	/* ��-greedy�@�̒T��m���� */
	private static final double	EPSILON			= 0.05;
	/* WIN���̕�V */
	public static final int		WIN_REWARD		= 100;
	/* DRAW���̕�V */
	public static final int		DRAW_REWARD		= 0;
	/* LOSE���̕�V */
	public static final int		LOSE_REWARD		= 0;
	/* BURST���̕�V */
	public static final int		BURST_REWARD	= 0;

	/*
	 * Q�l
	 * [dealer(2-11)]
	 * [player(12-21,over]
	 * [handtype(soft/hard:0/1)]
	 * [action(stand/hit:0/1)]
	 * [average/count]
	 */
	private double				q[][][][][];
	private static final int[]	Q_LEN			= { 12, 23, HandType.values().length,
			Action.values().length, 2 };

	/**
	 * �R���X�g���N�^
	 */
	public MonteCarlo() {
		// Q�l�̏�����
		q = new double[Q_LEN[0]][Q_LEN[1]][Q_LEN[2]][Q_LEN[3]][Q_LEN[4]];
	}

	/**
	 * Q�l�̏�����
	 */
	public void initQ() {
		for (int x = 0; x < Q_LEN[0]; x++) {
			for (int y = 0; y < Q_LEN[1]; y++) {
				for (int z = 0; z < Q_LEN[2]; z++) {
					for (int a = 0; a < Q_LEN[3]; a++) {
						if (y == 21) {
							q[x][y][z][Action.STAND.ordinal()][0] = WIN_REWARD;
							q[x][y][z][Action.STAND.ordinal()][1] = 0.0;
						} else if (y == 22) {
							q[x][y][z][a][0] = LOSE_REWARD;
							q[x][y][z][a][1] = 0.0;
						} else {
							for (int v = 0; v < Q_LEN[4]; v++) {
								q[x][y][z][a][v] = 0.0;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * �s���̑I�� ��-greedy�@
	 * 
	 * @return
	 */
	public int eGreedy(int dealer, int player, int type) {
		int select = 0;
		Random rand = new Random();
		int randNum = rand.nextInt(100 + 1);

		// �m��(1-��)��Q�l�̗p
		if (randNum > EPSILON * 100.0) {
			// �Â̊m�� Q�l���ő�ƂȂ�悤��a��I��
			for (int a = 0; a < Q_LEN[3]; a++) {
				if (q[dealer][player][type][select][0] < q[dealer][player][type][a][0]) {
					select = a;
				}
			}
			// �m���ÂŒT��
		} else {
			select = rand.nextInt(Q_LEN[3]);
		}
		return select;
	}

	/**
	 * Q�l�̍X�V
	 * 
	 * @param reward
	 * @param a
	 */
	public void updateQ(List<Qarg> qargs, int reward) {
		// Q�l�̍X�V
		for (Qarg qarg : qargs) {
			double tmpval = q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][0];
			double tmpcnt = q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][1];
			q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][0] = (reward + tmpval * tmpcnt) / (tmpcnt + 1);
			q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][1] = tmpcnt + 1;
//			System.out.println(
//					"d:" + qarg.dealer + ",p:" + qarg.player + ",h:" + qarg.handtype + ",a:" + qarg.action
//							+ " updated from r:" + reward + ",q:" + tmpval + ",c:" + tmpcnt + ",nq:"
//							+ q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][0] + ",nc:"
//							+ q[qarg.dealer][qarg.player][qarg.handtype][qarg.action][1]);
		}
	}

	/**
	 * ���ׂĂ�Q�l���o��
	 */
	public String printQ() {
		StringBuilder sb = new StringBuilder();
		sb.append("dealer,player,handtype,action,Q-value" + "\n");
		for (int x = 0; x < Q_LEN[0]; x++) {
			for (int y = 0; y < Q_LEN[1]; y++) {
				for (int z = 0; z < Q_LEN[2]; z++) {
					for (int a = 0; a < Q_LEN[3]; a++) {
						sb.append(x + ", " + y + ", " + z + ", " + a + ", " + q[x][y][z][a][0] + "\n");
					}
				}
			}
		}
		return new String(sb);
	}
}