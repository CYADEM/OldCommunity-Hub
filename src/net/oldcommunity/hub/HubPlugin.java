package net.oldcommunity.hub;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.oldcommunity.hub.board.HubAdapter;
import net.oldcommunity.hub.commands.SetSpawnCommand;
import net.oldcommunity.hub.listener.EnderButListener;
import net.oldcommunity.hub.listener.PlayerListener;
import net.oldcommunity.hub.mobs.MobManager;
import net.oldcommunity.hub.mobs.commands.MobCommands;
import net.oldcommunity.hub.mobs.nms.*;
import net.oldcommunity.hub.utilities.ServerUtils;
import net.oldcommunity.hub.utilities.command.CommandFramework;
import net.xlduo.axis.Axis;
import net.xlduo.axis.board.BoardManager;
import net.xlduo.axis.utilities.chat.ColorText;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class HubPlugin extends JavaPlugin {

    private MobManager mobManager;

    @Override
    public void onEnable() {
        load();
    }

    @Override
    public void onDisable() {
        mobManager.saveMobsToConfig();
        for (String name : mobManager.getMobHandlerMap().keySet()) {
            mobManager.removeMob(name);
        }
        unregisterEntities();
    }

    private void load() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new ServerUtils(this));

        for (World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if (entity instanceof Player) {
                    continue;
                }
                entity.remove();
            }
        }

        Axis.getPlugin().setBoardManager(new BoardManager(new HubAdapter()));
        //CorePlugin.getInstance().setBoardManager(new BoardManager(new HubAdapter()));

        registerListeners(new PlayerListener(), (mobManager = new MobManager()), new EnderButListener());

        CommandFramework commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new MobCommands());
        commandFramework.registerCommands(new SetSpawnCommand());
        commandFramework.registerHelp();

        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));
        registerEntities();

        //new AnnounceTask();
    }

    private void registerEntities() {
        Bukkit.getConsoleSender().sendMessage(ColorText.translate("&eRegistering entities..."));
        CustomEntityType.registerEntity("WitherSkeleton", 5, EntitySkeleton.class, CustomWitherSkeleton.class);
        CustomEntityType.registerEntity("Creeper", 50, EntityCreeper.class, CustomCreeper.class);
        CustomEntityType.registerEntity("Skeleton", 51, EntitySkeleton.class, CustomSkeleton.class);
        CustomEntityType.registerEntity("Spider", 52, EntitySpider.class, CustomSpider.class);
        CustomEntityType.registerEntity("Zombie", 54, EntityZombie.class, CustomZombie.class);
        CustomEntityType.registerEntity("Slime", 55, EntitySlime.class, CustomSlime.class);
        CustomEntityType.registerEntity("Ghast", 56, EntityGhast.class, CustomGhast.class);
        CustomEntityType.registerEntity("ZombiePig", 57, EntityPigZombie.class, CustomZombiePig.class);
        CustomEntityType.registerEntity("Enderman", 58, EntityEnderman.class, CustomEnderman.class);
        CustomEntityType.registerEntity("CaveSpider", 59, EntityCaveSpider.class, CustomCaveSpider.class);
        CustomEntityType.registerEntity("Silverfish", 60, EntitySilverfish.class, CustomSilverfish.class);
        CustomEntityType.registerEntity("Blaze", 61, EntityBlaze.class, CustomBlaze.class);
        CustomEntityType.registerEntity("MagmaCube", 62, EntityMagmaCube.class, CustomMagmaCube.class);
        CustomEntityType.registerEntity("Wither", 64, EntityWither.class, CustomWither.class);
        CustomEntityType.registerEntity("Witch", 66, EntityWitch.class, CustomWitch.class);

        CustomEntityType.registerEntity("Bat", 65, EntityBat.class, CustomBat.class);
        CustomEntityType.registerEntity("Pig", 90, EntityPig.class, CustomPig.class);
        CustomEntityType.registerEntity("Sheep", 91, EntitySheep.class, CustomSheep.class);
        CustomEntityType.registerEntity("Cow", 92, EntityCow.class, CustomCow.class);
        CustomEntityType.registerEntity("Chicken", 93, EntityChicken.class, CustomChicken.class);
        CustomEntityType.registerEntity("Squid", 94, EntitySquid.class, CustomSquid.class);
        CustomEntityType.registerEntity("Wolf", 95, EntityWolf.class, CustomWolf.class);
        CustomEntityType.registerEntity("Mushroom", 96, EntityMushroomCow.class, CustomMushroom.class);
        CustomEntityType.registerEntity("SnowGolem", 97, EntitySnowman.class, CustomSnowGolem.class);
        CustomEntityType.registerEntity("IronGolem", 99, EntityIronGolem.class, CustomIronGolem.class);
        CustomEntityType.registerEntity("Horse", 100, EntityHorse.class, CustomHorse.class);
        CustomEntityType.registerEntity("Villager", 120, EntityVillager.class, CustomVillager.class);
    }

    private void unregisterEntities() {
        Bukkit.getConsoleSender().sendMessage(ColorText.translate("&eUnregistering entities..."));
        CustomEntityType.unRegisterEntity("WitherSkeleton", 5, EntitySkeleton.class, CustomWitherSkeleton.class);
        CustomEntityType.unRegisterEntity("Creeper", 50, EntityCreeper.class, CustomCreeper.class);
        CustomEntityType.unRegisterEntity("Skeleton", 51, EntitySkeleton.class, CustomSkeleton.class);
        CustomEntityType.unRegisterEntity("Spider", 52, EntitySpider.class, CustomSpider.class);
        CustomEntityType.unRegisterEntity("Zombie", 54, EntityZombie.class, CustomZombie.class);
        CustomEntityType.unRegisterEntity("Slime", 55, EntitySlime.class, CustomSlime.class);
        CustomEntityType.unRegisterEntity("Ghast", 56, EntityGhast.class, CustomGhast.class);
        CustomEntityType.unRegisterEntity("ZombiePig", 57, EntityPigZombie.class, CustomZombiePig.class);
        CustomEntityType.unRegisterEntity("Enderman", 58, EntityEnderman.class, CustomEnderman.class);
        CustomEntityType.unRegisterEntity("CaveSpider", 59, EntityCaveSpider.class, CustomCaveSpider.class);
        CustomEntityType.unRegisterEntity("Silverfish", 60, EntitySilverfish.class, CustomSilverfish.class);
        CustomEntityType.unRegisterEntity("Blaze", 61, EntityBlaze.class, CustomBlaze.class);
        CustomEntityType.unRegisterEntity("MagmaCube", 62, EntityMagmaCube.class, CustomMagmaCube.class);
        CustomEntityType.unRegisterEntity("Wither", 64, EntityWither.class, CustomWither.class);
        CustomEntityType.unRegisterEntity("Witch", 66, EntityWitch.class, CustomWitch.class);

        CustomEntityType.unRegisterEntity("Bat", 65, EntityBat.class, CustomBat.class);
        CustomEntityType.unRegisterEntity("Pig", 90, EntityPig.class, CustomPig.class);
        CustomEntityType.unRegisterEntity("Sheep", 91, EntitySheep.class, CustomSheep.class);
        CustomEntityType.unRegisterEntity("Cow", 92, EntityCow.class, CustomCow.class);
        CustomEntityType.unRegisterEntity("Chicken", 93, EntityChicken.class, CustomChicken.class);
        CustomEntityType.unRegisterEntity("Squid", 94, EntitySquid.class, CustomSquid.class);
        CustomEntityType.unRegisterEntity("Wolf", 95, EntityWolf.class, CustomWolf.class);
        CustomEntityType.unRegisterEntity("Mushroom", 96, EntityMushroomCow.class, CustomMushroom.class);
        CustomEntityType.unRegisterEntity("SnowGolem", 97, EntitySnowman.class, CustomSnowGolem.class);
        CustomEntityType.unRegisterEntity("IronGolem", 99, EntityIronGolem.class, CustomIronGolem.class);
        CustomEntityType.unRegisterEntity("Horse", 100, EntityHorse.class, CustomHorse.class);
        CustomEntityType.unRegisterEntity("Villager", 120, EntityVillager.class, CustomVillager.class);
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public static HubPlugin getInstance() {
        return getPlugin(HubPlugin.class);
    }
}