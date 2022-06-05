package me.manraj514.seedbasket.seedbasket;

import me.manraj514.seedbasket.SeedBasket;
import me.manraj514.seedbasket.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class SeedBasketListener implements Listener {
    private final SeedBasket plugin;

    private final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public SeedBasketListener(SeedBasket plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(MessageUtils.colorize("&5Basket of Seeds"))) {
                event.setCancelled(true);
                player.sendMessage(MessageUtils.colorize("&aOpening SeedBasket... "));

                Inventory inventory = Bukkit.createInventory(player, 54, MessageUtils.colorize("&5&l" + player.getName() + "&5&l's" + " &5&lSeed Storage"));

                if (plugin.getMenus().containsKey(player.getUniqueId())) {
                    inventory.setContents(plugin.getMenus().get(player.getUniqueId()));
                }
                player.openInventory(inventory);
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(MessageUtils.colorize("&5Basket of Seeds"))) {
                Block clickedBlock = event.getClickedBlock();
                event.setCancelled(true);

                if (clickedBlock != null) {
                    if (clickedBlock.getType() == Material.FARMLAND) {
                        if (plugin.getMenus().containsKey(player.getUniqueId())) {

                            Inventory inventory = Bukkit.createInventory(player, 54, MessageUtils.colorize("&5&l" + player.getName() + "&5&l's" + " &5&lSeed Storage"));
                            inventory.setContents(plugin.getMenus().get(player.getUniqueId()));

                            if (checkForSeedType(inventory) == null) {
                                player.sendMessage(MessageUtils.colorize("&cYou have no seeds in your seed storage!"));
                                return;
                            }

                            BlockFace face = yawToFace(player.getLocation().getYaw());
                            Block block = clickedBlock.getRelative(face);
                            Block aboveClickedBlock = clickedBlock.getLocation().add(0, 1, 0).getBlock();

                            Material seedType = checkForSeedType(inventory);

                            if (seedType == null){
                                player.sendMessage(MessageUtils.colorize("&cYou don't have a valid seed in your inventory!"));
                                return;
                            }

                            Material cropType = seedToCrop(seedType);

                            if (cropType == null) {
                                player.sendMessage(MessageUtils.colorize("&cInvalid Crop Type!"));
                                return;
                            }

                            ItemStack itemInInv = getFirstItemInInv(inventory, seedType);

                            if (itemInInv == null){
                                player.sendMessage(MessageUtils.colorize("&cSomething bad happened lol"));
                                return;
                            }

                            if (aboveClickedBlock.getType() == Material.AIR){
                                aboveClickedBlock.setType(cropType);
                                itemInInv.setAmount(itemInInv.getAmount() - 1);
                            }else{
                                player.sendMessage(MessageUtils.colorize("&cSomething sus happened"));
                                return;
                            }

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    System.out.println(face);

                                    if (block.getType() == Material.FARMLAND && block.getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                                        Block block1 = block.getLocation().add(0, 1, 0).getBlock();
                                        block1.setType(cropType);

                                        if (itemInInv.getAmount() != 0) {
                                            final int finalAmount = itemInInv.getAmount() - 1;
                                            if (getFirstItemInInvButInt(inventory, seedType) != -1){
                                                ItemStack itemSlotIndex = getItem(inventory, getFirstItemInInvButInt(inventory, seedType));
                                                itemSlotIndex.setAmount(finalAmount);
                                                ItemStack[] itemStacks = { itemSlotIndex };
                                                plugin.getMenus().put(event.getPlayer().getUniqueId(), itemStacks);
                                                System.out.println(finalAmount);
                                            }
                                        } else {
                                            System.out.println("The item is 0");
                                            cancel();
                                        }
                                    } else {
                                        System.out.println(block.getLocation().add(0, 1, 0).getBlock().getType());
                                        System.out.println("error1");
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(plugin, 0, 20);
                        } else {
                            player.sendMessage(MessageUtils.colorize("&cPlease put seeds first into your seed basket."));
                        }
                    }
                }
            }
        }
    }

    private ItemStack getItem(Inventory inventory, int inventory1) {
        return inventory.getItem(inventory1);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(MessageUtils.colorize("&5&l" + event.getPlayer().getName() + "&5&l's" + " &5&lSeed Storage"))) {
            plugin.getMenus().put(event.getPlayer().getUniqueId(), event.getInventory().getContents());

            event.getPlayer().sendMessage(MessageUtils.colorize("&aSaved your basket of seeds!"));
        }
    }

    private int getFirstItemInInvButInt(Inventory inventory, Material cropType) {
        int seedIndexLocation = -1;
        ItemStack currentItem;

        for (int i = 0; i < inventory.getSize(); i++) {
            currentItem = inventory.getItem(i);
            if (currentItem != null) {
                seedIndexLocation = i;

                i = inventory.getSize();
            }
        }

        if (seedIndexLocation != -1) {
            ItemStack item = inventory.getItem(seedIndexLocation);
            if (item != null) {
                if (item.getType() == cropType) {
                    return seedIndexLocation;
                }
            }
        }
        return -1;
    }

    private ItemStack getFirstItemInInv(Inventory inventory, Material cropType) {
        int seedIndexLocation = -1;
        ItemStack currentItem;

        for (int i = 0; i < inventory.getSize(); i++) {
            currentItem = inventory.getItem(i);
            if (currentItem != null) {
                seedIndexLocation = i;

                i = inventory.getSize();
            }
        }

        if (seedIndexLocation != -1) {
            ItemStack item = inventory.getItem(seedIndexLocation);
            if (item != null) {
                if (item.getType() == cropType) {
                    return item;
                }
            }
        }
        return null;
    }

    private Material checkForSeedType(Inventory inventory) {
        int seedIndexLocation = -1;
        ItemStack currentItem;

        for (int i = 0; i < inventory.getSize(); i++) {
            currentItem = inventory.getItem(i);
            if (currentItem != null) {
                if (isItemValidSeedType(currentItem.getType())) {
                    seedIndexLocation = i;

                    i = inventory.getSize();
                }
            }
        }

        if (seedIndexLocation != -1) {
            ItemStack seedItemStack = inventory.getItem(seedIndexLocation);
            if (seedItemStack != null) {
                if (isItemValidSeedType(seedItemStack.getType())) {
                    return seedItemStack.getType();
                }
            }
        }
        return null;
    }

    private Material seedToCrop(Material seedType) {
        switch (seedType) {
            case WHEAT_SEEDS -> {
                return Material.WHEAT;
            }
        }
        return null;
    }

    private BlockFace yawToFace(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3];
        return switch (face) {
            case NORTH -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.NORTH;
            case EAST -> BlockFace.WEST;
            case WEST -> BlockFace.EAST;
            default -> face;
        };
    }

    private boolean isItemValidSeedType(Material seedType) {
        return switch (seedType) {
            case POTATO, CARROT, BEETROOT_SEEDS, WHEAT_SEEDS -> true;
            default -> false;
        };
    }
}