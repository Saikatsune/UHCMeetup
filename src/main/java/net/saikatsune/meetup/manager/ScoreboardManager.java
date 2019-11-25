package net.saikatsune.meetup.manager;


import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
public class ScoreboardManager {
    
    private Game game = Game.getInstance();

    private int taskID;
    
    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective("practice", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);

        changeScoreboard(player);
    }

    private void changeScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }

        Objective oldObj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        oldObj.unregister();

        if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) != null) {
            scoreboard.getObjective(DisplaySlot.BELOW_NAME).unregister();
        }

        Objective obj = scoreboard.registerNewObjective("practice", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.setDisplayName(game.getConfig().getString("SCOREBOARD.HEADER").replace("&", "§")
                .replace(">>", "»"));

        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            Team newLine = scoreboard.registerNewTeam(ChatColor.GREEN.toString());
            newLine.addEntry("§1§7§m--------");
            newLine.setSuffix("----------");
            obj.getScore("§1§7§m--------").setScore(11);

            Team players = scoreboard.registerNewTeam(ChatColor.ITALIC.toString());
            players.addEntry(game.getsColor() + "Players: ");
            players.setSuffix(game.getmColor() + game.getPlayers().size());
            obj.getScore(game.getsColor() + "Players: ").setScore(10);

            Team space1 = scoreboard.registerNewTeam(ChatColor.STRIKETHROUGH.toString());
            space1.addEntry(" ");
            obj.getScore(" ").setScore(9);

            Team none = scoreboard.registerNewTeam(ChatColor.AQUA.toString());
            none.addEntry(game.getsColor() + "Default: ");
            none.setSuffix(game.getmColor() + Scenarios.Default.getVotes());
            obj.getScore(game.getsColor() + "Default: ").setScore(8);

            Team timeBomb = scoreboard.registerNewTeam(ChatColor.RED.toString());
            timeBomb.addEntry(game.getsColor() + "TimeBomb: ");
            timeBomb.setSuffix(game.getmColor() + Scenarios.TimeBomb.getVotes());
            obj.getScore(game.getsColor() + "TimeBomb: ").setScore(7);

            Team noClean = scoreboard.registerNewTeam(ChatColor.BLUE.toString());
            noClean.addEntry(game.getsColor() + "NoClean: ");
            noClean.setSuffix(game.getmColor() + Scenarios.NoClean.getVotes());
            obj.getScore(game.getsColor() + "NoClean: ").setScore(6);

            Team fireless = scoreboard.registerNewTeam(ChatColor.MAGIC.toString());
            fireless.addEntry(game.getsColor() + "Fireless: ");
            fireless.setSuffix(game.getmColor() + Scenarios.Fireless.getVotes());
            obj.getScore(game.getsColor() + "Fireless: ").setScore(5);

            Team bowless = scoreboard.registerNewTeam(ChatColor.WHITE.toString());
            bowless.addEntry(game.getsColor() + "Bowless: ");
            bowless.setSuffix(game.getmColor() + Scenarios.Bowless.getVotes());
            obj.getScore(game.getsColor() + "Bowless: ").setScore(4);

            Team rodless = scoreboard.registerNewTeam(ChatColor.YELLOW.toString());
            rodless.addEntry(game.getsColor() + "Rodless: ");
            rodless.setSuffix(game.getmColor() + Scenarios.Rodless.getVotes());
            obj.getScore(game.getsColor() + "Rodless: ").setScore(3);

            Team soup = scoreboard.registerNewTeam(ChatColor.GRAY.toString());
            soup.addEntry(game.getsColor() + "Soup: ");
            soup.setSuffix(game.getmColor() + Scenarios.Soup.getVotes());
            obj.getScore(game.getsColor() + "Soup: ").setScore(2);

            Team footer = scoreboard.registerNewTeam(ChatColor.DARK_AQUA.toString());
            footer.addEntry("§7§m--------");
            footer.setSuffix("----------");
            obj.getScore("§7§m--------").setScore(1);
        } else if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            Team newLine = scoreboard.registerNewTeam(ChatColor.GREEN.toString());
            newLine.addEntry("§1§7§m--------");
            newLine.setSuffix("----------");
            obj.getScore("§1§7§m--------").setScore(6);

            Team gameTime = scoreboard.registerNewTeam(ChatColor.RED.toString());
            gameTime.addEntry(game.getsColor() + "Game Time: ");
            gameTime.setSuffix(game.getmColor() + game.getTimeTask().getFormattedTime());
            obj.getScore(game.getsColor() + "Game Time: ").setScore(5);

            Team remaining = scoreboard.registerNewTeam(ChatColor.BLACK.toString());
            remaining.addEntry(game.getsColor() + "Remaining: ");
            remaining.setSuffix(game.getmColor() + game.getPlayers().size());
            obj.getScore(game.getsColor() + "Remaining: ").setScore(4);

            Team kills = scoreboard.registerNewTeam(ChatColor.GOLD.toString());
            kills.addEntry(game.getsColor() + "Kills: ");
            kills.setSuffix(game.getmColor() + game.getPlayerKills().get(player.getUniqueId()));
            obj.getScore(game.getsColor() + "Kills: ").setScore(3);

            Team border = scoreboard.registerNewTeam(ChatColor.STRIKETHROUGH.toString());
            border.addEntry(game.getsColor() + "Border: ");
            border.setSuffix(game.getmColor() + game.getGameManager().getBorderSize());
            obj.getScore(game.getsColor() + "Border: ").setScore(2);

            Team footer = scoreboard.registerNewTeam(ChatColor.DARK_AQUA.toString());
            footer.addEntry("§7§m--------");
            footer.setSuffix("----------");
            obj.getScore("§7§m--------").setScore(1);

            Objective healthPList = scoreboard.registerNewObjective("h", "health");
            healthPList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            Objective healthName = scoreboard.registerNewObjective("h1", "health");

            healthName.setDisplayName(ChatColor.DARK_RED + "❤");
            healthName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
    }

    public void updateScoreboard() {
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(game, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    Scoreboard scoreboard = allPlayers.getScoreboard();
                    if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                        Team players = scoreboard.getTeam(ChatColor.ITALIC.toString());
                        players.setSuffix(game.getmColor() + game.getPlayers().size());

                        Team none = scoreboard.getTeam(ChatColor.AQUA.toString());
                        none.setSuffix(game.getmColor() + Scenarios.Default.getVotes());

                        Team timeBomb = scoreboard.getTeam(ChatColor.RED.toString());
                        timeBomb.setSuffix(game.getmColor() + Scenarios.TimeBomb.getVotes());

                        Team noClean = scoreboard.getTeam(ChatColor.BLUE.toString());
                        noClean.setSuffix(game.getmColor() + Scenarios.NoClean.getVotes());

                        Team fireless = scoreboard.getTeam(ChatColor.MAGIC.toString());
                        fireless.setSuffix(game.getmColor() + Scenarios.Fireless.getVotes());

                        Team bowless = scoreboard.getTeam(ChatColor.WHITE.toString());
                        bowless.setSuffix(game.getmColor() + Scenarios.Bowless.getVotes());

                        Team rodless = scoreboard.getTeam(ChatColor.YELLOW.toString());
                        rodless.setSuffix(game.getmColor() + Scenarios.Rodless.getVotes());

                        Team soup = scoreboard.getTeam(ChatColor.GRAY.toString());
                        soup.setSuffix(game.getmColor() + Scenarios.Soup.getVotes());
                    } else if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                        Team gameTime = scoreboard.getTeam(ChatColor.RED.toString());
                        gameTime.setSuffix(game.getmColor() + game.getTimeTask().getFormattedTime());

                        Team remaining = scoreboard.getTeam(ChatColor.BLACK.toString());
                        remaining.setSuffix(game.getmColor() + game.getPlayers().size());

                        Team kills = scoreboard.getTeam(ChatColor.GOLD.toString());
                        kills.setSuffix(game.getmColor() + game.getPlayerKills().get(allPlayers.getUniqueId()));

                        Team border = scoreboard.getTeam(ChatColor.STRIKETHROUGH.toString());
                        border.setSuffix(game.getmColor() + game.getGameManager().getBorderSize());
                    }
                }
            }
        }, 0, 20);
    }

    public void cancelUpdates() {
        Bukkit.getScheduler().cancelTask(taskID);
    }


}
