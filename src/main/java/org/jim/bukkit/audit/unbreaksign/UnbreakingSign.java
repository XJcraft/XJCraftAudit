package org.jim.bukkit.audit.unbreaksign;

import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;

public class UnbreakingSign extends IModule {

    public UnbreakingSign(AuditPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().registerEvents(new SignUnbreakingListener());

    }

    @Override
    public void onDisable() {

    }

}
