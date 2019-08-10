package org.jim.bukkit.audit.script;

import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.IModule;

public class RunScript extends IModule {

    public RunScript(AuditPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().getCommandHandler().register(new RunScriptCmd(this));

    }

}
