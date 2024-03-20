package FantasyForge;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FantasyForgeApplication {
    private JFrame frame;
    private final List<PlayerName> players = new ArrayList<>();
    private PlayerTableModel playerTableModel;

    public FantasyForgeApplication() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Fantasy Forge Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Initializing the table model and table
        playerTableModel = new PlayerTableModel(players);
        JTable playerTable = new JTable(playerTableModel);
        frame.add(new JScrollPane(playerTable), BorderLayout.CENTER); // Use JScrollPane for scrolling

        JPanel controlPanel = new JPanel();
        JButton addPlayerButton = new JButton("Add Player");
        JButton generateOptimizedCombinationButton = new JButton("Generate Optimized Combination");
        JSpinner groupSizeSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));

        addPlayerButton.addActionListener(e -> openAddPlayerDialog());
        generateOptimizedCombinationButton.addActionListener(e -> generateAndDisplayOptimizedCombinations((Integer) groupSizeSpinner.getValue()));

        controlPanel.add(addPlayerButton);
        controlPanel.add(new JLabel("Group Size:"));
        controlPanel.add(groupSizeSpinner);
        controlPanel.add(generateOptimizedCombinationButton);

        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    private void openAddPlayerDialog() {
        JTextField playerNameField = new JTextField();
        JTextField propLineField = new JTextField();
        JTextField playerOddsField = new JTextField();
        JComboBox<String> consideredStarCombo = new JComboBox<>(new String[]{"y", "n"});
        JSpinner scaleFactorSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        JCheckBox hasEstimatedValueCheck = new JCheckBox();
        JTextField estimatedValueField = new JTextField();
        JTextField hitRateField = new JTextField();
        JComboBox<String> matchupCombo = new JComboBox<>(new String[]{"good", "average", "bad"});
        matchupCombo.setSelectedIndex(1); // Default to "average"

        final JComponent[] inputs = new JComponent[]{
                new JLabel("Player Name"), playerNameField,
                new JLabel("Prop Line"), propLineField,
                new JLabel("Player Odds"), playerOddsField,
                new JLabel("Considered Star (y/n)"), consideredStarCombo,
                new JLabel("Scale Factor (1-5)"), scaleFactorSpinner,
                new JLabel("Has Estimated Value"), hasEstimatedValueCheck,
                new JLabel("Estimated Value"), estimatedValueField,
                new JLabel("Hit Rate"), hitRateField,
                new JLabel("Matchup"), matchupCombo
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Add Player", JOptionPane.DEFAULT_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String playerName = playerNameField.getText();
                String propLine = propLineField.getText();
                int playerOdds = Integer.parseInt(playerOddsField.getText());
                String consideredStar = (String) consideredStarCombo.getSelectedItem();
                int scaleFactor = (Integer) scaleFactorSpinner.getValue();
                boolean hasEstimatedValue = hasEstimatedValueCheck.isSelected();
                double estimatedValue = hasEstimatedValue ? Double.parseDouble(estimatedValueField.getText()) : 0;
                String hitRate = hitRateField.getText();
                String matchup = (String) matchupCombo.getSelectedItem();

                PlayerName player = new PlayerName(playerName, propLine, playerOdds, consideredStar, scaleFactor, hasEstimatedValue, estimatedValue, hitRate);
                player.setMatchup(matchup);
                // Calculate ranking score and then update the table
                double rankingScore = RankingScoreCalculator.calculateRankingScore(player);
                player.setRankingScore(rankingScore);

                // Add player to the list and update the table
                addPlayerAndUpdateTable(player);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for odds and estimated value.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addPlayerAndUpdateTable(PlayerName player) {
        players.add(player);
        playerTableModel.fireTableDataChanged(); // Notify the table model that data has changed
    }

    private void generateAndDisplayOptimizedCombinations(int groupSize) {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No players have been added.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<List<PlayerName>> combinations = FantasyTeamOptimizer.generateCombinations(new ArrayList<>(players), groupSize);
        if (combinations.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No combinations found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<PlayerName> bestCombination = FantasyTeamOptimizer.selectBestCombination(combinations);
        String summary = FantasyTeamOptimizer.generateCombinationSummary(bestCombination);

        JTextArea textArea = new JTextArea(summary);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Optimized Combination Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FantasyForgeApplication().show());
    }

    // Inner class for the player table model
    static class PlayerTableModel extends AbstractTableModel {
        private final List<PlayerName> players;
        private final String[] columnNames = {"Rank", "Player Name", "Prop Line", "Odds", "Star", "Scale Factor", "Estimated Value", "Hit Rate (%)", "Ranking Score"};

        public PlayerTableModel(List<PlayerName> players) {
            this.players = players;
        }

        @Override
        public int getRowCount() {
            return players.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PlayerName player = players.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> rowIndex + 1;
                case 1 -> player.getPlayerName();
                // Add cases for other columns
                default -> "";
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }
}
