package FantasyForge;

import java.util.*;

import static FantasyForge.PlayerName.abbreviatedPropLine;


public class Main {

    private static final double MIN_SCORE = -500.0;

    // Method to find the closest money line
    private static int findClosestMoneyLine(double playerOdds, Odds[] oddsArray) {
        int closestMoneyLine = 0;
        double minDifference = Double.MAX_VALUE;

        for (Odds oddsObj : oddsArray) {
            int oddsValue = oddsObj.getMoneyLine();
            // Calculate the difference between playerOdds and the odds from oddsArray
            double difference = Math.abs(playerOdds - oddsValue);

            // Update the closest money line if the current difference is smaller
            if (difference < minDifference) {
                minDifference = difference;
                closestMoneyLine = oddsValue;
            }
        }

        return closestMoneyLine;
    }

    // Method to calculate hit rate percentage
    private static double calculateHitRatePercentage(double combinedProbability) {
        // Ensure the combined probability is within the valid range [0, 1]
        combinedProbability = Math.min(Math.max(combinedProbability, 0), 1);

        // Convert combined probability to percentage
        return combinedProbability * 100;
    }

    // Method to calculate combined implied probability using Multiplicative Rule
    private static double calculateCombinedImpliedProbability(List<PlayerName> combination, Odds[] oddsArray) {
        if (combination.isEmpty()) {
            return 0.0; // If there are no players, return 0 probability
        }
        double combinedImpliedProbability = 1.0;
        for (PlayerName player : combination) {
            int playerMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
            double playerImpliedProbability = OddsConverter.impliedProbability(playerMoneyLine);
            combinedImpliedProbability *= (1 - playerImpliedProbability); // Probability of not winning
        }
        return 1 - combinedImpliedProbability; // Probability of winning (at least one player winning)
    }

    private static double calculateCombinedProbability(List<PlayerName> combination, Odds[] oddsArray) {
        if (combination.isEmpty()) {
            return 0.0; // If there are no players, return 0 probability
        }
        double combinedProbability = 1.0;
        for (PlayerName player : combination) {
            int playerMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
            double playerImpliedProbability = OddsConverter.impliedProbability(playerMoneyLine);
            combinedProbability *= playerImpliedProbability;
        }
        return combinedProbability;
    }


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);


        System.out.println("How many players do you want to enter?");
        int numOfPlayers = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                numOfPlayers = input.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                input.next(); // Consume the invalid input
            }
        }

        PlayerName[] players = new PlayerName[numOfPlayers];

// Gather player information
        for (int i = 0; i < numOfPlayers; i++) {
            players[i] = new PlayerName("", "", 0, "", 0, false, 0, "");
            boolean playerInputInvalid = false;

            while (!playerInputInvalid) {
                try {
                    players[i].execute();
                    playerInputInvalid = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input for player " + (i + 1) + ". Please enter valid data.");
                    input.next(); // Consume the invalid input
                }
            }
        }

// Ask if the user wants to add more players
        boolean keepInserting = true;

        while (keepInserting) {
            input.nextLine(); // consume newline character
            System.out.println("Do you want to keep inserting picks? (y/n)");

            String answer = input.nextLine();

            if (answer.equalsIgnoreCase("n")) {
                keepInserting = false;
            } else if (answer.equalsIgnoreCase("y")) {
                System.out.println("How many players would you like to add?");
                int additionalPlayers = input.nextInt();
                input.nextLine(); // consume newline character

                PlayerName[] newPlayers = new PlayerName[numOfPlayers + additionalPlayers];
                System.arraycopy(players, 0, newPlayers, 0, numOfPlayers);

                for (int i = numOfPlayers; i < numOfPlayers + additionalPlayers; i++) {
                    newPlayers[i] = new PlayerName("", "", 0, "", 0, false, 0, "");
                    boolean playerInputInvalid = false;

                    while (!playerInputInvalid) {
                        try {
                            newPlayers[i].execute();
                            playerInputInvalid = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input for player " + (i + 1) + ". Please enter valid data.");
                            input.next(); // Consume the invalid input
                        }
                    }
                }

                players = newPlayers;
                numOfPlayers += additionalPlayers;
            } else {
                System.out.println("Invalid input. Assuming 'n' (no) by default.");
                keepInserting = false;
            }
        }

