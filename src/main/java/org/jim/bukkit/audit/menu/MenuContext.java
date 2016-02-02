package org.jim.bukkit.audit.menu;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class MenuContext {

	private MenuFolder root;
	
	private MenuFolder current;
	
	private int selectedIndex;
	
	
	private Player player;
	//
	private Scoreboard scoreboard;
	private Objective objective;

	private Scoreboard oldScoreboard;
	
	public MenuContext(MenuFolder root,Player player) {
		super();
		this.root = root;
		this.current = root;
		load(player);
	}
	
	public void scorllUp(){
		selectedIndex = (selectedIndex-1)%current.getChilds().size();
		update();
	}
	public void scorllDown(){
		selectedIndex = (selectedIndex+1)%current.getChilds().size();
		update();
	}

	private void load(Player player) {
		if(this.player != null)
			unload();
		this.player = player;
		oldScoreboard = player.getScoreboard();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		player.setScoreboard(scoreboard);
		objective = scoreboard.registerNewObjective("objective", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.YELLOW+root.getLabel());
		update();
	}

	public void unload() {
		player.setScoreboard(oldScoreboard);
		player = null;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void update(){
		reset();
		List<MenuItem> items = current.getChilds();
		int total = items.size();
		int index = 0;
		for(MenuItem item : items){
			String label = /*item.isFolder()?"□":"■"+*/item.getLabel();
			if(index == selectedIndex)
				label = ChatColor.YELLOW+"-"+label;
			if(label.length()< 16){
				setScore(label, total--);
				index++;
			}else{
				System.out.println(label);
			}
		}
	}
	private Set<String> scoreCache = new HashSet<String>();
	
	private void reset(){
		for(String name : scoreCache){
			scoreboard.resetScores(name);
		}
		scoreCache.clear();
	}
	
	private void setScore(String name,int score){
		objective.getScore(name).setScore(score);
		scoreCache.add(name);
	}

	public boolean click() {
		OnClickListener listener = current.getChilds().get(selectedIndex).onClickListener;
		if(listener!= null){
			return listener.onClick(this);
		}
		return false;
	}
	public void back(){
		current = current.parent;
		update();
	}
	public void inter(){
		current = (MenuFolder) current.getChilds().get(selectedIndex);
		update();
	}
}
