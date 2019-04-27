package org.jim.bukkit.audit.menu;

import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuModule extends IModule {

	private MenuFolder root;
	private Map<UUID, MenuContext> menus = new HashMap<UUID, MenuContext>();

	public MenuModule(AuditPlugin plugin) {
		super(plugin);

	}

	@Override
	public void onDisable() {
		for (MenuContext menu : menus.values())
			menu.unload();
		menus.clear();
	}

	@Override
	public void onEnable() {
		MenuCmd cmd = new MenuCmd(this);
		getPlugin().getCommandHandler().register(cmd);
		getPlugin().registerEvents(cmd);
	}

	public MenuFolder getRoot() {
		if (root == null) {
			File f = new File(System.getProperty("user.dir"));
			root = new MenuFolder(f.getName());
			iterate(root, f);
			root.addItem(new MenuItem("Exit", new OnClickListener() {

				@Override
				public boolean onClick(MenuContext context) {
					context.unload();
					return true;
				}
			}));
		}
		return root;
	}

	private void iterate(MenuFolder menu, File f) {
		for (File file : f.listFiles()) {
			if (file.getName().length() > 13)
				continue;
			if (file.isDirectory()) {
				MenuFolder m = new MenuFolder(file.getName());
				m.setOnClickListener(MenuItem.enter);
				menu.addItem(m);
				iterate(m, file);
			} else {
				MenuItem m = new MenuItem(file.getName());
				menu.addItem(m);
			}
		}
		menu.addItem(new MenuItem("Back", MenuItem.back));
	}

	public Map<UUID, MenuContext> getMenus() {
		return menus;
	}

	public static void main(String[] args) {
		System.getProperties().list(System.out);
	}
}
