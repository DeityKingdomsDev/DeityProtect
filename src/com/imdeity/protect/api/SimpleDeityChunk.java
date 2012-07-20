package com.imdeity.protect.api;

import org.bukkit.World;

import com.imdeity.protect.enums.DeityChunkPermissionTypes;

public class SimpleDeityChunk extends DeityChunk {
    
    public SimpleDeityChunk(int id, World world, int xCoord, int zCoord, String owner) {
        super(id, world, xCoord, zCoord, owner);
    }
    
    @Override
    public boolean runPermissionCheck(DeityChunkPermissionTypes type, String playerToVerify) {
        if (this.getId() <= 0) { return true; }
        if (type == DeityChunkPermissionTypes.ACCESS) { return true; }
        if (type == DeityChunkPermissionTypes.MOB_SPAWNING) { return true; }
        if (type == DeityChunkPermissionTypes.PVP) { return false; }
        if (this.getOwner() != null && this.getOwner().equalsIgnoreCase(playerToVerify)) { return true; }
        return false;
    }
    
}
