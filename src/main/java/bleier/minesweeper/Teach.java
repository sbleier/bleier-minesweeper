package bleier.minesweeper;

import basicneuralnetwork.NeuralNetwork;

import java.io.IOException;
import java.util.Random;

public class Teach {

    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int MINES = 3;
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {

        //create neural network
        NeuralNetwork nn = new NeuralNetwork(ROWS * COLS, 128, ROWS * COLS);

        for (int i = 0; i < 1000000; i++) {
            Minesweeper original = new Minesweeper(ROWS, COLS, MINES);
            original.reveal(RANDOM.nextInt(ROWS), RANDOM.nextInt(COLS));
            while (!original.isGameOver() && !original.isGameWon()) {
                Minesweeper copy = original.deepCopy();
                int flagsBefore = copy.getFlagCount();
                copy.autoFlag();
                int flagsAfter = copy.getFlagCount();
                boolean flagAdded = flagsAfter > flagsBefore;

                double[] originalInput = original.toInput();
                double[] copyOutput = copy.toOutput();
                nn.train(originalInput, copyOutput);

                original = copy;
                if (flagAdded) {
                    original.autoReveal();
                } else {
                    original.reveal(RANDOM.nextInt(ROWS), RANDOM.nextInt(COLS));
                }
            }
        }

        try {
            nn.writeToFile("minesweeper_nn.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
