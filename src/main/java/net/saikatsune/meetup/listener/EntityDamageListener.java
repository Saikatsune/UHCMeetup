package net.saikatsune.meetup.listener;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.PlayerState;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EntityDamageListener implements Listener {

    private Game game = Game.getInstance();

    @EventHandler
    public void handleEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();

            if(game.getSpectators().contains(player)) event.setCancelled(true);

            if(!(game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
                event.setCancelled(true);
            } else {
                if(!game.getTimeTask().isRunning()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getDamager() instanceof Player) {

                Player attacker = (Player) event.getDamager();

                if(game.getSpectators().contains(attacker)) event.setCancelled(true);

            }
        }

        if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player) {
                Player player = (Player)event.getEntity();
                if (((Player)event.getEntity()).getHealth() - event.getFinalDamage() > 0.0D) {
                    ((Player)((Arrow)event.getDamager()).getShooter()).sendMessage(game.getPrefix() + game.getmColor() + player.getName() + "'s" + game.getsColor() +
                            " health is at " + ChatColor.RED + Math.round(player.getHealth() - 1) + "❤!");
                }
            }
        }

    }

    @EventHandler
    public void handlePlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(player.getKiller() != null) {
            game.getPlayerKills().put(player.getKiller().getUniqueId(), game.getPlayerKills().get(player.getKiller().getUniqueId()) + 1);

            if(game.isDatabaseActive()) {
                game.getDatabaseManager().addKills(player.getKiller(), 1);
            }

            Player killer = player.getKiller();
            event.setDeathMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + "[" +
                    game.getPlayerKills().get(player.getUniqueId()) + "] " + ChatColor.YELLOW + "was" +
                    " slain by " + ChatColor.RED + killer.getName() + ChatColor.GRAY + "[" + game.getPlayerKills().get(killer.getUniqueId()) + "].");

            player.getKiller().getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.EXP_BOTTLE).setAmount(32).build());
            player.getKiller().getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.GOLDEN_APPLE)
                    .setDisplayName(ChatColor.GOLD + "Golden Head").build());
        } else {
            event.setDeathMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + "[" +
                    game.getPlayerKills().get(player.getUniqueId()) + "] " + ChatColor.YELLOW + "has" +
                    " died mysteriously.");
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();

                game.getGameManager().setPlayerState(player, PlayerState.SPECTATOR);

                game.getGameManager().checkWinner();

                if(game.isDatabaseActive()) {
                    game.getDatabaseManager().addDeaths(player, 1);
                }

            }
        }.runTaskLater(game, 20);

    }

    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if(game.getSpectators().contains(player)) event.setCancelled(true);

        if(!(game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else {
            if (event.getFoodLevel() < player.getFoodLevel() && (new Random())
                    .nextInt(100) > 4)
                event.setCancelled(true);
        }
    }

}
