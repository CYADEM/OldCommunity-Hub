package net.oldcommunity.hub.mobs.commands;

import net.oldcommunity.hub.HubPlugin;
import net.oldcommunity.hub.mobs.MobHandler;
import net.oldcommunity.hub.utilities.command.Command;
import net.oldcommunity.hub.utilities.command.CommandArgs;
import net.xlduo.axis.inventory.InventoryMaker;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.item.ItemMaker;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MobCommands {

    @Command(name = "mob", inGameOnly = true, permission = "op")
    public void mob(CommandArgs args) {
        Player player = args.getPlayer();
        player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 15) + "&r &6&lMOB &7&m" + StringUtils.repeat("-", 15) + "\n&6/mob create <name> <displayName> <serverName> <serverAddress>"));
    }

    @Command(name = "mob.create", inGameOnly = true, permission = "op")
    public void create(CommandArgs args) {
        Player player = args.getPlayer();
        String[] argz = args.getArgs();
        if (argz.length < 4) {
            player.sendMessage(ColorText.translate("&cUsage: /mob create <name> <displayName> <serverName> <serverAddress>"));
        } else {
            String name = argz[0], displayName = argz[1], serverName = argz[2], serverAddress = argz[3];
            MobHandler mobHandler = HubPlugin.getInstance().getMobManager().getMob(name);
            if (mobHandler != null) {
                player.sendMessage(ColorText.translate("&cA mob with that name already exists."));
            } else {
                Map<String, Object> stringMap = new HashMap<>();
                InventoryMaker invMaker = new InventoryMaker("Select an entity type", 1);
                ItemStack itemStack = player.getItemInHand();

                InventoryMaker inventoryMaker = new InventoryMaker("Enchant?", 1);

                InventoryMaker lastMaker = new InventoryMaker("Wither?", 1);

                lastMaker.setItem(2, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        stringMap.put("wither", true);
                        try {
                            HubPlugin.getInstance().getMobManager().addMob(name, serverName, displayName, EntityType.CREEPER, player.getLocation(), serverAddress, true, true, (itemStack != null && itemStack.getType() != Material.AIR ? itemStack.getType() : null));
                        } catch (Exception ignored) {
                            player.sendMessage(ColorText.translate("&cThat mob could not be created!"));
                        }
                        player.closeInventory();
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDurability(5).setDisplayname("&a&lYES").create();
                    }
                });

                lastMaker.setItem(6, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        stringMap.put("wither", false);
                        try {
                            HubPlugin.getInstance().getMobManager().addMob(name, serverName, displayName, (EntityType) stringMap.get("entity"), player.getLocation(), serverAddress, (boolean) stringMap.get("enchant"), (boolean) stringMap.get("wither"), (itemStack != null && itemStack.getType() != Material.AIR ? itemStack.getType() : null));
                        } catch (Exception ignored) {
                            player.sendMessage(ColorText.translate("&cThat mob could not be created!"));
                        }
                        player.closeInventory();
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDurability(14).setDisplayname("&c&lNO").create();
                    }
                });

                inventoryMaker.setItem(2, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        stringMap.put("enchant", true);
                        player.openInventory(lastMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDurability(5).setDisplayname("&a&lYES").create();
                    }
                });

                inventoryMaker.setItem(6, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        stringMap.put("enchant", false);
                        player.openInventory(lastMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDurability(14).setDisplayname("&c&lNO").create();
                    }
                });


                for (EntityType entityType : EntityType.values()) {
                    if (!entityType.isSpawnable()) {
                        continue;
                    }
                    invMaker.addItem(new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            stringMap.put("entity", entityType);
                            player.openInventory(inventoryMaker.getCurrentPage());
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(Material.DIAMOND).setDisplayname(entityType.getName()).create();
                        }
                    });
                }

                player.openInventory(invMaker.getCurrentPage());
            }
        }
    }
}