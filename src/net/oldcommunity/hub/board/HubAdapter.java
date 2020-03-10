package net.oldcommunity.hub.board;

import net.oldcommunity.hub.utilities.ServerUtils;
import net.xlduo.axis.AxisAPI;
import net.xlduo.axis.board.BoardAdapter;
import net.xlduo.axis.board.BoardStyle;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HubAdapter implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return "&4&lSoupLand &7\u2503 &fHub";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        toReturn.add("&7&m" + StringUtils.repeat("-", 20));
        toReturn.add("&4Online");
        toReturn.add("&f" + ServerUtils.getPlayerCount(null));
        toReturn.add("");
        toReturn.add("&4Rank");
        toReturn.add(AxisAPI.getRank(player).getColor() + AxisAPI.getRank(player).getName());
        toReturn.add("");
        toReturn.add("&cwww.soupland.us");
        toReturn.add("&7&m" + StringUtils.repeat("-", 20));
        /*toReturn.add("&a&lOnline: &f" + ServerUtils.getPlayerCount(null));
        toReturn.add("&e&lRank: " + AxisAPI.getRank(player).getColor() + AxisAPI.getRank(player).getName());*/
        return toReturn;
    }

    @Override
    public BoardStyle getBoardStyle(Player player) {
        return BoardStyle.MODERN;
    }
}