package org.jim.bukkit.audit.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;

public abstract class ICmd  {
	
	private CommandHandler handler;

	private String cmdName;
	protected String extra = "";
	protected String usage = "";
	protected Integer minParam = 0;
	protected Integer maxParam = Integer.MAX_VALUE;
	protected boolean show = true;
	
	public ICmd(String cmdName){
		this.cmdName = cmdName;
	}
	

	public ICmd(String cmdName, String extra, String usage) {
		this.cmdName = cmdName;
		this.extra = extra;
		this.usage = usage;
	}


	public abstract boolean onCommand(CommandSender sender, String[] args);

	public abstract String permission();

	public String toHelp(String mainCmd) {
		return ChatColor.AQUA+mainCmd + " §r" + cmdName + " " + extra + " -- " + usage;
	}
	
	public String toHelp(){
		return toHelp("/xjcraft");
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}


	public CommandHandler getHandler() {
		return handler;
	}


	public void setHandler(CommandHandler handler) {
		this.handler = handler;
	}


	public String getCmdName() {
		return cmdName;
	}
	public boolean legalParam(String[] param){
		int len = param.length;
		return len>=minParam && len<=maxParam;
	}

	public AuditPlugin getPlugin(){
		return AuditPlugin.getPlugin();
	}

	public boolean isShow() {
		return show;
	}
}
