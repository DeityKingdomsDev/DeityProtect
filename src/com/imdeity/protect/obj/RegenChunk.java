package com.imdeity.protect.obj;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.protect.DeityProtect;

public class RegenChunk {
    
    private int id;
    private World world;
    private int xCoord;
    private int zCoord;
    private Date lastUpdated;
    
    public RegenChunk(int id, World world, int xCoord, int zCoord, Date lastUpdated) {
        this.id = id;
        this.world = world;
        this.xCoord = xCoord;
        this.zCoord = zCoord;
        this.lastUpdated = lastUpdated;
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
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    private Location getMinLocation() {
        return new Location(world, getX() * 16, 0, getZ() * 16);
    }
    
    private Location getMaxLocation() {
        return new Location(world, (getX() * 16) + 15, world.getMaxHeight(), (getZ() * 16) + 15);
    }
    
    public void regen() {
        System.out.println("regenning: " + world.getName() + ", " + xCoord + ", " + zCoord + " | " + lastUpdated);
        DeityAPI.getAPI().getWorldEditAPI().regenArea(getMinLocation(), getMaxLocation());
    }
    
    public void update() {
        this.lastUpdated = new Date();
        String sql = "UPDATE " + DeityProtect.getRegenTable() + " SET last_updated = ? WHERE id = ?;";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, lastUpdated, id);
    }
    
    public void remove() {
        DeityAPI.getAPI().getDataAPI().getMySQL().write("DELETE FROM " + DeityProtect.getRegenTable() + " WHERE id = ?;", id);
    }
}
