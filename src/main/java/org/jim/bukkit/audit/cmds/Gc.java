package org.jim.bukkit.audit.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;

public class Gc extends ICmd {

    public Gc() {
        super("gc", "", "回收系统无用的资源");
    }

    @Override
    public boolean onCommand(final CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "系统资源回收中...");
        AuditPlugin.getPlugin().submit(() -> {
            long begin = System.currentTimeMillis();
            long totalMemory = Runtime.getRuntime().totalMemory();
            System.gc();
            sender.sendMessage(
                    "回收完毕，用时： " + (System.currentTimeMillis() - begin)
                            + "ms, 释放内存： "
                            + (totalMemory
                            - Runtime.getRuntime().totalMemory())
                            / (1024 * 1024d)
                            + "mb");
        });
        return true;
    }

    @Override
    public String permission() {
        return "xjcraft.gc";
    }

}
