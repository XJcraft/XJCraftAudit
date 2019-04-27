package org.jim.bukkit.audit.entitylimit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;
import org.jim.bukkit.audit.util.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EntityControl extends IModule {

	private static EntityControl instance = null;
	private Task task;
	private Map<EntityType, Integer> entityLimit =
			new HashMap<EntityType, Integer>();
	private int radius = 100;
	private final Logger log = AuditPlugin.getPlugin().getLogger();

	public EntityControl(AuditPlugin plugin) {
		super(plugin);
		instance = this;
	}

	protected void destory() {
		Task.stop(task);
		instance = null;
	}

	public static EntityControl getInstance() {
		return instance;
	}

	public Map<EntityType, Integer> getLimit() {
		Map<EntityType, Integer> map = new HashMap<EntityType, Integer>();
		map.putAll(entityLimit);
		return map;
	}

	public int clear(Player player) {
		return clear(player, radius);
	}

	public int clear(Player player, int radius) {
		Map<EntityType, Integer> map = getLimit();
		int total = 0;
		for (Entity entity : player.getNearbyEntities(radius, 255d, radius)) {
			Integer count = map.get(entity.getType());
			if (count != null) {
				if (count > 0) {
					map.put(entity.getType(), count - 1);
				} else {
					if (entity instanceof LivingEntity) {
						LivingEntity l = (LivingEntity) entity;
						if (l.getCustomName() != null) {
							continue;
						}
					}
					entity.remove();
					total++;
				}
			}
		}
		return total;
	}

	public int getRadius() {
		return radius;
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {
		destory();
	}

	@Override
	public void reloadConfig() {
		final FileConfiguration conf = getPlugin().getConfig();
		List<String> list = conf.getStringList("entityControl.limit");
		// Map<EntityType, Integer> entityLimit = null;
		if (list != null && !list.isEmpty()) {
			// entityLimit = new HashMap<EntityType, Integer>();
			for (String line : list) {
				String[] array = line.split("=", 2);
				if (array.length >= 2) {
					int count = Integer.parseInt(array[1].trim());
					EntityType type = getEntityType(array[0].trim());
					if (type != null) {
						entityLimit.put(type, count);
					}
				}
			}
		}
		// instance = new EntityControl(entityLimit);
		getPlugin().getLogger().info("limit: " + entityLimit);
		radius = conf.getInt("entityControl.radius");
		if (conf.getBoolean("entityControl.autoClear", false)) {
			Task.stop(task);
			task = new Task(AuditPlugin.getPlugin()) {

				@Override
				public void run() {
					log.info("Entity clear Thread start");
					for (Player player : Bukkit.getOnlinePlayers()) {
						int t = EntityControl.getInstance().clear(player);
						log.info("Clear " + player.getName()
								+ " NearbyEntities --> " + t);
					}
					log.info("Entity clear Thread stop");
				}
			};
			task.start(20l, 20 * conf.getInt("entityControl.interval"));
		}

	}
	
	private EntityType getEntityType(String nameOrId) {
		EntityType type = EntityType.fromName(nameOrId);
		if (type == null) {
			try {
				int id = Integer.parseInt(nameOrId);
				type = EntityType.fromId(id);
				
				log.warning(String.format("[EntityLimit] You should not use ID: %d", id)); // 警告不应该使用 id, 而是应该使用名称
				
				if (type == null) {
					log.warning(String.format("[EntityLimit] Failed to load %s, unknown entity id", nameOrId)); // 警告加载这一项失败, 实体 id 不存在
				} else {
					log.warning(String.format("[EntityLimit] Please use the entity name: %s", type.getName())); // 警告不应该使用 id, 而是应该使用名称
				}
			} catch (NumberFormatException e) {
				log.warning(String.format("[EntityLimit] Failed to load %s, unknown entity name", nameOrId)); // 警告加载这一项失败, 未知的实体名
			}
		}
		
		return type;
	}
}
