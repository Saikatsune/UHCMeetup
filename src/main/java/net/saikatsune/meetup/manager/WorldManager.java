package net.saikatsune.meetup.manager;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.handler.FileHandler;;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Consumer;

public class WorldManager {

    private Game game = Game.getInstance();

    public void createWorld(String worldName, World.Environment environment, WorldType worldType) {
        World world = Bukkit.createWorld(new WorldCreator(worldName).environment(environment).type(worldType));
        world.setDifficulty(Difficulty.EASY);
        world.setTime(0);
        world.setThundering(false);
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
    }

    public void loadWorld(String worldName, int worldRadius, int loadingSpeed) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldName + " set " + worldRadius + " " + worldRadius + " 0 0");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldName + " fill " + loadingSpeed);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldName + " fill confirm");
    }

    public void shrinkBorder(String worldName, int size) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + worldName + " set " + size + " " + size + " 0 0");
    }

    public void createTotalShrink() {
        if(game.getGameManager().getBorderSize() > 100) {
            game.getGameManager().setBorderSize(100);
            this.shrinkBorder("uhc_meetup", 100);
            this.createBorderLayer("uhc_meetup", 100, 4, null);
            Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going" +
                    " to shrink in " + game.getmColor() + 1 + " minute.");
        } else if(game.getGameManager().getBorderSize() == 100) {
            game.getGameManager().setBorderSize(75);
            this.shrinkBorder("uhc_meetup", 75);
            this.createBorderLayer("uhc_meetup", 75, 4, null);
            Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going" +
                    " to shrink in " + game.getmColor() + 1 + " minute.");
        } else if(game.getGameManager().getBorderSize() == 75) {
            game.getGameManager().setBorderSize(50);
            this.shrinkBorder("uhc_meetup", 50);
            this.createBorderLayer("uhc_meetup", 50, 4, null);
            Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going" +
                    " to shrink in " + game.getmColor() + 1 + " minute.");
        } else if(game.getGameManager().getBorderSize() == 50) {
            game.getGameManager().setBorderSize(25);
            this.shrinkBorder("uhc_meetup", 25);
            this.createBorderLayer("uhc_meetup", 25, 4, null);
        }

        Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border has shrunken to " +
                game.getmColor() + game.getGameManager().getBorderSize() + " blocks" + game.getsColor() + ".");
        game.getGameManager().playSound();
    }

    public void createBorderLayer(String borderWorld, int radius, int amount, Consumer<String> done) {
        World world = Bukkit.getWorld(borderWorld);
        if (world == null) return;
        LinkedList<Location> locations = new LinkedList<>();

        for (int i = 0; i < amount; i++) {
            for (int z = -radius; z <= radius; z++) {
                Location location = new Location(world, radius, world.getHighestBlockYAt(radius, z) + i, z);
                locations.add(location);
            }
            for (int z = -radius; z <= radius; z++) {
                Location location = new Location(world, -radius, world.getHighestBlockYAt(-radius, z) + i, z);
                locations.add(location);
            }
            for (int x = -radius; x <= radius; x++) {
                Location location = new Location(world, x, world.getHighestBlockYAt(x, radius) + i, radius);
                locations.add(location);
            }
            for (int x = -radius; x <= radius; x++) {
                Location location = new Location(world, x, world.getHighestBlockYAt(x, -radius) + i, -radius);
                locations.add(location);
            }
        }

        new BukkitRunnable() {

            private int max = 50;
            @Override
            public void run() {
                for (int i = 0; i < max; i++) {
                    if (locations.isEmpty()) {
                        if (done != null) done.accept("done");
                        this.cancel();
                        break;
                    }
                    locations.remove().getBlock().setType(Material.BEDROCK);
                }
            }
        }.runTaskTimer(game, 0, 1);
    }

    public void unloadWorld(World worldName) {
        if(worldName != null) {
            Bukkit.unloadWorld(worldName, false);
        }
    }

    public void deleteWorld(String worldName) {
        this.unloadWorld(Bukkit.getWorld(worldName));
        this.deleteFiles(new File(Bukkit.getWorldContainer(), worldName));
    }

    private boolean deleteFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

}
