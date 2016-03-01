package org.jim.bukkit.audit.apply;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;
import org.jim.bukkit.audit.PlayerMeta;
import org.jim.bukkit.audit.Status;
import org.jim.bukkit.audit.util.Logs;
import org.jim.bukkit.audit.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class ApplyHelper extends IModule {

	public static final String LOCATION_BASE = "base-location";
	public static final String LOCATION_TOWN = "town-location";
	public static final String LOCATION_CMDTOWN = "town-cmdlocation";
	public static final String LOCATION_CMDBASE = "base-cmdlocation";

	private Map<String, SoftReference<PlayerMeta>> metas = new HashMap<String, SoftReference<PlayerMeta>>();
	private AuditPlugin plugin;
	private File conf = null;
	private Location spawn;
	private Material homeBlock;

	public ApplyHelper(AuditPlugin p) {
		super(p);
		this.plugin = p;
		conf = new File(p.getDataFolder(), "status");
		if (!conf.exists())
			conf.mkdirs();
	}

	public Location getRespawnLocation(Player player) {
		return null;
	}

	public boolean isApply(Player player) {
		return getStatus(player) != Status.UNAPPLIED;
	}

	public boolean isApply(String player) {
		return getStatus(player) != Status.UNAPPLIED;
	}

	public void setStatus(Player player, Status s) {
		PlayerMeta meta = getOrCreateMeta(player);
		Status oldStatus = meta.getStatus();
		if (oldStatus != s) {
			setNiceName(player.getPlayer());
			// if (oldStatus == Status.UNAPPLIED)
			// meta.setApplyTime(System.currentTimeMillis());
			meta.setStatus(s);
			save(player, meta);
			AuditPlugin.callEvent(new StatusChangeEvent(player, s));
		}
	}

	public void applied(Player player) {
		PlayerMeta meta = getOrCreateMeta(player);
		//Status oldStatus = meta.getStatus();
		setNiceName(player.getPlayer());
		//if (oldStatus == Status.UNAPPLIED)
		meta.setApplyTime(System.currentTimeMillis());
		meta.setStatus(Status.APPLIED);
		save(player, meta);
		AuditPlugin.callEvent(new StatusChangeEvent(player, Status.APPLIED));
	}

	public void setStatus(String player, Status s) {
		PlayerMeta meta = getOrCreateMeta(player);
		Status oldStatus = meta.getStatus();
		if (oldStatus != s) {
			if (oldStatus == Status.UNAPPLIED)
				meta.setApplyTime(System.currentTimeMillis());
			meta.setStatus(s);
			save(player, meta);
		}
	}

	public Status getStatus(Player player) {
		return getStatus(player.getName());
	}

	public Status getStatus(String player) {
		PlayerMeta meta = getPlayerMeta(player);
		return meta == null ? Status.UNAPPLIED : meta.getStatus();
	}

	public Location getPlayerLocation(Player player, String key) {
		PlayerMeta meta = getPlayerMeta(player);
		return meta == null ? null : meta.getLocation(key);
	}

	public void setPlayerLocation(Player player, String key, Location location) {
		PlayerMeta meta = getOrCreateMeta(player);
		meta.setLocation(key, location);
		save(player, meta);
	}

	public void setPlayerLocation(String player, String key, Location location) {
		PlayerMeta meta = getOrCreateMeta(player);
		meta.setLocation(key, location);
		save(player, meta);
	}

	public void removePlayerLocation(Player player, String key) {
		PlayerMeta meta = getPlayerMeta(player);
		if (meta != null) {
			meta.removeLocation(key);
			save(player, meta);
		}
	}

	public void unload(Player player) {
		metas.remove(player.getName());
	}

	protected void save(Player player, PlayerMeta meta) {
		try {
			Logs.info("Saving status: " + player.getName());
			// meta.save(new File(conf, player.getName() + ".yml"));
			meta.save();
		} catch (IOException e) {
			plugin.getLogger().warning("保存玩家" + player.getName() + "配置失败! ");
			e.printStackTrace();
		}
	}

	protected void save(String player, PlayerMeta meta) {
		try {
			Logs.info("Saving status: " + player);
			meta.save(new File(conf, player + ".yml"));
		} catch (IOException e) {
			plugin.getLogger().warning("保存玩家" + player + "配置失败! ");
			e.printStackTrace();
		}
	}

	public PlayerMeta getOrCreateMeta(Player player) {
		return getOrCreateMeta(player.getName());
	}

	public PlayerMeta getOrCreateMeta(String player) {
		PlayerMeta meta = getPlayerMeta(player);
		if (meta == null) {
			meta = new PlayerMeta();
			meta.setPlayer(player);
			meta.setDataFile(new File(conf, player + ".yml"));
			metas.put(player, new SoftReference<PlayerMeta>(meta));
		}
		return meta;
	}

	public PlayerMeta getPlayerMeta(Player player) {
		return getPlayerMeta(player.getName());
	};

	public PlayerMeta getPlayerMeta(String name) {
		SoftReference<PlayerMeta> meta = metas.get(name.toLowerCase());
		PlayerMeta pm = null;
		if (meta == null || (pm = meta.get()) == null) {
			//Logs.info("Loading status: " + name);
			File metaFile = getFile(conf, name + ".yml");
			if (metaFile == null || !metaFile.exists()) {
				return pm;
			} else {
				pm = new PlayerMeta();
				try {
					pm.load(metaFile);
					metas.put(name.toLowerCase(),
							new SoftReference<PlayerMeta>(pm));
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return pm;
	};

	public File getFile(File dir, String name) {
		for (String f : dir.list())
			if (f.equalsIgnoreCase(name))
				return new File(conf, name);
		return null;
	}

	@Override
	public void onEnable() {
		if (getPlugin().getConfig().getBoolean("apply.enable")) {
			metas.clear(); // Clear Cache
			getPlugin().registerEvents(new ApplyListener(this));
			getPlugin().getCommandHandler().register(new Apply());
			getPlugin().getCommandHandler().register(new Fix());
			getPlugin().getCommandHandler().register(new Give());
			getPlugin().getCommandHandler().register(
					new org.jim.bukkit.audit.apply.Status());
			getPlugin().getCommandHandler().register(new Rename());
			getPlugin().getCommand("rename").setExecutor(new CommandExecutor() {

				@Override
				public boolean onCommand(CommandSender sender, Command command,
						String label, String[] args) {
					if (!(sender instanceof Player))
						return true;
					if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
						sender.sendMessage("/rename [name] <lore[,]>   消耗一根羽毛，给一张纸改名");
						return true;
					}
					Rename.rename((Player) sender, args[0],
							args.length > 1 ? args[1] : null);
					return true;
				}
			});
		}
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadConfig() {

		homeBlock = Material.getMaterial(getConfig().getInt(
				"apply.command-block"));
		Integer x = getPlugin().getConfig().getInt("spawn.x");
		Integer y = getPlugin().getConfig().getInt("spawn.y");
		Integer z = getPlugin().getConfig().getInt("spawn.z");
		World world = getPlugin().getServer().getWorld(
				getPlugin().getConfig().getString("spawn.world", "world"));
		if (world == null) {
			throw new IllegalArgumentException(getPlugin().getConfig()
					.getString("spawn.world") + " 不存在");
		}
		spawn = new Location(world, x, y, z);
		getPlugin().getLogger().info("备用出生坐标：" + spawn);
		super.reloadConfig();
	}

	public Location getSpawn() {
		return spawn;
	}

	public Location getRespawn(Player player) {
		if (player.getBedSpawnLocation() != null) {
			return player.getBedSpawnLocation();
		}
		PlayerMeta meta = getOrCreateMeta(player);
		if (meta.getStatus() == Status.UNAPPLIED)
			return spawn.getWorld().getSpawnLocation();
		if (meta.getStatus() == Status.APPLIED
				&& meta.containsLocation("apply-location")) {
			return meta.getLocation("apply-location");
		}
		return spawn;
	}

	public boolean isSpawn(World w) {
		return spawn.getWorld().getName().equals(w.getName());
	}

	public Material getHomeBlock() {
		return homeBlock;
	}

	public static void setNiceName(final Player p) {
		if (p == null
				|| !p.isOnline()
				|| !AuditPlugin.getPlugin().getConfig()
						.getBoolean("nickname-prefix.enable", true))
			return;

		AuditPlugin.getPlugin().submit(new Runnable() {

			@Override
			public void run() {
				try {
					if (p.isOp()) {
						Util.setDisplayName(p, "nickname-prefix.op");
					} else if (AuditPlugin.getPlugin().getHelper().isApply(p)) {
						Util.setDisplayName(p, "nickname-prefix.applied");
					} else {
						Util.setDisplayName(p, "nickname-prefix.unapplied");
					}
				} catch (Exception e) {
					AuditPlugin
							.getPlugin()
							.getLogger()
							.log(Level.WARNING,
									"Set nickname for " + p.getName()
											+ " error", e);
				}
			}
		});

	}


	public static String lastCode(String input) {
		int pos = input.lastIndexOf("§");
		if ((pos == -1) || (pos + 1 == input.length())) {
			return "";
		}
		return input.substring(pos, pos + 2);
	}
}
