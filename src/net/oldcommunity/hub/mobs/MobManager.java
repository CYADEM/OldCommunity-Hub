package net.oldcommunity.hub.mobs;

import lombok.Getter;
import net.oldcommunity.hub.HubPlugin;
import net.xlduo.axis.configuration.Config;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.item.ItemMaker;
import net.xlduo.axis.utilities.location.LocationUtils;
import net.xlduo.axis.utilities.task.TaskUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EntityEquipment;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MobManager implements Listener {

    private Map<String, MobHandler> mobHandlerMap = new HashMap<>();
    private Map<String, ServerInfo> serverInfoMap = new HashMap<>();

    public MobManager() {
        Config config = new Config(HubPlugin.getInstance(), "mobs.yml");
        if (config.contains("mobs")) {
            for (String mob : config.getConfigurationSection("mobs").getKeys(false)) {
                String key = "mobs." + mob;
                String name = config.getString(key + ".server");
                String displayName = ColorText.translate(config.getString(key + ".displayName"));
                EntityType type = this.fromName(config.getString(key + ".mobType"));
                Location loc = LocationUtils.getLocation(config.getString(key + ".location"));
                String address = config.getString(key + ".ip");
                boolean enchant = config.getBoolean(key + ".enchant");
                boolean isWither = config.getBoolean(key + ".wither");
                TaskUtil.runTask(() -> {
                    try {
                        MobHandler mobHandler = this.addMob(mob, name, displayName, type, loc, address, enchant, isWither, Material.matchMaterial(config.getString(key + ".equipment.hand")));

                        EntityEquipment equip = mobHandler.getEntity().getEquipment();

                        String helmet = config.getString(key + ".equipment.helmet");
                        String chestplate = config.getString(key + ".equipment.chestplate");
                        String leggings = config.getString(key + ".equipment.leggings");
                        String boots = config.getString(key + ".equipment.boots");

                        if (enchant) {
                            if (helmet != null && !helmet.equals("AIR")) {
                                equip.setHelmet(new ItemMaker(Material.matchMaterial(helmet)).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).create());
                            }
                            if (chestplate != null && !chestplate.equals("AIR")) {
                                equip.setChestplate(new ItemMaker(Material.matchMaterial(chestplate)).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).create());
                            }
                            if (leggings != null && !leggings.equals("AIR")) {
                                equip.setLeggings(new ItemMaker(Material.matchMaterial(leggings)).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).create());
                            }
                            if (boots != null && !boots.equals("AIR")) {
                                equip.setBoots(new ItemMaker(Material.matchMaterial(boots)).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).create());
                            }
                        } else {
                            if (helmet != null && !helmet.equals("AIR")) {
                                equip.setHelmet(new ItemMaker(Material.matchMaterial(helmet)).create());
                            }
                            if (chestplate != null && !chestplate.equals("AIR")) {
                                equip.setChestplate(new ItemMaker(Material.matchMaterial(chestplate)).create());
                            }
                            if (leggings != null && !leggings.equals("AIR")) {
                                equip.setLeggings(new ItemMaker(Material.matchMaterial(leggings)).create());
                            }
                            if (boots != null && !boots.equals("AIR")) {
                                equip.setBoots(new ItemMaker(Material.matchMaterial(boots)).create());
                            }
                        }
                        System.out.println("MOB: " + mobHandler.getName() + ": " + loc.getBlockX() + ',' + loc.getBlockY() + ',' + loc.getBlockZ());
                    } catch (Exception ignored) {
                    }

                });
            }
        }
        TaskUtil.runTaskTimer(() -> serverInfoMap.values().forEach(ServerInfo::updatePlayerCount), 0L, 2 * 20L);
        TaskUtil.runTaskTimer(() -> mobHandlerMap.values().forEach(MobHandler::update), 0L, 30L);
    }

    public MobHandler addMob(String name, String server, String displayName, EntityType type, Location location, String address, boolean enchant, boolean isWither, Material hand) throws Exception {
        if (mobHandlerMap.containsKey(name)) {
            throw new Exception("A mob with that name already exists");
        }
        address = address.toLowerCase();
        address = (address.contains(":") ? address : (address + ":25565"));
        ServerInfo serverInfo;
        if (serverInfoMap.containsKey(server)) {
            serverInfo = serverInfoMap.get(server.toLowerCase());
        } else {
            serverInfo = new ServerInfo(server, address);
            serverInfoMap.put(server.toLowerCase(), serverInfo);
        }

        MobHandler mob = new MobHandler(name, displayName, type, location, serverInfo, enchant, isWither, hand);
        if (mob.getEntity() != null) {
            mobHandlerMap.put(name, mob);
            return mob;
        }
        return null;
    }

    private EntityType fromName(String string) {
        for (EntityType type : EntityType.values()) {
            if (type.toString().equals(string)) {
                return type;
            }
        }
        return null;
    }

    public MobHandler getMob(String name) {
        if (mobHandlerMap.containsKey(name)) {
            return mobHandlerMap.get(name);
        }
        return null;
    }

    public void removeMob(String name) {
        if (mobHandlerMap.containsKey(name)) {
            mobHandlerMap.get(name).remove();
            mobHandlerMap.remove(name);
        }
    }

    public void saveMobsToConfig() {
        Config config = new Config(HubPlugin.getInstance(), "mobs.yml");
        for (MobHandler mob : mobHandlerMap.values()) {
            System.out.println("SAVED: " + mob.getName());
            String pathBase = "mobs." + mob.getName();
            config.set(pathBase + ".displayName", mob.getDisplayName());
            config.set(pathBase + ".mobType", mob.getType());
            config.set(pathBase + ".server", mob.getServerInfo().getName());
            config.set(pathBase + ".location", LocationUtils.getString(mob.getLocation()));
            config.set(pathBase + ".ip", mob.getServerInfo().getAddress());
            config.set(pathBase + ".enchant", mob.isEnchant());
            config.set(pathBase + ".wither", mob.isWither());
            EntityEquipment equip = mob.getEntity().getEquipment();
            if (equip != null) {
                config.set(pathBase + ".equipment.boots", equip.getBoots().getType().name());
                config.set(pathBase + ".equipment.leggings", equip.getLeggings().getType().name());
                config.set(pathBase + ".equipment.chestplate", equip.getChestplate().getType().name());
                config.set(pathBase + ".equipment.helmet", equip.getHelmet().getType().name());
                config.set(pathBase + ".equipment.hand", equip.getItemInHand().getType().name());
            }
            config.save();
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (MobHandler mob : mobHandlerMap.values()) {
            if (mob.getLocation().getChunk() == event.getChunk()) {
                event.setCancelled(true);
            }
        }
    }
}