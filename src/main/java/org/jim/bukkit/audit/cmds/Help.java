package org.jim.bukkit.audit.cmds;

import org.bukkit.command.CommandSender;

public class Help extends ICmd {


	public Help() {
		super("help", "", "○rz");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		StringBuilder str = new StringBuilder();
		str.append("----- XJCraft Command List ------\n");
		for (ICmd cmd : CommandHandler.getInstance().getCommands().values()) {
			if (sender.hasPermission(cmd.permission()) && cmd.isShow()) {
				str.append(cmd.toHelp()).append('\n');
			}
		}
		sender.sendMessage(str.toString());
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.help";
	}

}
