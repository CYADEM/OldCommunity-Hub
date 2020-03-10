package net.oldcommunity.hub.mobs;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.oldcommunity.hub.mobs.nms.*;
import net.xlduo.axis.utilities.chat.ColorText;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

@Getter
public class MobHandler {
    private String name, displayName, type;
    @Setter
    private Location location;
    private LivingEntity entity;
    private ServerInfo serverInfo;
    private boolean enchant, wither;
    private Entity handler;

    MobHandler(String name, String displayName, EntityType type, Location location, ServerInfo server, boolean enchant, boolean wither, Material hand) {
        location.getChunk().load();
        this.name = name;
        this.displayName = displayName;
        this.type = type.toString();
        this.location = location;
        this.enchant = enchant;
        this.wither = wither;
        location.getChunk().load();
        Entity entity;
        if (type.equals(EntityType.SKELETON)) {
            entity = new CustomSkeleton(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.ZOMBIE)) {
            entity = new CustomZombie(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SPIDER)) {
            entity = new CustomSpider(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.HORSE)) {
            entity = new CustomHorse(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.CREEPER)) {
            entity = new CustomCreeper(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.WITHER)) {
            entity = new CustomWither(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.BAT)) {
            entity = new CustomBat(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.BLAZE)) {
            entity = new CustomBlaze(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.CHICKEN)) {
            entity = new CustomChicken(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.COW)) {
            entity = new CustomCow(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.PIG)) {
            entity = new CustomPig(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.ENDERMAN)) {
            entity = new CustomEnderman(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.WOLF)) {
            entity = new CustomWolf(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.WITCH)) {
            entity = new CustomWitch(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.VILLAGER)) {
            entity = new CustomVillager(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SNOWMAN)) {
            entity = new CustomSnowGolem(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.IRON_GOLEM)) {
            entity = new CustomIronGolem(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SLIME)) {
            entity = new CustomSlime(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SILVERFISH)) {
            entity = new CustomSilverfish(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SHEEP)) {
            entity = new CustomSheep(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.PIG_ZOMBIE)) {
            entity = new CustomZombiePig(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.SQUID)) {
            entity = new CustomSquid(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.MAGMA_CUBE)) {
            entity = new CustomMagmaCube(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.GHAST)) {
            entity = new CustomGhast(((CraftWorld) location.getWorld()).getHandle());
        } else if (type.equals(EntityType.CAVE_SPIDER)) {
            entity = new CustomCaveSpider(((CraftWorld) location.getWorld()).getHandle());
        } else {
            entity = new CustomZombie(((CraftWorld) location.getWorld()).getHandle());
        }
        if (wither) {
            Skeleton skeleton = (Skeleton) entity.getBukkitEntity();
            skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
        }
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        setHeadYaw(entity.getBukkitEntity(), location.getYaw());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.f(location.getYaw());
        entity.g(location.getYaw());
        (this.entity = (LivingEntity) entity.getBukkitEntity()).setCustomNameVisible(true);
        (this.entity = (LivingEntity) entity.getBukkitEntity()).setRemoveWhenFarAway(false);
        handler = entity;
        if (hand != null) {
            if (hand.equals(Material.POTION)) {
                ItemStack itemStack = new ItemStack(Item.getById(hand.getId()));

                itemStack.setData(16421);

                entity.setEquipment(0, itemStack);
            } else {
                entity.setEquipment(0, new ItemStack(Item.getById(hand.getId())));
            }
        }
        this.serverInfo = server;
        this.update();
    }

    private static float clampYaw(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.F;
        }
        return yaw;
    }

    void update() {
        entity.setCustomName(ColorText.translate(displayName + ' ' + serverInfo.getParsedName()));
    }

    void remove() {
        entity.remove();
    }

    private static void setHeadYaw(org.bukkit.entity.Entity entity, float yaw) {
        if (!(entity instanceof LivingEntity))
            return;
        EntityLiving handle = (EntityLiving) ((CraftEntity) entity).getHandle();
        yaw = clampYaw(yaw);
        handle.aJ = yaw;
        if (!(handle instanceof EntityHuman)) {

            handle.aM = yaw;
        }
        handle.aK = yaw;
    }
}