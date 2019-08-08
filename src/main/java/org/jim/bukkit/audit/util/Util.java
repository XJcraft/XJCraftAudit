package org.jim.bukkit.audit.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jim.bukkit.audit.AuditPlugin;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Util {


	/**
	 * 给block添加按钮或者压力板
	 * 
	 * @param block
	 */
	public static void addButton(Block block) {
		// 上面 压力板
		Block upBlock = block.getRelative(BlockFace.UP);
		if (Material.AIR == upBlock.getType()) {
            upBlock.setType(Material.OAK_PRESSURE_PLATE);
			upBlock.getState().update();
		}
	}

	public static void setCmdBlock(Block block, Location location) {
        if (block.getType() == Material.COMMAND_BLOCK) {
			Block safeBlock = getSafeBlock(location.getBlock());
			Location safeLocation = safeBlock.getLocation();

			String line = AuditPlugin.getPlugin().getConfig()
					.getString("cmd-line", "/minecraft:tp @p[distance=..5] ${x} ${y} ${z}");
			line = line.replace("${x}", Integer.toString(safeLocation.getBlockX()));
			line = line.replace("${y}", Integer.toString(safeLocation.getBlockY()));
			line = line.replace("${z}", Integer.toString(safeLocation.getBlockZ()));
			CommandBlock state = (CommandBlock) block.getState();
			state.setCommand(line);
			state.update();
		}
	}

	/**
	 * 寻找一个对玩家来说，可以安全的 tp 过去而不会导致窒息的方块
	 * <p>会从目标方块开始向上搜寻</p>
	 * @param block 目标方块
	 * @return 搜索到的方块
	 */
	private static Block getSafeBlock(final Block block) {
		Block safeBlock = block;
		while (!(safeBlock.getRelative(0, 1, 0).isPassable() && safeBlock.getRelative(0, 2, 0).isPassable())) {
			safeBlock = safeBlock.getRelative(0, 1, 0);
		}
		return safeBlock;
	}

	/*
	 * public static void setNiceName(final Player p) { if (p == null || !p.isOnline() ||
	 * !AuditPlugin.getPlugin().getConfig().getBoolean("nickname-prefix.enable", true)) return;
	 * 
	 * executor.submit(new Runnable() {
	 * 
	 * @Override public void run() { try { if (p.isOp()) { to("nickname-prefix.op", p); } else if
	 * (AuditPlugin.getPlugin().getHelper().isApply(p)) { to("nickname-prefix.applied", p); } else {
	 * to("nickname-prefix.unapplied", p); } } catch (Exception e) {
	 * AuditPlugin.getPlugin().getLogger() .log(Level.WARNING, "Set nickname for "+p.getName()+
	 * " error", e); } } });
	 * 
	 * }
	 */

	public static void setDisplayName(Player p, String str) throws Exception {
		String nick =
				p.getDisplayName() == null ? p.getName() : p.getDisplayName();
		String prefix = AuditPlugin.getPlugin().getConfig().getString(str);
		prefix = prefix.replace('&', ChatColor.COLOR_CHAR);
		String output = prefix + nick + ChatColor.RESET;
		p.setDisplayName(output);
		if (output.length() > 16) {
			output = prefix + nick; // 长度大于16时不使用后缀
		}
		if (output.length() > 16) {
			output = lastCode(prefix) + nick;// 去掉后缀仍过长，保留前缀最后的ChatColor
		}
		if ((output.length() > 16)) {
			output = lastCode(prefix) + nick.substring(0, 14);// nickName截掉了，保留前缀最后的ChatColor
		}
		if (output.charAt(output.length() - 1) == '§') {// 若以§结尾则去掉
			output = output.substring(0, output.length() - 1);
		}
		p.setPlayerListName(output);
	}

	public static boolean isFull(Inventory inventory) {
		return inventory.firstEmpty() < 0;
	}

	public static String lastCode(String input) {
		int pos = input.lastIndexOf("§");
		if ((pos == -1) || (pos + 1 == input.length())) {
			return "";
		}
		return input.substring(pos, pos + 2);
	}

	public static Integer getInt(String s, Integer def) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static Double getDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0d;
		}
	}

	/*
	 * public static void testMerge(final File dir){ executor.submit(new Runnable() {
	 * 
	 * @Override public void run() { SamePlayerMerge s = new SamePlayerMerge(); try {
	 * s.testMerge(dir); } catch (Exception e) { e.printStackTrace(); } } }); }
	 */
	public static BufferedImage scaleImage(BufferedImage image, int width,
			int height, boolean checks) {
		// if ((checks) && (Config.SIZE_CENTER) && (image.getWidth() < width) && (image.getHeight()
		// < height)) return image;
		if ((image.getWidth() == width) && (image.getHeight() == height))
			return image;
		float ratio = image.getHeight() / image.getWidth();
		int newWidth = width;
		int newHeight = height;
		if (checks) {
			newHeight = (int) (newWidth * ratio);
			if (newHeight > height) {
				newHeight = height;
				newWidth = (int) (newHeight / ratio);
			}
		}

		BufferedImage resized =
				new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, newWidth, newHeight, 0, 0, image.getWidth(),
				image.getHeight(), null);
		g.dispose();
		return resized;
	}

	public static void change(Block cmdBlock, Material command) {
		if (cmdBlock != null && cmdBlock.getType() != command) {
			cmdBlock.setType(command);
			// cmdBlock.getState().update();
		}

	}
}
