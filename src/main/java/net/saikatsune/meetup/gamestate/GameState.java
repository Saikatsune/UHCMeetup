package net.saikatsune.meetup.gamestate;

public abstract class GameState {

    public static final int LOBBY = 0,
                            INGAME = 1,
                            ENDING = 2;

    public abstract void start();
    public abstract void stop();

}
