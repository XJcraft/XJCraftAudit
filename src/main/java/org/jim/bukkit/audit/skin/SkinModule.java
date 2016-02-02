package org.jim.bukkit.audit.skin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;
import org.jim.bukkit.audit.util.HttpUtil;
import org.jim.bukkit.audit.util.Lang;
import org.jim.bukkit.audit.util.Logs;
import org.jim.bukkit.audit.util.ReflectUtil;
import org.jim.bukkit.audit.util.Task;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;

public class SkinModule extends IModule implements Listener {

	private Task task;
	private int count = 0;
	private boolean enable = false;

	public SkinModule(AuditPlugin plugin) {
		super(plugin);
	}

	@Override
	public void onEnable() {
		enable = true;
		getPlugin().registerEvents(this);
	}

	@Override
	public void onDisable() {
		Task.stop(task);
	}

	@EventHandler
	public void onJoin(PlayerLoginEvent event) {
		if (!enable)
			return;
		//
		try {
			// EntityPlayer
			Player player = event.getPlayer();
			Object ePlayer = ReflectUtil.invokeMethod(player, "getHandle");
			GameProfile g = (GameProfile) ReflectUtil.invokeMethod(ePlayer, "getProfile");
			fillGameProfile(g);
			count = 0;
		} catch (Exception e) {
			count++;
			e.printStackTrace();
		}
		if (count > 10) {
			Logs.info("error count > 10 ,close skin module");
			enable = false;
		}
	}

	private void fillGameProfile(GameProfile g) throws IOException {
		if(Lang.first(g.getProperties().get("textures"))!=null)
			return;
		Property textureProperty = null;
		long start =System.currentTimeMillis();
		textureProperty = getTextProperty(g.getName());
		getPlugin().getLogger().info("GetTextProperty("+g.getName()+") used " +(System.currentTimeMillis()-start)/1000d+" s");
		g.getProperties().put("textures", textureProperty);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Property getTextProperty(String name) {
		try {
			MinecraftTexturesPayload payload = new MinecraftTexturesPayload();
			Gson gson = new Gson();
			String url = "http://mc.0ydy.com/api/skin/" + name;
			String json = HttpUtil.get(url);

			Map propertyMap = new HashMap();
			Map map = gson.fromJson(json, HashMap.class);
			Map data = (Map) map.get("data");
			// 正版
			if (data.containsKey("textures")) {
				Map textures = (Map) data.get("textures");
				return new Property("textures", (String) textures.get("value"), (String) textures.get("signature"));
			}
			// 盗版
			if (data.containsKey("skin")) {
				Map skin = (Map) data.get("skin");
				propertyMap.put(MinecraftProfileTexture.Type.SKIN,
						new MinecraftProfileTexture((String) skin.get("url"), null));
			}
			if (data.containsKey("cloak")) {
				Map skin = (Map) data.get("cloak");
				propertyMap.put(MinecraftProfileTexture.Type.CAPE,
						new MinecraftProfileTexture((String) skin.get("url"), null));
			}
			ReflectUtil.setValue(payload, "textures", propertyMap);
			String dataString = gson.toJson(payload);
			dataString = new String(Base64.encodeBase64(dataString.getBytes(Charsets.UTF_8)));
			Property textProperty = new Property("textures", dataString, "");
			return textProperty;
		} catch (Exception e) {
			e.printStackTrace();
			return new Property("textures", "");
		}
	}
	
	private static String decodeProperty(String value){
		return  new String(Base64.decodeBase64(value), Charsets.UTF_8);
	}

	public static void main(String[] args) {
		/*SkinModule s = new SkinModule(null);
		Property property = s.getTextProperty("jim_");
		System.out.println("decode: " + property.getValue());
		String json = new String(Base64.decodeBase64(property.getValue()), Charsets.UTF_8);
		System.out.println("decode: " + json);
		MinecraftTexturesPayload result = (MinecraftTexturesPayload) new Gson().fromJson(json,
				MinecraftTexturesPayload.class);
		System.out.println(result.getTextures());*/
		System.out.println(decodeProperty("eyJ0aW1lc3RhbXAiOjE0Mzk5NjUyODkyMTQsInByb2ZpbGVJZCI6IjNkNzgzODhmZTFlNjQ0YmZiZDc3OGJhMzk2MDM4NWZlIiwicHJvZmlsZU5hbWUiOiJhcmlzdWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIwMjQ2OGVlYThmOGU0YTRjNTcxMmVjZjhiZjg4ODdlZTUzMGM5ODc3NmFhYTQ2NzJjMWE5ZmE4YWRmZjllNCJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc4OTMyNzBmN2RmNjdhMjBlYTljOGFiNTJkN2FmZjIzMjcwZmM0M2ExMWZiM2RhZTYyMjkxNjM0YWY0MjQifX19"));
	}
}
