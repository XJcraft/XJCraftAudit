package org.jim.bukkit.audit.entitylimit;

import org.bukkit.entity.EntityType;

public class EntityCount implements Comparable<EntityCount> {

	private Integer count = 0;
	private EntityType type;

	public EntityCount(EntityType type) {
		this.type = type;
	}

	public void add() {
		count++;
	}

	public Integer getCount() {
		return count;
	}

	public EntityType getType() {
		return type;
	}

	@Override
	public int compareTo(EntityCount o) {
		return o.count - count;
	}

}
