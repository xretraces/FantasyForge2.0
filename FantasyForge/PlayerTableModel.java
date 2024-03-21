package FantasyForge;

import javax.swing.table.AbstractTableModel;
import java.util.List;

class PlayerTableModel extends AbstractTableModel {
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
            case 2 -> player.getPropLine();
            case 3 -> player.getPlayerOdds();
            case 4 -> player.getConsideredStar();
            case 5 -> player.getScaleFactor();
            case 6 -> player.hasEstimatedValue() ? "Yes" : "No";
            case 7 -> player.getHitRate();
            case 8 -> String.format("%.2f", player.getRankingScore());
            default -> "";
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}