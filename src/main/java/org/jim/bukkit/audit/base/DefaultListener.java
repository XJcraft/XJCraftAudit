package org.jim.bukkit.audit.base;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.jim.bukkit.audit.util.LocationUtil;

public class DefaultListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void signColorFix(SignChangeEvent event) {
		String[] lines = event.getLines();
		if (lines != null) {
			for (int i = 0; i < lines.length; i++)
				if (lines[i] != null)
					event.setLine(i, ChatColor.translateAlternateColorCodes('&',
							lines[i]));
		}
	}

	/**
	 * 禁止除主世界的实体传送 Cancel event when a non-player entity contacting with a portal about to teleport
	 * 
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onEntityPortal(EntityPortalEvent event) {
		if (event.getTo() != null) {
			// 1 来处非主世界 2 去处为末地
			World fromWorld = event.getFrom().getWorld();
			World toWorld = event.getTo().getWorld();
			// 主世界
			World mainWorld = Bukkit.getWorlds().get(0);
			if (!fromWorld.equals(mainWorld)
					&& toWorld.getName().endsWith("the_end")) {
				// 取消事件
				event.setCancelled(true);
				// 广播一下给op吧
				Command.broadcastCommandMessage(Bukkit.getConsoleSender(),
						LocationUtil.toString(event.getFrom()) + " 试图传送实体 "
								+ event.getEntity());
				if (event.getEntity() instanceof LivingEntity) {
					LivingEntity e = (LivingEntity) event.getEntity();
					e.damage(e.getHealth());
				} else {
					event.getEntity().remove();
				}
			}
		}
	}

}
