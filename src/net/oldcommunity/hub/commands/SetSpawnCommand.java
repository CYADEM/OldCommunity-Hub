package net.oldcommunity.hub.commands;

import net.oldcommunity.hub.HubPlugin;
import net.oldcommunity.hub.utilities.ServerUtils;
import net.oldcommunity.hub.utilities.command.Command;
import net.oldcommunity.hub.utilities.command.CommandArgs;
import net.xlduo.axis.configuration.Config;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.location.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    @Command(name = "setspawn", permission = "op", inGameOnly = true)
    public void setSpawn(CommandArgs args) {
        Player player = args.getPlayer();
        Location location = player.getLocation();
        Config config = new Config(HubPlugin.getInstance(), "spawn.yml");
        config.set("location", LocationUtils.getString(location));
        config.save();
        ServerUtils.setSpawn(location);
        player.sendMessage(ColorText.translate("&eSpawn modified!"));

        Bukkit.getOnlinePlayers().forEach(o -> o.teleport(location));
    }
}