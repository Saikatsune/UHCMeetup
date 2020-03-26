package net.saikatsune.meetup.gamestate.states;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.GameState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class IngameState extends GameState {

    private Game game = Game.getInstance();

    public void start() {

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            allPlayers.getInventory().clear();

            if(game.getPlayers().contains(allPlayers)) {
                allPlayers.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 127));
                allPlayers.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, -5));

                game.getLoggedPlayers().add(allPlayers.getUniqueId());
            }
        }

        game.getGameManager().scatterPlayers();

        Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "All players have been scattered.");
        Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The game starts in " + game.getmColor() +
                "10 " + game.getsColor() + "seconds.");

        for (Player allPlayers : game.getPlayers()) {
            game.getGameManager().equipPlayerRandomly(allPlayers);
        }

        game.setStartedWith(game.getPlayers().size());

        new BukkitRunnable() {
            @Override
            public void run() {
                game.getTimeTask().startTask();

                Bukkit.broadcastMessage(game.getPrefix() + ChatColor.GREEN + "The game has started. Good Luck!");

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
        }.runTaskLater(game, 10 * 20);
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
