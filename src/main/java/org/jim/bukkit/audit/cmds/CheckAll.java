package org.jim.bukkit.audit.cmds;

import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.entitylimit.EntityCheck;

public class CheckAll extends ICmd {

    public CheckAll() {
        super("checkall", "[radius]", "列出所有在线玩家周围Entity数量");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        int radius = AuditPlugin.getPlugin().getConfig()
                .getInt("entityCheck.radius", 100);
        try {
            radius = (args != null && args.length > 0)
                    ? Integer.parseInt(args[0]) : radius;
        } catch (NumberFormatException e) {
        }
        EntityCheck c = new EntityCheck();
        c.checkAll2(sender, radius);
        return true;
    }

    @Override
    public String permission() {
        return "xjcraft.checkall";
    }

}
