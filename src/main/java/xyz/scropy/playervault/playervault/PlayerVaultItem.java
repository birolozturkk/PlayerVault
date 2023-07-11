package xyz.scropy.playervault.playervault;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.scropy.playervault.database.DatabaseObject;

import java.util.UUID;


@NoArgsConstructor
@Getter
@DatabaseTable(tableName = "playervault_items")
public class PlayerVaultItem extends DatabaseObject {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private UUID playerId;

    @DatabaseField
    private String vaultId;

    @DatabaseField
    private int page;

    @DatabaseField
    private String item;

    public PlayerVaultItem(UUID playerId, String vaultId, int page, String item) {
        this.playerId = playerId;
        this.vaultId = vaultId;
        this.page = page;
        this.item = item;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
