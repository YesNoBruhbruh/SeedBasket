package me.manraj514.seedbasket.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkullUtils {

    private static final String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E2YmY5MTZlMjhjY2I4MGI0ZWJmYWNmOTg2ODZhZDZhZjdjNGZiMjU3ZTU3YThjYjc4YzcxZDE5ZGNjYjIifX19";

    private static ItemStack seedBasket;

    public static ItemStack getSeedBasket(){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");

        lore.add(MessageUtils.colorize("&6Ability: Seed Storage " + "&e&lLEFT CLICK"));
        lore.add(MessageUtils.colorize("&7Place seeds inside the basket"));

        lore.add("");

        lore.add(MessageUtils.colorize("&6Ability: Farmer's Delight " + "&e&lRIGHT CLICK"));
        lore.add(MessageUtils.colorize("&7Automatically Seed a row of"));
        lore.add(MessageUtils.colorize("&7farmland."));

        lore.add("");

        lore.add(MessageUtils.colorize("&5&lEPIC"));

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));

        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        meta.setDisplayName(MessageUtils.colorize("&5Basket of Seeds"));
        meta.setLore(lore);
        item.setItemMeta(meta);

        seedBasket = item;
        return item;
    }

    public static ItemStack getSeedBasketItem() {
        return seedBasket;
    }
}
