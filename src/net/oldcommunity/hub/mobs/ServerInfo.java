package net.oldcommunity.hub.mobs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.oldcommunity.hub.utilities.ServerUtils;
import net.xlduo.axis.utilities.chat.ColorText;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

@Getter
public class ServerInfo implements Cloneable {
    private String name, address;
    private int onlinePlayers, maxPlayers;
    private boolean online;

    ServerInfo(String name, String address) {
        this.name = name;
        this.address = address;
        this.onlinePlayers = 0;
        this.maxPlayers = 0;
        this.online = false;
    }

    void updatePlayerCount() {
        String[] ip = this.address.split(":");
        online = isOnline(Integer.parseInt(ip[1]));
        onlinePlayers = ServerUtils.getPlayerCount(name);
        int[] faggot = Pinger.ping(ip[0], Integer.parseInt(ip[1]));
        maxPlayers = faggot[1];
    }

    String getParsedName() {
        String response = "&e[";
        if (online) {
            response += "&a" + onlinePlayers + "&e/&a" + maxPlayers;
        } else {
            response += "&cOffline";
        }
        response += "&e]";
        return ColorText.translate(response);
    }

    private boolean isOnline(int port) {
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

    public static class Pinger {
        static int[] ping(String host, int port) {
            try (Socket socket = new Socket(host, port);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream());
                 ByteArrayOutputStream frame = new ByteArrayOutputStream();
                 DataOutputStream frameOut = new DataOutputStream(frame)) {
                writeVarInt(0, frameOut);
                writeVarInt(4, frameOut);
                writeString(host, frameOut);
                frameOut.writeShort(port);
                writeVarInt(1, frameOut);
                writeVarInt(frame.size(), out);
                frame.writeTo(out);
                frame.reset();
                writeVarInt(0, frameOut);
                writeVarInt(frame.size(), out);
                frame.writeTo(out);
                frame.reset();
                int len = readVarInt(in);
                byte[] packet = new byte[len];
                in.readFully(packet);
                try (ByteArrayInputStream inPacket = new ByteArrayInputStream(packet);
                     DataInputStream inFrame = new DataInputStream(inPacket)) {
                    int id = readVarInt(inFrame);
                    if (id != 0) {
                        throw new IllegalStateException("Wrong ping response");
                    }
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) parser.parse(readString(inFrame));
                    JsonObject jsonPlayers = (JsonObject) jsonObject.get("players");
                    return new int[]{jsonPlayers.get("online").getAsInt(), jsonPlayers.get("max").getAsInt()};
                }
            } catch (Exception e) {
                return new int[]{-1, -1};
            }
        }

        static void writeString(String s, DataOutput out) throws IOException {
            byte[] b = s.getBytes(StandardCharsets.UTF_8);
            writeVarInt(b.length, out);
            out.write(b);
        }

        static String readString(DataInput in) throws IOException {
            int len = readVarInt(in);
            byte[] b = new byte[len];
            in.readFully(b);
            return new String(b, StandardCharsets.UTF_8);
        }

        static int readVarInt(DataInput input) throws IOException {
            int out = 0;
            int bytes = 0;
            byte in;
            do {
                in = input.readByte();
                out |= (in & 0x7F) << bytes++ * 7;
                if (bytes > 32) {
                    throw new RuntimeException("VarInt too big");
                }
            } while ((in & 0x80) == 0x80);
            return out;
        }

        static void writeVarInt(int value, DataOutput output) throws IOException {
            do {
                int part = value & 0x7F;
                value >>>= 7;
                if (value != 0) {
                    part |= 0x80;
                }
                output.writeByte(part);
            } while (value != 0);
        }
    }
}