package FantasyForge;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<List<PlayerName>> combinations = FantasyTeamOptimizer.generateCombinations(new ArrayList<>(players), groupSize);
        if (combinations.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No combinations found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<PlayerName> bestCombination = FantasyTeamOptimizer.selectBestCombination(combinations);

        // Perform odds calculations for each player in the best combination
        // Initialize this array with your actual odds data
        for (PlayerName player : bestCombination) {
            player.calculateOdds(oddsArray);
        }

        // Now that each player has updated odds, implied probability, and hit rate percentage,
        // you can incorporate this information into your summary
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append(FantasyTeamOptimizer.generateCombinationSummary(bestCombination));
        summaryBuilder.append("\n\nOdds and Probabilities:\n");
        for (PlayerName player : bestCombination) {
            summaryBuilder.append(player.getPlayerName())
                    .append(": Closest Money Line = ").append(player.getClosestMoneyLine())
                    .append(", Implied Probability = ").append(String.format("%.2f", player.getImpliedProbability()))
                    .append("%, Hit Rate = ").append(String.format("%.2f", player.getHitRatePercentage()))
                    .append("%\n");
        }

        JTextArea textArea = new JTextArea(summaryBuilder.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Optimized Combination Summary", JOptionPane.INFORMATION_MESSAGE);
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