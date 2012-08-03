package com.imdeity.protect.api;

import org.bukkit.World;

import com.imdeity.protect.DeityProtect;
import com.imdeity.protect.DeityProtectionConfigHelper;
import com.imdeity.protect.enums.DeityChunkPermissionTypes;

public class SimpleDeityChunk extends DeityChunk {
    
    public SimpleDeityChunk(int id, World world, int xCoord, int zCoord, String owner) {
        super(id, world, xCoord, zCoord, owner);
    }
    
    @Override
    public boolean runPermissionCheck(DeityChunkPermissionTypes type, String playerToVerify) {
//        if (this.getId() <= 0) { return true; }
        World world = this.getWorld();
        if (type == DeityChunkPermissionTypes.ACCESS) {
            return DeityProtect.plugin.config.getBoolean(String.format(DeityProtectionConfigHelper.WORLD_ACCESS_NODE, world.getName()));
        } else if (type == DeityChunkPermissionTypes.MOB_SPAWNING) {
            return DeityProtect.plugin.config.getBoolean(String.format(DeityProtectionConfigHelper.WORLD_MOB_SPAWN_NODE, world.getName()));
        } else if (type == DeityChunkPermissionTypes.PVP) {
            return DeityProtect.plugin.config.getBoolean(String.format(DeityProtectionConfigHelper.WORLD_PVP_NODE, world.getName()));
        } else if (type == DeityChunkPermissionTypes.USE) {
            return DeityProtect.plugin.config.getBoolean(String.format(DeityProtectionConfigHelper.WORLD_USE_NODE, world.getName()));
        } else if (type == DeityChunkPermissionTypes.EDIT) {
            if (this.getOwner() != null && this.getOwner().equalsIgnoreCase(playerToVerify)) { return true; }
            return DeityProtect.plugin.config.getBoolean(String.format(DeityProtectionConfigHelper.WORLD_EDIT_NODE, world.getName()));
        } else {
            return false;
        }
    }
    
}
