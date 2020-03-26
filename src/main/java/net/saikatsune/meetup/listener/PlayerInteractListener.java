package net.saikatsune.meetup.listener;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("deprecation")
public class PlayerInteractListener implements Listener {

    private Game game = Game.getInstance();

    @EventHandler
    public void handlePlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if ((event.getItem().getType() != null) &&
                (event.getItem().getType() == Material.GOLDEN_APPLE) &&
                (event.getItem().getItemMeta().getDisplayName() != null) &&
                (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head"))) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        } else if ((event.getItem().getType() != null) &&
                (event.getItem().getType() == Material.GOLDEN_APPLE)) {
            if(event.getItem().getData().getData() == 1) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handlePlayerPickupEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if(game.getSpectators().contains(player))event.setCancelled(true);

        if(!(game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else {
            if(!player.getWorld().getName().equals("uhc_meetup")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handlePlayerDropEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(game.getSpectators().contains(player))event.setCancelled(true);

        if(!(game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else {
            if(!player.getWorld().getName().equals("uhc_meetup")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(game.getSpectators().contains(player)) event.setCancelled(true);

        if(game.getSpectators().contains(player)) {
            if(player.getItemInHand().getType() == Material.NETHER_STAR) {
                if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(game.getmColor() + "Teleport to Center")) {
                    player.teleport(new Location(Bukkit.getWorld("uhc_meetup"), 0 , 100, 0));
                    player.sendMessage(game.getPrefix() + game.getsColor() + "You have been teleported to the " + game.getmColor() + "center of the map" + game.getsColor() + "!");
                }
            } else if(player.getItemInHand().getType() == Material.BEACON) {
                if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(game.getmColor() + "Random Player")) {
                    if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                        if(game.getPlayers().size() >= 2) {
                            if(game.getGameManager().getRandomPlayer() != player) {
                                player.teleport(game.getGameManager().getRandomPlayer());
                            }
                        }
                    } else {
                        player.sendMessage(game.getPrefix() + ChatColor.RED + "There is currently no game running!");
                    }
                }
            } else if(player.getItemInHand().getType() == Material.WATCH) {
                if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(game.getmColor() + "Players")) {
                    if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                        game.getInventoryHandler().handlePlayersInventory(player);
                    } else {
                        player.sendMessage(game.getPrefix() + ChatColor.RED + "There is currently no game running!");
                    }
                }
            }
        } else if(player.getItemInHand().getType() == Material.PAPER) {
            if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(game.getmColor() +
                    "§lScenario Voting")) {
                if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    game.getInventoryHandler().handleVotingInventory(player);
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerInteractAtEntityEvent(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player interacted = (Player) event.getRightClicked();

            if(player.getItemInHand().getType() == Material.BOOK) {
                if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(game.getmColor() + "Inspect Inventory")) {
                    if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                        Inventory inventory = Bukkit.createInventory(null, 54, game.getmColor() + "" + interacted.getName() + "'s Inventory");
                        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);
                        PlayerInventory playerInventory = interacted.getInventory();
                        inventory.setItem(0, pane);
                        inventory.setItem(1, pane);
                        inventory.setItem(2, playerInventory.getHelmet());
                        inventory.setItem(3, playerInventory.getChestplate());
                        inventory.setItem(4, pane);
                        inventory.setItem(5, playerInventory.getLeggings());
                        inventory.setItem(6, playerInventory.getBoots());
                        inventory.setItem(7, pane);
                        inventory.setItem(8, pane);
                        for (int i = 9; i < 45; i++)
                        {
                            int slot = i - 9;
                            inventory.setItem(i, playerInventory.getItem(slot));
                        }
                        ItemStack level = new ItemHandler(Material.EXP_BOTTLE).setDisplayName(game.getmColor() + interacted.getLevel() + " levels").build();
                        ItemStack health = new ItemHandler(Material.POTION).setDisplayName(game.getmColor() + Math.round(interacted.getHealth()) + "/" + (int) interacted.getMaxHealth()).build();
                        ItemStack head = new ItemHandler(Material.CAKE).setDisplayName(game.getmColor() + interacted.getName()).build();
                        ItemStack hunger = new ItemHandler(Material.COOKED_BEEF).setDisplayName(game.getmColor() + interacted.getFoodLevel() + "/20").build();
                        inventory.setItem(45, pane);
                        inventory.setItem(46, level);
                        inventory.setItem(47, pane);
                        inventory.setItem(48, health);
                        inventory.setItem(49, pane);
                        inventory.setItem(50, head);
                        inventory.setItem(51, pane);
                        inventory.setItem(52, hunger);
                        inventory.setItem(53, pane);
                        player.openInventory(inventory);
                    } else {
                        player.sendMessage(game.getPrefix() + ChatColor.RED + "There is currently no game running!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(game.getSpectators().contains(player)) event.setCancelled(true);

        if(event.getClickedInventory() != null) {
            if(event.getCurrentItem() != null) {
                if(event.getClickedInventory().getName().contains(game.getsColor() + "Stats")) {
                    event.setCancelled(true);
                }

                if(event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    if(game.getSpectators().contains(player)) {
                        Player target = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
                        player.teleport(target);
                    }
                }
            }
        }
    }

}
