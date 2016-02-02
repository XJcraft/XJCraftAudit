package org.jim.bukkit.audit.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.entitylimit.EntityControl;
import org.jim.bukkit.audit.util.Lang;

public class Clear extends ICmd {

	public Clear() {
		super("clear", "[player] [radius]", "清除玩家周围多余的Entity");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		Player player = Lang.getRealPlayer(sender, args);
		if (player != null) {
			Integer radius = EntityControl.getInstance().getRadius();
			if (args != null && args.length >= 2) {
				try {
					radius = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
				}
			}
			int total = EntityControl.getInstance().clear(player, radius);
			sender.sendMessage("共移除Entity " + ChatColor.BLUE + total
					+ ChatColor.WHITE + " 个");
			return true;
		}
		sender.sendMessage("该玩家不存在或未在线");
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.clear";
	}

}
