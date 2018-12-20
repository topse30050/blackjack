package jp.topse.swdev.bigdata.blackjack.suga.ml.montecarlo2;

class Qarg {
	int	dealer;
	int	player;
	int	handtype;
	int	drawnum;
	int	action;

	Qarg(int dealer, int player, int handtype, int drawnum, int action) {
		this.dealer = dealer;
		this.player = player;
		this.handtype = handtype;
		this.drawnum = drawnum;
		this.action = action;
	}
}