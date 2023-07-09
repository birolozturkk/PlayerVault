package xyz.scropy.playervault.vault;

import lombok.Getter;

@Getter
public class Vault {

    private final String id;
    private final String name;
    private final VaultCondition condition;

    public Vault(String id, String name, VaultCondition condition) {
        this.id = id;
        this.name = name;
        this.condition = condition;
    }
}
