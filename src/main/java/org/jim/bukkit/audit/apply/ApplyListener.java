package org.jim.bukkit.audit.apply;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jim.bukkit.audit.Status;
import org.jim.bukkit.audit.util.Lang;
import org.jim.bukkit.audit.util.Title;
import org.jim.bukkit.audit.util.Util;

public class ApplyListener implements Listener {

	private ApplyHelper helper;

	public ApplyListener(ApplyHelper helper) {
		super();
		this.helper = helper;
	}

	// 优先级调高?
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		helper.setNiceName(player);
		if(event.isBedSpawn()) return;
		// 通过考核并且没有床出生
		if (helper.isApply(player) && !event.isBedSpawn()
				&& helper.isSpawn(event.getRespawnLocation().getWorld())) {
			event.setRespawnLocation(helper.getRespawn(player).clone());
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		helper.unload(event.getPlayer());
	}
	// 修复末地到主世界的bug
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event){
		onPlayerTeleport(event);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.END_PORTAL && event.getTo()!=null) {
			Player player = event.getPlayer();
			if (helper.isApply(player) && player.getBedSpawnLocation() == null
					&& helper.isSpawn(event.getTo().getWorld())) {
				event.setTo(helper.getRespawn(player).clone());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block clicked = event.getClickedBlock();
		if (clicked == null || clicked.getType() != Material.COMMAND)
			return;
		// cmd block
		CommandBlock cmb = (CommandBlock) event.getClickedBlock().getState();
		ItemStack item = event.getItem();
		if (item != null
				&& Material.SIGN == item.getType()
				&& Lang.isEmpty(cmb.getCommand())
				&& helper.getStatus(event.getPlayer()) == Status.APPLIED_VILLAGE) {
			event.setUseItemInHand(Event.Result.ALLOW);
			event.setUseInteractedBlock(Event.Result.DENY);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType()!=helper.getHomeBlock())
			return;
		// 放置命令方块
		Player player = event.getPlayer();
		Status status = helper.getStatus(player);
		if (status == Status.APPLIED) {
			// APPLIED状态下放置命令方块，传送点为备用出生点
			Block cmdBlock = event.getBlock();
			Util.change(cmdBlock,Material.COMMAND);
			Util.setCmdBlock(cmdBlock, helper.getSpawn());
			// }
			// add button
			Util.addButton(cmdBlock);
			// update status
			helper.setStatus(player, Status.APPLIED_VILLAGE);
			Location location = player.getLocation();
			// 储存基地地点和命令方块地点
			helper.setPlayerLocation(player, "base-location", location);
			helper.setPlayerLocation(player, "base-cmdlocation",
					cmdBlock.getLocation());
			// sign
			BlockFace face = BlockFace.EAST;
			HashSet<Byte> set = new HashSet<Byte>();
			List<Block> blocks = player.getLastTwoTargetBlocks(set, 10);
			if (blocks.size() > 1) {
				face = blocks.get(1).getFace(blocks.get(0));
			}
			addSign2(cmdBlock, face, player.getName());

		}

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		helper.setNiceName(event.getPlayer());
	}



	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Location location = player.getLocation();
		Block depBlock = getSignDep(event.getBlock());
		if (depBlock != null && depBlock.getType() == Material.COMMAND
				&& helper.getStatus(player) == Status.APPLIED_VILLAGE) {
			CommandBlock cmdState = (CommandBlock) depBlock.getState();
			if (Lang.isEmpty(cmdState.getCommand())) {
				event.setLine(0, player.getName());
				Location baseLocation = helper.getPlayerLocation(player,
						"base-location");
				Location cmdLocation = helper.getPlayerLocation(player,
						"base-cmdlocation");
				helper.setPlayerLocation(player, "town-cmdlocation",
						depBlock.getLocation());
				if (baseLocation == null || cmdLocation == null) {
					player.sendMessage(helper.getPlugin().getMessage(
							"message.baseNotExit"));
					return;
				}
				Block cmdBlock = player.getWorld().getBlockAt(cmdLocation);
				// 基地到小镇
				Util.setCmdBlock(cmdBlock, location);
				// 小镇到基地
				Util.setCmdBlock(depBlock, baseLocation);
				// add button
				Util.addButton(depBlock);
				helper.setStatus(player, Status.APPLIED_VILLAGE_BASE);
				//player.sendMessage(helper.getPlugin().getMessage(
				//		"message.passageCompleted"));
				new Title("恭喜发财!",ChatColor.AQUA+ helper.getPlugin().getMessage(
							"message.passageCompleted")).send(player);
			}
		}
	}

	@EventHandler
	public void onPlayerApply(PlayerApplyEvent event) {
		Player player = event.getPlayer();
		Status status = helper.getStatus(player);
		if (status == Status.UNAPPLIED) {
			if (event.isArmorAccept()) {
				// 背包满时提示
				// give sign
				ItemStack item = giveSignBlock(player);
				if (Util.isFull(player.getInventory())) {
					player.getInventory().remove(item);
					player.sendMessage(helper.getPlugin().getMessage(
							"message.tip"));
				} else {
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .5f, .8f);
					// give command block
					giveCmdBlock(player);
					player.sendMessage(helper.getPlugin().getMessage(
							"message.applySuccess"));
					//player.sendMessage(ChatColor.AQUA + "请查看背包，并赶紧建立小镇和基地的双向传送");
					new Title("审核通过!",ChatColor.AQUA + "请查看背包，并赶紧建立小镇和基地的双向传送").send(player);
					// broadcast
					String m = helper.getPlugin()
							.getMessage("message.broadcast")
							.replace("<player>", player.getName());
					helper.getPlugin().getServer().broadcastMessage(m);
					helper.setPlayerLocation(player, "apply-location", player.getLocation());
					helper.applied(player);
					// Util.setNiceName(player);
				}
			} else {
				player.sendMessage(helper.getPlugin().getMessage(
						"message.applyFail"));
			}
		} else {
			player.sendMessage(helper.getPlugin()
					.getMessage("message.applyDup")); 
		}

	}

	/**
	 * 在某个block附近添加一个sign
	 * 
	 * @param target
	 * @param face
	 * @param str
	 */
	@Deprecated
	public void addSign(Block target, BlockFace face, String str) {
		Block signBlock = null;
		if (Material.AIR == target.getType()) {
			signBlock = target;
		} else {
			signBlock = target.getRelative(face);
		}
		if (signBlock.getType() != Material.AIR)
			return;
		signBlock.setType(Material.SIGN_POST);
		Sign sign = (Sign) signBlock.getState();
		sign.setLine(0, str);
		((org.bukkit.material.Sign) sign.getData()).setFacingDirection(face);
		sign.update();
	}

	public void addSign2(Block target, BlockFace face, String str) {
		Block signBlock = null;
		if (Material.AIR == target.getType()) {
			signBlock = target;
		} else {
			signBlock = target.getRelative(face);
		}
		if (signBlock.getType() != Material.AIR)
			return;
		signBlock.setType(Material.WALL_SIGN);
		Sign sign = (Sign) signBlock.getState();
		sign.setLine(0, str);
		((org.bukkit.material.Sign) sign.getData()).setFacingDirection(face);
		sign.update();
	}

	public Block getSignDep(Block sign) {
		return Lang.getSignDep(sign);
	}

	public ItemStack giveCmdBlock(Player player) {
		ItemStack commadBlock = new ItemStack(helper.getHomeBlock());
		ItemMeta cItemMeta = commadBlock.getItemMeta();
		cItemMeta.setDisplayName(helper.getPlugin().getConfig()
				.getString("commandBlock.displayName"));
		cItemMeta.setLore(helper.getPlugin().getConfig()
				.getStringList("commandBlock.lore"));
		commadBlock.setItemMeta(cItemMeta);
		player.getInventory().addItem(commadBlock);
		return commadBlock;
	}

	public ItemStack giveSignBlock(Player player) {
		ItemStack sign = new ItemStack(Material.SIGN);
		ItemMeta sItemMeta = sign.getItemMeta();
		sItemMeta.setDisplayName(helper.getPlugin().getConfig()
				.getString("signBlock.displayName"));
		sItemMeta.setLore(helper.getPlugin().getConfig()
				.getStringList("signBlock.lore"));
		sign.setItemMeta(sItemMeta);
		player.getInventory().addItem(sign);
		return sign;
	}
}
