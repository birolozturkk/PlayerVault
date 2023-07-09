package xyz.scropy.playervault.database;

public class DatabaseObject {

    private boolean changed = true;

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
