package org.jim.bukkit.audit.apply;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.PlayerMeta;
import org.jim.bukkit.audit.cmds.ICmd;

public class TP extends ICmd {
	public TP() {
		super("tp", "<player> [base|town]", "传送到玩家的 小镇|基地 所在的位置");

		this.minParam = 1;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "该命令只能由玩家使用");
			return true;
		}

		final Player player = (Player) sender;
		final String targetPlayerName = args[0];

		// 获取 PlayerMeta
		final PlayerMeta meta = AuditPlugin.getPlugin().getHelper()
				.getPlayerMeta(targetPlayerName);
		if (meta == null) {
			sender.sendMessage(ChatColor.YELLOW + "未找到玩家" + targetPlayerName);
			return true;
		}

		// 取的目标地址
		final String type = args.length > 1 ? args[1] : "base";
		Location targetLocation = null;
		switch (type) {
			case "base":
				targetLocation = meta.getLocation(ApplyHelper.LOCATION_BASE);
				break;
			case "town":
				targetLocation = meta.getLocation(ApplyHelper.LOCATION_CMDTOWN);
				break;
			default:
				player.sendMessage(ChatColor.RED + type + "未定义");
				return true;
		}
		if (targetLocation == null) {
			sender.sendMessage(ChatColor.YELLOW + "未找到玩家" + targetPlayerName
					+ "的" + type + "地址");
			return true;
		}

		// 将地址的视角设置为当前当前玩家的视角
		final Location playerLocation = player.getLocation();

		targetLocation.setPitch(playerLocation.getPitch());
		targetLocation.setYaw(playerLocation.getYaw());

		// 向上寻找一个安全的位置
		if (targetLocation.getWorld().equals(playerLocation.getWorld())) {
			final World world = targetLocation.getWorld();
			while (world.getBlockAt(targetLocation).getType() != Material.AIR
					&& targetLocation.getY() < 256.0D) {
				targetLocation.setY(targetLocation.getY() + 1.0D);
			}
		}

		// 传送玩家
		player.teleport(targetLocation);

		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.tp";
	}
}