// At this point, all players are gathered


        for (PlayerName player : players) {
            double rankingScore = RankingScoreCalculator.calculateRankingScore(player);
            // Pass the PlayerName object itself instead of individual attributes;
            player.setRankingScore(rankingScore); // Set the calculated ranking score for the player
        }


        System.out.println("Enter today's total bet amount:");
        double totalBetAmount = input.nextDouble();

        double twentyPercent = totalBetAmount * 0.2;
        double thirtyPercent = totalBetAmount * 0.3;

        System.out.printf("20%% of today's total bet amount: %.2f\n", twentyPercent);
        System.out.printf("30%% of today's total bet amount: %.2f\n", thirtyPercent);

        // Rank the players based on odds value, being a star, and scale factor
        // Sort the players array based on their ranking score
        Arrays.sort(players, (p1, p2) -> Double.compare(p2.getRankingScore(), p1.getRankingScore()));

        // Calculate the maximum width for each column
        int maxRankWidth = String.valueOf(numOfPlayers).length();
        int maxNameWidth = 0;
        int maxPropLineWidth = 0;
        int maxOddsWidth = 0;
        int maxStarWidth = 0;
        int maxScaleFactorWidth = 0;
        int maxEstimatedValueWidth = 0;
        int maxHitRateWidth = 0;
        int maxRankingScoreWidth = 0;

        for (int i = 0; i < numOfPlayers; i++) {
            maxNameWidth = Math.max(maxNameWidth, players[i].getPlayerName().length());
            maxPropLineWidth = Math.max(maxPropLineWidth, players[i].getPropLine().length());
            maxOddsWidth = Math.max(maxOddsWidth, String.valueOf(players[i].getPlayerOdds()).length());
            maxStarWidth = Math.max(maxStarWidth, players[i].getConsideredStar().length());
            maxScaleFactorWidth = Math.max(maxScaleFactorWidth, String.valueOf(players[i].getScaleFactor()).length());
            maxEstimatedValueWidth = Math.max(maxEstimatedValueWidth, (players[i].hasEstimatedValue() ? "Yes" : "No").length());
            maxHitRateWidth = Math.max(maxHitRateWidth, String.valueOf(players[i].getHitRate()).length());
            maxRankingScoreWidth = Math.max(maxRankingScoreWidth, String.valueOf(players[i].getRankingScore()).length());
        }
// Print the players and their rank
        System.out.printf("%-" + maxRankWidth + "s | %-" + maxNameWidth + "s | %-" + maxPropLineWidth + "s | %-" + maxOddsWidth + "s | %-" + maxStarWidth + "s | %-" + maxScaleFactorWidth + "s | %-" + maxEstimatedValueWidth + "s | %-" + maxHitRateWidth + "s | %-" + maxRankingScoreWidth + "s\n",
                "Rank", "Player Name", "Prop Line", "Odds", "Star", "Scale Factor", "Estimated Value", "Hit Rate (%)", "Ranking Score");
        System.out.println("-".repeat(maxRankWidth + maxNameWidth + maxPropLineWidth + maxOddsWidth + maxStarWidth + maxScaleFactorWidth + maxEstimatedValueWidth + maxHitRateWidth + maxRankingScoreWidth + 5)); // Add 11 for spacing and separators

