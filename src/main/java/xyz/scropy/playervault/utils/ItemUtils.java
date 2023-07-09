package xyz.scropy.playervault.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import xyz.scropy.playervault.ItemStackBuilder;
import xyz.scropy.playervault.Placeholder;

import java.util.List;

public class ItemUtils {

    public static ItemStackBuilder makeItem(ConfigurationSection itemSection, List<Placeholder> placeholders) {
        if(itemSection == null) return new ItemStackBuilder(Material.AIR);
        String displayName = StringUtils.format(itemSection.getString("display-name"), placeholders);
        List<String> lore = StringUtils.format(itemSection.getStringList("lore"), placeholders);

        return new ItemStackBuilder(Material.valueOf(itemSection.getString("material")))
                .name(displayName)
                .lore(lore)
                .glowing(itemSection.getBoolean("glowing"))
                .amount(itemSection.getInt("amount"));
    }
}
