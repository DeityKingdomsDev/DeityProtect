package com.imdeity.protect.api;

import org.bukkit.World;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.protect.enums.DeityChunkPermissionTypes;
import com.imdeity.protect.obj.ProtectionManager;

public abstract class DeityChunk {
    private int id;
    private World world;
    private int xCoord = -1;
    private int zCoord = -1;
    private String owner;
    private boolean hasUpdated = false;
    
    public DeityChunk(int id, World world, int xCoord, int zCoord, String owner) {
        this.id = id;
        this.world = world;
        this.xCoord = xCoord;
        this.zCoord = zCoord;
        this.owner = owner;
        ProtectionManager.addDeityChunkToCache(this);
    }
    
    public int getId() {
        return id;
    }
    
    public World getWorld() {
        return world;
    }
    
    public int getX() {
        return xCoord;
    }
    
    public int getZ() {
        return zCoord;
    }
    
    public String getOwner() {
        return owner;
    }

    public boolean isChunk(String world, int xCoord, int zCoord) {
        if (this.world != null && world != null && this.world.getName().equalsIgnoreCase(world) && this.xCoord == xCoord
                && this.zCoord == zCoord) { return true; }
        return false;
    }
    
    public boolean isChunk(DeityChunk chunk) {
        return isChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }
    
    public void setOwner(String newOwner) {
        this.owner = newOwner;
        this.hasUpdated();
    }
    
    public boolean hasEditPemission(String playerToVerify) {
        return runPermissionCheck(DeityChunkPermissionTypes.EDIT, playerToVerify);
    }
    
    public boolean hasUsePemission(String playerToVerify) {
        return runPermissionCheck(DeityChunkPermissionTypes.USE, playerToVerify);
    }
    
    public boolean hasAccessPemission(String playerToVerify) {
        return runPermissionCheck(DeityChunkPermissionTypes.ACCESS, playerToVerify);
    }
    
    public boolean canMobSpawn(String mobType) {
        return runPermissionCheck(DeityChunkPermissionTypes.MOB_SPAWNING, mobType);
    }
    
    public boolean canPvp(String playerToVerify) {
        return runPermissionCheck(DeityChunkPermissionTypes.PVP, playerToVerify);
    }
    
    public boolean canExplode(String entityType) {
        return runPermissionCheck(DeityChunkPermissionTypes.EXPLOSION, entityType);
    }
    
    public abstract boolean runPermissionCheck(DeityChunkPermissionTypes type, String requester);

    public void hasUpdated() {
        this.hasUpdated = true;
    }

    public void save() {
        if (hasUpdated) {
            hasUpdated = false;
            String sql = "UPDATE " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks")
                    + " SET owner = ?, world = ?, x_coord = ?, z_coord = ? WHERE id = ?;";
            DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, owner, world.getName(), xCoord, zCoord, id);
        }
    }

    public void remove() {
        String sql = "DELETE FROM " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks")
                + " WHERE id = ?";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, id);
    }
}
