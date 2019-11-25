package net.saikatsune.meetup.handler;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryHandler {

    private Game game = Game.getInstance();

    private void fillEmptySlots(Inventory inventory) {
        for(int slot = 0; slot < inventory.getSize(); slot++) {
            if(inventory.getItem(slot) == null) {
                inventory.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE));
            }
        }
    }

    public void handleVotingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Scenario Voting");

        inventory.setItem(1, new ItemHandler(Material.TORCH).setDisplayName(game.getmColor() + "No Gamemodes").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Default.getVotes(), "§7§m------------").build());

        inventory.setItem(2, new ItemHandler(Material.FISHING_ROD).setDisplayName(game.getmColor() + "Rodless").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Rodless.getVotes(), "§7§m------------").build());

        inventory.setItem(3, new ItemHandler(Material.BOW).setDisplayName(game.getmColor() + "Bowless").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Bowless.getVotes(), "§7§m------------").build());

        inventory.setItem(4, new ItemHandler(Material.DIAMOND_SWORD).setDisplayName(game.getmColor() + "NoClean").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.NoClean.getVotes(), "§7§m------------").build());

        inventory.setItem(5, new ItemHandler(Material.FIRE).setDisplayName(game.getmColor() + "Fireless").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Fireless.getVotes(), "§7§m------------").build());

        inventory.setItem(6, new ItemHandler(Material.TNT).setDisplayName(game.getmColor() + "TimeBomb").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.TimeBomb.getVotes(), "§7§m------------").build());

        inventory.setItem(7, new ItemHandler(Material.MUSHROOM_SOUP).setDisplayName(game.getmColor() + "Soup").
                setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Soup.getVotes(), "§7§m------------").build());

        this.fillEmptySlots(inventory);

        player.openInventory(inventory);
    }

    public void handleStatsInventory(Player player, OfflinePlayer toWatch) {
        Inventory inventory = Bukkit.createInventory(null, 9*1, game.getsColor() + "Stats: " + game.getmColor() + toWatch.getName());

        inventory.setItem(2, new ItemHandler(Material.IRON_SWORD).setDisplayName(game.getsColor() + "Kills: " + game.getmColor() +
                game.getDatabaseManager().getKills(toWatch)).build());

        inventory.setItem(4, new ItemHandler(Material.FIREBALL).setDisplayName(game.getsColor() + "Deaths: " + game.getmColor() +
                game.getDatabaseManager().getDeaths(toWatch)).build());

        inventory.setItem(6, new ItemHandler(Material.NETHER_STAR).setDisplayName(game.getsColor() + "Wins: " + game.getmColor() +
                game.getDatabaseManager().getWins(toWatch)).build());

        this.fillEmptySlots(inventory);

        player.openInventory(inventory);
    }

    public void handleStaffInventory(Player player) {
        Inventory inventory = player.getInventory();

        inventory.setItem(0, new ItemHandler(Material.WATCH).setDisplayName(game.getmColor() + "Players").build());
        inventory.setItem(1, new ItemHandler(Material.BEACON).setDisplayName(game.getmColor() + "Random Player").build());

        inventory.setItem(4, new ItemHandler(Material.NETHER_STAR).setDisplayName(game.getmColor() + "Teleport to Center").build());

        inventory.setItem(8, new ItemHandler(Material.BOOK).setDisplayName(game.getmColor() + "Inspect Inventory").build());
    }

    public void handlePlayersInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 6*9, game.getmColor() + "Alive Players");

        for (Player allPlayers : game.getPlayers()) {
            ItemStack playerStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta playersMeta = (SkullMeta) playerStack.getItemMeta();
            playersMeta.setOwner(allPlayers.getName());
            playersMeta.setDisplayName(allPlayers.getName());
            playerStack.setItemMeta(playersMeta);
            inventory.addItem(playerStack);
        }

        player.openInventory(inventory);
    }

}
