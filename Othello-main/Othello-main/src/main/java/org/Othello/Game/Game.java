package org.Othello.Game;


import java.util.Scanner;

public class Game {

    Scanner scanner = new Scanner(System.in);
    private Board board = new Board();
    private int PlayerColor;
    public Game(){
        PlayerColor = 1;
    }

    public int[][] getboard(){
        return board.getBoard();
    }

    public void setboard(int[][] board){
        this.board.setBoard(board);
    }

    public int getPlayerColor(){
        return PlayerColor;
    }

    public void setPlayerColor(int playerColor){
        PlayerColor = playerColor;
    }

    public boolean ValidMove(int row, int col){
        boolean validMove = board.canFlip(row,col,getPlayerColor());
        return validMove;
    }

    //现在设置游戏开始方法
    public void start(){
        while(true){
            int row,line;
            board.judgeAndHint(PlayerColor);
            board.display();
            if (board.skip()){
                System.out.println("无处落子，改为下一个玩家行棋");
                PlayerColor = (PlayerColor == 1) ? 2 : 1;
            }
            System.out.println("当前玩家为" + ((PlayerColor== 1) ? "黑方" : "白方"));
            System.out.println("请输入行，列（1-8）: ");
            row = scanner.nextInt() - 1;
            line = scanner.nextInt() - 1;
            if (board.canFlip(row,line,PlayerColor)){
                board.placeAndFlip(row,line,PlayerColor);
                int[] counter = board.count();
                System.out.println("黑棋: " + counter[1] + " 白棋: " + counter[2]);
                PlayerColor = (PlayerColor == 1) ? 2 : 1;//切换玩家
            }else {
                System.out.println("不能在这下");
            }

            if(Gameover()){
                break;
            }
        }
        winner();
    }
    //接下来是判断游戏是否结束的方法
    public boolean Gameover(){
        int [] counter = board.count();
        boolean tem = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.canFlip(i,j,1) || board.canFlip(i,j,2)){
                    return false;
                }
            }
        }
        return tem;
    }
    //显示获胜者的方法
    public void winner(){
        int[] counter = board.count();
        if (counter[1] > counter[2]) {
            System.out.println("黑棋获胜");
        } else if (counter[1] == counter[2]) {
            System.out.println("平局！");
        }else{
            System.out.println("白棋获胜！");
        }
    }
    //做一个当没有地方可以下时跳过该回合的方法



}
