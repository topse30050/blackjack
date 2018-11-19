package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.*;

public class Topse30050AI implements DecisionMaker {

    /**
     * ゲーム方針は下記の通り。<br/>
     * <ol>
     * <li>手持ちが5枚を越えている場合は初期状態の合計点数17未満ならHITの思考とする</li>
     * <li>学習結果モデル(BUST判定)に手持ちのカード(5枚不足分は0とする)を入力しBUSTしそうならSTANDする</li>
     * <li>学習結果モデル(勝敗判定)にディーラーのUPカード、手持ちのカード(5枚不足分は0とする)を入力し、
     *     今の状態で合計点数が12未満または予測が負けの場合はHIT、それ以外はSTANDする。</li>
     * </ol>
     */
    @Override
    public Action decide(Player player, Game game) {

        Hand myHand = game.getPlayerHands().get(player);
        int nowTotal = myHand.eval();
        int nowCardCount = myHand.getCount();

        if (nowCardCount > 5) {
            return (nowTotal < 17) ? Action.HIT : Action.STAND;
        }

        int[] cardIndexes = new int[5];
        for (int i = 0; i < nowCardCount; i++) {
            cardIndexes[i] = myHand.get(i).getIndex();
        }
        for (int i = nowCardCount; i < cardIndexes.length; i++) {
            cardIndexes[i] = 0;
        }

        Boolean isBust = Topse30050Model.isBust(cardIndexes[0], cardIndexes[1], cardIndexes[2], cardIndexes[3], cardIndexes[4]);
        if (isBust == null || isBust) {
            return Action.STAND;
        }

        Result.Type predictedResult = Topse30050Model.getPredictedResult(
                game.getUpCard().getIndex(),
                cardIndexes[0],
                cardIndexes[1],
                cardIndexes[2],
                cardIndexes[3],
                cardIndexes[4]);
        if (predictedResult == null) {
            return Action.STAND;
        } else if (predictedResult.equals(Result.Type.LOSE) || nowTotal < 12) {
            return Action.HIT;
        } else {
            return Action.STAND;
        }
    }

}
