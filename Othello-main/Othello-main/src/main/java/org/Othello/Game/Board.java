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

    // 以下数字2表示白棋，数字1表示黑棋
    public Board() {
        board = new int[8][8];
        board[3][3] = 2;
        board[4][4] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
        PlayerColor = 1;
    }

    public Board(int[][]board , int currentPlayer){
        this.board = board;
        this.PlayerColor = currentPlayer;
    }



    // 显示棋盘的方法
    public void display() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    System.out.print("黑 ");
                } else if (board[i][j] == 2) {
                    System.out.print("白 ");
                } else if (board[i][j] == 0) {
                    System.out.print("空 ");
                } else if (board[i][j] == 6) {
                    System.out.print("口 "); // 用于表示可以落子的地方作为提示
                }
            }
            System.out.println();
        }
        System.out.println();
    }


    // 放置并翻转棋子
    public void placeAndFlip(int row, int col, int color) {
        if (!canFlip(row, col, color)) {
            throw new IllegalArgumentException("非法落子位置：" + row + ", " + col);
        }

        board[row][col] = color; // 放置棋子
        int enemy = (color == 1)? 2 : 1; // 定义敌方颜色
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

    // 获取棋盘上双方棋子数量的方法
    public int[] count() {
        int[] counter = new int[3]; // 下令0代表空格数，1代表黑棋数，2代表白棋数
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
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

    // 检查落子是否有效并给予可以落子的提示
    public boolean judgeAndHint(int color) {
        boolean temp1 = false;
        clearHints(); // 清除之前的提示位置
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // 判断当前位置是空白并且可以翻转对方棋子
                if (board[i][j] == 0 && canFlip(i, j, color)) {
                    board[i][j] = 6; // 如果canFlip返回true，则该位置为合法位置
                    //System.out.println("合法落子位置：(" + (i + 1) + ", " + (j + 1) + ")"); // 打印调试信息
                    temp1 = true;// 如果有合法位置返回true
                }
            }
        }
        return temp1;
    }

    public void clearHints() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 6) {
                    board[i][j] = 0;
                }
            }
        }
    }

    public boolean canFlip(int row, int col, int color) {
        // 检查边界条件
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] == 1 || board[row][col] == 2) {
            return false; // 非法坐标或非空格，直接返回 false
        }

        int enemy = (color == 1)? 2 : 1; // 定义敌方颜色
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

    public boolean skip() {
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 6) {
                    counter++;
                }
            }
        }
        if (counter == 0) {//一个可以落子的地方都没有
            System.out.println("没地方下了，跳过该回合");
            return true;
        } else {
            return false;
        }
    }

