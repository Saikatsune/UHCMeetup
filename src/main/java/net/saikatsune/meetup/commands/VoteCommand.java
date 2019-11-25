package net.saikatsune.meetup.commands;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VoteCommand implements CommandExecutor, Listener {

    private Game game = Game.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("vote")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    game.getInventoryHandler().handleVotingInventory(player);
                } else {
                    player.sendMessage(game.getPrefix() + ChatColor.RED + "The game has already started!");
                }
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Scenarios votedScenario;

        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            if(event.getClickedInventory() != null) {
                if(event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    if(!game.getVoted().containsKey(player.getUniqueId())) {
                        if(event.getCurrentItem().getType() == Material.TORCH) {
                            votedScenario = Scenarios.Default;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.FISHING_ROD) {
                            votedScenario = Scenarios.Rodless;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.BOW) {
                            votedScenario = Scenarios.Bowless;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.TNT) {
                            votedScenario = Scenarios.TimeBomb;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.FIRE) {
                            votedScenario = Scenarios.Fireless;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
                            votedScenario = Scenarios.NoClean;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.MUSHROOM_SOUP) {
                            votedScenario = Scenarios.Soup;
                            game.getVoted().put(player.getUniqueId(), votedScenario);
                            votedScenario.addVote();
                        } else if(event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                            event.setCancelled(true);
                        }

                        player.closeInventory();

                        try {
                            player.sendMessage(game.getPrefix() + ChatColor.GRAY + "You have voted for: " +
                                    ChatColor.YELLOW + game.getVoted().get(player.getUniqueId()) + " " +  ChatColor.GRAY +
                                    "(Total votes: " + game.getVoted().get(player.getUniqueId()).getVotes() + ")");
                        } catch (Exception exception) {

                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(game.getPrefix() + ChatColor.RED + "You have already voted for: " +
                                ChatColor.YELLOW + game.getVoted().get(player.getUniqueId()));
                    }
                }
            }
        }
    }
}
