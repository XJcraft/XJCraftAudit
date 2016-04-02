package org.jim.bukkit.audit.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;

public class CommandHandler implements TabExecutor {
	private static CommandHandler instance;
	private Map<String, ICmd> commands = new LinkedHashMap<>();

	public CommandHandler(AuditPlugin plugin) {
		// this.plugin = plugin;
		instance = this;
		loadCommands();
	}

	private void loadCommands() {
		commands.put("check", new Check());
		commands.put("checkall", new CheckAll());
		commands.put("help", new Help());
		commands.put("clear", new Clear());
		commands.put("chunk", new Chunk());
		commands.put("gc", new Gc());
		commands.put("reload", new Reload());
		commands.put("plugin", new PluginCmd());
		// commands.put("test", new CTest());
	}

	public void register(ICmd cmd) {
		cmd.setHandler(this);
		commands.put(cmd.getCmdName(), cmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args == null || args.length < 1) {
			showHelp(sender);
			return true;
		}
		ICmd sub = commands.get(args[0].toLowerCase());
		boolean flag = false;
		try {
			if (sub != null && sender.hasPermission(sub.permission())) {
				String[] params = new String[0];
				if (args.length >= 2) {
					LinkedList<String> list = new LinkedList<>();
					list.addAll(Arrays.asList(args));
					list.removeFirst();
					params = list.toArray(new String[list.size()]);
				}
				if (!sub.legalParam(params)) {
					sender.sendMessage(ChatColor.RED + "参数不合法!请重新输入。");
					return true;
				}
				flag = sub.onCommand(sender, params);
				if (!flag)
					sender.sendMessage(sub.toHelp());
			}
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
			return true;
		}

		return true;
	}

	public static CommandHandler getInstance() {
		return instance;
	}

	public Map<String, ICmd> getCommands() {
		return commands;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		if (args == null || args.length < 1) {
			showHelp(sender);
			return null;
		}
		List<String> result = new ArrayList<>();
		if (args.length == 1) {
			String name = args[0].toLowerCase();
			for (String key : commands.keySet())
				if (key.startsWith(name)) {
					ICmd c = commands.get(key);
					if (sender.hasPermission(c.permission()) && c.isShow())
						result.add(key);
				}

		} else if (args.length > 1) {
			String arg2 = args[1].toLowerCase();
			ICmd s = getCommand(args[0].toLowerCase());
			if (s != null) {
				sender.sendMessage(s.toHelp());
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				String pName = player.getName().toLowerCase();
				if (pName.startsWith(arg2))
					result.add(pName);
			}
		}
		return result;
	}

	public void showHelp(CommandSender sender) {
		commands.get("help").onCommand(sender, new String[0]);
	}

	public ICmd getCommand(String name) {
		if (name == null)
			return null;
		for (String key : commands.keySet()) {
			if (key.startsWith(name))
				return commands.get(key);
		}
		return null;
	}

}
