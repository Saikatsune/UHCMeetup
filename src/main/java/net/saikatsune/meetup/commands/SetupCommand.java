package net.saikatsune.meetup.commands;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    private Game game = Game.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("setup")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("setspawn")) {
                        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                            game.getLocationManager().setLocation("Lobby-Spawn", player.getLocation());
                            player.sendMessage(game.getPrefix() + ChatColor.GREEN + "The lobby-spawn has been successfully set.");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /setup (setspawn)");
                }
            }
        }
        return false;
    }
}
