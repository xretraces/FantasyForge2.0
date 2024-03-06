package FantasyForge;

// ProjectedPick.java
public abstract class ProjectedPick {
    protected String playerName;
    protected int playerOdds;

    public ProjectedPick(String playerName, int playerOdds) {
        this.playerName = playerName;
        this.playerOdds = playerOdds;
    }

    public abstract void execute();
}