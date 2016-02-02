package org.jim.bukkit.audit.util;

import org.jim.bukkit.audit.AuditPlugin;

public class Logs {

	public static void info(String msg){
		AuditPlugin.getPlugin().getLogger().info(msg);
	}
	
	public static void waring(String msg){
		AuditPlugin.getPlugin().getLogger().warning(msg);
	}
}
