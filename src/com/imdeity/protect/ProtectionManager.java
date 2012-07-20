package com.imdeity.protect;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.protect.api.DeityChunk;
import com.imdeity.protect.api.SimpleDeityChunk;

public class ProtectionManager {
    
    public static List<DeityChunk> loadedChunks = new ArrayList<DeityChunk>();
    
    public static DeityChunk getChunk(String worldname, int xCoord, int zCoord) {
        for (DeityChunk chunk : loadedChunks) {
            if (chunk.isChunk(worldname, xCoord, zCoord) && chunk.getId() > 0) { return chunk; }
        }
        return new SimpleDeityChunk(-1, DeityProtect.plugin.getServer().getWorld(worldname), xCoord, zCoord, null);
    }
    
    private static DeityChunk getChunkFromSQL(String worldname, int xCoord, int zCoord) {
        for (DeityChunk chunk : loadedChunks) {
            if (chunk.isChunk(worldname, xCoord, zCoord) && chunk.getId() > 0) { return chunk; }
        }
        String sql = "SELECT * FROM " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + " WHERE world = ? AND x_coord = ? AND z_coord = ?;";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql, worldname, xCoord, zCoord);
        if (query != null && query.hasRows()) {
            try {
                int id = query.getInteger(0, "id");
                World world = DeityProtect.plugin.getServer().getWorld(worldname);
                String owner = query.getString(0, "owner");
                DeityChunk chunk = new SimpleDeityChunk(id, world, xCoord, zCoord, owner);
                addDeityChunkToCache(chunk);
                return chunk;
            } catch (SQLDataException e) {
                e.printStackTrace();
            }
        }
        return new SimpleDeityChunk(-1, DeityProtect.plugin.getServer().getWorld(worldname), xCoord, zCoord, null);
    }
    
    public static void loadChunks() {
        String sql = "SELECT * FROM " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + ";";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql);
        if (query != null && query.hasRows()) {
            for (int i = 0; i < query.rowCount(); i++) {
                try {
                    int id = query.getInteger(i, "id");
                    World world = DeityProtect.plugin.getServer().getWorld(query.getString(i, "world"));
                    String owner = query.getString(i, "owner");
                    int xCoord = query.getInteger(i, "x_coord");
                    int zCoord = query.getInteger(i, "z_coord");
                    DeityChunk chunk = new SimpleDeityChunk(id, world, xCoord, zCoord, owner);
                    addDeityChunkToCache(chunk);
                } catch (SQLDataException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void reload() {
        loadedChunks.clear();
        loadChunks();
    }
    
    public static DeityChunk getChunk(Location location) {
        return getChunk(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
    }
    
    public static void addDeityChunkToCache(DeityChunk chunk) {
        int index = -1;
        for (int i = 0; i < loadedChunks.size(); i++) {
            DeityChunk c = loadedChunks.get(i);
            if (c.getId() == chunk.getId() && (c instanceof SimpleDeityChunk)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            loadedChunks.remove(index);
        }
        loadedChunks.add(chunk);
    }
    
    public static int addNewDeityChunk(String world, int xCoord, int zCoord) {
        String sql = "INSERT INTO " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + " (owner, world, x_coord, z_coord) VALUES (null, ?, ?, ?);";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, world, xCoord, zCoord);
        int id = getChunkFromSQL(world, xCoord, zCoord).getId();
        
        return id;
    }
    
    public static void removeDeityChunk(String world, int xCoord, int zCoord) {
        String sql = "DELETE FROM " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + " WHERE world = ? AND x_coord = ? AND z_coord = ?;";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, world, xCoord, zCoord);
        removeDeityChunkFromCache(world, xCoord, zCoord);
    }
    
    public static void removeDeityChunk(int id) {
        String sql = "DELETE FROM " + DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_protect_", "chunks") + " WHERE id = ?";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, id);
        removeDeityChunkFromCache(id);
    }
    
    public static void removeDeityChunkFromCache(int id) {
        for (int i = 0; i < loadedChunks.size(); i++) {
            DeityChunk chunk = loadedChunks.get(i);
            if (chunk.getId() == id) {
                loadedChunks.remove(i);
                return;
            }
        }
    }
    
    private static void removeDeityChunkFromCache(String world, int xCoord, int zCoord) {
        for (int i = 0; i < loadedChunks.size(); i++) {
            DeityChunk chunk = loadedChunks.get(i);
            if (chunk.isChunk(world, xCoord, zCoord)) {
                loadedChunks.remove(i);
                return;
            }
        }
    }
}
