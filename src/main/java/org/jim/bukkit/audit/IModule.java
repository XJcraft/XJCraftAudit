package org.jim.bukkit.audit;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class IModule {

	private AuditPlugin plugin;

	public void onEnable() {};

	public void onDisable() {};

	public void reloadConfig() {};

	public IModule(AuditPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	public AuditPlugin getPlugin() {
		return plugin;
	}

	public FileConfiguration getConfig() {
		return getPlugin().getConfig();
	}
}
