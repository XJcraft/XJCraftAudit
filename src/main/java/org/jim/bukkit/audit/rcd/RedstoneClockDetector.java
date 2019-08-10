package org.jim.bukkit.audit.rcd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;

public class RedstoneClockDetector extends IModule implements Listener {

	public RedstoneClockDetector(AuditPlugin plugin) {
		super(plugin);
		instance = this;
	}

	private static RedstoneClockDetector instance;// = new RedstoneClockDetector();
	private Map<Location, RestoneRecord> records = new HashMap<>();
	private boolean enable = false;
	// private static Map<String, RedstoneClockDetector> instances = new HashMap<>();


	public void add(Block block) {
		Location loc = block.getLocation();
		RestoneRecord record = records.get(loc);
		if (record == null) {
			record = new RestoneRecord(loc);
			records.put(loc, record);
		}
		record.add();
	}

	public Map<Location, RestoneRecord> getRecords() {
		return records;
	}

	public void clearRecords() {
		this.records.clear();
	}

	public static RedstoneClockDetector me() {
		return instance;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@EventHandler
	public void onRestone(BlockRedstoneEvent event) {
		// 为了节省资源，rcd采用单例，所以管理员之间同时使用会造成干扰
		RedstoneClockDetector rcd = this;// RedstoneClockDetector.me();
		if (rcd.isEnable()) {
			rcd.add(event.getBlock());
		}
	}

	public static class RestoneRecord implements Comparable<RestoneRecord> {

		Location location;
		int count = 0;
		Long lastTime;

		public RestoneRecord(Location location) {
			super();
			this.location = location;
		}

		void add() {
			count++;
			// updateTime();
		}

		@Override
		public int compareTo(RestoneRecord o) {
			return o.count - count;
		}

		public void updateTime() {
			lastTime = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return "RestoneRecord [location=" + location + ", count=" + count
					+ "]";
		}

		public Location getLocation() {
			return location;
		}

		public int getCount() {
			return count;
		}

	}

	@Override
	public void onEnable() {
		instance = this;
		getPlugin().registerEvents(this);
		getPlugin().getCommandHandler().register(new Rcd());
	}

	@Override
	public void onDisable() {

	}
}
