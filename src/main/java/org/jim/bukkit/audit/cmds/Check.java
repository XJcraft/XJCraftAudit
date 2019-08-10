package org.jim.bukkit.audit.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.entitylimit.EntityCheck;

public class Check extends ICmd {

    public Check() {
        super("check", "[player] [radius]", "检测玩家周围Entity信息");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        int radius = AuditPlugin.getPlugin().getConfig()
                .getInt("entityCheck.radius", 100);
        Player p = (sender instanceof Player) ? (Player) sender : null;
        Player onLinePlayer = (args != null && args.length > 0)
                ? AuditPlugin.getPlugin().getServer().getPlayer(args[0]) : p;
        if (onLinePlayer == null) {
            sender.sendMessage("玩家不存在或未在线");
            return true;
        }
        try {
            radius = (args != null && args.length > 1)
                    ? Integer.parseInt(args[1]) : radius;
        } catch (NumberFormatException e) {
        }
        EntityCheck c = new EntityCheck();
        c.checkEntity(sender, onLinePlayer, radius);
        return true;
    }

    @Override
    public String permission() {
        return "xjcraft.check";
    }

}
