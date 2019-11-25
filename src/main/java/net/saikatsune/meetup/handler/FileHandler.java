package net.saikatsune.meetup.handler;

public class FileHandler {

    public boolean isNumeric(String string) {
        try {
            Double d = Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            double d;
            return false;
        }
        return true;
    }

}
