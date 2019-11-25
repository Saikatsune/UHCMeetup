package net.saikatsune.meetup.commands;

import net.saikatsune.meetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("deprecation")
public class StatsCommand implements CommandExecutor, Listener {

    private Game game = Game.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("stats")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(game.isDatabaseActive()) {
                    if(args.length == 1) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                        if(game.getDatabaseManager().isPlayerRegistered(target)) {
                            game.getInventoryHandler().handleStatsInventory(player, target);
                        } else {
                            player.sendMessage(game.getPrefix() + ChatColor.RED + target.getName() + " is not registered in the database!");
                        }
                    } else if(args.length == 0) {
                        game.getInventoryHandler().handleStatsInventory(player, player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /stats (player)");
                    }
                } else {
                    player.sendMessage(game.getPrefix() + ChatColor.RED + "Stats are currently disabled!");
                }
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if(event.getCurrentItem() != null) {
            if(event.getClickedInventory() != null) {
                if(event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || event.getCurrentItem().getType() ==
                        Material.IRON_SWORD || event.getCurrentItem().getType() == Material.FIREBALL || event.getCurrentItem()
                        .getType() == Material.NETHER_STAR) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
