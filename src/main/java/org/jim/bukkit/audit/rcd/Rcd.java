package org.jim.bukkit.audit.rcd;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.cmds.ICmd;
import org.jim.bukkit.audit.util.LocationUtil;

public class Rcd extends ICmd {

	private Map<String, SoftReference<QueryRecord>> cache = new HashMap<>();
	private static int pageSize = 10;

	public Rcd() {
		super("rcd", "[page] [second]", "检测高频红石");
	}

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		final int pageNum = getInt(args, 0, 1);
		final int second = getInt(args, 1, 10);
		QueryRecord query = new QueryRecord(pageNum, second);
		if (pageNum == 1) {
			showAsync(sender, query);
			return true;
		}
		SoftReference<QueryRecord> ref = cache.get(sender.getName());
		QueryRecord oldQuery = null;
		if (ref != null) {
			oldQuery = ref.get();
		}
		if (oldQuery == null || oldQuery.isDeprecated()) {
			showAsync(sender, query);
		} else {
			oldQuery.pageNum = query.pageNum;
			show(sender, oldQuery);
		}
		return true;
	}

	public void showAsync(final CommandSender sender, final QueryRecord query) {
		final RedstoneClockDetector rcd = RedstoneClockDetector.me();
		rcd.setEnable(true);
		sender.sendMessage("正在检测红石信号...需用时： " + ChatColor.YELLOW + query.second
				+ ChatColor.WHITE + "秒");
		sender.getServer().getScheduler().runTaskLater(AuditPlugin.getPlugin(),
				() -> {
					rcd.setEnable(false);
					query.record = new TreeSet<>(rcd.getRecords().values());
					rcd.clearRecords();
					query.time = System.currentTimeMillis();
					show(sender, query);
				}, query.second * 20L);
	}

	public void show(CommandSender sender, QueryRecord query) {
		cache.put(sender.getName(), new SoftReference<>(query));
		int start = (query.pageNum - 1) * pageSize;
		int end = query.pageNum * pageSize;
		int current = 0;
		long ticks = query.second * 20L;
		sender.sendMessage(String.format(
				"-------Restone clock detector(Statistics used: %ds,Record: %d,Page:%d/%d)------",
				query.second, query.record.size(), query.pageNum,
				query.pageSum()));
		Iterator<RedstoneClockDetector.RestoneRecord> it =
				query.record.iterator();
		while (current < end && it.hasNext()) {
			current++;
			RedstoneClockDetector.RestoneRecord record = it.next();
			if (start < current) {
				Location l = record.getLocation();
				int count = record.getCount();
				sender.sendMessage(String.format(
						"%s,\u00A76%s\u00A7f Count: \u00A7e%s\u00A7f Rate: \u00A7e%s\u00A7f pt",
						current, LocationUtil.toString(l), count,
						count * 1.0 / ticks));
			}
		}
	}

	@Override
	public String permission() {
		return "xjcraft.rcd";
	}

	public int getInt(String[] array, int index, int def) {
		if (array == null || index < 0 || index > array.length)
			return def;
		int value;
		try {
			value = Integer.parseInt(array[index]);
		} catch (Exception e) {
			value = def;
		}
		return value;
	}

	private static class QueryRecord {
		int pageNum;
		int second;
		// int ticks;
		Set<RedstoneClockDetector.RestoneRecord> record;
		Long time = 0L;

		public QueryRecord(int pageNum, int second) {
			super();
			this.pageNum = pageNum;
			this.second = second;

		}

		public boolean isDeprecated() {
			return System.currentTimeMillis() - time > 20 * 1000;// 30s cache
		}

		public int pageSum() {
			int size = record.size();
			int pagesum = size / pageSize;
			if (size % pageSize != 0) {
				pagesum++;
			}
			return pagesum;
		}
	}
}
