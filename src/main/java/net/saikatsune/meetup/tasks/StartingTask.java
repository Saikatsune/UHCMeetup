package net.saikatsune.meetup.tasks;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.gamestate.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask {

    private Game game = Game.getInstance();

    private int taskID;

    private boolean running;

    private int startingTime = 60;
    private int resetTime = 60;

    public void startTask() {
        running = true;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(game, new BukkitRunnable() {
            @Override
            public void run() {
                switch (startingTime) {
                    case 60: case 30: case 20: case 10: case 5: case 4: case 3: case 2:
                        Bukkit.broadcastMessage(game.getPrefix() + ChatColor.GREEN + "The scatter starts" +
                                " in " + startingTime + " seconds.");
                        game.getGameManager().playSound();
                        break;
                    case 1:
                        Bukkit.broadcastMessage(game.getPrefix() + ChatColor.GREEN + "The scatter starts" +
                                " in " + startingTime + " second.");
                        game.getGameManager().playSound();
                        break;
                    case 0:
                        game.getGameStateManager().setGameState(GameState.INGAME);
                        game.getGameManager().playSound();
                        Bukkit.getScheduler().cancelTask(taskID);
                        break;
                    default:
                        break;
                }
                startingTime--;
            }
        }, 0, 20);
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(taskID);
        startingTime = resetTime;
        running = false;
        Bukkit.broadcastMessage(game.getPrefix() + ChatColor.RED + "The scatter start has canceled due to lack of players.");
    }

    public boolean isRunning() {
        return running;
    }

    public void setStartingTime(int startingTime) {
        this.startingTime = startingTime;
    }

    public int getStartingTime() {
        return startingTime;
    }
}
