package jp.topse.swdev.bigdata.blackjack.topse30050;

import jp.topse.swdev.bigdata.blackjack.*;

public class Topse30050AI implements DecisionMaker {

    /**
     * ゲーム方針は下記の通り。<br/>
     * 学習結果モデルに
     *
     * を入力し
     *
     * する。
     */
    @Override
    public Action decide(Player player, Game game) {

        Hand myHand = game.getPlayerHands().get(player);
        int nowTotal = myHand.eval();
        int nowCardCount = myHand.getCount();

//        System.out.println();
//        System.out.println("ディーラーのアップカード:" + game.getUpCard());
//        System.out.println(player.getName() + "の今の手持ち合計:" + nowTotal);
//        System.out.println(player.getName() + "の今の手持ち枚数:" + nowCardCount);
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
        Result.Type predictedResult = Topse30050Model.getPredictedResult(
                game.getUpCard().getIndex(),
                cardIndexes[0],
                cardIndexes[1],
                cardIndexes[2],
                cardIndexes[3],
                cardIndexes[4]);
//System.out.println("今の状態での勝敗予想:" + predictedResult);
//System.out.println(((predictedResult == null || predictedResult.equals(Result.Type.WIN)) ? Action.STAND : Action.HIT).toString() + "する");
        if (predictedResult == null) {
            return Action.STAND;
        } else if (predictedResult.equals(Result.Type.LOSE) || nowTotal < 12) {
            return Action.HIT;
        } else {
            return Action.STAND;
        }

//        return (game.getPlayerHands().get(player).eval() < 17) ? Action.HIT : Action.STAND;
    }

}
