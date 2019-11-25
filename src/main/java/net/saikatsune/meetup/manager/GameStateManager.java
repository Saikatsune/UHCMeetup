package net.saikatsune.meetup.manager;

import net.saikatsune.meetup.gamestate.GameState;
import net.saikatsune.meetup.gamestate.states.EndingState;
import net.saikatsune.meetup.gamestate.states.IngameState;
import net.saikatsune.meetup.gamestate.states.LobbyState;

public class GameStateManager {

    private GameState[] gameStates = new GameState[3];
    private GameState currentGameState;

    public GameStateManager() {
        gameStates[0] = new LobbyState();
        gameStates[1] = new IngameState();
        gameStates[2] = new EndingState();
    }

    public void setGameState(int gameStateIndex) {
        if(currentGameState != null) {
            currentGameState.stop();
        }
        currentGameState = gameStates[gameStateIndex];
        currentGameState.start();
    }

    public void stopCurrentGameState() {
        currentGameState.stop();
        currentGameState = null;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

}
