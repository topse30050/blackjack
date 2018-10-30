package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.Action;
import jp.topse.swdev.bigdata.blackjack.DecisionMaker;
import jp.topse.swdev.bigdata.blackjack.Game;
import jp.topse.swdev.bigdata.blackjack.Player;

public class Topse30050AI implements DecisionMaker {

    /**
     * ゲーム方針は下記の通り。<br/>
     * 学習結果モデルにディーラーのアップカード、自身のプレイ順序、ゲーム開始時に初期配布された2枚、現在の手持ち枚数に1枚足したものを入力し、
     * 得られた結果が勝ち、引き分けであればHIT、負けであればSTANDをアクションとして返す。
     */
    @Override
    public Action decide(Player player, Game game) {
        Action decidedAction = Action.STAND;

        System.out.println();
        System.out.println("ディーラーのアップカード:" + game.getUpCard().getValue());
        System.out.println(player.getName() + ":" + game.getPlayerHands().get(player).eval());
        System.out.println();

        //TODO:Weka学習済みモデルを読み込んで判断
        if (game.getPlayerHands().get(player).eval() < 17) {
            decidedAction = Action.HIT;
        }

        return decidedAction;
    }

}
