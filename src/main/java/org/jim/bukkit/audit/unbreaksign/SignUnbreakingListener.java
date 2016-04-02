package org.jim.bukkit.audit.unbreaksign;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class SignUnbreakingListener implements Listener {

	private static final String MESSAGE = ChatColor.AQUA + "这个木牌好耐操啊~~";
	private static final BlockFace[] FACES2 = { BlockFace.NORTH,
			BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP,
			BlockFace.DOWN };

	protected SignUnbreakingListener(){}
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (matchPattern(event.getLine(0))) {
			if (hasPermission(event.getPlayer())) {
				event.getPlayer().sendMessage(MESSAGE);
			} else {
				event.setLine(0, "");
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		for (BlockFace face : FACES2) {
			if (check(event.getBlock().getRelative(face))) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();
		while (it.hasNext()) {
			if (check(it.next()))
				it.remove();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event) {
		handleEvent(event, event.getPlayer(), event.getBlock());
	}

	protected void handleEvent(Cancellable event, Player player, Block block) {
		if (!hasPermission(player) && check(block)) {
			event.setCancelled(true);
			if (player != null)
				player.sendMessage(MESSAGE);
		}
	}

	public boolean check(Block block) {
		SignHandler sign = new SignHandler(block);
		return sign.isProtected();
	}

	protected boolean hasPermission(Player player) {
		if (player == null)
			return false;
		return player.hasPermission("xjcraft.signunbreak");
	}

	protected static boolean matchPattern(String line) {
		return "[unbreak]".equals(removeColor(line));
	}

	public static String removeColor(String str) {
		if (str == null)
			return null;
		str = str.replaceAll("(?i)&[0-F]", "");
		return str.replaceAll("(?i)§[0-F]", "").toLowerCase();
	}

}
