package org.Othello.GUI;
import org.Othello.Game.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReversiGUI extends JFrame {
    private JButton[][] boardButtons;
    private int boardSize;
    private Board board;
    //Icon blackIcon = new ImageIcon("picture/black.png");
    //Icon whiteIcon = new ImageIcon("picture/white.png");



    public ReversiGUI(int size,Board board) {
        this.boardSize = size;
        this.board = board;
        boardButtons = new JButton[boardSize][boardSize];
        board.setPlayerColor(1);
        initializeBoard();
    }



    //初始化界面
    private void initializeBoard() {




        //主游戏界面
        JPanel mainGame = new JPanel(new GridLayout(boardSize, boardSize));
        mainGame.setSize(600,600);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();
                //boardButtons[i][j].setBackground(Color.GREEN);
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




        //辅助选项


        //保存
        JPanel config = new JPanel(new GridLayout());
        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });


        //加载
        JButton loadButton = new JButton("Load Game");
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

        //排版
        JFrame frame = new JFrame();
        frame.setTitle("Reversi Game");
        frame.setSize(600,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainGame, BorderLayout.CENTER);
        frame.add(config, BorderLayout.SOUTH);
        frame.setVisible(true);


        updateBoard(board.getBoard());
    }


    //点击
    private void handleBoardClick(int row, int col) {
        if (board.canFlip(row,col,board.getPlayerColor())) {
            board.placeAndFlip(row,col,board.getPlayerColor());
            board.setPlayerColor((board.getPlayerColor() == 1) ? 2 : 1);
            updateBoard(board.getBoard());
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
                    //boardButtons[i][j].setIcon(blackIcon);
                    boardButtons[i][j].setBackground(Color.BLACK);
                } else if (boardState[i][j] == 2) {
                    //boardButtons[i][j].setIcon(whiteIcon);
                    boardButtons[i][j].setBackground(Color.white);
                }else if (boardState[i][j] == 6) {
                    boardButtons[i][j].setBackground(Color.green);
                } else {
                    boardButtons[i][j].setBackground(Color.gray);
                }
            }
        }

        if(over()){
           win();
       } else if(board.skip()){
           board.setPlayerColor((board.getPlayerColor() == 1) ? 2 : 1);
           JOptionPane.showMessageDialog(null,"无处可走，切换玩家");}

    }

    //存档方法
    public void saveFile() {

        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for(int[] row : board.getBoard()) {
                    for(int element : row) {
                        writer.write(Integer.toString(element));
                        writer.write(" ");
                    }
                    writer.newLine();
                }
                writer.write(Integer.toString(board.getPlayerColor()));


                JOptionPane.showMessageDialog(null,"文件保存成功！");

            }catch(IOException e){
                JOptionPane.showMessageDialog(null,"失败");
            }
        }

    }



    
    //加载存档方法

    public void loadFile() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<List<Integer>> boardStateList = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] elements = line.trim().split("\\s+");
                    List<Integer> row = new ArrayList<>();
                    for (String element : elements) {
                        row.add(Integer.parseInt(element));
                    }
                    boardStateList.add(row);
                }

                if (!boardStateList.isEmpty()) {
                    String lastLine = reader.readLine();
                    if (lastLine != null) {
                        int currentPlayer = Integer.parseInt(lastLine.trim());
                        board.setPlayerColor(currentPlayer);
                    }
                }

                int[][] boardState = new int[boardStateList.size()][];
                for (int i = 0; i < boardStateList.size(); i++) {
                    boardState[i] = boardStateList.get(i).stream().mapToInt(Integer::intValue).toArray();
                }

                board.setBoard(boardState);

                updateBoard(board.getBoard());

                JOptionPane.showMessageDialog(null, "文件加载成功！");

            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "加载失败：" + e.getMessage());
            }
        }
    }



    //
    public void restartGame() {
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
    }





    public void win(){
        int[] counter = board.count();
        if (counter[1] > counter[2]) {
            JOptionPane.showMessageDialog(null,"黑棋胜利");
        } else if (counter[1] == counter[2]) {
            JOptionPane.showMessageDialog(null,"平局");
        }else{
            JOptionPane.showMessageDialog(null,"白棋胜利");
        }
    }



    public boolean over(){
        boolean tem = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.canFlip(i,j,0) || board.canFlip(i,j,2)){
                    return false;
                }
            }
        }
        return tem;
    }




}
