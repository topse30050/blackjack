package jp.topse.swdev.bigdata.blackjack.suga.ml.qlearning;

import java.util.Random;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.suga.ml.HandType;

public class QLearning {
	/* ��-greedy�@�̒T��m���� */
	private static final double	EPSILON			= 0.20;
	/* �w�K���� */
	private static final double	ALPHA			= 0.001;
	/* �������� */
	private static final double	GAMMA			= 0.90;
	//	/* �w�K�� */
	//	private static final int	LEARNING_TIMES	= 1000000;
	//	/* Q�̏����l�̍ő�l(�����̍ő�l) */
	//	private static final int	INIT_Q_MAX	= 30;

	/* WIN���̕�V */
	public static final int		WIN_REWARD		= 700;
	/* DRAW���̕�V */
	public static final int		DRAW_REWARD		= 0;
	/* LOSE���̕�V */
	public static final int		LOSE_REWARD		= -100;
	/* BURST���̕�V */
	public static final int		BURST_REWARD	= -90;
	/* HIT����Burst���Ȃ������Ƃ��̕�V */
	public static final int		REMAIN_REWARD	= 1;

	/*
	 * Q�l
	 * [dealer(2-11)]
	 * [player(12-21,over]
	 * [handtype(soft/hard:0/1)]
	 * [tactics(stand/hit:0/1)]
	 */
	private double				q[][][][];
	private static final int[]	Q_LEN			= { 12, 23, HandType.values().length,
			Action.values().length };

	/**
	 * �R���X�g���N�^
	 */
	public QLearning() {
		// Q�l�̏�����
		q = new double[Q_LEN[0]][Q_LEN[1]][Q_LEN[2]][Q_LEN[3]];
	}

	/**
	 * Q�l�̏�����
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
				if (q[dealer][player][type][select] < q[dealer][player][type][a]) {
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
	public void updateQ(int dealer, int player, int handtype, int action, int nextplayer, int nexthandtype,
			int reward) {

		// ���s'�ōs��������Q�l���ő�ƂȂ�悤�ȍs��
		int maxA = 0;
		for (int i = 0; i < Q_LEN[3]; i++) {
			if (q[dealer][nextplayer][nexthandtype][maxA] < q[dealer][nextplayer][nexthandtype][i]) {
				maxA = i;
			}
		}

		// Q�l�̍X�V
		q[dealer][player][handtype][action] = (1.0 - ALPHA) * q[dealer][player][handtype][action] + ALPHA
				* (reward + GAMMA * q[dealer][nextplayer][nexthandtype][maxA]);
		//		System.out.println(
		//				"d:" + dealer + ",p:" + player + ",h:" + handtype + ",a:" + action + " updated from np:" + nextplayer
		//						+ ",nh:" + nexthandtype + ",r:" + reward + ",q:" + q[dealer][nextplayer][nexthandtype][maxA]);
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
						sb.append(x + ", " + y + ", " + z + ", " + a + ", " + q[x][y][z][a] + "\n");
					}
				}
			}
		}
		return new String(sb);
	}
}