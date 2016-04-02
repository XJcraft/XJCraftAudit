package org.jim.bukkit.audit.menu;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jim.bukkit.audit.cmds.ICmd;

public class MenuCmd extends ICmd implements Listener {

	private MenuModule module;

	public MenuCmd(MenuModule module) {
		super("menu", "", "功能菜单(测试中...)");
		this.module = module;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (module.getMenus().containsKey(p.getUniqueId())) {
				sender.sendMessage(ChatColor.AQUA + "请看右边→_→");
				return true;
			}
			module.getMenus().put(p.getUniqueId(),
					new MenuContext(module.getRoot(), p));
		}

		return false;
	}

	@Override
	public String permission() {
		return "xjcraft.menu";
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (module.getMenus().containsKey(event.getPlayer().getUniqueId())) {
			MenuContext menu =
					module.getMenus().get(event.getPlayer().getUniqueId());
			menu.click();
		}
	}

	@EventHandler
	public void itemHeld(PlayerItemHeldEvent event) {
		if (module.getMenus().containsKey(event.getPlayer().getUniqueId())) {
			MenuContext menu =
					module.getMenus().get(event.getPlayer().getUniqueId());
			boolean left = event.getNewSlot() < event.getPreviousSlot();
			if (left)
				menu.scorllUp();
			else
				menu.scorllDown();
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		if (module.getMenus().containsKey(event.getPlayer().getUniqueId())) {
			MenuContext menu =
					module.getMenus().get(event.getPlayer().getUniqueId());
			menu.unload();
			module.getMenus().remove(menu);
		}

	}
}
