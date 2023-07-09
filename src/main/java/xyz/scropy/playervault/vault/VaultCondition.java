package xyz.scropy.playervault.vault;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VaultCondition {

    private final List<Material> materials;
    private final List<Integer> modeDataList;
    private final List<String> nbtList;

    public VaultCondition(List<Material> materials, List<Integer> modeDataList, List<String> nbtList) {
        this.materials = materials;
        this.modeDataList = modeDataList;
        this.nbtList = nbtList;
    }

    public boolean control(ItemStack itemStack) {
        return materials.contains(itemStack.getType()) || (itemStack.getItemMeta().hasCustomModelData() &&
                modeDataList.contains(itemStack.getItemMeta().getCustomModelData())) ||
                nbtList.stream().anyMatch(nbt -> NBT.get(itemStack, readableNBT -> readableNBT.hasTag(nbt)));
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<Integer> getModeDataList() {
        return modeDataList;
    }

    public List<String> getNbtList() {
        return nbtList;
    }
}
