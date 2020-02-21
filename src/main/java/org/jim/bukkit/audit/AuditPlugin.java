package org.jim.bukkit.audit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jim.bukkit.audit.apply.ApplyHelper;
import org.jim.bukkit.audit.apply.MaterialAudit;
import org.jim.bukkit.audit.apply.PlayerApplyEvent;
import org.jim.bukkit.audit.base.DefaultListener;
import org.jim.bukkit.audit.cmds.CommandHandler;
import org.jim.bukkit.audit.entitylimit.EntityControl;
import org.jim.bukkit.audit.rcd.RedstoneClockDetector;
import org.jim.bukkit.audit.script.RunScript;
import org.jim.bukkit.audit.skin.SkinModule;
import org.jim.bukkit.audit.unbreaksign.UnbreakingSign;
import org.jim.bukkit.audit.util.JavaPluginFix;

import java.util.ArrayList;
import java.util.List;

/**
 * 本来是定位成XJCraft的考核插件，结果还加了一些乱七八糟的模块
 *
 * @author jimliang
 */
public class AuditPlugin extends JavaPluginFix {

    MaterialAudit materialAudit = new MaterialAudit();
    public ApplyHelper helper = null;

    private static AuditPlugin instance;
    private List<IModule> modules = new ArrayList<IModule>() {
        public boolean add(IModule e) {
            AuditPlugin.instance.getLogger()
                    .info("Add Module :" + e.getClass());
            return super.add(e);
        }
    };
    private CommandHandler commandHandler;

    public AuditPlugin() {
        instance = this;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.saveDefaultConfig();
        ConfigurationSerialization.registerClass(LocationRef.class);
        helper = new ApplyHelper(this);
        modules.add(helper);
        modules.add(new UnbreakingSign(this));
        modules.add(new RunScript(this));
        modules.add(new EntityControl(this));
        modules.add(new SkinModule(this));
        modules.add(new RedstoneClockDetector(this));
    }

    @Override
    public void onDisable() {
        for (IModule m : modules)
            m.onDisable();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        commandHandler = new CommandHandler(this);
        getCommand("xjcraft").setExecutor(commandHandler);
        getCommand("xj").setExecutor(commandHandler);
        for (IModule m : modules) {
            m.onEnable();
        }
        registerEvents(new DefaultListener());
//		registerEvents(new AutoSeedListener());
    }

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        getLogger().info("Reloading " + getName());
        materialAudit.clearMaterial();
        materialAudit.addMaterials(
                getConfig().getStringList("accept-armorContent"));
        for (IModule m : modules)
            m.reloadConfig();
    }

    public static final AuditPlugin getPlugin() {
        return instance;
    }

    public void apply(Player player) {
        getServer().getPluginManager().callEvent(new PlayerApplyEvent(player));
    }

    public MaterialAudit getMaterialAudit() {
        return materialAudit;
    }

    public ApplyHelper getHelper() {
        return helper;
    }

    public void submit(Runnable command) {
        getServer().getScheduler().runTaskAsynchronously(this, command);
    }

    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public String getMessage(String path) {
        return this.getConfig().getString(path);
    }
}
