package org.Othello;

import org.Othello.Game.Board;
import org.Othello.GUI.ReversiGUI;

public class Main {
    public static void main(String[] args) {
        int boardSize = 8;
        Board board = new Board();
        ReversiGUI gui = new ReversiGUI(boardSize,board);

    }
}
