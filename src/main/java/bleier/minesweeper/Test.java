package bleier.minesweeper;

import basicneuralnetwork.NeuralNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {

    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int MINES = 3;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {

        NeuralNetwork nn = null;
        try {
            nn = NeuralNetwork.readFromFile("minesweeper_nn.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int winCount = 0;
        int totalMoves = 0;

        for (int i = 0; i < 1000; i++) {
            int moves = 0;

            Minesweeper minesweeper = new Minesweeper(ROWS, COLS, MINES);
            minesweeper.reveal(RANDOM.nextInt(ROWS), RANDOM.nextInt(COLS));

            while (!minesweeper.isGameOver() && !minesweeper.isGameWon()) {
                moves++;
                double[] input = minesweeper.toInput();
                double[] output = nn.guess(input);
                boolean flagAdded = false;

                for (int j = 0; j < output.length; j++) {
                    if (output[j] >= .9) {
                        int r = j / COLS;
                        int c = j % COLS;
                        if (minesweeper.flag(r, c)) {
                            flagAdded = true;
                        }
                    }
                }
                if (flagAdded) {
                    minesweeper.autoReveal();
                } else {
                    // to avoid infinite loop to only reveal cells that are not revealed or flagged
                    List<int[]> candidates = new ArrayList<>();
                    for (int r = 0; r < ROWS; r++) {
                        for (int c = 0; c < COLS; c++) {
                            if (!minesweeper.getRevealed(r, c) && !minesweeper.getFlagged(r, c)) {
                                candidates.add(new int[]{r, c});
                            }
                        }
                    }
                    if (candidates.isEmpty()) {
                        // no possible moves left so game is effectively over
                        break;
                    }

                    int[] cell = candidates.get(RANDOM.nextInt(candidates.size()));
                    minesweeper.reveal(cell[0], cell[1]);
                }
            }
            if (minesweeper.isGameWon()) {
                winCount++;
            }
            totalMoves += moves;
        }
        System.out.println("Average moves per game: " + ((double) totalMoves / 1000.0));

        System.out.println("Win rate: " + ((double) winCount / 1000.0) * 100.0 + "%");


    }
}
