package net.oldcommunity.hub.utilities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.oldcommunity.hub.HubPlugin;
import net.xlduo.axis.configuration.Config;
import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.location.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ServerUtils implements PluginMessageListener {

    private static Map<String, Integer> playersOnline = new HashMap<>();
    @Getter
    @Setter
    private static Location spawn;

    public ServerUtils(JavaPlugin javaPlugin) {
        spawn = LocationUtils.getLocation(new Config(javaPlugin, "spawn.yml").getString("location"));
        new BukkitRunnable() {

            @Override
            public void run() {
                for (String server : new Config(javaPlugin, "servers.yml").getStringList("Servers")) {
                    ByteArrayDataOutput globalOut = ByteStreams.newDataOutput();
                    globalOut.writeUTF("PlayerCount");
                    globalOut.writeUTF(server);
                    javaPlugin.getServer().sendPluginMessage(javaPlugin, "BungeeCord", globalOut.toByteArray());
                }
            }
        }.runTaskTimer(javaPlugin, 20L, 20L);
    }

    public static int getPlayerCount(String server) {
        if (server == null) {
            server = "ALL";
        }
        return playersOnline.getOrDefault(server, 0);
    }

    public static void sendPlayerToServer(Player player, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        player.sendMessage(ColorText.translate("&eSending you to " + server + "..."));
        output.writeUTF("ConnectOther");
        output.writeUTF(player.getName());
        output.writeUTF(server);
        player.sendPluginMessage(HubPlugin.getInstance(), "BungeeCord", output.toByteArray());
    }

    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String command = in.readUTF();
        if (!command.equals("PlayerCount")) {
            return;
        }
        String server = in.readUTF();
        int playerCount = in.readInt();
        playersOnline.put(server, playerCount);
    }
}