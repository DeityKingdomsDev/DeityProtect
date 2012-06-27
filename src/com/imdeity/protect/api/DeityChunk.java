package com.imdeity.protect.api;

import org.bukkit.World;

import com.imdeity.protect.DeityProtect;
import com.imdeity.protect.ProtectionManager;
import com.imdeity.protect.enums.DeityChunkPermissionTypes;

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

    public boolean isChunk(String world, int xCoord, int zCoord) {
        if (this.world != null && world != null && this.world.getName().equalsIgnoreCase(world) && this.xCoord == xCoord && this.zCoord == zCoord) { return true; }
        return false;
    }

    public void hasUpdated() {
        this.hasUpdated = true;
    }

    public void save() {
        if (hasUpdated) {
            hasUpdated = false;
            String sql = "UPDATE " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " SET owner = ?, world = ?, x_coord = ?, z_coord = ? WHERE id = ?;";
            DeityProtect.plugin.db.write(sql, owner, world.getName(), xCoord, zCoord, id);
        }
    }

    public void remove() {
        String sql = "DELETE FROM " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " WHERE id = ?";
        DeityProtect.plugin.db.write(sql, id);
    }

    public String getOwner() {
        return owner;
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

    public boolean canHostileMobsSpawn() {
        return false;
    }

    public boolean canExplosionsOccur() {
        return false;
    }

    public abstract boolean runPermissionCheck(DeityChunkPermissionTypes type, String playerToVerify);
}
