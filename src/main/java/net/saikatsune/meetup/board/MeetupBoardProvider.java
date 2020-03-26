package net.saikatsune.meetup.board;

import net.saikatsune.meetup.Game;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.gamestate.states.LobbyState;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MeetupBoardProvider implements BoardProvider {

    private Game game = Game.getInstance();

    private String scoreboardTitle = game.getScoreboardConfig()
            .getString("SCOREBOARDS.TITLE");

    public MeetupBoardProvider(Game game) {
        this.game = game;
    }

    @Override
    public String getTitle(Player player) {
        return scoreboardTitle.replace("&", "§");
    }

    @Override
    public List<String> getBoardLines(Player player) {
        List<String> lines = new ArrayList<>();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        FileConfiguration config = game.getScoreboardConfig();

        if(game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            for (String string : config.getStringList("SCOREBOARDS.LOBBY.LINES")) {
                string = string.replace("%players%", String.valueOf(game.getPlayers().size())).
                        replace("%maximumPlayers%", String.valueOf(Bukkit.getMaxPlayers())).
                        replace("%defaultVotes%", String.valueOf(Scenarios.Default.getVotes())).
                        replace("%timebombVotes%", String.valueOf(Scenarios.TimeBomb.getVotes())).
                        replace("%nocleanVotes%", String.valueOf(Scenarios.NoClean.getVotes())).
                        replace("%firelessVotes%", String.valueOf(Scenarios.Fireless.getVotes())).
                        replace("%bowlessVotes%", String.valueOf(Scenarios.Bowless.getVotes())).
                        replace("%rodlessVotes%", String.valueOf(Scenarios.Rodless.getVotes())).
                        replace("%soupVotes%", String.valueOf(Scenarios.Soup.getVotes())).
                                replace("%spectators%", "" + game.getSpectators().size());
                lines.add(string.replace("&", "§").replace("%spectators%", String.valueOf(game.getSpectators().size())));
            }
        } else {
            for (String string : config.getStringList("SCOREBOARDS.INGAME.LINES")) {
                string = string.replace("%players%", String.valueOf(game.getPlayers().size())).
                        replace("%gameTime%", game.getTimeTask().getFormattedTime()).
                        replace("%kills%", String.valueOf(game.getPlayerKills().get(player.getUniqueId()))).
                        replace("%borderSize%", String.valueOf(game.getGameManager().getBorderSize()).
                        replace("%startedWith%", String.valueOf(game.getStartedWith()).
                        replace("%spectators%", "" + game.getSpectators().size())));
                lines.add(string.replace("&", "§").replace("%spectators%", String.valueOf(game.getSpectators().size()))
                    .replace("%startedWith%", String.valueOf(game.getStartedWith())));
            }
        }

        return lines;
    }
}
