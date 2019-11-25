package net.saikatsune.meetup.listener.scenarios;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.IngameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FirelessListener implements Listener {

    private Game game = Game.getInstance();

    @EventHandler
    public void handleEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {

            if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                if(Scenarios.Fireless.isEnabled()) {
                    if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                            event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
