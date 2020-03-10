package net.oldcommunity.hub.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnderButListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player shooter = event.getPlayer();
            if (shooter.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if (shooter.getItemInHand() != null && shooter.getItemInHand().getType() != Material.AIR && shooter.getItemInHand().getType() == Material.ENDER_PEARL) {
                shooter.setVelocity(shooter.getLocation().getDirection().multiply(3.0f));
                event.setUseItemInHand(Event.Result.DENY);
                int amount;
                if ((amount = shooter.getItemInHand().getAmount()) < 2) {
                    shooter.setItemInHand(new ItemStack(Material.AIR));
                } else {
                    shooter.getItemInHand().setAmount(amount - 1);
                }
                shooter.updateInventory();
            }
        }
    }
}