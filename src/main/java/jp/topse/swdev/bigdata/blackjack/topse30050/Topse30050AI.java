package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.*;

public class Topse30050AI implements DecisionMaker {

    /**
     * ゲーム方針は下記の通り。<br/>
     * 学習結果モデルに
     * <ul>
     * <li>ディーラーのアップカード、自身のプレイ順序、ゲーム開始時に初期配布された2枚、現在の前の合計点、現在の手持ち枚数</li>
     * <li>ディーラーのアップカード、自身のプレイ順序、ゲーム開始時に初期配布された2枚、現在の合計点、現在の手持ち枚数に1枚足したもの</li>
     * </ul>
     * を入力し得られた結果に応じてHITかSTANDを選択する。
     */
    @Override
    public Action decide(Player player, Game game) {

        // TODO ゲーム中に取得できる項目を模索中(もしかして自身のプレイ順序は取ってこれない？)
        Hand myHand = game.getPlayerHands().get(player);
//        System.out.println();
//        System.out.println("ディーラーのアップカード:" + game.getUpCard().getValue());
//        System.out.println(player.getName() + "の今の手持ち合計:" + myHand.eval());
//        System.out.println(player.getName() + "の今の手持ち枚数:" + myHand.getCount());
//        String separator = "";
//        for (int i = 0; i < myHand.getCount(); i++) {
//            System.out.print(separator + myHand.get(i));
//            separator = ",";
//        }
//        System.out.println("の最後のカード:" + myHand.get(myHand.getCount() - 1));
//        System.out.println("今のカードを引く前の合計:" + ((myHand.getCount() == 2) ? 0 : (myHand.eval() - myHand.get(myHand.getCount() - 1).getValue())));
//        System.out.println();
//        game.getPlayerHands().forEach((player1, hand) -> {
//            System.out.println(player1.getName() + "の今の手持ち合計:" + hand.eval());
//            String separator = "";
//            for (int i = 0; i < hand.getCount(); i++) {
//                System.out.print(separator + hand.get(i));
//                separator = ",";
//            }
//            System.out.println();
//        });

//        Result.Type predictedResult = MakerForWekaModel.getPredictedResult(game.getUpCard().getValue(), 3, myHand.get(0).getValue(), myHand.get(1).getValue(), myHand.getCount() + 1);
//        Result.Type predictedResult = MakerForWekaModel.getPredictedResult(game.getUpCard().getValue(), myHand.get(0).getValue(), myHand.get(1).getValue(), myHand.getCount() + 1);
//        return  (predictedResult == null || predictedResult.equals(Result.Type.LOSE)) ? Action.STAND : Action.HIT;

        Result.Type nowPredictedResult = MakerForWekaModel.getPredictedResult(game.getUpCard().getValue(), myHand.get(0).getValue(), myHand.get(1).getValue(), ((myHand.getCount() == 2) ? 0 : (myHand.eval() - myHand.get(myHand.getCount() - 1).getValue())), myHand.getCount());
        Result.Type nextPredictedResult = MakerForWekaModel.getPredictedResult(game.getUpCard().getValue(), myHand.get(0).getValue(), myHand.get(1).getValue(), myHand.eval(), myHand.getCount() + 1);
        if (nowPredictedResult == null || nextPredictedResult == null) {
            return Action.STAND;
        } else if (nowPredictedResult.equals(Result.Type.LOSE)) {
            // 今のままでは負けなら引く
            return Action.HIT;
        } else if (nowPredictedResult.equals(Result.Type.DRAW) && nextPredictedResult.equals(Result.Type.WIN)) {
            // 今のままは引き分け、引けば勝ちの予想なら攻めて引く
            return Action.HIT;
        } else {
            return Action.STAND;
        }

//        return (game.getPlayerHands().get(player).eval() < 17) ? Action.HIT : Action.STAND;
    }

}
