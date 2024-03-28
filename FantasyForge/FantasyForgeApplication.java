package FantasyForge;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FantasyForgeApplication {
    private JFrame frame;
    private final List<PlayerName> players = new ArrayList<>();
    private PlayerTableModel playerTableModel;
    private JTable playerTable;
    private final Odds[] oddsArray; // Add this line

    // Modify the constructor
    public FantasyForgeApplication(Odds[] oddsArray) {
        this.oddsArray = oddsArray; // Set the odds array
        initializeUI();
    }


    private void initializeUI() {
        frame = new JFrame("Fantasy Forge Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Load the image as an ImageIcon
        ImageIcon imgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("ImageIcon.png")));
        // Set the image as the icon of the JFrame
        frame.setIconImage(imgIcon.getImage());

        // Welcome Message
        String welcomeText = "<html><div style='text-align: center;'>"
                + "<h2>Welcome to Fantasy Forge!</h2>"
                + "Dive into the ultimate fantasy sports experience with Fantasy Forge, your go-to application for building and optimizing your dream team. Whether you're preparing for the draft, seeking the perfect lineup, or looking for statistical insights to outmaneuver the competition, Fantasy Forge is here to elevate your game.<br><br>"
                + "<strong>Features at a Glance:</strong><br>"
                + "- <strong>Add Player:</strong> Seamlessly add player information and statistics to craft your roster.<br>"
                + "- <strong>Generate Optimized Combination:</strong> Utilize our advanced algorithms to discover the most promising team combinations based on your criteria.<br>"
                + "- <strong>Insights and Analytics:</strong> Gain valuable insights into player performance and team dynamics to make informed decisions.<br><br>"
                + "Get started by adding players to your team or explore our features to discover how Fantasy Forge can transform your fantasy sports strategy. Happy team building!"
                + "</div></html>";

        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.getContentPane().add(welcomeLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        JButton addPlayerButton = new JButton("Add Player");
        JButton generateOptimizedCombinationButton = new JButton("Generate Optimized Combination");
        JSpinner groupSizeSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));
        JButton tutorialButton = new JButton("Tutorial"); // Tutorial button
        JButton showStatsButton = new JButton("Show Player Stats");
        showStatsButton.addActionListener(e -> displayPlayerStats());
        // Button to display player ranking scores
        JButton displayRankingsButton = new JButton("Display Player Ranking Scores");
        displayRankingsButton.addActionListener(e -> {
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No players have been added.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                displayPlayerRankingScores();
            }
        });
        JButton showBestCombinationButton = new JButton("Show Best Combination");
        showBestCombinationButton.addActionListener(e -> showBestCombination());


        addPlayerButton.addActionListener(e -> openAddPlayerDialog());
        generateOptimizedCombinationButton.addActionListener(e -> {
            generateAndDisplayOptimizedCombinations((Integer) groupSizeSpinner.getValue());
            initializeAndShowPlayerTable(); // Initialize and show the table after data is ready
        });
        tutorialButton.addActionListener(e -> showTutorialDialog()); // Add action listener for the tutorial button

        controlPanel.add(addPlayerButton);
        controlPanel.add(new JLabel("Group Size:"));
        controlPanel.add(groupSizeSpinner);
        controlPanel.add(generateOptimizedCombinationButton);
        controlPanel.add(tutorialButton); // Add the tutorial button to the control panel
        controlPanel.add(showStatsButton); // Add the "Show Player Stats" button to the panel
        controlPanel.add(displayRankingsButton);
        controlPanel.add(showBestCombinationButton); // Add the button to the control panel


        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }


    private void showTutorialDialog() {
        String tutorialText = "<html><body style='width: 200px; padding: 10px;'>"
                + "<h1>Fantasy Forge Tutorial</h1>"
                + "<ol>"
                + "<li><b>Add Players:</b> Click 'Add Player' to enter player information.</li>"
                + "<li><b>Generate Combinations:</b> After adding players, click 'Generate Optimized Combination' to see the best team setup.</li>"
                + "<li><b>Interpret Results:</b> Review the optimized combination in the popup dialog.</li>"
                + "</ol>"
                + "For more details, refer to the application documentation or contact support."
                + "</body></html>";

        JOptionPane.showMessageDialog(frame, tutorialText, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
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
        JTextField projectedLineField = new JTextField(); // Field for entering projected line
        JTextField insightsField = new JTextField();
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
                new JLabel("Projected Line (optional)"), projectedLineField, // Add projected line label and field
                new JLabel("Matchup"), matchupCombo,
                new JLabel("Player Insights"), insightsField,
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
                Double projectedLine = projectedLineField.getText().isEmpty() ? null : Double.parseDouble(projectedLineField.getText()); // Parse projected line, allowing for null
                String insights = insightsField.getText();
                double insightsDeduction = calculateInsightsDeduction(insights);
                PlayerName player = new PlayerName(playerName, propLine, playerOdds, consideredStar, scaleFactor, hasEstimatedValue, estimatedValue, hitRate);
                player.setMatchup(matchup);
                player.setProjectedLine(projectedLine); // Set the projected line

                // Calculate ranking score and then update the table
                double rankingScore = RankingScoreCalculator.calculateRankingScore(player) + insightsDeduction;
                player.setRankingScore(rankingScore);

                // Add player to the list and update the table
                addPlayerAndUpdateTable(player);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for odds, estimated value, and projected line.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addPlayerAndUpdateTable(PlayerName player) {
        players.add(player);
        if (playerTableModel == null) {
            initializeAndShowPlayerTable();
        } else {
            playerTableModel.fireTableDataChanged();
        }
    }


    private void generateAndDisplayOptimizedCombinations(int groupSize) {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No players have been added.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sort the players based on their ranking scores in descending order
        players.sort(Comparator.comparingDouble(PlayerName::getRankingScore).reversed());

        // Assign ranks to the sorted players based on their order
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRank(i + 1);
        }

        // Display the players along with their assigned ranks
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append(String.format("%-4s | %-15s | %-28s | %-5s | %-4s | %-13s | %-15s | %-11s | %-15s\n",
                "Rank", "Player Name", "Prop Line", "Odds", "Star", "Scale Factor", "Estimated Value", "Hit Rate (%)", "Ranking Score"));
        summaryBuilder.append("-".repeat(129)).append("\n"); // Adjust based on actual column widths

        for (PlayerName player : players) {
            summaryBuilder.append(String.format("%-4d | %-15s | %-28s | %-5d | %-4s | %-13d | %-15s | %-11s | %.2f\n",
                    player.getRank(), player.getPlayerName(), player.getPropLine(),
                    player.getPlayerOdds(), player.getConsideredStar(), player.getScaleFactor(),
                    player.hasEstimatedValue() ? "Yes" : "No", player.getHitRate(), player.getRankingScore()));
        }

        // Generate and display possible combinations
        summaryBuilder.append("\nPossible Combinations:\n");
        for (int i = 2; i <= Math.min(6, players.size()); i++) {
            summaryBuilder.append("\n").append(i).append("-Man Power Play:\n");
            List<List<PlayerName>> combinations = FantasyTeamOptimizer.generateCombinations(new ArrayList<>(players), i);
            for (List<PlayerName> combination : combinations) {
                Set<PlayerName> combinationSet = new HashSet<>(combination);
                if (combinationSet.size() != i) {
                    continue;
                }
                double combinedRankingScore = 0.0;
                summaryBuilder.append("Combination:\n");
                for (PlayerName player : combination) {
                    summaryBuilder.append("- ").append(player.getPlayerName()).append(": ").append(player.getRankingScore()).append("\n");
                    combinedRankingScore += player.getRankingScore();
                }
                summaryBuilder.append("Total Score: ").append(combinedRankingScore).append("\n\n");
            }
        }

        // Display the result in a dialog
        JTextArea textArea = new JTextArea(summaryBuilder.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Optimized Combination Summary", JOptionPane.INFORMATION_MESSAGE);
    }




    private void showBestCombination() {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No players have been added.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate the best combination based on ranking score
        List<PlayerName> bestCombination = calculateBestCombinationByRankingScore();

        // Display the best combination along with its detailed stats
        if (bestCombination != null) {
            String summaryBuilder = "Best Combination:\n" +
                    FantasyTeamOptimizer.generateCombinationSummary(bestCombination);

            // Add detailed stats for each player in the best combination
            for (PlayerName player : bestCombination) {
                // Calculate closest moneyline
                int closestMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
                // Calculate implied probability
                double impliedProbability = OddsConverter.impliedProbability(closestMoneyLine);
                // Calculate hit rate percentage
                double hitRatePercentage = player.getHitRatePercentage(); // Assuming this method exists in the PlayerName class

                // Print player stats
                System.out.println("Player: " + player.getPlayerName());
                System.out.println("Closest Moneyline: " + closestMoneyLine);
                System.out.println("Implied Probability: " + String.format("%.2f", impliedProbability) + "%");
                System.out.println("Hit Rate Percentage: " + String.format("%.2f", hitRatePercentage) + "%\n");
            }


            JTextArea textArea = new JTextArea(summaryBuilder);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(frame, scrollPane, "Best Combination by Ranking Score", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No best combination found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<PlayerName> calculateBestCombinationByRankingScore() {
        // Ensure there are players available
        if (players.isEmpty()) {
            return null; // Return null if no players are available
        }

        // Sort players based on their ranking scores in descending order
        players.sort(Comparator.comparingDouble(PlayerName::getRankingScore).reversed());

        // Assuming you want to consider the top few players for the best combination
        int numTopPlayers = Math.min(players.size(), 5); // You can adjust this number as needed

        // Create a sublist containing the top players based on ranking score
        List<PlayerName> topPlayers = players.subList(0, numTopPlayers);

        // Now, you have the top players sorted by ranking score
        // You can use these players to form the best combination(s) as required

        // For example, you might want to return the top player as the best combination
        List<PlayerName> bestCombination = new ArrayList<>();
        bestCombination.add(topPlayers.get(0)); // Add the top player to the best combination

        return bestCombination; // Return the best combination(s) found
    }

    public static double calculateInsightsDeduction(String insights) {
        double deduction = 0.0;

        // Check if player exceeded or failed to exceed
        if (insights.contains("Exceeded")) {
            deduction += 0.3;
        } else if (insights.contains("Failed to exceed")) {
            deduction -= 0.3;
        }

        // Check consecutive games
        if (insights.contains("in 5 straight games") || insights.contains("in 5 of his last 5 games")) {
            deduction += 0.3;
        } else if (insights.contains("in 4 straight games") || insights.contains("in 4 of his last 5 games")) {
            deduction += 0.2;
        } else if (insights.contains("in 3 straight games") || insights.contains("in 3 of his last 5 games")) {
            deduction += 0.1;
        } else if (insights.contains("in 2 straight games") || insights.contains("in 2 of his last 5 games")) {
            deduction -= 0.1;
        } else if (insights.contains("in 1 straight games") || insights.contains("in 1 of his last 5 games")) {
            deduction -= 0.2;
        } else if (insights.contains("in 0 straight games") || insights.contains("in 0 of his last 5 games")) {
            deduction -= 0.3;
        }

        return deduction;
    }


    private void displayPlayerStats() {
        StringBuilder statsBuilder = new StringBuilder();
        statsBuilder.append("Player Stats:\n");
        statsBuilder.append("-----------------------------------------------------------------------------------\n");
        statsBuilder.append(String.format("| %-15s | %-20s | %-19s | %-12s | %-13s |\n", "Player Name", "Closest Money Line", "Implied Probability", "Hit Rate (%)", "Ranking Score"));
        statsBuilder.append("-----------------------------------------------------------------------------------\n");

        for (PlayerName player : players) {
            int closestMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
            double impliedProbability = OddsConverter.impliedProbability(closestMoneyLine);
            double hitRatePercentage = OddsConverter.hitRatePercentage(Math.abs(impliedProbability)); // Ensuring positivity as shown in your snippet

            statsBuilder.append(String.format("| %-15s | %-20d | %-19.2f | %-12.2f | %-13.2f |\n",
                    player.getPlayerName(),
                    closestMoneyLine,
                    Math.abs(impliedProbability), // Ensured positive
                    hitRatePercentage,
                    player.getRankingScore()));
        }

        statsBuilder.append("-----------------------------------------------------------------------------------\n");

        JTextArea textArea = new JTextArea(statsBuilder.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Player Statistics", JOptionPane.INFORMATION_MESSAGE);
    }


    private void displayPlayerRankingScores() {
        StringBuilder rankingsBuilder = new StringBuilder();
        rankingsBuilder.append("Player Ranking Scores:\n");
        rankingsBuilder.append(String.format("%-2s%-15s%-3s%s\n", "Rank", "Player Name", "-", "Ranking Score"));
        rankingsBuilder.append("-".repeat(35)).append("\n");

        for (int i = 0; i < players.size(); i++) {
            rankingsBuilder.append(String.format("%-2d%-15s%-3s%.2f\n", (i + 1), players.get(i).getPlayerName(), "-", players.get(i).getRankingScore()));
        }

        JTextArea textArea = new JTextArea(rankingsBuilder.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Player Ranking Scores", JOptionPane.INFORMATION_MESSAGE);
    }


    // This method finds the closest money line value in the oddsArray for a given player's odds
    private int findClosestMoneyLine(int playerOdds, Odds[] oddsArray) {
        int closest = oddsArray[0].getMoneyLine();
        int smallestDifference = Math.abs(playerOdds - closest);

        for (Odds odds : oddsArray) {
            int currentDifference = Math.abs(playerOdds - odds.getMoneyLine());
            if (currentDifference < smallestDifference) {
                closest = odds.getMoneyLine();
                smallestDifference = currentDifference;
            }
        }

        return closest;
    }


    public void show() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializeAndShowPlayerTable() {
        if (playerTable == null) { // Initialize the table only if it hasn't been initialized yet
            playerTableModel = new PlayerTableModel(players);
            playerTable = new JTable(playerTableModel);
            frame.add(new JScrollPane(playerTable), BorderLayout.CENTER);
            frame.validate(); // Validate the frame to ensure the newly added components are displayed properly
        } else {
            playerTableModel.fireTableDataChanged(); // If the table already exists, just update the data
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDlg = new LoginDialog(null);
            loginDlg.setVisible(true);
            // Check if login succeeded
            if (loginDlg.isSucceeded()) {
                // Ensure you have a method to get the odds array. This might look like:
                // Odds[] oddsArray = Main.getOddsArray();
                FantasyForgeApplication app = new FantasyForgeApplication(Main.getOddsArray());
                app.show();
            } else {
                System.exit(0); // Or handle this case as you see fit
            }
        });
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