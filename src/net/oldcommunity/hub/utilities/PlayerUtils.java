package net.oldcommunity.hub.utilities;

import net.xlduo.axis.AxisAPI;
import net.xlduo.axis.inventory.InventoryMaker;
import net.xlduo.axis.profile.Profile;
import net.xlduo.axis.utilities.AxisUtils;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.item.ItemMaker;
import net.xlduo.axis.utilities.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerUtils {

    private static List<Player> playerCache = new ArrayList<>();
    private static Map<Player, Long> playerLongMap = new HashMap<>();

    public static void resetPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();

        player.setGameMode(GameMode.ADVENTURE);

        if (player.hasPermission(AxisUtils.DONATOR_PERMISSION)) {
            player.setAllowFlight(true);
        }

        playerCache.remove(player);
        playerLongMap.remove(player);

        player.setCanPickupItems(false);
        //player.setWalkSpeed(0.5f);

        inventory.clear();
        inventory.setArmorContents(null);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        player.teleport(ServerUtils.getSpawn());

        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(player.getName());
        itemStack.setItemMeta(skullMeta);

        inventory.setItem(0, new ItemMaker(itemStack).setDisplayname("&aMi Perfil &7(Clic Derecho)").addLore("&7Clic derecho para ver tu perfil.").setInteractRight(PlayerUtils::openProfileInventory).create());
        inventory.setItem(4, new ItemMaker(Material.COMPASS).setDisplayname("&aModalidades &7(Clic Derecho)").addLore("&7Clic derecho para observar nuestras modalidades.").setInteractRight(PlayerUtils::openServerSelector).create());
        inventory.setItem(7, new ItemMaker(Material.INK_SACK).setDisplayname("&eJugadores Visibles: &a✔").setDurability(10).setPlayerInteract(PlayerUtils::togglePlayerVisibility).addLore("&7Clic derecho para ocultar jugadores.").create());
    }

    private static void togglePlayerVisibility(Player player) {
        if (playerLongMap.containsKey(player) && (playerLongMap.get(player) - System.currentTimeMillis()) > 0L) {
            player.sendMessage(ColorText.translate("&eTú puedes usar esto cada &c5 segundos&e."));
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (playerCache.contains(player)) {
            playerCache.remove(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (AxisAPI.getProfile(online).isVanished()) {
                    continue;
                }
                player.showPlayer(online);
            }

            inventory.setItem(7, new ItemMaker(Material.INK_SACK).setDurability(10).setDisplayname("&eJugadores Visibles: &a✔").addLore("&7Clic derecho para ocultar jugadores.").setInteractRight(PlayerUtils::togglePlayerVisibility).create());
        } else {
            playerCache.add(player);

            for (Player online : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(online);
            }

            inventory.setItem(7, new ItemMaker(Material.INK_SACK).setDurability(8).setDisplayname("&eJugadores Visibles: &c✗").addLore("&7Clic derecho para ver jugadores.").setInteractRight(PlayerUtils::togglePlayerVisibility).create());
        }

        playerLongMap.put(player, TimeUtils.parse("5s") + System.currentTimeMillis());
        player.updateInventory();
    }

    private static void openProfileInventory(Player player) {
        InventoryMaker inventoryMaker = new InventoryMaker("Mi Perfil", 5);
        Profile profile = AxisAPI.getProfile(player);

        inventoryMaker.setItem(13, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                skullMeta.setOwner(player.getName());
                itemStack.setItemMeta(skullMeta);
                return new ItemMaker(itemStack).setDisplayname("&aPlayer").addLore("&7Rank: " + profile.getRank().getColor() + profile.getRank().getName()).create();
            }
        });

        inventoryMaker.setItem(29, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                player.sendMessage(ColorText.translate("&cPronto..."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.PAPER).setDisplayname("&aEstadisticas").addLore("&7Ver estadisticas de juegos.").create();
            }
        });

        inventoryMaker.setItem(30, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("titles");
            }

            @Override
            public ItemStack getItemStack() {
                boolean hasTag = profile.getTag() != null && profile.getTag().getPrefix() != null;
                return new ItemMaker(Material.NAME_TAG).setDisplayname("&aEtiquetas").addLore("&7" + (hasTag ? "Tu etiqueta actual es: " + profile.getTag().getPrefix() : "Tu no tienes una etiqueta.")).create();
            }
        });

        inventoryMaker.setItem(32, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.REDSTONE_COMPARATOR).setDisplayname("&aPreferencias").addLore("&7Clic para modificar tus configuraciones.").create();
            }
        });

        inventoryMaker.setItem(33, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(15).setDisplayname(" ").create();
            }
        });

        player.openInventory(inventoryMaker.getCurrentPage());
    }

    private static void openServerSelector(Player player) {
        InventoryMaker maker = new InventoryMaker("Modo de Juegos", 1);

        maker.setItem(0, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                if (isOnline(25570)) {
                    ServerUtils.sendPlayerToServer(player, "Factions");
                } else {
                    player.sendMessage(ColorText.translate("&c&lDISCULPA! &fEl servidor actualmente se encuentra apagado."));
                    player.sendMessage(ColorText.translate("&fPor favor manten la calma e intenta más tarde nuevamente..."));
                }
            }

            @Override
            public ItemStack getItemStack() {
                List<String> lore = new ArrayList<>();
                if (isOnline(25570)) {
                    lore.add("&7Ven con tus amigos o equipo a conquistar");
                    lore.add("&7y domina junto a ellos!");
                    lore.add("");
                    lore.add("&eJugadores Conectados: &f" + ServerUtils.getPlayerCount("Factions"));
                } else {
                    lore.add("&cEl servidor ahora mismo se encuentra &e&lAPAGADO&c!");
                }
                return new ItemMaker((isOnline(25570) ? Material.DIAMOND_AXE : Material.REDSTONE_BLOCK)).setDisplayname("&c&lFactions").addLore(lore).create();
            }
        });

        maker.setItem(1, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                if (isOnline(25567)) {
                    ServerUtils.sendPlayerToServer(player, "KitMap");
                } else {
                    player.sendMessage(ColorText.translate("&c&lDISCULPA! &fEl servidor actualmente se encuentra apagado."));
                    player.sendMessage(ColorText.translate("&fPor favor manten la calma e intenta más tarde nuevamente..."));
                }
            }

            @Override
            public ItemStack getItemStack() {
                List<String> lore = new ArrayList<>();
                if (isOnline(25567)) {
                    lore.add("&7Ven a disfrutar y a ser el mejor!");
                    lore.add("");
                    lore.add("&eJugadores Conectados: &f" + ServerUtils.getPlayerCount("SkyWars"));
                } else {
                    lore.add("&cEl servidor ahora mismo se encuentra &e&lAPAGADO&c!");
                }
                return new ItemMaker((isOnline(25567) ? Material.DIAMOND_PICKAXE : Material.REDSTONE_BLOCK)).setDisplayname("&2&lKitMap").addLore(lore).create();
            }
        });

        player.openInventory(maker.getCurrentPage());
    }

    private static boolean isOnline(int port) {
        try {
            SocketAddress address = new InetSocketAddress("localhost", port);
            Socket socket = new Socket();
            socket.connect(address, 1000);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}