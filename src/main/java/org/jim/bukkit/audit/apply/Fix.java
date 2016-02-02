package org.jim.bukkit.audit.apply;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.rmi.CORBA.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.cmds.ICmd;
import org.jim.bukkit.audit.util.Assert;
import org.jim.bukkit.audit.util.Lang;
import org.jim.bukkit.audit.util.LocationUtil;
import org.jim.bukkit.audit.util.Logs;

public class Fix extends ICmd {

	private static final int DEFALUT_RADIUS = 5;

	public Fix() {
		super("fix", "<base|town> [player] <radius>", "修复玩家 小镇-基地 传送法阵");
		minParam = 2;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		Assert.isTrue((sender instanceof Player), "该命令只能玩家使用");
		int radius = DEFALUT_RADIUS;
		if (args.length == 3) {
			radius = Lang.parseInt(args[2], DEFALUT_RADIUS);
		}
		Player sendPlayer = (Player) sender;
		Location location = sendPlayer.getLocation();
		//OfflinePlayer oplayer = Bukkit.getOfflinePlayer(args[1]);

		// base town basecmd
		Block cmd = getBlock(location, Material.COMMAND, radius);
		Assert.notNull(
				cmd,
				String.format("未找到命令方块!(l: %s,r: %s)",
						LocationUtil.toString(location), radius));

		Location base = null, town = null, basecmd = null,towncmd = null;
		if ("base".equals(args[0])) {
			// commandblock --> location
			sender.sendMessage("基地 --> 小镇");
			basecmd = cmd.getLocation();
			sender.sendMessage("--找到:  基地 " + LocationUtil.toString(basecmd));
			town = getCmdLocation((CommandBlock) cmd.getState());
			Assert.notNull(town, "命令方块中未发现坐标！！");
			sender.sendMessage("--找到:  小镇 " + LocationUtil.toString(town));
			//sender.sendMessage("小镇 --> 基地");
			CommandBlock townCmd = getCmdBlock(town, radius);
			//Assert.notNull(towncmd, "命令方块中未发现坐标！！");
			if(townCmd!=null){
				towncmd = townCmd.getLocation();
				//base = getCmdLocation(townCmd);
			}else{
				sender.sendMessage(ChatColor.RED+"未发现小镇的命令方块！");
			}
		} else if ("town".equals(args[0])) {
			// commandblock --> location
			sender.sendMessage("小镇 --> 基地");
			town = cmd.getLocation();
			sender.sendMessage("--找到:  小镇 " + LocationUtil.toString(town));
			base = getCmdLocation((CommandBlock) cmd.getState());
			sender.sendMessage("--找到:  基地 " + LocationUtil.toString(base));
			CommandBlock baseCmd = getCmdBlock(base, radius);
			if (baseCmd != null){
				basecmd = baseCmd.getLocation();
			}else{
				sender.sendMessage(ChatColor.RED+"未发现基地的命令方块！");
			}
		} else {
			sender.sendMessage(ChatColor.RED + args[0] + " 未定义");
			return true;
		}
		if(base!=null){
			update(sender, args[1], "base-location", base);
		}
		if(town!=null){
			update(sender, args[1], "town-cmdlocation", town);
		}
		if(basecmd !=null){
			update(sender, args[1], "base-cmdlocation", basecmd);
		}
		if( basecmd !=null && town !=null)
			org.jim.bukkit.audit.util.Util.setCmdBlock(basecmd.getBlock(), town);
		if( towncmd !=null && base !=null)
			org.jim.bukkit.audit.util.Util.setCmdBlock(towncmd.getBlock(), base);
		sender.sendMessage("资料更新成功!");
		return true;
	}

	private void update(CommandSender sender, String oplayer, String string,
			Location loc) {
		//Logs.info(String.format("update %s '%s'--> %s", oplayer, string,
				//LocationUtil.toString(loc)));
		if (loc != null) {
			AuditPlugin.getPlugin().getHelper()
					.setPlayerLocation(oplayer, string, loc);
		}
	}

	public CommandBlock getCmdBlock(Location loc, int radius) {
		Block block = getBlock(loc, Material.COMMAND, radius);
		return block == null ? null : (CommandBlock) block.getState();
	}

	public Block getBlock(Location loc, Material material, int radius) {
		if (loc == null)
			return null;
		int x1 = loc.getBlockX() - radius;
		int x2 = loc.getBlockX() + radius;
		int y1 = loc.getBlockY() - radius;
		int y2 = loc.getBlockY() + radius;
		int z1 = loc.getBlockZ() - radius;
		int z2 = loc.getBlockZ() + radius;
		World world = loc.getWorld();
		for (int x = x1; x < x2; x++)
			for (int y = y1; y < y2; y++)
				for (int z = z1; z < z2; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() == material)
						return block;
				}
		return null;
	}

	private static Pattern p = Pattern
			.compile("([\\-\\d]+)\\s+([\\-\\d]+)\\s+([\\-\\d]+)");

	public Location getCmdLocation(CommandBlock cmd) {
		String line = cmd.getCommand();
		if (line == null) {
			return null;
		}
		line = line.trim();
		Matcher m = p.matcher(line);
		if (m.find()) {
			return new Location(cmd.getWorld(), Lang.parseDouble(m.group(1)),
					Lang.parseDouble(m.group(2)), Lang.parseDouble(m.group(3)));
		}
		return null;
	}

	@Override
	public String permission() {
		return "xjcraft.fix";
	}

}
