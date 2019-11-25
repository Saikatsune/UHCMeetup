package net.saikatsune.meetup.enums;

public enum Scenarios {

    Default(false, 0),
    Bowless(false, 0),
    NoClean(false, 0),
    Rodless(false, 0),
    Fireless(false, 0),
    TimeBomb(false, 0),
    Soup(false, 0);

    private boolean enabled;
    private int votes;

    Scenarios(boolean enabled, int votes) {
        this.enabled = enabled;
        this.votes = votes;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addVote() {
        this.votes += 1;
    }

    public void removeVote() {
        this.votes -= 1;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getVotes() {
        return votes;
    }
}
