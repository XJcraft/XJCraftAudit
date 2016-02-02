package org.jim.bukkit.audit.apply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.XJException;
import org.jim.bukkit.audit.cmds.ICmd;

public class Apply extends ICmd {

	public Apply() {
		super("apply","","申请考核");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player sendPlayer = (Player) sender;
			getPlugin().apply(sendPlayer);
			return true;
		}
		XJException.throwMe("该命令只能玩家使用");
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.apply";
	}

}
