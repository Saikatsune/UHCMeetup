package net.saikatsune.meetup.listener.scenarios;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeBombListener implements Listener {
    
    private Game game = Game.getInstance();

    private String prefix = game.getPrefix();

    private String mColor = game.getmColor();
    private String sColor = game.getsColor();

    @EventHandler
    public void handlePlayerDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Location location = player.getLocation();
        Inventory inventory = player.getInventory();

        if (!Scenarios.TimeBomb.isEnabled()) {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.GOLDEN_APPLE).setDisplayName(ChatColor.GOLD + "Golden Head").build());
        } else {
            if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                event.getDrops().clear();
                player.getLocation().getBlock().breakNaturally();
                player.getLocation().getBlock().setType(Material.CHEST);
                player.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().breakNaturally();
                player.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().setType(Material.CHEST);
                player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);
                player.getLocation().add(1.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);
                Chest chest = (Chest) player.getLocation().getBlock().getState();


                chest.getInventory().setContents(inventory.getContents());
                chest.getInventory().addItem(player.getInventory().getArmorContents());
                chest.getInventory().addItem(new ItemHandler(Material.GOLDEN_APPLE).setDisplayName(ChatColor.GOLD + "Golden Head").build());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        location.getWorld().createExplosion(location, 6.0F);
                        location.getWorld().strikeLightning(location);
                        Bukkit.broadcastMessage(prefix + mColor + "[TimeBomb] " + player.getName() + "'s " + sColor + "corpse has exploded!");
                    }
                }.runTaskLater(game, 30 * 20);
            }
        }
    }
    
}
