package org.jim.bukkit.audit.skin;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SkinModule extends IModule implements Listener {

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
        if (Lang.first(g.getProperties().get("textures")) != null)
            return;
        Property textureProperty = null;
        long start = System.currentTimeMillis();
        textureProperty = getTextProperty(g.getName());
        getPlugin().getLogger().info("GetTextProperty(" + g.getName() + ") used " + (System.currentTimeMillis() - start) / 1000d + " s");
        g.getProperties().put("textures", textureProperty);

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Property getTextProperty(String name) {
        try {
            MinecraftTexturesPayload payload = new MinecraftTexturesPayload();
            Gson gson = new Gson();
            String url = "http://bbs.xjcraft.org/api/skin/" + name;
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

    private static String decodeProperty(String value) {
        return new String(Base64.decodeBase64(value), Charsets.UTF_8);
    }

}
