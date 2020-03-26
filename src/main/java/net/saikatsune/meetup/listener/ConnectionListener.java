package net.saikatsune.meetup.listener;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.PlayerState;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.EndingState;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ConnectionListener implements Listener {

    private Game game = Game.getInstance();

    private int minimalPlayers = game.getConfig().getInt("GAME.MIN-PLAYERS");

    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        if(game.isDatabaseActive()) {
            game.getDatabaseManager().registerPlayer(player);
        }

        game.getPlayerKills().putIfAbsent(player.getUniqueId(), 0);

        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            game.getGameManager().setPlayerState(player, PlayerState.PLAYER);

            event.setJoinMessage(game.getPrefix() + ChatColor.GREEN + player.getName() +
                    " has joined the game. " + ChatColor.GRAY + "(" + game.getPlayers().size() +
                    "/" + Bukkit.getMaxPlayers() + ")");

            try {
                player.teleport(game.getLocationManager().getLocation("Lobby-Spawn"));
            } catch (Exception exception) {
                player.sendMessage(game.getPrefix() + ChatColor.RED + "The lobby-spawn has not been set yet. " +
                        "Contact an admin to solve this problem.");
            }

            game.getGameManager().resetPlayer(player);

            if(game.getPlayers().size() >= minimalPlayers) {
                if(!game.getStartingTask().isRunning()) {
                    game.getStartingTask().startTask();
                }
            }

            player.getInventory().setItem(4, new ItemHandler(Material.PAPER).setDisplayName(game.getmColor() +
                    "§lScenario Voting").build());

            player.sendMessage(game.getPrefix() + game.getsColor() + "Use " + game.getmColor() + "/vote " +
                    game.getsColor() + "to vote for a scenario for this game.");
        } else if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            game.getGameManager().setPlayerState(player, PlayerState.SPECTATOR);

            event.setJoinMessage("");
        }
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        game.getPlayers().remove(player);
        game.getSpectators().remove(player);

        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {

            event.setQuitMessage(game.getPrefix() + ChatColor.RED + player.getName() +
                    " has left the game. " + ChatColor.GRAY + "(" + game.getPlayers().size() +
                    "/" + Bukkit.getMaxPlayers() + ")");

            if(game.getVoted().containsKey(player.getUniqueId())) {
                Scenarios votedScenario = game.getVoted().get(player.getUniqueId());
                votedScenario.removeVote();
                game.getVoted().remove(player.getUniqueId());
            }

            if(game.getPlayers().size() < minimalPlayers) {
                if(game.getStartingTask().isRunning()) {
                    game.getStartingTask().stopTask();
                }
            }
        } else if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            event.setQuitMessage("");

            if(game.getLoggedPlayers().contains(player.getUniqueId())) {

                player.damage(24);
                game.getLoggedPlayers().remove(player.getUniqueId());
            }

            game.getGameManager().checkWinner();
        }
    }

    @EventHandler
    public void handlePlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if(game.getGameStateManager().getCurrentGameState() instanceof EndingState) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED +
                    "The game is already about to stop.");
        } else if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            if(!player.hasPermission("meetup.staff")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED +
                        "The game has already begun.");
            }
        } else if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            if(game.isPreparing()) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED +
                        "The game is currently preparing.");
            }
        }
    }

}
