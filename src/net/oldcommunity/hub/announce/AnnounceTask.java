package net.oldcommunity.hub.announce;

import net.xlduo.axis.utilities.chat.ColorText;
import net.xlduo.axis.utilities.task.TaskUtil;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AnnounceTask {

    public AnnounceTask() {
        List<String> strings = Arrays.asList("&6Join on our website! &eoldcommunity.net", "&6Join on our discord! &ediscord.oldcommunity.net");
        TaskUtil.runTaskTimer(() -> Bukkit.broadcastMessage(ColorText.translate(strings.get(new Random().nextInt(strings.size())))), 0L, 3 * 60 * 20L);
    }
}