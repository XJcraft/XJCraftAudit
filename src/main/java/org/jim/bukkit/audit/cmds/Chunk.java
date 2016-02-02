package org.jim.bukkit.audit.cmds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jim.bukkit.audit.util.ChunkCount;

public class Chunk extends ICmd {

	public Chunk() {
		super("chunk", "(list [num]|unload)", "(⊙ｏ⊙)");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(args !=null && args.length>0){
			if("unload".equalsIgnoreCase(args[0])){
				int chunkcount = 0;
				World[] worlds = (World[]) Bukkit.getServer().getWorlds()
						.toArray(new World[0]);
				for (World world : worlds) {
					for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
						if (chunk.unload(true, true)) {
							chunkcount++;
						}
					}
				}
				sender.sendMessage(ChatColor.GOLD.toString() + chunkcount + ChatColor.AQUA
						+ " chunks 被卸载!");
				return true;
			}else if("list".equalsIgnoreCase(args[0])){
				int num = 10;
				try {
					num = (args!=null &&args.length>1)?Integer.parseInt(args[1]):num;
				} catch (NumberFormatException e) {
					
				}
				Map<org.bukkit.Chunk, ChunkCount> map = new HashMap<org.bukkit.Chunk, ChunkCount>();
				for(World world : Bukkit.getWorlds()){
					for(Entity entity : world.getEntities()){
						org.bukkit.Chunk c = entity.getLocation().getChunk();
						ChunkCount cc = map.get(c);
						if(cc == null){
							cc = new ChunkCount(c);
							map.put(c, cc);
						}
						cc.add();
					}
				}
				Set<ChunkCount> set = new TreeSet<>(map.values());
				StringBuffer m = new StringBuffer();
				Iterator<ChunkCount> it = set.iterator();
				m.append("------ Chunk Info ------\n");
				while(num>0 && it.hasNext()){
					ChunkCount c = it.next();
					m.append("Chunk"+ ChatColor.BLUE +"[World:" + c.getChunk().getWorld().getName()+ ",x:" + c.getChunk().getX()+",z:" +c.getChunk().getZ()+ "]"
							+ ChatColor.WHITE + "  Entitys: " + ChatColor.RED
							+ c.getCount() + ChatColor.WHITE + "\n");
					num--;
				}
				sender.sendMessage(m.toString());
				return true;
			}
		}
		sender.sendMessage(toHelp("xjcraft"));
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.chunk";
	}

}
