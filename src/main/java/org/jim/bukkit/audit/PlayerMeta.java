package org.jim.bukkit.audit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.jim.bukkit.audit.util.LocationUtil;
import org.jim.bukkit.audit.util.Storeable;

public class PlayerMeta extends Storeable {

	private Status status = Status.UNAPPLIED;
	private long applyTime;
	private Map<String, LocationRef> locations =
			new HashMap<>();
	private File dataFile;
	private String player;

	@Override
	public MemorySection serial(MemorySection yaml) {
		yaml.set("status", status.getType());
		if (applyTime != 0)
			yaml.set("apply-time", applyTime);
		for (Map.Entry<String, LocationRef> e : locations.entrySet()) {
			LocationUtil.putLocation(yaml, e.getValue(),
					"locations." + e.getKey());
		}
		return yaml;
	}

	@Override
	public void unSerial(MemorySection yaml) {
		status = Status.get(yaml.getInt("status", 1));
		applyTime = yaml.getLong("apply-time");
		MemorySection section = (MemorySection) yaml.get("locations");
		if (section != null)
			for (String key : section.getKeys(false)) {
				locations.put(key,
						LocationUtil.getLocationRef(yaml, "locations." + key));
			}
	}

	@Override
	public String toString() {
		return "PlayerMeta [status=" + status + ", locations=" + locations
				+ "]";
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Location getLocation(String key) {
		LocationRef ref = locations.get(key);
		return ref == null ? null : ref.getRef();
	}

	public LocationRef getLocationRef(String key) {
		return locations.get(key);
	}

	public void setLocation(String key, Location location) {
		this.locations.put(key, new LocationRef(location));
	}

	public void removeLocation(String key) {
		this.locations.remove(key);
	}

	public long getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}

	@Override
	public void load(File file) throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		super.load(file);
		this.dataFile = file;
		String fileName = file.getName();
		this.player = fileName.substring(0, fileName.lastIndexOf("."));
	}

	public void save() throws IOException {
		if (dataFile != null)
			super.save(dataFile);
	}

	public Map<String, LocationRef> getLocations() {
		return locations;
	}

	public void setLocations(Map<String, LocationRef> locations) {
		this.locations = locations;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public boolean containsLocation(String key) {
		return locations.containsKey(key);
	}
}
