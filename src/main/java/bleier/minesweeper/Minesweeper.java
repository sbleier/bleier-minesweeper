package bleier.minesweeper;

import java.util.*;

public class Minesweeper {
    private int[][] board;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int rows;
    private int cols;
    private int numMines;
    private boolean gameOver;
    private boolean gameWon;
    private boolean firstClick;
    private static final Random RAND = new Random();

    public Minesweeper(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;
        this.board = new int[rows][cols];
        this.revealed = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
        this.gameOver = false;
        this.gameWon = false;
        this.firstClick = true;
    }

    public void placeMines(int excludeRow, int excludeCol) {
        int placed = 0;
        while (placed < numMines) {
            int r = RAND.nextInt(rows);
            int c = RAND.nextInt(cols);
            //-1 represents a mine
            if (board[r][c] != -1 && !(r == excludeRow && c == excludeCol)) {
                board[r][c] = -1;
                placed++;
            }
        }
        calculateNumbers();
    }

    private void calculateNumbers() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == -1) {
                    continue;
                }
                int count = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr;
                        int nc = c + dc;
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == -1) {
                            count++;
                        }
                    }
                }
                board[r][c] = count;
            }
        }
    }

    public boolean reveal(int row, int col) {
        if (gameOver || revealed[row][col] || flagged[row][col]) {
            return false;
        }

        if (firstClick) {
            placeMines(row, col);
            firstClick = false;
        }

        revealed[row][col] = true;

        if (board[row][col] == -1) {
            gameOver = true;
            revealAllMines();
            return false;
        }

        if (board[row][col] == 0) {
            revealNeighbors(row, col);
        }

        checkWin();
        return true;
    }

    private void revealNeighbors(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !revealed[nr][nc] && !flagged[nr][nc]) {
                    revealed[nr][nc] = true;
                    if (board[nr][nc] == 0) {
                        revealNeighbors(nr, nc);
                    }
                }
            }
        }
    }

    public void toggleFlag(int row, int col) {
        if (gameOver || revealed[row][col]) {
            return;
        }
        flagged[row][col] = !flagged[row][col];
    }

    private void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == -1) {
                    revealed[r][c] = true;
                }
            }
        }
    }

    private void checkWin() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] != -1 && !revealed[r][c]) {
                    return;
                }
            }
        }
        gameWon = true;
        gameOver = true;
    }

    public int getFlagCount() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (flagged[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean getRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean getFlagged(int row, int col) {
        return flagged[row][col];
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getNumMines() {
        return numMines;
    }
}