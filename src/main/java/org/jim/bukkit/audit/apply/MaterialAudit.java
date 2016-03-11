package org.jim.bukkit.audit.apply;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jim.bukkit.audit.util.Logs;
/**
 * 检验装备的审核
 * @author jimliang
 *
 */
public class MaterialAudit {

	private Set<Material> materials = new HashSet<Material>();

	public void addMaterial(Material m) {
		materials.add(m);
		Logs.info("Add audit metarial: "+m+"("+m.getId()+")");
	}

	@SuppressWarnings("deprecation")
	public void addMaterial(Integer type) {
		if (type != null && type > 0)
			addMaterial(Material.getMaterial(type));
	}
	
	public void clearMaterial() {
		materials.clear();
	}

	public void addMaterials(List<Integer> types) {
		if (types != null)
			for (Integer i : types)
				addMaterial(i);
	}

	public ItemStack[] accept(ItemStack[] items) {
		List<ItemStack> unaccepts = new ArrayList<ItemStack>();
		for (ItemStack item : items) {
			if (item == null) {
				unaccepts.add(item);
			} else if (!materials.contains(item.getType())) {
				unaccepts.add(item);
				//AuditPlugin.getPlugin().info(item + " 不符合");
			}
		}
		return unaccepts.toArray(new ItemStack[0]);
	}
	
	public ItemStack[] accept(Player player) {
		return accept(player.getInventory().getArmorContents());
	}
	 
}
