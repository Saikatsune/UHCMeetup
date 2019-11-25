package net.saikatsune.meetup.listener;

import me.uhc.worldborder.Events.WorldBorderFillFinishedEvent;
import net.saikatsune.meetup.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChunkListener implements Listener {

    private Game game = Game.getInstance();

    @EventHandler
    public void handleChunkLoadingEvent(WorldBorderFillFinishedEvent event) {
        game.setPreparing(false);
    }

}
