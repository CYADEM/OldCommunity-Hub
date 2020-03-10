package net.oldcommunity.hub.listener;

import net.oldcommunity.hub.HubPlugin;
import net.oldcommunity.hub.mobs.MobHandler;
import net.oldcommunity.hub.utilities.PlayerUtils;
import net.oldcommunity.hub.utilities.ServerUtils;
import net.xlduo.axis.utilities.AxisUtils;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.task.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        /*if (player.hasPermission(CoreUtils.STAFF_PERMISSION)) {
            ProfileManager.getProfile(player).setStaffMode(false);
        }*/
        PlayerUtils.resetPlayer(player);

        if (player.hasPermission(AxisUtils.DONATOR_PERMISSION)) {
            Bukkit.broadcastMessage(ColorText.translate(player.getDisplayName() + " &6joined the lobby!"));
        }
        
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();

        TaskUtil.runTaskLater(() -> {
            player.spigot().respawn();
            PlayerUtils.resetPlayer(player);
        }, 2L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (to.getBlockY() <= 0) {
            event.getPlayer().teleport(ServerUtils.getSpawn());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();

            if (entity.getCustomName() == null) {
                return;
            }

            for (MobHandler mobHandler : HubPlugin.getInstance().getMobManager().getMobHandlerMap().values()) {

                if (entity.getCustomName().equals(mobHandler.getEntity().getCustomName())) {
                    ServerUtils.sendPlayerToServer(player, mobHandler.getServerInfo().getName());
                    break;
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof LivingEntity) {
            Player player = event.getPlayer();
            LivingEntity entity = (LivingEntity) event.getRightClicked();

            if (entity.getCustomName() == null) {
                return;
            }

            for (MobHandler mobHandler : HubPlugin.getInstance().getMobManager().getMobHandlerMap().values()) {

                if (entity.getCustomName().equals(mobHandler.getEntity().getCustomName())) {
                    ServerUtils.sendPlayerToServer(player, mobHandler.getServerInfo().getName());
                    break;
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReason().startsWith("Flying is not enabled")) {
            event.setCancelled(true);
            event.getPlayer().teleport(ServerUtils.getSpawn());
        }
    }

}