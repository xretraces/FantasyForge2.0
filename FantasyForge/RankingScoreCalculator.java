package FantasyForge;

public class RankingScoreCalculator {
    public static double calculateRankingScore(PlayerName player) {
        double oddsWeight;
        double scaleFactorWeight = 0;
        double starPlayerBonus = 0;
        double estimatedValueWeight = 0.2;

        // Adjust odds weight based on the sign and magnitude of odds
        int oddsValue = player.getPlayerOdds();

        if (oddsValue < -400) {
            oddsWeight = 5; // Example weight for great negative odds
        } else if (oddsValue < -200) {
            oddsWeight = 4; // Example weight for good negative odds
        } else if (oddsValue < -1) {
            oddsWeight = 3; // Example weight for decent negative odds
        } else if (oddsValue == 0) {
            oddsWeight = 2; // Example weight for neutral odds
        } else if (oddsValue <= 1000) {
            oddsWeight = 1; // Example weight for unfavorable positive odds
        } else {
            oddsWeight = 0; // Handle other cases if needed
        }


        // Scale the odds weight to produce a value between 1 and 10
        oddsWeight = oddsWeight / 10.0 * 10.0;

// Positive weight for scale factor
        double scaleFactor = player.getScaleFactor();
        if (scaleFactor == 5) {
            scaleFactorWeight *= 1.5; // Adjust as needed
        } else if (scaleFactor == 4) {
            scaleFactorWeight *= 1.25; // Adjust as needed
        } else if (scaleFactor == 3) {
            scaleFactorWeight *= 1.0;
        } else if (scaleFactor == 2) {
            scaleFactorWeight *= 0.75; // Adjust as needed
        } else if (scaleFactor == 1) {
            scaleFactorWeight *= 0.5; // Adjust as needed
        }


        // Adjust estimated value weight
        player.hasEstimatedValue();// Adjust as needed

        // Check if the player is a star player
        if (player.getConsideredStar().equalsIgnoreCase("y")) {
            starPlayerBonus = 10; // Add bonus only if the player is considered a star
        }




        // Retrieve matchup information from the player object
        String matchup = player.getMatchup();

        // Use matchup information to adjust ranking score calculation
        double matchupAdjustment = calculateMatchupAdjustment(matchup, player.getPropLine());

        double estimatedValueAdjustment = calculateEstimatedValueAdjustment(player);

        double hitRateAdjustment = calculateHitRateAdjustment(player.getHitRate());

        // Calculate the ranking score with matchup adjustment

        return oddsWeight + scaleFactorWeight + starPlayerBonus + estimatedValueWeight + matchupAdjustment + hitRateAdjustment + estimatedValueAdjustment;
    }

    // Add method to calculate hit rate adjustment
    public static double calculateHitRateAdjustment(String hitRate) {
        if (hitRate.equalsIgnoreCase("no history")) {
            return 0.0; // or any default adjustment value
        } else {
            double adjustment;
            // Parse hit rate percentage from the string
            int percentage = Integer.parseInt(hitRate.replaceAll("[^0-9]", ""));
            // Calculate adjustment based on hit rate
            adjustment = percentage / 20.0; // Every 10% is 0.5 point adjustment
            return adjustment;
        }
    }

    // Add method to calculate estimated value adjustment
    public static double calculateEstimatedValueAdjustment(PlayerName player) {
        double adjustment = 0.0;
        if (player.hasEstimatedValue()) {
            // Adjust the score based on the estimated value
            adjustment = player.getEstimatedValue() * 0.2; // Example adjustment formula
        }
        return adjustment;
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

    public static double calculateMatchupAdjustment(String matchup, String propLine) {
        if (propLine.toLowerCase().contains("less") && matchup.equalsIgnoreCase("good")) {
            return -5; // If prop line contains "Less" and matchup is good, adjust score negatively
        } else {
            // Otherwise, apply default adjustments
            return switch (matchup.toLowerCase()) {
                case "good" -> 5;
                case "average" -> 0;
                default -> -5;
            };
        }
    }

    public static String generatePlayerSummary(PlayerName player) {
        StringBuilder summary = new StringBuilder();
        summary.append("Player Summary for ").append(player.playerName).append(":\n");

        // Determine odds description based on odds value
        String oddsDescription;
        int oddsValue = player.getPlayerOdds();
        if (oddsValue <= -110 && oddsValue >= -165) {
            oddsDescription = "Favorable odds";
        } else if (oddsValue <= -170 && oddsValue >= -400) {
            oddsDescription = "Great odds";
        } else {
            oddsDescription = "Unfavorable odds";
        }
        summary.append("- Odds: ").append(oddsDescription).append("\n");

        // Add explanations based on other ranking factors
        summary.append("- Scale Factor: ").append(player.getScaleFactor()).append(" (Higher is better)\n");
        summary.append("- Star Player: ").append(player.getConsideredStar().equalsIgnoreCase("y") ? "Yes\n" : "No\n");
        summary.append("- Estimated Value: ").append(player.hasEstimatedValue() ? "Present\n" : "Not present\n");
        summary.append("- Insights: ").append(player.getInsights()).append("\n");

        // Matchup adjustment explanation
        String matchupExplanation = switch (player.getMatchup().toLowerCase()) {
            case "good" -> "Good matchup (+5 points)\n";
            case "average" -> "Average matchup (No adjustment)\n";
            default -> "Poor matchup (-5 points)\n";
        };
        summary.append("- Matchup Adjustment: ").append(matchupExplanation);

        // Add final ranking score
        summary.append("- Final Ranking Score: ").append(calculateRankingScore(player)).append("\n");

        return summary.toString();
    }

    // Example usage
    public static void main(String[] args) {
        // Example insights
        String insights = "Derrick White 17.5 Points + Rebounds v. Hawks - More (-140) Scale Factor: 3 | Hit Rate 70%" +
                " (Derrick White has failed to exceed 18.5 points + rebounds in 4 of his last 5 games at home " +
                "(15.8 points + rebounds/game average). (Good Matchup)✅)";

        // Calculate deduction based on insights
        double deduction = calculateInsightsDeduction(insights);
        System.out.println("Deduction: " + deduction);
    }
}




