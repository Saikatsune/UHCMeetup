package net.saikatsune.meetup.commands;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceStartCommand implements CommandExecutor {

    private Game game = Game.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("forcestart")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    if(game.getStartingTask().isRunning()) {
                        if(game.getStartingTask().getStartingTime() > 20) {
                            game.getStartingTask().setStartingTime(10);
                            player.sendMessage(game.getPrefix() + ChatColor.GREEN + "You have success" +
                                    "fully force-started the game!");
                        } else {
                            player.sendMessage(game.getPrefix() + ChatColor.RED + "The game is already starting.");
                        }
                    } else {
                        player.sendMessage(game.getPrefix() + ChatColor.RED + "There are not enough player to force-" +
                                "start the game.");
                    }
                } else {
                    player.sendMessage(game.getPrefix() + ChatColor.RED + "The game has already begun!");
                }
            }
        }
        return false;
    }
}
