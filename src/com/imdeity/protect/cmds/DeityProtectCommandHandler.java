package com.imdeity.protect.cmds;

import com.imdeity.deityapi.api.DeityCommandHandler;

public class DeityProtectCommandHandler extends DeityCommandHandler {
    
    public DeityProtectCommandHandler(String pluginName) {
        super(pluginName, "DeityProtect");
    }
    
    @Override
    protected void initRegisteredCommands() {
        this.registerCommand("reload", null, "", "Reloads the config and language files", new DeityProtectReloadCommand(), "DeityProtect.admin");
    }
}
