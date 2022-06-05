package me.manraj514.seedbasket;

import me.manraj514.seedbasket.commands.SeedBasketCommand;
import me.manraj514.seedbasket.seedbasket.SeedBasketListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SeedBasket extends JavaPlugin {

    private Map<UUID, ItemStack[]> menus;

    @Override
    public void onEnable() {
        menus = new HashMap<>();

        saveDefaultConfig();

        if (getConfig().contains("data")) {
            restoreInvs();
            getConfig().set("data", null);
            saveConfig();
            reloadConfig();
        }

        getCommand("giveseedbasket").setExecutor(new SeedBasketCommand(this));

        getServer().getPluginManager().registerEvents(new SeedBasketListener(this), this);
    }

    @Override
    public void onDisable() {
        if (!menus.isEmpty()){
            saveInvs();
        }
    }

    public void saveInvs() {
        for (Map.Entry<UUID, ItemStack[]> entry : menus.entrySet()){
            getConfig().set("data." + entry.getKey(), entry.getValue());
        }
        saveConfig();
        reloadConfig();
    }

    public void restoreInvs() {
        getConfig().getConfigurationSection("data").getKeys(false).forEach(key -> {
            @SuppressWarnings("unchecked")
            ItemStack[] content = ((List<ItemStack>) getConfig().get("data." + key)).toArray(new ItemStack[0]);
            menus.put(UUID.fromString(key), content);
        });
    }

    public Map<UUID, ItemStack[]> getMenus() {
        return menus;
    }
}
