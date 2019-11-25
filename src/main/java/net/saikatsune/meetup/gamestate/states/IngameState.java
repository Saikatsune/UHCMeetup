package net.saikatsune.meetup.gamestate.states;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.GameState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class IngameState extends GameState {

    private Game game = Game.getInstance();

    public void start() {

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            allPlayers.getInventory().clear();
        }

        game.getGameManager().scatterPlayers();

        game.getTimeTask().startTask();

        for (Player allPlayers : game.getPlayers()) {
            game.getGameManager().equipPlayerRandomly(allPlayers);
        }

        Bukkit.broadcastMessage(game.getPrefix() + ChatColor.GREEN + "The game has started. Good Luck!");

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            game.getScoreboardManager().createScoreboard(allPlayers);
        }

        game.getGameManager().activateScenarios();

        if(Scenarios.Soup.isEnabled()) {
            for (Player allPlayers : game.getPlayers()) {
                allPlayers.getInventory().addItem(new ItemHandler(Material.BROWN_MUSHROOM).setAmount(32).build());
                allPlayers.getInventory().addItem(new ItemHandler(Material.RED_MUSHROOM).setAmount(32).build());
                allPlayers.getInventory().addItem(new ItemHandler(Material.BOWL).setAmount(32).build());
            }
        }

        if(game.getGameManager().getWonScenarios().contains(Scenarios.Bowless)) {
            for (Player allPlayers : game.getPlayers()) {
                allPlayers.getInventory().remove(Material.BOW);
            }
        } else if(game.getGameManager().getWonScenarios().contains(Scenarios.Rodless)) {
            for (Player allPlayers : game.getPlayers()) {
                allPlayers.getInventory().remove(Material.FISHING_ROD);
            }
        }

        Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink " +
                "to " + game.getmColor() + getNextBorder() + " blocks" + game.getsColor() + " in " +
                game.getmColor() + game.getTimeTask().getFirstShrink() + " minutes" + game.getsColor() + ".");
    }

    private int getNextBorder() {
        if(game.getGameManager().getBorderSize() > 100) {
            return 100;
        } else if(game.getGameManager().getBorderSize() == 100) {
            return 75;
        }
        return 0;
    }

    public void stop() {

    }
}
