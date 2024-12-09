package org.Othello.Game;

import java.util.ArrayList;
import java.util.List;

public class Board {




    private int[][] board;
    private int PlayerColor;

    public int getPlayerColor() {
        return PlayerColor;
    }

    public void setPlayerColor(int playerColor) {
        PlayerColor = playerColor;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    //以下数字2表示白棋，数字1表示黑棋
    public Board(){
        board = new int[8][8];
        board[3][3] = 2;
        board[4][4] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
    }
    //显示棋盘的方法
    public void display(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] == 1){
                    System.out.print("黑 ");
                }else if (board[i][j] == 2){
                    System.out.print("白 ");
                } else if (board[i][j] == 0) {
                    System.out.print("空 ");
                } else if (board[i][j] == 6) {
                    System.out.print("口 ");//用于表示可以落子的地方作为提示
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    //放置并翻转棋子
    public void placeAndFlip(int row, int col, int color) {
        if (!canFlip(row, col, color)) {
            throw new IllegalArgumentException("非法落子位置：" + row + ", " + col);
        }

        board[row][col] = color; // 放置棋子
        int enemy = (color == 1) ? 2 : 1; // 定义敌方颜色
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1}; // 八个方向
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        // 遍历八个方向进行翻转
        for (int i = 0; i < 8; i++) {
            int nx = row + dx[i];
            int ny = col + dy[i];
            List<int[]> toFlip = new ArrayList<>(); // 记录需要翻转的棋子

            // 找到需要翻转的敌方棋子
            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                if (board[nx][ny] == enemy) {
                    toFlip.add(new int[]{nx, ny}); // 将敌方棋子加入列表
                } else if (board[nx][ny] == color) {
                    for (int[] pos : toFlip) {
                        board[pos[0]][pos[1]] = color; // 翻转所有记录的棋子
                    }
                    break;
                } else {
                    break; // 遇到空格或越界，停止翻转
                }
                nx += dx[i];
                ny += dy[i];
            }
        }
    }

    //获取棋盘上双方棋子数量的方法
    public  int[] count(){
        int[] counter = new int[3];//下令0代表空格数，1代表黑棋数，2代表白棋数
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1){
                    counter[1]++;
                } else if (board[i][j] == 2) {
                    counter[2]++;
                } else if (board[i][j] == 0 || board[i][j] == 6) {
                    counter[0]++;
                }
            }

        }
        return counter;
    }
    //检查落子是否有效并给予可以落子的提示
    public boolean judgeAndHint(int color) {
        boolean temp1 = false;
        clearHints();  // 清除之前的提示位置
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // 判断当前位置是空白并且可以翻转对方棋子
                if (board[i][j] == 0 && canFlip(i, j, color)) {
                    board[i][j] = 6;  // 如果canFlip返回true，则该位置为合法位置
                    System.out.println("合法落子位置：(" + (i + 1) + ", " + (j + 1) + ")");  // 打印调试信息
                    temp1 = true;
                }
            }
        }
        return temp1;  // 如果有合法位置返回true
    }


    public void clearHints(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 6){
                    board[i][j] = 0;
                }
            }
        }
    }
    public boolean canFlip(int row, int col, int color) {
        // 检查边界条件
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] != 0) {
            return false; // 非法坐标或非空格，直接返回 false
        }

        int enemy = (color == 1) ? 2 : 1; // 定义敌方颜色
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1}; // 八个方向
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        // 遍历八个方向
        for (int i = 0; i < 8; i++) {
            int nx = row + dx[i];
            int ny = col + dy[i];
            boolean foundEnemy = false; // 标记是否找到敌方棋子

            // 沿当前方向查找
            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                if (board[nx][ny] == enemy) {
                    foundEnemy = true; // 找到敌方棋子
                } else if (board[nx][ny] == color && foundEnemy) {
                    return true; // 如果找到己方棋子且中间有敌方棋子，合法
                } else {
                    break; // 遇到空格或己方棋子，终止检查
                }
                nx += dx[i];
                ny += dy[i]; // 沿当前方向继续检查
            }
        }
        return false; // 所有方向都不合法
    }


    public boolean skip(){
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 6){
                    counter++;
                }
            }
        }
        if(counter == 0){
            System.out.println("没地方下了，跳过该回合");
            return true;
        }else {
            return false;
        }
    }




}
