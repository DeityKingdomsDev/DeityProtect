package com.imdeity.protect;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.imdeity.protect.api.DeityChunk;
import com.imdeity.protect.api.SimpleDeityChunk;
import com.imdeity.protect.utils.DatabaseResults;

public class ProtectionManager {

    private static List<DeityChunk> loadedChunks = new ArrayList<DeityChunk>();

    public static DeityChunk getChunk(String worldname, int xCoord, int zCoord) {
        for (DeityChunk chunk : loadedChunks) {
            if (chunk.isChunk(worldname, xCoord, zCoord)) { return chunk; }
        }
        String sql = "SELECT * FROM " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " WHERE world = ? AND x_coord = ? AND z_coord = ?;";
        DatabaseResults query = DeityProtect.plugin.db.read2(sql, worldname, xCoord, zCoord);
        if (query != null && query.hasRows()) {
            try {
                int id = query.getInteger(0, "id");
                World world = DeityProtect.plugin.getServer().getWorld(worldname);
                String owner = query.getString(0, "owner");
                DeityChunk chunk = new SimpleDeityChunk(id, world, xCoord, zCoord, owner);
                loadedChunks.add(chunk);
                return chunk;
            } catch (SQLDataException e) {
                e.printStackTrace();
            }
        }
        return new SimpleDeityChunk(-1, null, xCoord, zCoord, null);
    }

    public static void addDeityChunkToCache(DeityChunk chunk) {
        if (chunk == null || chunk.getWorld() == null) { return; }
        for (DeityChunk c : loadedChunks) {
            if (c.isChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ())) {
                if (!(c instanceof SimpleDeityChunk)) {
                    return;
                }
            }
        }
        loadedChunks.add(chunk);
    }

    public static int addNewDeityChunk(String world, int xCoord, int zCoord) {
        String sql = "INSERT INTO " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " (owner, world, x_coord, z_coord) VALUES (null, ?, ?, ?);";
        DeityProtect.plugin.db.write(sql, world, xCoord, zCoord);
        int id = getChunk(world, xCoord, zCoord).getId();
        removeDeityChunkFromCache(id);
        return id;
    }

    public static void removeDeityChunk(String world, int xCoord, int zCoord) {
        String sql = "DELETE FROM " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " WHERE world = ? AND x_coord = ? AND z_coord = ?;";
        DeityProtect.plugin.db.write(sql, world, xCoord, zCoord);
        removeDeityChunkFromCache(world, xCoord, zCoord);
    }

    public static void removeDeityChunk(int id) {
        String sql = "DELETE FROM " + DeityProtect.plugin.db.tableName("deity_protect_", "chunks") + " WHERE id = ?";
        DeityProtect.plugin.db.write(sql, id);
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
