package com.imdeity.protect.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.imdeity.protect.DeityProtect;
import com.imdeity.protect.ProtectionManager;
import com.imdeity.protect.api.DeityChunk;

public class ProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());

            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cdestroy blocks &fhere!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cplace blocks &fhere!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());

            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cedit signs &fhere!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Chunk chunkFrom = event.getBlock().getChunk();
        if (event.getBlocks() != null && !event.getBlocks().isEmpty()) {
            for (Block block : event.getBlocks()) {
                Chunk chunkTo = block.getChunk();
                if (!chunkFrom.equals(chunkTo)) {
                    if (!verifyBlockMove(block, event.getDirection())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isSticky()) {
            Block block = event.getBlock();
            if (block.getType() != Material.PISTON_STICKY_BASE) { return; }
            block = block.getRelative(event.getDirection()).getRelative(event.getDirection());
            if ((block.getType() != Material.AIR) && (!block.isLiquid())) {
                if (!verifyBlockMove(block, event.getDirection().getOppositeFace())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.fixLocation(event.getFrom()).distance(this.fixLocation(event.getTo())) == 0.0) { return; }
        Player player = event.getPlayer();
        World world = event.getFrom().getWorld();
        Chunk chunkFrom = world.getChunkAt(event.getFrom());
        Chunk chunkTo = world.getChunkAt(event.getTo());
        if (!chunkFrom.equals(chunkTo)) {
            DeityChunk dChunkTo = ProtectionManager.getChunk(player.getWorld().getName(), chunkTo.getX(), chunkTo.getZ());

            if (!dChunkTo.hasAccessPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &center &fhere!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());

            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cempty buckets &fhere!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());

            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cempty buckets &fhere!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        if (clicked == null) { return; }
        if ((player != null) && (player instanceof Player)) { return; }

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (!dChunk.hasUsePemission(player.getName())) {
            if (clicked.getType().equals(Material.CHEST)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &copen chests &fhere!");
            } else if ((clicked.getType().equals(Material.WOODEN_DOOR)) || (clicked.getType().equals(Material.IRON_DOOR))) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &copen doors &fhere!");
            } else if (clicked.getType().equals(Material.STONE_BUTTON)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cpush buttons &fhere!");
            } else if (clicked.getType().equals(Material.LEVER)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cswitch levers &fhere!");
            } else if (clicked.getType().equals(Material.FURNACE)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse furnaces &fhere!");
            } else if (clicked.getType().equals(Material.BED)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse beds &fhere!");
            } else if ((clicked.getType().equals(Material.WOOD_PLATE)) || (clicked.getType().equals(Material.STONE_PLATE))) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse pressure plates &fhere!");
            } else if (clicked.getType().equals(Material.WORKBENCH)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse workbenches &fhere!");
            } else if (clicked.getType().equals(Material.TRAP_DOOR)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse trap doors &fhere!");
            } else if (clicked.getType().equals(Material.DISPENSER)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse dispensers &fhere!");
            } else if (clicked.getType().equals(Material.NOTE_BLOCK)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse note blocks &fhere!");
            } else if (clicked.getType().equals(Material.JUKEBOX)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cuse jukeboxes &fhere!");
            } else if (clicked.getType().equals(Material.BOAT)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cget in boats &fhere!");
            } else if (clicked.getType().equals(Material.MINECART)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, "&fYou aren't allowed to &cget in minecarts &fhere!");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if ((event.getPlayer() instanceof Player)) {
            Player player = event.getPlayer();
            World world = event.getPlayer().getWorld();
            Chunk chunk = world.getChunkAt(event.getRightClicked().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            if (!dChunk.hasUsePemission(((Player) event.getPlayer()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) { return; }
        Chunk chunk = event.getLocation().getWorld().getChunkAt(event.getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(event.getLocation().getWorld().getName(), chunk.getX(), chunk.getZ());
        List<EntityType> peacefulMobs = new ArrayList<EntityType>();

        peacefulMobs.add(EntityType.CHICKEN);
        peacefulMobs.add(EntityType.COW);
        peacefulMobs.add(EntityType.MUSHROOM_COW);
        peacefulMobs.add(EntityType.PIG);
        peacefulMobs.add(EntityType.SHEEP);
        peacefulMobs.add(EntityType.SQUID);
        peacefulMobs.add(EntityType.VILLAGER);
        peacefulMobs.add(EntityType.WOLF);
        peacefulMobs.add(EntityType.OCELOT);

        if (dChunk.canHostileMobsSpawn()) {
            EntityType entity = event.getEntity().getType();
            if (!peacefulMobs.contains(entity)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Chunk chunk = event.getLocation().getWorld().getChunkAt(event.getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(event.getLocation().getWorld().getName(), chunk.getX(), chunk.getZ());

        if (!dChunk.canExplosionsOccur()) {
            event.setCancelled(true);
        }
    }

    private boolean verifyBlockMove(Block block, BlockFace direction) {
        Block blockTo = block.getRelative(direction);
        Location loc = block.getLocation();
        Location locTo = blockTo.getLocation();
        DeityChunk chunkFrom = ProtectionManager.getChunk(block.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ());
        DeityChunk chunkTo = ProtectionManager.getChunk(block.getWorld().getName(), locTo.getChunk().getX(), locTo.getChunk().getZ());
        if (!chunkFrom.equals(chunkTo)) {
            if (chunkTo.getOwner() != null && chunkFrom.getOwner() != null) {
                if (!chunkTo.getOwner().equalsIgnoreCase(chunkFrom.getOwner())) { return false; }
            }
        }
        return true;
    }

    private Location fixLocation(Location loc) {
        loc.setX((int) loc.getX());
        loc.setY((int) loc.getY());
        loc.setZ((int) loc.getZ());
        return loc;
    }
}
