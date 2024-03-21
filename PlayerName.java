package FantasyForge;

import java.util.Scanner;

import static FantasyForge.Main.calculateHitRatePercentage;
import static FantasyForge.Main.findClosestMoneyLine;

public class PlayerName extends ProjectedPick {
    private String consideredStar;
    private int scaleFactor;
    private final String name;
    private final double score;
    private String propLine;
    private boolean hasEstimatedValue;
    private double estimatedValue;
    private double rankingScore;
    private String matchup;
    private String insights; // new field to store player insights
    private String hitRate; // new field for hit rate
    private int closestMoneyLine;
    private double impliedProbability;
    private double hitRatePercentage;


    public PlayerName(String playerName, String propLine, int playerOdds, String consideredStar, int scaleFactor, boolean hasEstimatedValue, double estimatedValue, String hitRate) {
        super(playerName, playerOdds);
        this.propLine = propLine;
        this.consideredStar = consideredStar;
        this.scaleFactor = scaleFactor;
        this.name = playerName;
        this.score = 0; // No need to calculate score here
        this.hasEstimatedValue = hasEstimatedValue;
        this.estimatedValue = estimatedValue;
        this.hitRate = hitRate;
    }


    // Getters and setters

    @Override
    public String toString() {
        return name; // Assuming 'name' is the player's name field in your PlayerName class
    }

    public String getPropLine() {
        return propLine;
    }

    public double getScore() {
        return score;
    }

    public boolean hasEstimatedValue() {
        return hasEstimatedValue;
    }

    public int getEstimatedValue() {
        return (int) estimatedValue;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerOdds() {
        return playerOdds;
    }

    public String getConsideredStar() {
        return consideredStar;
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public double getRankingScore() {
        return rankingScore;
    }

    public void setMatchup(String matchup) {
        this.matchup = matchup;
    }

    public void setRankingScore(double rankingScore) {
        this.rankingScore = rankingScore;
    }

    // New methods for odds calculation
    public int getClosestMoneyLine() {
        return closestMoneyLine;
    }

    public void setClosestMoneyLine(int closestMoneyLine) {
        this.closestMoneyLine = closestMoneyLine;
    }

    public double getImpliedProbability() {
        return impliedProbability;
    }

    public void setImpliedProbability(double impliedProbability) {
        this.impliedProbability = impliedProbability;
    }

    public double getHitRatePercentage() {
        return hitRatePercentage;
    }

    public void setHitRatePercentage(double hitRatePercentage) {
        this.hitRatePercentage = hitRatePercentage;
    }

    public void calculateOdds(Odds[] oddsArray) {
        this.closestMoneyLine = findClosestMoneyLine(this.playerOdds, oddsArray);
        this.impliedProbability = OddsConverter.impliedProbability(this.closestMoneyLine);
        this.hitRatePercentage = calculateHitRatePercentage(this.impliedProbability);
    }

    public void enterInsights() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter insights for " + name + ":");
        insights = input.nextLine();
    }

    public String getInsights() {
        return insights;
    }

    public String getMatchup() {
        return matchup;
    }

    public String getHitRate() {
        return hitRate;
    }

    // Method to abbreviate specific phrases in the prop line
    public static String abbreviatedPropLine(String propLine) {
        // Define mappings of phrases to abbreviations
        String[][] mappings = {
                {"Points", "PTS"},
                {"Rebounds", "REB"},
                {"Assists", "AST"},
                {"Steals", "STL"},
                {"Blocks", "BLK"},
                {"Blocks + Steals", "Blks + Stls"},
                {"Field Goals Made", "FGM"},
                {"Field Goals Attempted", "FGA"},
                {"Free Throws Made", "FTM"},
                {"Free Throws Attempted", "FTA"},
                {"Three-Point Field Goals Made", "3-PT Made"},
                {"Three-Point Field Goals Attempted", "3-PT Att."},
                {"Turnovers", "TO"},
                {"Personal Fouls", "PF"},
                {"Rebounds + Assists", "Rebs + Ast"},
                {"Points + Assists", "Pts + Ast"},
                {"Points + Rebounds", "Pts + Rebs"},
                {"Rebounds + Assists", "Rebs + Ast"},
                {"Points + Rebounds + Assists", "Pts + Rebs + Ast"},
                {"Fantasy Score", "FS"},
                {"3's Made", "3-PT Made"},
                // Add more mappings as needed
        };


        // Replace each phrase with its abbreviation
        for (String[] mapping : mappings) {
            propLine = propLine.replace(mapping[0], mapping[1]);
        }

        return propLine;
    }



    public void execute() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter player name:");
        playerName = input.nextLine();

        // Move the propLine prompt here
        System.out.println("Enter the player's Prop Line (e.g., Points 20.5 over):");
        propLine = input.nextLine();

        System.out.println("Enter player odds:");
        playerOdds = input.nextInt();

        input.nextLine(); // consume newline character

        System.out.println("Is player considered a star? (y/n)");
        consideredStar = input.nextLine();

        System.out.println("Enter 1-5 scale factor:");
        scaleFactor = input.nextInt();

        input.nextLine(); // inputs a space character

        System.out.println("Does this player have Estimated Value? (y/n)");
        String hasEstimatedValueInput = input.nextLine();

        if (hasEstimatedValueInput.equalsIgnoreCase("y")) {
            hasEstimatedValue = true;
            System.out.println("Enter the estimated value for the player's Prop Line:");
            estimatedValue = input.nextInt();
            // Consume the newline character
            input.nextLine();
        } else {
            hasEstimatedValue = false;
            estimatedValue = 0;
        }

        System.out.println("Enter the projected matchup for the player (Good/Average/Bad):");
        matchup = input.nextLine();
        this.setMatchup(matchup); // Set the matchup field before calculating ranking score

        System.out.println("Enter " + playerName + " hit rate:");
        hitRate = input.nextLine();

        // After setting up player information, prompt user to enter insights
        enterInsights();

        //Calculate ranking score using RankingScoreCalculator class
        rankingScore = RankingScoreCalculator.calculateRankingScore(this);
        setRankingScore(rankingScore);

        //calculate insights deduction
        double insightsDeduction = RankingScoreCalculator.calculateInsightsDeduction(insights);

        // Apply deductions to the ranking score
        rankingScore -= insightsDeduction;

        System.out.println("Do you want to keep inserting picks? (y/n)");
        String answer = input.nextLine();

        // Process the user's response
        if (answer.equalsIgnoreCase("n")) {
            System.out.println("No more picks will be inserted.");
        } else if (answer.equalsIgnoreCase("y")) {
            System.out.println("Proceeding to insert more picks.");
            // You can add additional logic here if needed
        } else {
            System.out.println("Invalid input. Assuming 'n' (no) by default.");
        }
    }
}