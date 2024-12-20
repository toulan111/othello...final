package org.Othello.GUI;

import org.Othello.Game.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class ReversiGUI extends JFrame {
    private JButton[][] boardButtons;
    private int boardSize;
    private Board board;
    private int difficulty;

    //悔棋
    public List<int[][]> boardHistory;


    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    //timer初始设置
    private final int lastingTime = 10;
    private int player1Time = lastingTime;
    private int player2Time = lastingTime;

    Timer player1Timer = new Timer("Player1Timer");
    Timer player2Timer = new Timer("Player2Timer");
    TimerTask playerTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (board.getPlayerColor() == 1) {
                if (player1Time > 0) {
                    player1Time--;
                    player1Label.setText("黑棋剩余时间：" + player1Time);
                } else {
                    JOptionPane.showMessageDialog(null, "时间超时，白棋胜利");
                    stopPlayerTimer();
                }

            } else {
                if (player2Time > 0) {
                    player2Time--;
                    player2Label.setText("白棋剩余时间：" + player2Time);
                } else {
                    JOptionPane.showMessageDialog(null, "时间超时，黑棋胜利");
                    stopPlayerTimer();
                }
            }
        }
    };

    private JLabel player1Label = new JLabel("黑棋剩余时间：10");
    private JLabel player2Label = new JLabel("白棋剩余时间：10");

    public ReversiGUI(int size, Board board) {
        this.boardSize = size;
        this.board = board;
        boardButtons = new JButton[size][size];
        boardHistory = new ArrayList<>();
        board.setPlayerColor(1);
        initializeBoard();
        startPlayerTimer(board.getPlayerColor());
    }

    //初始化界面
    private void initializeBoard() {
        boardHistory.add(copyBoard(board.getBoard()));

        //主游戏界面
        JPanel mainGame = new JPanel(new GridLayout(boardSize, boardSize));
        mainGame.setSize(600, 600);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();
                int row = i;
                int col = j;
                boardButtons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleBoardClick(row, col);
                    }
                });
                mainGame.add(boardButtons[i][j]);
            }
        }

        //难度选择
        JButton easy = new JButton("Easy");
        easy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(1);
                restartGame();
            }
        });
        JButton medium = new JButton("Medium");
        medium.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(2);
                restartGame();
            }
        });
        JButton hard = new JButton("Hard");
        hard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(3);
                restartGame();
            }
        });


        //悔棋键

        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });


        //计时的Label
        JPanel time = new JPanel(new GridLayout());
        time.add(player1Label);
        time.add(player2Label);
        time.add(easy);
        time.add(medium);
        time.add(hard);

        //辅助选项

        //保存
        JPanel config = new JPanel(new GridLayout());
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        //加载
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        //重启
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
                updateBoard(board.getBoard());
                JOptionPane.showMessageDialog(ReversiGUI.this, "Game Restarted!");
            }
        });

        //退出
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        config.add(saveButton);
        config.add(loadButton);
        config.add(restartButton);
        config.add(exitButton);
        config.add(back);


        //排版
        JFrame frame = new JFrame();
        frame.setTitle("Reversi Game");
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainGame, BorderLayout.CENTER);
        frame.add(config, BorderLayout.SOUTH);
        frame.add(time, BorderLayout.NORTH);
        frame.setVisible(true);

        updateBoard(board.getBoard());
    }

    //点击
    private void handleBoardClick(int row, int col) {
        if (board.canFlip(row, col, board.getPlayerColor())) {

            board.placeAndFlip(row, col, board.getPlayerColor());
            board.setPlayerColor((board.getPlayerColor() == 1) ? 2 : 1);
            updateBoard(board.getBoard());
            if (board.getPlayerColor() != 1) {// 如果AI走棋
                makeAIMove(getDifficulty());
            }
            player1Time = lastingTime;
            player2Time = lastingTime;

            boardHistory.add(copyBoard(board.getBoard()));





        } else {
            JOptionPane.showMessageDialog(this, "Invalid move!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //刷新
    public void updateBoard(int[][] boardState) {
        board.judgeAndHint(board.getPlayerColor());
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardState[i][j] == 1) {
                    boardButtons[i][j].setBackground(Color.BLACK);
                } else if (boardState[i][j] == 2) {
                    boardButtons[i][j].setBackground(Color.WHITE);
                } else if (boardState[i][j] == 6) {
                    boardButtons[i][j].setBackground(Color.GREEN);
                } else {
                    boardButtons[i][j].setBackground(Color.GRAY);
                }
            }
        }

        if (over()) {
            win();
        } else if (board.skip()) {
            board.setPlayerColor((board.getPlayerColor() == 1) ? 2 : 1);
            JOptionPane.showMessageDialog(null, "无处可走，切换玩家");
            board.judgeAndHint(board.getPlayerColor());
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (boardState[i][j] == 1) {
                        boardButtons[i][j].setBackground(Color.BLACK);
                    } else if (boardState[i][j] == 2) {
                        boardButtons[i][j].setBackground(Color.WHITE);
                    } else if (boardState[i][j] == 6) {
                        boardButtons[i][j].setBackground(Color.GREEN);
                    } else {
                        boardButtons[i][j].setBackground(Color.GRAY);
                    }
                }
            }

        }

    }

    //存档方法
    public void saveFile() {
        stopPlayerTimer();
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                for (int i = 0; i < boardHistory.size(); i++) {
                    for (int[] row : boardHistory.get(i)) {
                        for (int element : row) {
                            writer.write(Integer.toString(element));
                            writer.write(" ");
                        }
                        writer.newLine();
                    }
                }
                restartTimer();
                startPlayerTimer(board.getPlayerColor());
                JOptionPane.showMessageDialog(null, "文件保存成功！");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "失败");
                restartTimer();
                startPlayerTimer(board.getPlayerColor());
            }
        } else {
            restartTimer();
            startPlayerTimer(board.getPlayerColor());
        }

    }

    //加载存档方法


    public void loadFile() {
        stopPlayerTimer();
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                boardHistory.clear();
                List<int[]> boardState = new ArrayList<>();
                String line;
                int rowCount = 0;

                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) { // 忽略空行
                        String[] elements = line.trim().split(" ");
                        if (elements.length != 8) {
                            throw new IOException("每一行必须包含恰好 8 个整数。");
                        }

                        int[] row = new int[8];
                        for (int i = 0; i < 8; i++) {
                            row[i] = Integer.parseInt(elements[i]);
                        }
                        boardState.add(row);
                        rowCount++;

                        // 每 8 行代表一个棋盘状态
                        if (rowCount == 8) {
                            boardHistory.add(boardState.toArray(new int[0][]));
                            boardState.clear(); // 准备读取下一个棋盘状态
                            rowCount = 0;
                        }
                    }
                }

                board.setBoard(boardHistory.get(boardHistory.size() - 1));
                restartTimer();
                startPlayerTimer(board.getPlayerColor());
                updateBoard(board.getBoard());

                JOptionPane.showMessageDialog(null, "文件加载成功！");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "文件读取失败：" + e.getMessage());
                restartTimer();
                startPlayerTimer(board.getPlayerColor());
            }
        } else {
            restartTimer();
            startPlayerTimer(board.getPlayerColor());
        }
    }

    //重启游戏
    public void restartGame() {
        stopPlayerTimer();
        int[][] restartBoard = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int mid = boardSize / 2;
                restartBoard[mid - 1][mid - 1] = 2;
                restartBoard[mid - 1][mid] = 1;
                restartBoard[mid][mid - 1] = 1;
                restartBoard[mid][mid] = 2;
            }
        }
        int currentPlayer = 1;
        board.setPlayerColor(currentPlayer);
        board.setBoard(restartBoard);
        restartTimer();
        startPlayerTimer(board.getPlayerColor());
        boardHistory.add(board.getBoard());
    }

    //胜利情况
    public void win() {
        stopPlayerTimer();
        int[] counter = board.count();
        if (counter[1] > counter[2]) {
            JOptionPane.showMessageDialog(null, "黑棋胜利");
        } else if (counter[1] == counter[2]) {
            JOptionPane.showMessageDialog(null, "平局");
        } else {
            JOptionPane.showMessageDialog(null, "白棋胜利");
        }
    }

    //结束判断
    public boolean over() {
        boolean tem = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.canFlip(i, j, 1) || board.canFlip(i, j, 2)) {
                    return false;
                }
            }
        }
        return tem;
    }

    //计时开始
    public void startPlayerTimer(int player) {
        Timer timer = (player == 1) ? player1Timer : player2Timer;
        timer.scheduleAtFixedRate(playerTimerTask, 0, 1000);

    }

    //停止task
    public void stopPlayerTimer() {
        playerTimerTask.cancel();
    }

    //重置并启动timerTask
    public void restartTimer() {
        player1Time = lastingTime;
        player2Time = lastingTime;
        playerTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (board.getPlayerColor() == 1) {
                    if (player1Time > 0) {
                        player1Time--;
                        player1Label.setText("黑棋剩余时间：" + player1Time);
                    } else {
                        JOptionPane.showMessageDialog(null, "时间超时，白棋胜利");
                        stopPlayerTimer();
                    }

                } else {
                    if (player2Time > 0) {
                        player2Time--;
                        player2Label.setText("白棋剩余时间：" + player2Time);
                    } else {
                        JOptionPane.showMessageDialog(null, "时间超时，黑棋胜利");
                        stopPlayerTimer();
                    }
                }
            }
        };

    }

    public void makeAIMove(int difficulty) {
        int[][] boardl = board.getBoard();
        try {
            System.out.println("AI思考中");
            Thread.sleep(2000); // 暂停2000毫秒，即2秒
            System.out.println("结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;//随便初始化的，反正就是不影响后面赋值
        int maximizingPlayer = board.getPlayerColor();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((boardl[i][j] == 0 || boardl[i][j] == 6) && board.canFlip(i, j, maximizingPlayer)) {
                    // 这里复制一份棋盘状态进行操作，避免影响原始棋盘
                    int[][] copiedBoard = copyBoard(boardl);
                    copiedBoard[i][j] = maximizingPlayer;
                    if(getDifficulty() == 1){
                        int score = board.alphaBeta(copiedBoard, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true, maximizingPlayer);
                        if (score > bestScore) {
                            bestScore = score;
                            bestRow = i;
                            bestCol = j;
                        }
                    } else if (getDifficulty() == 2) {
                        int score = board.alphaBeta(copiedBoard, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true, maximizingPlayer);
                        if (score > bestScore) {
                            bestScore = score;
                            bestRow = i;
                            bestCol = j;
                        }
                    } else if (getDifficulty() == 3) {
                        int score = board.alphaBeta(copiedBoard, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true, maximizingPlayer);
                        if (score > bestScore) {
                            bestScore = score;
                            bestRow = i;
                            bestCol = j;
                        }
                    }

                }
            }
        }

        if (bestRow != -1 && bestCol != -1) {
            board.placeAndFlip(bestRow, bestCol, maximizingPlayer);
            System.out.println("最佳行: " + bestRow + ", 最佳列: " + bestCol + ", 最佳分数: " + bestScore);
            board.setPlayerColor((board.getPlayerColor() == 1) ? 2 : 1);
            updateBoard(board.getBoard());
        }
    }

    private int[][] copyBoard(int[][] originalBoard) {
        int[][] copiedBoard = new int[originalBoard.length][];
        for (int i = 0; i < originalBoard.length; i++) {
            copiedBoard[i] = originalBoard[i].clone();
        }
        return copiedBoard;
    }


    public void back() {
        if (boardHistory.size() > 1) {
            boardHistory.remove(boardHistory.size() - 1);
            board.setBoard(copyBoard(boardHistory.get(boardHistory.size() - 1)));
            updateBoard(board.getBoard());
            player1Time = lastingTime;
            player2Time = lastingTime;
        }else{
            JOptionPane.showMessageDialog(null,"无法悔棋");
        }
    }


}