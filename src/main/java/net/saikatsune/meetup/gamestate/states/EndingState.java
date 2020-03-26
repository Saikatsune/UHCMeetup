package net.saikatsune.meetup.gamestate.states;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.gamestate.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingState extends GameState {

    private Game game = Game.getInstance();

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                game.getTimeTask().stopTask();
            }
        }.runTaskLater(game, 20);
    }

    public void stop() {

    }
}
