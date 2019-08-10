package org.jim.bukkit.audit.apply;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jim.bukkit.audit.util.Logs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 检验装备的审核
 * 
 * @author jimliang
 *
 */
public class MaterialAudit {

	private Set<Material> materials = new HashSet<>();

	public void addMaterial(Material m) {
		materials.add(m);
        Logs.info("Add audit metarial: " + m + "(" + m.name() + ")");
	}

	@SuppressWarnings("deprecation")
	public void addMaterial(String type) {
		if (type != null)
			addMaterial(Material.getMaterial(type));
	}

	public void clearMaterial() {
		materials.clear();
	}

	public void addMaterials(List<String> types) {
		if (types != null)
			for (String i : types)
				addMaterial(i);
	}

	public ItemStack[] accept(ItemStack[] items) {
		List<ItemStack> unaccepts = new ArrayList<>();
		for (ItemStack item : items) {
			if (item == null) {
				unaccepts.add(item);
			} else if (!materials.contains(item.getType())) {
				unaccepts.add(item);
			}
		}
		return unaccepts.toArray(new ItemStack[0]);
	}

	public ItemStack[] accept(Player player) {
		return accept(player.getInventory().getArmorContents());
	}

}
