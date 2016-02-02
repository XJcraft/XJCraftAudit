package org.jim.bukkit.audit.apply;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jim.bukkit.audit.Status;

public class StatusChangeEvent extends Event{

	private static HandlerList list = new HandlerList();
	
	private Player player;
	private Status status;
	
	public StatusChangeEvent(Player player, Status status) {
		super();
		this.player = player;
		this.status = status;
	}

	public Player getPlayer() {
		return player;
	}

	public Status getStatus() {
		return status;
	}

	public static HandlerList getHandlerList() {
	    return list;
	}

	@Override
	public HandlerList getHandlers() {
		return list;
	}
}
