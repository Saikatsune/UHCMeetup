package net.saikatsune.meetup.manager;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.PlayerState;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.GameState;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.handler.ItemHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class GameManager {

    private Game game = Game.getInstance();

    public ArrayList<Scenarios> wonScenarios = new ArrayList<>();

    private int borderSize = game.getConfig().getInt("GAME.MAP-RADIUS");

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public void resetPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setExp(0);
    }

    public void playSound() {
        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            allPlayers.playSound(allPlayers.getLocation(), Sound.ORB_PICKUP, 1, 1);
        }
    }
    
    public void checkWinner() {
        if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            if(game.getPlayers().size() == 1) {
                game.getGameStateManager().setGameState(GameState.ENDING);

                for (Player allPlayers : game.getPlayers()) {
                    Bukkit.broadcastMessage(game.getPrefix() + game.getmColor() + "Congratulations to " + allPlayers.getName() + " for winning this game!");

                    if(game.isDatabaseActive()) {
                        game.getDatabaseManager().addWins(allPlayers, 1);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            allPlayers.getWorld().spawn(allPlayers.getLocation(), Firework.class);
                        }
                    }.runTaskTimer(game, 0, 20);
                }

                Bukkit.broadcastMessage(game.getPrefix() + ChatColor.RED + "The server restarts in 20 seconds!");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }.runTaskLater(game, 20 * 20) ;
            }
        }
    }

    public void setPlayerState(Player player, PlayerState playerState) {
        game.getPlayerState().put(player, playerState);

        if(playerState == PlayerState.PLAYER) {
            game.getPlayers().add(player);
            game.getSpectators().remove(player);

            player.setGameMode(GameMode.SURVIVAL);

            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                allPlayers.showPlayer(player);
            }
        } else if(playerState == PlayerState.SPECTATOR) {
            game.getSpectators().add(player);
            game.getPlayers().remove(player);
            player.teleport(new Location(Bukkit.getWorld("uhc_meetup"), 0, 100, 0));

            this.resetPlayer(player);


            player.setGameMode(GameMode.CREATIVE);

            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                allPlayers.hidePlayer(player);
            }

            if(player.hasPermission("meetup.staff")) {
                game.getInventoryHandler().handleStaffInventory(player);
            } else {
                player.getInventory().setItem(0, new ItemHandler(Material.WATCH).setDisplayName(game.getmColor() + "Players").build());
                player.getInventory().setItem(1, new ItemHandler(Material.BEACON).setDisplayName(game.getmColor() + "Random Player").build());
            }

            player.sendMessage(game.getPrefix() + ChatColor.YELLOW + "You are now spectating the game!");
        }
    }

    public void scatterPlayers() {
        for (Player allPlayers : game.getPlayers()) {
            int x = new Random().nextInt(game.getConfig().getInt("GAME.MAP-RADIUS") - 1);
            int z = new Random().nextInt(game.getConfig().getInt("GAME.MAP-RADIUS") - 1);
            int y = Bukkit.getWorld("uhc_meetup").getHighestBlockYAt(x, z) + 2;

            Location teleportLocation = new Location(Bukkit.getWorld("uhc_meetup"), x, y, z);

            allPlayers.teleport(teleportLocation);
        }
    }

    public void activateScenarios() {
        List<Integer> noScenario, bowlessScenario, noCleanScenario,
                             rodlessScenario, firelessScenario, timeBombScenario,
                             soupScenario;

        noScenario = new ArrayList<>();
        bowlessScenario = new ArrayList<>();
        noCleanScenario = new ArrayList<>();
        rodlessScenario = new ArrayList<>();
        firelessScenario = new ArrayList<>();
        timeBombScenario = new ArrayList<>();
        soupScenario = new ArrayList<>();
        
        for (Player allPlayers : game.getPlayers()) {
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Default) {
                noScenario.add(Scenarios.Default.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Bowless) {
                bowlessScenario.add(Scenarios.Bowless.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.NoClean) {
                noCleanScenario.add(Scenarios.NoClean.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Rodless) {
                rodlessScenario.add(Scenarios.Rodless.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Fireless) {
                firelessScenario.add(Scenarios.Fireless.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.TimeBomb) {
                timeBombScenario.add(Scenarios.TimeBomb.getVotes());
            }
            if(game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Soup) {
                soupScenario.add(Scenarios.Soup.getVotes());
            }
        }

        //Default
        if(noScenario.size() > bowlessScenario.size() && noScenario.size() > noCleanScenario.size()
                && noScenario.size() > rodlessScenario.size() && noScenario.size() > firelessScenario.size()
                && noScenario.size() > timeBombScenario.size() && noScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.Default);
        }
        //Bowless
        if(bowlessScenario.size() > noScenario.size() && bowlessScenario.size() > noCleanScenario.size()
                && bowlessScenario.size() > rodlessScenario.size() && bowlessScenario.size() > firelessScenario.size()
                && bowlessScenario.size() > timeBombScenario.size() && bowlessScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.Bowless);
        }
        //NoClean
        if(noCleanScenario.size() > noScenario.size() && noCleanScenario.size() > bowlessScenario.size()
                && noCleanScenario.size() > rodlessScenario.size() && noCleanScenario.size() > firelessScenario.size()
                && noCleanScenario.size() > timeBombScenario.size() && noCleanScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.NoClean);
        }
        //Rodless
        if(rodlessScenario.size() > bowlessScenario.size() && rodlessScenario.size() > noCleanScenario.size()
                && rodlessScenario.size() > noScenario.size() && rodlessScenario.size() > firelessScenario.size()
                && rodlessScenario.size() > timeBombScenario.size() && rodlessScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.Rodless);
        }
        //Fireless
        if(firelessScenario.size() > bowlessScenario.size() && firelessScenario.size() > noCleanScenario.size()
                && firelessScenario.size() > rodlessScenario.size() && firelessScenario.size() > noScenario.size()
                && firelessScenario.size() > timeBombScenario.size() && firelessScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.Fireless);
        }
        //TimeBomb
        if(timeBombScenario.size() > bowlessScenario.size() && timeBombScenario.size() > noCleanScenario.size()
                && timeBombScenario.size() > rodlessScenario.size() && timeBombScenario.size() > firelessScenario.size()
                && timeBombScenario.size() > noScenario.size() && timeBombScenario.size() > soupScenario.size()) {
            wonScenarios.add(Scenarios.TimeBomb);
        }
        //Soup
        if(soupScenario.size() > bowlessScenario.size() && soupScenario.size() > noCleanScenario.size()
                && soupScenario.size() > rodlessScenario.size() && soupScenario.size() > firelessScenario.size()
                && soupScenario.size() > timeBombScenario.size() && soupScenario.size() > noScenario.size()) {
            wonScenarios.add(Scenarios.Soup);
        }
        //Scenarios Activation
        List<String> scenariosToString = new ArrayList<>();

        for (Scenarios votedScenarios : Scenarios.values()) {
            if(!wonScenarios.isEmpty()) {
                if(wonScenarios.contains(votedScenarios)) {
                    votedScenarios.setEnabled(true);
                    scenariosToString.add(wonScenarios.toString());
                }
            } else {
                wonScenarios.add(Scenarios.Default);
                votedScenarios.setEnabled(true);
                scenariosToString.add(wonScenarios.toString());
            }
        }

        String scenarioInString = wonScenarios.toString().replaceAll("(^\\[|\\]$)", "");

        Bukkit.broadcastMessage(game.getPrefix() + ChatColor.GRAY + "The voted scenario is " +
                game.getmColor() + scenarioInString + ChatColor.GRAY + ".");
    }

    public Player getRandomPlayer() {
        int playerNumber = new Random().nextInt(game.getPlayers().size());
        return (Player) game.getPlayers().toArray()[playerNumber];
    }

    public ArrayList<Scenarios> getWonScenarios() {
        return wonScenarios;
    }

    public void equipPlayerRandomly(Player player) {
        this.randomizeArmor(player);

        player.getInventory().setItem(0, getRandomSword());
        player.getInventory().setItem(1, getRandomBow());
        player.getInventory().setItem(2, new ItemHandler(Material.FISHING_ROD).build());
        player.getInventory().setItem(3, getRandomSecondary());
        player.getInventory().addItem(new ItemHandler(Material.WOOD).setAmount(64).build());
        player.getInventory().addItem(new ItemHandler(Material.COBBLESTONE).setAmount(64).build());
        player.getInventory().addItem(new ItemHandler(Material.GOLDEN_APPLE).setAmount(8).build());
        player.getInventory().addItem(new ItemHandler(Material.GOLDEN_APPLE).setAmount(2).setDisplayName(ChatColor.GOLD + "Golden Head").build());
        player.getInventory().addItem(new ItemHandler(Material.DIAMOND_PICKAXE).build());
        player.getInventory().addItem(new ItemHandler(Material.DIAMOND_AXE).build());
        player.getInventory().addItem(new ItemHandler(Material.WATER_BUCKET).build());
        player.getInventory().addItem(new ItemHandler(Material.WATER_BUCKET).build());
        player.getInventory().addItem(new ItemHandler(Material.LAVA_BUCKET).build());
        player.getInventory().addItem(new ItemHandler(Material.LAVA_BUCKET).build());
        player.getInventory().addItem(new ItemHandler(Material.COOKED_BEEF).setAmount(32).build());
        player.getInventory().addItem(new ItemHandler(Material.ARROW).setAmount(32).build());
        player.getInventory().addItem(new ItemHandler(Material.ANVIL).build());
    }

    private void randomizeArmor(Player player) {
        player.getInventory().setHelmet(getRandomHelmet());
        player.getInventory().setChestplate(getRandomChestplate());
        player.getInventory().setLeggings(getRandomLegs());
        player.getInventory().setBoots(getRandomBoots());
    }

    private ItemStack getRandomHelmet() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 2.0D + 1.0D);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_HELMET);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_HELMET);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        return item;
    }

    private ItemStack getRandomChestplate() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 3.0D + 1.0D);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        }
        return item;
    }

    private ItemStack getRandomLegs() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 3.0D + 1.0D);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        }
        return item;
    }

    private ItemStack getRandomBoots() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 2.0D + 1.0D);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_BOOTS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_BOOTS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        return item;
    }

    private ItemStack getRandomSword() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 4.0D) + 1;
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        }
        if (rand == 4) {
            item = new ItemStack(Material.IRON_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
        }
        return item;
    }

    private ItemStack getRandomBow() {
        ItemStack item = null;
        int rand = (int)(Math.random() * 5.0D) + 1;
        if (rand == 1) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        if (rand == 2) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        }
        if (rand == 4) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        }
        if (rand == 5) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        return item;
    }

    private ItemStack getRandomSecondary() {
        ItemStack item = null;
        int randomInt = (int)(Math.random() * 3.0D + 1.0D);
        if (randomInt == 1) {
            Random random = new Random();
            int randomNum = random.nextInt(5) + 5;
            item = new ItemStack(Material.WEB, randomNum);
        }
        if (randomInt == 2) {
            item = new ItemStack(Material.FLINT_AND_STEEL);
        }
        if (randomInt == 3) {
            Random random = new Random();
            item = new ItemStack(Material.ENDER_PEARL, 2);
        }
        return item;
    }

    public int getBorderSize() {
        return borderSize;
    }
}
