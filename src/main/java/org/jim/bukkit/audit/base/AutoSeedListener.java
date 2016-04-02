package org.jim.bukkit.audit.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jim.bukkit.audit.AuditPlugin;

public class AutoSeedListener implements Listener {

	private Map<String, SeedSession> session =
			new HashMap<String, SeedSession>();


	// 拿着木棍右击耕地
	@EventHandler(ignoreCancelled = true)
	public void begin(final PlayerInteractEvent event) {
		final Block clickBlock = event.getClickedBlock();
		final Material material = event.getMaterial();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& material == Material.STICK
				&& clickBlock.getType() == Material.SOIL) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTaskAsynchronously(AuditPlugin.getPlugin(),
					new Runnable() {

						@Override
						public void run() {
							Inventory inventory = Bukkit.createInventory(
									event.getPlayer(), 9, "Planter");
							event.getPlayer().openInventory(inventory);
							SeedSession seed = new SeedSession(clickBlock);
							seed.inventory = inventory;
							session.put(event.getPlayer().getName(), seed);
						}

					});
		}
	}

	@EventHandler
	public void end(final InventoryCloseEvent event) {
		if ("Planter".equals(event.getInventory().getTitle())
				&& session.containsKey(event.getPlayer().getName())) {
			final SeedSession se = session.get(event.getPlayer().getName());
			session.remove(se);
			Bukkit.getScheduler().runTaskAsynchronously(AuditPlugin.getPlugin(),
					new Runnable() {

						@Override
						public void run() {
							sessionHandle(se, (Player) event.getPlayer());
						}
					});
		}
	}

	private void sessionHandle(SeedSession se, Player player) {
		World world = player.getWorld();
		// Location location = player.getLocation();
		int total = 0;
		int remove = 0;
		HashMap<Integer, ? extends ItemStack> map =
				se.inventory.all(Material.SEEDS);
		for (ItemStack stack : map.values())
			total += stack.getAmount();
		if (total == 0) {
			dropAll(se.clicked.getLocation(), se.inventory);
		} else {
			for (Block block : se.soils) {
				Block up = block.getRelative(BlockFace.UP);
				if (Material.AIR == up.getType() && --total >= 0) {
					up.setType(Material.CROPS);
					remove++;
					world.playSound(up.getLocation(),
							Sound.ENTITY_PLAYER_LEVELUP, 0.1f, .2f);
					world.playEffect(up.getLocation(), Effect.SMOKE, 0);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			se.inventory.removeItem(new ItemStack(Material.SEEDS, remove));
			dropAll(se.clicked.getLocation(), se.inventory);
		}

	}

	public void dropAll(Location location, Inventory inventory) {
		for (ItemStack st : inventory.getContents()) {
			if (st != null && st.getType() != Material.AIR) {
				location.getWorld().dropItem(location, st);
			}
		}
		inventory.clear();
	}

	public static List<Block> searchSoils(Block block) {
		List<Block> soils = new ArrayList<Block>();
		soils.add(block);
		int x = 1;
		boolean search = true;
		while (search) {
			search = search(soils, block, x++);
		}
		return soils;
	}

	private static boolean search(List<Block> soils, Block block, int x) {
		int total = 0;
		int s = 1 + 2 * x;// 边长
		Block b = block.getRelative(-x, 0, -x);// 左下
		// 向上
		for (int i = 1; i < s; i++) {
			b = b.getWorld().getBlockAt(b.getX() + 1, b.getY(), b.getZ());
			if (Material.SOIL == b.getType()) {
				soils.add(b);
				total++;
			}
		}
		// 向右
		for (int i = 1; i < s; i++) {
			b = b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 1);
			if (Material.SOIL == b.getType()) {
				soils.add(b);
				total++;
			}
		}
		// 向下
		for (int i = 1; i < s; i++) {
			b = b.getWorld().getBlockAt(b.getX() - 1, b.getY(), b.getZ());
			if (Material.SOIL == b.getType()) {
				soils.add(b);
				total++;
			}
		}
		// 向左
		for (int i = 1; i < s; i++) {
			b = b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1);
			if (Material.SOIL == b.getType()) {
				soils.add(b);
				total++;
			}
		}
		return total > 0;
	}

	class SeedSession {
		Block clicked;
		List<Block> soils;
		Inventory inventory;

		public SeedSession(Block clicked) {
			super();
			this.clicked = clicked;
			soils = AutoSeedListener.searchSoils(clicked);
		}

	}
}
