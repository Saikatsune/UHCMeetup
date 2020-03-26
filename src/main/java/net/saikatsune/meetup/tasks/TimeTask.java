package net.saikatsune.meetup.tasks;

import net.saikatsune.meetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeTask {

    private Game game = Game.getInstance();

    private int taskID;

    private int uptimeMinutes;
    private int uptimeSeconds;
    private int borderMinutes;
    private int firstShrink = 2;

    private boolean running = false;

    private int getNextBorder() {
        if(game.getGameManager().getBorderSize() > 100) {
            return 100;
        } else if(game.getGameManager().getBorderSize() == 100) {
            return 75;
        } else if(game.getGameManager().getBorderSize() == 75) {
            return 50;
        } else if(game.getGameManager().getBorderSize() == 50) {
            return 25;
        }
        return 0;
    }

    public void startTask() {
        running = true;

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(game, new BukkitRunnable() {
            @Override
            public void run() {
                uptimeSeconds++;

                if (uptimeSeconds == 60) {
                    uptimeSeconds = 0;
                    uptimeMinutes += 1;
                    borderMinutes += 1;
                }

                if (game.getGameManager().getBorderSize() > 25) {
                    if(borderMinutes == firstShrink - 1 || borderMinutes == firstShrink || borderMinutes == firstShrink + 1
                        || borderMinutes == firstShrink + 2) {
                        switch (uptimeSeconds) {
                            case 30:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 30 seconds.");
                                break;
                            case 40:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 20 seconds.");
                                break;
                            case 50:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 10 seconds.");
                                break;
                            case 51:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 9 seconds.");
                                break;
                            case 52:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 8 seconds.");
                                break;
                            case 53:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 7 seconds.");
                                break;
                            case 54:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 6 seconds.");
                                break;
                            case 55:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 5 seconds.");
                                break;
                            case 56:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 4 seconds.");
                                break;
                            case 57:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 3 seconds.");
                                break;
                            case 58:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 2 seconds.");
                                break;
                            case 59:
                                game.getGameManager().playSound();
                                Bukkit.broadcastMessage(game.getPrefix() + game.getsColor() + "The border is going to shrink to " + getNextBorder() + "x" + getNextBorder() +
                                        " blocks in 1 second.");
                                break;
                        }
                    }

                    if(borderMinutes == firstShrink || borderMinutes == firstShrink + 1 || borderMinutes == firstShrink + 2
                            || borderMinutes == firstShrink + 3) {
                        if(uptimeSeconds == 0) {
                            game.getWorldManager().createTotalShrink();
                        }
                    }

                }
            }
        }, 0, 20);
    }

    public void stopTask() {
        running = false;

        Bukkit.getScheduler().cancelTask(taskID);
    }

    public String getFormattedTime() {
        String formattedTime = "";

        if (uptimeMinutes < 10)
            formattedTime += "0";
        formattedTime += uptimeMinutes + ":";

        if (uptimeSeconds < 10)
            formattedTime += "0";
        formattedTime += uptimeSeconds;

        return formattedTime;
    }

    public int getBorderSize() {
        return firstShrink;
    }

    public int getFirstShrink() {
        return firstShrink;
    }

    public boolean isRunning() {
        return running;
    }
}