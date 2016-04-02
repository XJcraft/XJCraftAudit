package org.jim.bukkit.audit.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;

public class Reload extends ICmd {

	public Reload() {
		super("reload", "", "重载插件");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		sender.sendMessage("重载中....");
		AuditPlugin.getPlugin().reloadConfig();
		sender.sendMessage(ChatColor.AQUA + "重载完毕");
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.reload";
	}

}
