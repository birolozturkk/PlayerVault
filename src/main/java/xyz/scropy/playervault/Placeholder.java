package xyz.scropy.playervault;


import java.util.ArrayList;
import java.util.List;

public class Placeholder {

    private final String key;
    private final String value;

    public Placeholder(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static PlaceholderBuilder builder() {
        return new PlaceholderBuilder();
    }

    public static class PlaceholderBuilder {

        private final List<Placeholder> placeholders = new ArrayList<>();

        private PlaceholderBuilder() {
            placeholders.add(new Placeholder("%prefix%", PlayerVaultPlugin.getInstance().getConfig().getString("prefix")));
        }

        public PlaceholderBuilder apply(String key, String value) {
            placeholders.add(new Placeholder(key, value));
            return this;
        }

        public List<Placeholder> build() {
            return placeholders;
        }
    }
}
