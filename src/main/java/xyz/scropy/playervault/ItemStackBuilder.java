package xyz.scropy.playervault;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemStackBuilder {

    private final ItemStack itemStack;

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
    }

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "ItemStack cannot be null").clone();
    }

    public ItemStackBuilder type(Material type) {
        return change(i -> i.setType(type));
    }

    public ItemStackBuilder amount(int amount) {
        return change(i -> i.setAmount(amount));
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return change(i -> i.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder unenchant(Enchantment enchantment) {
        return change(i -> i.removeEnchantment(enchantment));
    }

    public ItemStackBuilder name(String name) {
        return changeMeta(meta -> meta.setDisplayName(name));
    }

    public ItemStackBuilder lore(List<String> lore) {
        return changeMeta(meta -> meta.setLore(lore));
    }

    public ItemStackBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemStackBuilder addLore(String line) {
        if (line == null) return this;
        List<String> lore = itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() ?
                new ArrayList<>(itemStack.getItemMeta().getLore()) : new ArrayList<>();
        lore.add(line);
        return lore(lore);
    }

    public ItemStackBuilder glowing(boolean glowing) {
        if (glowing) return enchant(Enchantment.LUCK, 1).changeMeta(im -> im.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        return this;
    }

    public ItemStackBuilder glowing() {
        return glowing(true);
    }

    public ItemStackBuilder changeMeta(Consumer<? super ItemMeta> consumer) {
        return change(i -> {
            ItemMeta meta = i.getItemMeta();
            consumer.accept(meta);
            i.setItemMeta(meta);
        });
    }

    public ItemStackBuilder change(Consumer<? super ItemStack> consumer) {
        ItemStackBuilder builder = new ItemStackBuilder(itemStack);
        consumer.accept(builder.itemStack);
        return builder;
    }

    public ItemStack build() {
        return itemStack.clone();
    }

}