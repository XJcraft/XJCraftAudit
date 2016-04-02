package org.jim.bukkit.audit.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuFolder extends MenuItem {

	private List<MenuItem> childs = new ArrayList<MenuItem>();

	public MenuFolder(String label) {
		super(label);
	}

	public void addItem(MenuItem item) {
		item.parent = this;
		this.childs.add(item);
	}

	public void removeItem(MenuItem item) {
		item.parent = null;
		this.childs.remove(item);
	}

	public List<MenuItem> getChilds() {
		return childs;
	}

}