// 下面是AI算法的有关实现部分

    private static final int[][] POSITION_WEIGHTS = {
            {100, -20, 10, 10, 10, 10, -20, 100},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {10, -2, 3, 3, 3, 3, -2, 10},
            {10, -2, 3, 3, 3, 3, -2, 10},
            {10, -2, 3, 3, 3, 3, -2, 10},
            {10, -2, 3, 3, 3, 3, -2, 10},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {100, -20, 10, 10, 10, 10, -20, 100}
    };

    public static int evaluateBoard(int[][] board, int maximizingPlayer) {//值得注意的是，这里的计算是直接算整个棋盘对于maximizingPlayer的分数，代表着每次ai落子会比较整个棋盘的前后分数
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    if (maximizingPlayer == 1) {
                        score += 1 + POSITION_WEIGHTS[i][j];
                    } else {
                        score -= 1 + POSITION_WEIGHTS[i][j];
                    }
                } else if (board[i][j] == 2) {
                    if (maximizingPlayer == 2) {
                        score += 1 + POSITION_WEIGHTS[i][j];
                    } else {
                        score -= 1 + POSITION_WEIGHTS[i][j];
                    }
                }
            }
        }
        return score;
    }

    private List<int[]> getPotentialMoves(int color) {
        List<int[]> potentialMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((board[i][j] == 0 || board[i][j] == 6)  && canFlip(i, j, color)) {
                    potentialMoves.add(new int[]{i, j});
                }
            }
        }
        return potentialMoves;
    }

    public int alphaBeta(int[][] board, int depth, int alpha, int beta, boolean isMax, int maximizingPlayer) {
        // 打印当前搜索深度、节点类型、alpha和beta值以及正在搜索的棋盘状态（简略打印）

        System.out.println("深度: " + depth + ", " + (isMax? "最大化节点" : "最小化节点") +
                ", alpha: " + alpha + ", beta: " + beta + ", 当前棋盘（简略）:");
        printBoardBrief(board);

        if (depth == 0 || isGameOver(board)) {
            int evalScore = evaluateBoard(board, maximizingPlayer);
            System.out.println("到达叶子节点或游戏结束，评估分数: " + evalScore);
            return evalScore;
        }

        List<int[]> potentialMoves = getPotentialMoves(maximizingPlayer);
        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            List<Move> madeMoves = new ArrayList<>();//这个arraylist用于储存以行列以及该处棋子信息为成员变量的Move类变量
            for (int[] move : potentialMoves) {
                int i = move[0];
                int j = move[1];
                System.out.println("最大化节点，正在考虑落子位置: (" + i + ", " + j + ")");
                int originalPiece = board[i][j];
                board[i][j] = maximizingPlayer;
                madeMoves.add(new Move(i, j, originalPiece));
                int eval = alphaBeta(board, depth - 1, alpha, beta, false, (maximizingPlayer == 1)? 2 : 1);//通过递归获得子分支的估值，类似于最大分数maxevla的候选者
                maxEval = Math.max(maxEval, eval);//而maxeval则是类似于最大分数的纪录保持者，需要时时更新。
                alpha = Math.max(alpha, eval);
                System.out.println("最大化节点，落子位置 (" + i + ", " + j + ") 评估分数: " + eval +
                        ", 当前最佳值更新为: " + maxEval + ", alpha更新为: " + alpha);
                if (beta <= alpha) {
                    System.out.println("Beta剪枝，跳过剩余子节点搜索");
                    // 回退所有已做的落子操作
                    for (Move m : madeMoves) {
                        board[m.row][m.col] = m.originalPiece;
                    }
                    madeMoves.clear();
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Move> madeMoves = new ArrayList<>();
            for (int[] move : potentialMoves) {
                int i = move[0];
                int j = move[1];
                System.out.println("最小化节点，正在考虑落子位置: (" + i + ", " + j + ")");
                int originalPiece = board[i][j];
                board[i][j] = (maximizingPlayer == 1)? 2 : 1;
                madeMoves.add(new Move(i, j, originalPiece));
                int eval = alphaBeta(board, depth - 1, alpha, beta, true, maximizingPlayer);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                System.out.println("最小化节点，落子位置 (" + i + ", " + j + ") 评估分数: " + eval +
                        ", 当前最佳值更新为: " + minEval + ", beta更新为: " + beta);
                if (beta <= alpha) {
                    System.out.println("Alpha剪枝，跳过剩余子节点搜索");
                    // 回退所有已做的落子操作
                    for (Move m : madeMoves) {
                        board[m.row][m.col] = m.originalPiece;
                    }
                    madeMoves.clear();
                    break;
                }
            }
            return minEval;
        }
    }

    public boolean isGameOver(int[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (canFlip(i, j, 1)) {
                    return false; // 如果黑棋有合法走法，则游戏未结束
                }
                if (canFlip(i, j, 2)) {
                    return false; // 如果白棋有合法走法，则游戏未结束
                }
            }
        }
        return true; // 如果双方都无合法走法，则游戏结束
    }

    class Move {//便于alphabeta方法的进行
        int row;
        int col;
        int originalPiece;

        public Move(int row, int col, int originalPiece) {
            this.row = row;
            this.col = col;
            this.originalPiece = originalPiece;
        }
    }

    // 辅助方法，简略打印棋盘状态（只打印有棋子的位置）
    private void printBoardBrief(int[][] board) {
        System.out.print("  ");
        for (int j = 0; j < 8; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    System.out.print("B ");
                } else if (board[i][j] == 2) {
                    System.out.print("W ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}