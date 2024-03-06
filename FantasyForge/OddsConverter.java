package FantasyForge;

public class OddsConverter {

    // Conversion methods
    public static double decimalToAmerican(double decimalOdds) {
        return decimalOdds >= 2.0 ? (decimalOdds - 1.0) * 100.0 : -100.0 / (decimalOdds - 1.0);
    }

    public static double americanToDecimal(double americanOdds) {
        return americanOdds >= 0.0 ? (americanOdds / 100.0) + 1.0 : (100.0 / -americanOdds) + 1.0;
    }

    // Calculate implied probability
    public static double impliedProbability(double odds) {
        return odds >= 0.0 ? 100.0 / (odds + 100.0) : -odds / (odds - 100.0);
    }

    // Calculate hit rate percentage
    public static double hitRatePercentage(double impliedProbability) {
        return impliedProbability * 100.0;
    }

    // Calculate parlay odds
    public static double calculateParlayOdds(double[] playerOdds) {
        double multiplier = 1.0;

        // Calculate the multiplier by multiplying the decimal odds of all players
        for (double odds : playerOdds) {
            multiplier *= americanToDecimal(odds);
        }

        // Calculate the total parlay odds by subtracting 1 from the multiplier
        return multiplier - 1.0;
    }
}
