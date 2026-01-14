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

    public void autoFlag() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (this.getBoard()[r][c] > 0 && this.getRevealed(r, c) && !this.getFlagged(r, c)) {
                    // count hidden neighbors
                    //count flagged neighbors
                    int hiddenCount = 0;
                    int flaggedCount = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                if (!this.getRevealed(nr, nc) && !this.getFlagged(nr, nc)) {
                                    hiddenCount++;
                                }
                                if (this.getFlagged(nr, nc)) {
                                    flaggedCount++;
                                }
                            }
                        }
                    }
                    if (hiddenCount + flaggedCount == this.getBoard()[r][c]) {
                        // flag all hidden neighbors
                        for (int dr = -1; dr <= 1; dr++) {
                            for (int dc = -1; dc <= 1; dc++) {
                                int nr = r + dr;
                                int nc = c + dc;
                                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                    if (!this.getRevealed(nr, nc) && !this.getFlagged(nr, nc)) {
                                        this.toggleFlag(nr, nc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void autoReveal() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (this.getBoard()[r][c] > 0 && this.getRevealed(r, c) && !this.getFlagged(r, c)) {
                    // count flagged neighbors
                    int flaggedCount = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                if (this.getFlagged(nr, nc)) {
                                    flaggedCount++;
                                }
                            }
                        }
                    }
                    if (flaggedCount == this.getBoard()[r][c]) {
                        // reveal all hidden neighbors
                        for (int dr = -1; dr <= 1; dr++) {
                            for (int dc = -1; dc <= 1; dc++) {
                                int nr = r + dr;
                                int nc = c + dc;
                                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                    if (!this.getRevealed(nr, nc) && !this.getFlagged(nr, nc)) {
                                        this.reveal(nr, nc);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double[] toInput() {
        double[] input = new double[rows * cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (flagged[r][c]) {
                    input[r * cols + c] = 1.0;
                } else if (revealed[r][c]) {
                    input[r * cols + c] = 0.1 + 0.1 * board[r][c];
                } else {
                    input[r * cols + c] = 0;
                }
            }
        }
        return input;
    }

    public double[] toOutput() {
        double[] output = new double[rows * cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == -1) {
                    output[r * cols + c] = 1.0;
                } else {
                    output[r * cols + c] = 0.0;
                }
            }
        }
        return output;
    }

    public Minesweeper deepCopy() {
        Minesweeper copy = new Minesweeper(rows, cols, numMines);
        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.board[r], 0, copy.board[r], 0, cols);
            System.arraycopy(this.revealed[r], 0, copy.revealed[r], 0, cols);
            System.arraycopy(this.flagged[r], 0, copy.flagged[r], 0, cols);
        }
        copy.gameOver = this.gameOver;
        copy.gameWon = this.gameWon;
        copy.firstClick = this.firstClick;
        return copy;
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

    public boolean[][] getFlaggedArray() {
        return flagged;
    }
}