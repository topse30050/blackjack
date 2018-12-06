package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo;

class Qarg {
	int	dealer;
	int	player;
	int	handtype;
	int	action;

	Qarg(int dealer, int player, int handtype, int action) {
		this.dealer = dealer;
		this.player = player;
		this.handtype = handtype;
		this.action = action;
	}
}