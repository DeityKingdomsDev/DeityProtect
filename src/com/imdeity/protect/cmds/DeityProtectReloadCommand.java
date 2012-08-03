package com.imdeity.protect.cmds;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.protect.DeityProtect;

public class DeityProtectReloadCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        DeityProtect.plugin.reloadPlugin();
        DeityProtect.plugin.chat.out("Reloaded the config and language files");
        return true;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        DeityProtect.plugin.reloadPlugin();
        DeityProtect.plugin.chat.sendPlayerMessage(player, "Reloaded the config and language files");
        return true;
    }
    
}
