package org.jim.bukkit.audit.apply;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.PlayerMeta;
import org.jim.bukkit.audit.cmds.ICmd;
import org.jim.bukkit.audit.util.LocationUtil;

public class Status extends ICmd {

	public Status() {
		super("status", "[player]", "查看玩家状态");
		minParam = 1;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		PlayerMeta meta = AuditPlugin.getPlugin().getHelper()
				.getOrCreateMeta(args[0]);
		sender.sendMessage("玩家: " + ChatColor.BLUE + args[0]);
		sender.sendMessage("状态： " + ChatColor.ITALIC + meta.getStatus()+"("+meta.getStatus().getType()+")");
		if (meta.getStatus() != org.jim.bukkit.audit.Status.UNAPPLIED) {
			if (meta.getApplyTime() != 0) {
				sender.sendMessage("考核时间: "
						+ ChatColor.AQUA
						+ new SimpleDateFormat().format(new Date(meta
								.getApplyTime())));
			}
			sender.sendMessage("基地： "
					+ ChatColor.AQUA
					+ LocationUtil.toString(meta
							.getLocation(ApplyHelper.LOCATION_BASE)));
			sender.sendMessage("小镇的家： "
					+ ChatColor.AQUA
					+ LocationUtil.toString(meta
							.getLocation(ApplyHelper.LOCATION_CMDTOWN)));
		}
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.status";
	}

}
