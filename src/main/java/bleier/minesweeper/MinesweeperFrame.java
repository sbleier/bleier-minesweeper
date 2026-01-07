package bleier.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperFrame extends JFrame {
    private Minesweeper game;
    private JButton[][] cells;
    private JLabel flagLabel;
    private JLabel timeLabel;
    private JButton autoFlag;
    private JButton autoReveal;
    private Timer timer;
    private int timeElapsed;

    public MinesweeperFrame(Minesweeper game) {
        this.game = game;
        this.cells = new JButton[game.getRows()][game.getCols()];

        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        flagLabel = new JLabel("Flags: " + game.getNumMines());
        autoFlag = new JButton(" Auto-Flag");
        autoReveal = new JButton(" Auto-Reveal");
        JButton newGameButton = new JButton("New Game");
        timeLabel = new JLabel("Time: 000");

        JPanel topPanel = new JPanel();
        topPanel.add(flagLabel);
        topPanel.add(newGameButton);
        topPanel.add(timeLabel);
        topPanel.add(autoFlag);
        topPanel.add(autoReveal);
        add(topPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(game.getRows(), game.getCols()));

        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                cells[r][c] = new JButton();
                cells[r][c].setPreferredSize(new Dimension(40, 40));
                cells[r][c].setFont(new Font("Arial", Font.BOLD, 18));
                cells[r][c].setFocusPainted(false);
                cells[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cells[r][c].setOpaque(true);
                cells[r][c].setBackground(new Color(160, 160, 160));

                final int row = r;
                final int col = c;

                cells[r][c].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (game.isGameOver()) {
                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
                            game.toggleFlag(row, col);
                            updateBoard();
                        } else if (e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown()) {
                            game.reveal(row, col);
                            updateBoard();
                        }
                    }
                });

                boardPanel.add(cells[r][c]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        newGameButton.addActionListener(e -> {
            dispose();
            new MinesweeperFrame(new Minesweeper(9, 9, 10)).setVisible(true);
        });


        autoFlag.addActionListener(e -> {
            game.autoFlag();
            updateBoard();
        });

        autoReveal.addActionListener(e -> {
            game.autoReveal();
            updateBoard();
        });

        timeElapsed = 0;
        timer = new Timer(1000, e -> {
            if (!game.isGameOver()) {
                timeElapsed++;
                timeLabel.setText("Time: " + String.format("%03d", timeElapsed));
            }
        });
        timer.start();

        pack();
        setLocationRelativeTo(null);
    }

    private void updateBoard() {
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                JButton cell = cells[r][c];
                cell.setOpaque(true);
                int value = game.getBoard()[r][c];

                // If game is over, show all mines
                if (game.isGameOver() && value == -1) {
                    if (game.getFlagged(r, c)) {
                        cell.setText("F");
                        cell.setForeground(Color.WHITE);
                        cell.setBackground(Color.GREEN);
                    } else {
                        cell.setText("*");
                        cell.setForeground(Color.BLACK);
                        cell.setBackground(Color.RED);
                    }
                } else if (game.isGameOver() && game.getFlagged(r, c) && value != -1) {
                    // Wrong flag - flagged but not a mine
                    cell.setText("X");
                    cell.setForeground(Color.WHITE);
                    cell.setBackground(Color.ORANGE);
                } else if (game.getFlagged(r, c)) {
                    cell.setText("F");
                    cell.setForeground(Color.RED);
                    cell.setBackground(new Color(160, 160, 160));
                } else if (game.getRevealed(r, c)) {
                    if (value == -1) {
                        cell.setText("*");
                        cell.setForeground(Color.BLACK);
                        cell.setBackground(Color.RED);
                    } else if (value > 0) {
                        cell.setText(String.valueOf(value));
                        cell.setForeground(getNumberColor(value));
                        cell.setBackground(new Color(210, 210, 210));
                    } else {
                        cell.setText("");
                        cell.setBackground(new Color(210, 210, 210));
                    }
                } else {
                    cell.setText("");
                    cell.setBackground(new Color(160, 160, 160));
                }
            }
        }

        flagLabel.setText("Flags: " + (game.getNumMines() - game.getFlagCount()));

        if (game.isGameOver()) {
            timer.stop();
            if (game.isGameWon()) {
                JOptionPane.showMessageDialog(this, "You Win!");
            } else {
                JOptionPane.showMessageDialog(this, "Game Over!");
            }
        }
    }

    private Color getNumberColor(int num) {
        switch (num) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            case 5: return new Color(128, 0, 0);
            case 6: return new Color(0, 128, 128);
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Minesweeper game = new Minesweeper(9, 9, 10);
            MinesweeperFrame frame = new MinesweeperFrame(game);
            frame.setVisible(true);
        });
    }
}