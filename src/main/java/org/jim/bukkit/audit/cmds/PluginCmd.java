package org.jim.bukkit.audit.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginCmd extends ICmd {

	public PluginCmd() {
		super("plugin", "[load|unload|reload] plugin", "插件加载/卸载/重载");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length < 2)
			return false;
		try {
			if ("load".equalsIgnoreCase(args[0])) {
				loadPlugin(args[1], sender);
			} else if ("unload".equalsIgnoreCase(args[0])) {
				unloadPlugin(args[1], sender);
			} else if ("reload".equalsIgnoreCase(args[0])) {
				reloadPlugin(args[1], sender);
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "错误：" + e.getMessage());
		}
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.plugin";
	}

	private boolean loadPlugin(String pluginName, CommandSender sender) {
		try {
			PluginManager manager = Bukkit.getServer().getPluginManager();
			Plugin plugin = manager
					.loadPlugin(new File("plugins", pluginName + ".jar"));

			if (plugin == null) {
				sender.sendMessage(String
						.format(ChatColor.RED + "加载插件失败，%s 不存在", pluginName));
				return false;
			}

			plugin.onLoad();
			manager.enablePlugin(plugin);
			sender.sendMessage("加载成功！");
		} catch (Exception e) {
			sender.sendMessage("加载错误: " + e.getMessage());
			return false;
		}

		return true;
	}

	private boolean unloadPlugin(String pluginName, CommandSender sender)
			throws Exception {
		PluginManager manager = Bukkit.getServer().getPluginManager();
		SimplePluginManager spmanager = (SimplePluginManager) manager;

		if (spmanager != null) {
			Field pluginsField =
					spmanager.getClass().getDeclaredField("plugins");
			pluginsField.setAccessible(true);
			List plugins = (List) pluginsField.get(spmanager);

			Field lookupNamesField =
					spmanager.getClass().getDeclaredField("lookupNames");
			lookupNamesField.setAccessible(true);
			Map lookupNames = (Map) lookupNamesField.get(spmanager);

			Field commandMapField =
					spmanager.getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			SimpleCommandMap commandMap =
					(SimpleCommandMap) commandMapField.get(spmanager);

			Field knownCommandsField = null;
			Map knownCommands = null;

			if (commandMap != null) {
				knownCommandsField =
						commandMap.getClass().getDeclaredField("knownCommands");
				knownCommandsField.setAccessible(true);
				knownCommands = (Map) knownCommandsField.get(commandMap);
			}
			Plugin plugin = manager.getPlugin(pluginName);
			if (plugin == null) {
				sender.sendMessage(ChatColor.RED + "插件不存在!");
				return false;
			}
			manager.disablePlugin(plugin);

			if (plugins.contains(plugin)) {
				plugins.remove(plugin);
			}

			if ((lookupNames != null)
					&& (lookupNames.containsKey(pluginName))) {
				lookupNames.remove(pluginName);
			}

			if (commandMap != null)
				for (Iterator it = knownCommands.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();

					if ((entry.getValue() instanceof PluginCommand)) {
						PluginCommand command =
								(PluginCommand) entry.getValue();

						if (command.getPlugin() == plugin) {
							command.unregister(commandMap);
							it.remove();
						}
					}
				}
		}
		sender.sendMessage("卸载成功!");
		return true;
	}

	private boolean reloadPlugin(String pluginName, CommandSender sender)
			throws Exception {
		unloadPlugin(pluginName, sender);
		loadPlugin(pluginName, sender);
		return true;
	}
}
