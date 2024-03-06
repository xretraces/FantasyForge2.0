package FantasyForge;

import java.util.ArrayList;
import java.util.List;

public class FantasyTeamOptimizer {
    // Method to generate combinations of players for a given group size
    public static List<List<PlayerName>> generateCombinations(List<PlayerName> players, int groupSize) {
        List<List<PlayerName>> combinations = new ArrayList<>();
        generateCombinationsHelper(players, groupSize, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<PlayerName> players, int groupSize, int start, List<PlayerName> currentCombination, List<List<PlayerName>> combinations) {
        if (groupSize == 0) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = start; i <= players.size() - groupSize; i++) {
            currentCombination.add(players.get(i));
            generateCombinationsHelper(players, groupSize - 1, i + 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    // Method to select the best combination from a list of combinations
    public static List<PlayerName> selectBestCombination(List<List<PlayerName>> combinations) {
        List<PlayerName> bestCombination = new ArrayList<>();
        double bestScore = Double.MIN_VALUE;

        for (List<PlayerName> combination : combinations) {
            double score = calculateCombinationScore(combination);
            if (score > bestScore) {
                bestScore = score;
                bestCombination = new ArrayList<>(combination);
            }
        }

        return bestCombination;
    }

    // Method to calculate the overall score of a combination
    private static double calculateCombinationScore(List<PlayerName> combination) {
        double score = 0;
        for (PlayerName player : combination) {
            score += RankingScoreCalculator.calculateRankingScore(player);
        }
        return score;
    }
    // Method to generate a summary for a combination of players
    public static String generateCombinationSummary(List<PlayerName> combination) {
        StringBuilder summary = new StringBuilder();

        // Calculate total score for the combination
        double totalScore = 0;
        for (PlayerName player : combination) {
            totalScore += player.getRankingScore();
        }

        // Add header for the combination summary
        summary.append("Summary for Combination:\n");

        // Add information for each player in the combination
        for (PlayerName player : combination) {
            summary.append("- ").append(player.playerName).append(":\n");
            summary.append("  - Odds: ").append(player.getPlayerOdds()).append("\n");
            summary.append("  - Scale Factor: ").append(player.getScaleFactor()).append("\n");
            summary.append("  - Star Player: ").append(player.getConsideredStar()).append("\n");
            summary.append("  - Estimated Value: ").append(player.hasEstimatedValue() ? "Present" : "Not present").append("\n");
            summary.append("  - Matchup: ").append(player.getMatchup()).append("\n");
            summary.append("  - Insights: ").append(player.getInsights()).append("\n");
            summary.append("  - Ranking Score: ").append(player.getRankingScore()).append("\n");
        }

        // Add total score for the combination
        summary.append("Total Score: ").append(totalScore).append("\n");

        return summary.toString();
    }
}