// Iterate over players and print their information
        for (int i = 0; i < numOfPlayers; i++) {
            String abbreviatedPropLine = abbreviatedPropLine(players[i].getPropLine()); // Abbreviate the prop line
            String estimatedValueText = players[i].hasEstimatedValue() ? "Yes" : "No";
            System.out.printf("%-" + maxRankWidth + "d | %-" + maxNameWidth + "s | %-" + maxPropLineWidth + "s | %-" + maxOddsWidth + "d | %-" + maxStarWidth + "s | %-" + maxScaleFactorWidth + "d | %-" + maxEstimatedValueWidth + "s | %-" + maxHitRateWidth + "s | %-" + maxRankingScoreWidth + ".2f\n",
                    i + 1, players[i].getPlayerName(), abbreviatedPropLine,
                    players[i].getPlayerOdds(), players[i].getConsideredStar(),
                    players[i].getScaleFactor(), estimatedValueText, players[i].getHitRate(),
                    players[i].getRankingScore());
            System.out.println();
        }


        //Odds Data below, this is for calculating the closest hit rate to player
        // ranking score based on American Money Line projections:

        Odds[] oddsArray = {
                new Odds(-400, 80.00),
                new Odds(+400, 20.00),
                new Odds(-395, 79.80),
                new Odds(+395, 20.20),
                new Odds(-390, 79.60),
                new Odds(+390, 20.40),
                new Odds(-385, 79.40),
                new Odds(+385, 20.60),
                new Odds(-380, 79.20),
                new Odds(+380, 20.80),
                new Odds(-375, 78.90),
                new Odds(+375, 21.10),
                new Odds(-370, 78.70),
                new Odds(+370, 21.30),
                new Odds(-365, 78.50),
                new Odds(+365, 21.50),
                new Odds(-360, 78.30),
                new Odds(+360, 21.70),
                new Odds(-355, 78.00),
                new Odds(+355, 22.00),
                new Odds(-350, 77.80),
                new Odds(+350, 22.20),
                new Odds(-345, 77.50),
                new Odds(+345, 22.50),
                new Odds(-340, 77.30),
                new Odds(+340, 22.70),
                new Odds(-335, 77.00),
                new Odds(+335, 23.00),
                new Odds(-330, 76.70),
                new Odds(+330, 23.30),
                new Odds(-325, 76.50),
                new Odds(+325, 23.50),
                new Odds(-320, 76.20),
                new Odds(+320, 23.80),
                new Odds(-315, 75.90),
                new Odds(+315, 24.10),
                new Odds(-310, 75.60),
                new Odds(+310, 24.40),
                new Odds(-305, 75.30),
                new Odds(+305, 24.70),
                new Odds(-300, 75.00),
                new Odds(+300, 25.00),
                new Odds(-295, 74.70),
                new Odds(+295, 25.30),
                new Odds(-290, 74.40),
                new Odds(+290, 25.60),
                new Odds(-285, 74.00),
                new Odds(+285, 26.00),
                new Odds(-280, 73.70),
                new Odds(+280, 26.30),
                new Odds(-275, 73.30),
                new Odds(+275, 26.70),
                new Odds(-270, 73.00),
                new Odds(+270, 27.00),
                new Odds(-265, 72.60),
                new Odds(+265, 27.40),
                new Odds(-260, 72.20),
                new Odds(+260, 27.80),
                new Odds(-255, 71.80),
                new Odds(+255, 28.20),
                new Odds(-250, 71.40),
                new Odds(+250, 28.60),
                new Odds(-245, 71.00),
                new Odds(+245, 29.00),
                new Odds(-240, 70.60),
                new Odds(+240, 29.40),
                new Odds(-235, 70.10),
                new Odds(+235, 30.00),
                new Odds(-230, 69.70),
                new Odds(+230, 30.00),
                new Odds(-225, 69.20),
                new Odds(+225, 31.00),
                new Odds(-220, 68.80),
                new Odds(+220, 31.00),
                new Odds(-215, 68.30),
                new Odds(+215, 32.00),
                new Odds(-210, 67.70),
                new Odds(+210, 32.00),
                new Odds(-205, 67.20),
                new Odds(+205, 33.00),
                new Odds(-200, 66.70),
                new Odds(+200, 33.00),
                new Odds(-195, 66.10),
                new Odds(+195, 34.00),
                new Odds(-190, 65.50),
                new Odds(+190, 34.00),
                new Odds(-185, 64.90),
                new Odds(+185, 35.00),
                new Odds(-180, 64.30),
                new Odds(+180, 36.00),
                new Odds(-175, 63.60),
                new Odds(+175, 36.00),
                new Odds(-170, 63.00),
                new Odds(+170, 37.00),
                new Odds(-165, 62.30),
                new Odds(+165, 38.00),
                new Odds(-160, 61.50),
                new Odds(+160, 38.00),
                new Odds(-155, 60.80),
                new Odds(+155, 39.00),
                new Odds(-150, 60.00),
                new Odds(+150, 40.00),
                new Odds(-145, 59.20),
                new Odds(+145, 41.00),
                new Odds(-140, 58.30),
                new Odds(+140, 42.00),
                new Odds(-135, 57.40),
                new Odds(+135, 43.00),
                new Odds(-130, 56.50),
                new Odds(+130, 43.00),
                new Odds(-125, 55.60),
                new Odds(+125, 44.00),
                new Odds(-120, 54.50),
                new Odds(+120, 45.00),
                new Odds(-115, 53.50),
                new Odds(+115, 47.00),
                new Odds(-110, 52.40),
                new Odds(+110, 48.00),
                new Odds(-105, 51.20),
                new Odds(+105, 49.00),
                new Odds(-100, 50.00),
                new Odds(+100, 50.00),
        };

        // Iterate over players and find the closest odds with using Odds class
        for (PlayerName player : players) {
            int closestMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
            double impliedProbability = OddsConverter.impliedProbability(closestMoneyLine);

            // Ensure probabilities and percentages are positive
            impliedProbability = Math.abs(impliedProbability);


            // Add logic to use these values as needed for the application
            // Print individual player information
            System.out.println(player.getPlayerName() + ": Closest Money Line - " + closestMoneyLine);
            System.out.println("Implied Probability: " + impliedProbability);
            System.out.println("Hit Rate Percentage: " + (impliedProbability) + "%");


            // Calculate combined probability
            double combinedProbability = calculateCombinedProbability(Collections.singletonList(player), oddsArray);

            // Calculate combined implied probability
            double combinedImpliedProbability = calculateCombinedImpliedProbability(Collections.singletonList(player), oddsArray);

            // Calculate hit rate percentage from combined probability
            double combinedHitRatePercentage = calculateHitRatePercentage(Math.abs(combinedProbability));


            // Print out the combined probabilities
            System.out.println("Combined Probability: " + combinedProbability);
            System.out.println("Combined Implied Probability: " + combinedImpliedProbability);
            System.out.println("Combined Hit Rate Percentage: " + combinedHitRatePercentage + "%");


        }

        // Generate and print possible combinations
        Set<Set<PlayerName>> allCombinations = new HashSet<>();
        System.out.println("\nPossible Combinations:");
        for (int i = 2; i <= Math.min(6, numOfPlayers); i++) {
            System.out.printf("\n%d-Man Power Play:\n", i);
            List<List<PlayerName>> combinations = getCombinations(players, i);
            for (List<PlayerName> combination : combinations) {
                Set<PlayerName> combinationSet = new HashSet<>(combination);
                if (combinationSet.size() != i || allCombinations.contains(combinationSet)) {
                    continue;
                }

                // Calculate combined odds for the combination
                double combinedDecimalOdds = 1.0;
                for (PlayerName player : combination) {
                    int playerMoneyLine = findClosestMoneyLine(player.getRankingScore(), oddsArray);
                    double playerDecimalOdds = OddsConverter.americanToDecimal(playerMoneyLine);
                    combinedDecimalOdds *= playerDecimalOdds;
                }

                double combinedImpliedProbability = 0.0;
                double combinedHitRatePercentage;

                // Iterate over players in the combination and accumulate implied probability
                for (PlayerName player : combination) {
                    int playerMoneyLine = findClosestMoneyLine(player.getPlayerOdds(), oddsArray);
                    double playerImpliedProbability = OddsConverter.impliedProbability(playerMoneyLine);
                    combinedImpliedProbability += playerImpliedProbability;
                }

                // Calculate average implied probability for the combination
                combinedImpliedProbability /= combination.size();

                // Calculate hit rate percentage from the combined implied probability
                combinedHitRatePercentage = OddsConverter.hitRatePercentage(combinedImpliedProbability);

                // Existing code for printing individual player information
                double combinationScore = 0;
                for (PlayerName player : combination) {
                    combinationScore += player.getScore();
                }
                System.out.print(i + "-Man Power Play: ");
                StringJoiner playersString = new StringJoiner(", ");
                for (PlayerName player : combination) {
                    playersString.add(player.getPlayerName());
                }
                double oddsRankingScore = combinationScore;
                for (PlayerName player : combination) {
                    if (player.getConsideredStar().equals("y")) {
                        oddsRankingScore -= 100;
                    } else if (player.getConsideredStar().equals("n")) {
                        oddsRankingScore -= 0;
                    }
                    if (player.getScaleFactor() == 1) {
                        oddsRankingScore -= 10;
                    } else if (player.getScaleFactor() == 2) {
                        oddsRankingScore -= 20;
                    } else if (player.getScaleFactor() == 3) {
                        oddsRankingScore -= 30;
                    } else if (player.getScaleFactor() == 4) {
                        oddsRankingScore -= 40;
                    } else if (player.getScaleFactor() == 5) {
                        oddsRankingScore -= 50;
                    }
                }
                double modifiedBetAmount = i % 2 == 0 ? twentyPercent : thirtyPercent;
                double payout = calculatePayout(i, combination.size(), modifiedBetAmount);

                // Print combined odds information
                System.out.println("Combined Decimal Odds: " + combinedDecimalOdds);
                System.out.println("Combined Implied Probability: " + combinedImpliedProbability);
                System.out.println("Combined Hit Rate Percentage: " + combinedHitRatePercentage + "%");

                // Continue with existing code for printing player-specific information
                System.out.println(playersString + " - " + combinationScore + " (" + oddsRankingScore + ") Payout: $" + payout);
                allCombinations.add(combinationSet);
                // Add a line break for better separation
                System.out.println();
            }
        }

        // Print the player ranking scores and summaries
        System.out.println("\nPlayer Ranking Scores:");
        for (int i = 0; i < numOfPlayers; i++) {
            System.out.printf("%-2s%-15s%-3s%.2f\n", (i + 1) + ".", players[i].getPlayerName(), "-", players[i].getRankingScore());

            // Call the generatePlayerSummary method and print the summary
            System.out.println(RankingScoreCalculator.generatePlayerSummary(players[i]));

            // Add a line break for better separation
            System.out.println();
        }

        // Print the counter of possible combinations
        System.out.println("\nCounter of Possible Combinations:");
        for (int i = 2; i <= Math.min(6, numOfPlayers); i++) {
            List<List<PlayerName>> combinations = getCombinations(players, i);
            System.out.printf("# of Possible %d Man Combinations: %d\n", i, combinations.size());
        }

        // Declare the combinations list
        List<List<PlayerName>> combinations;

        // Iterate over different group sizes
        for (int groupSize = 2; groupSize <= Math.min(6, numOfPlayers); groupSize++) {
            // Generate combinations
            combinations = FantasyTeamOptimizer.generateCombinations(Arrays.asList(players), groupSize);

            // Select best combination
            List<PlayerName> bestCombination = FantasyTeamOptimizer.selectBestCombination(combinations);

            // Print best combination for current group size
            System.out.println(groupSize + "-Man Best Combination:");
            for (PlayerName player : bestCombination) {
                System.out.println(player.getPlayerName());
            }
            System.out.println(); // Add a blank line for separation

            // Generate and print summary for the best combination
            String combinationSummary = FantasyTeamOptimizer.generateCombinationSummary(bestCombination);
            System.out.println(combinationSummary);
            System.out.println(); // Add a blank line for separation
        }
    }

    private static double calculatePayout(int i, int correctPicks, double totalBetAmount) {
        double payoutMultiplier = switch (i) {
            case 6 -> correctPicks == 6 ? 25 : correctPicks == 5 ? 2 : 0.4;
            case 5 -> correctPicks == 5 ? 10 : correctPicks == 4 ? 2 : 0.4;
            case 4 -> correctPicks == 4 ? 10 : 1.5;
            case 3 -> correctPicks == 3 ? 5 : 1.25;
            case 2 -> 3;
            default -> 1;
        };
        return totalBetAmount * payoutMultiplier;
    }

    private static void getCombinationsHelper(PlayerName[] players, List<List<PlayerName>> combinations, int size, int start, List<PlayerName> currCombination, Set<PlayerName> usedPlayers) {
        if (start == size) {
            double combinationScore = 0;
            for (PlayerName player : currCombination) {
                combinationScore += player.getScore();
            }
            if (combinationScore >= MIN_SCORE) {
                combinations.add(new ArrayList<>(currCombination));
            }
            return;
        }
        for (PlayerName player : players) {
            if (!usedPlayers.contains(player)) {
                currCombination.add(player);
                usedPlayers.add(player);
                getCombinationsHelper(players, combinations, size, start + 1, currCombination, usedPlayers);
                usedPlayers.remove(player);
                currCombination.remove(currCombination.size() - 1);
            }
        }
    }

    private static List<List<PlayerName>> getCombinations(PlayerName[] players, int size) {
        List<List<PlayerName>> combinations = new ArrayList<>();
        List<PlayerName> currCombination = new ArrayList<>();
        Set<PlayerName> usedPlayers = new HashSet<>();
        getCombinationsHelper(players, combinations, size, 0, currCombination, usedPlayers);
        return combinations;
    }

}
