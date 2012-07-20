package com.imdeity.protect.events;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.imdeity.deityapi.api.DeityListener;
import com.imdeity.protect.DeityProtect;
import com.imdeity.protect.DeityProtectLangHelper;
import com.imdeity.protect.ProtectionManager;
import com.imdeity.protect.api.DeityChunk;

public class ProtectionListener extends DeityListener {
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BLOCK_BREAK));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BLOCK_PLACE));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_SIGN_EDIT));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
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
    
    @EventHandler(priority = EventPriority.NORMAL)
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
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.fixLocation(event.getFrom()).distance(this.fixLocation(event.getTo())) == 0.0) { return; }
        Player player = event.getPlayer();
        World world = event.getFrom().getWorld();
        Chunk chunkFrom = world.getChunkAt(event.getFrom());
        Chunk chunkTo = world.getChunkAt(event.getTo());
        if (!chunkFrom.equals(chunkTo)) {
            DeityChunk dChunkTo = ProtectionManager.getChunk(player.getWorld().getName(), chunkTo.getX(), chunkTo.getZ());
            
            if (!dChunkTo.hasAccessPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_ENTER));
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BUCKET_EMPTY));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if ((player != null) && (player instanceof Player)) {
            Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
            DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
            
            if (!dChunk.hasEditPemission(player.getName())) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BUCKET_FILL));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        if (clicked == null) { return; }
        if ((player == null) || !(player instanceof Player)) { return; }
        
        Chunk chunk = player.getWorld().getChunkAt(clicked.getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (!dChunk.hasUsePemission(player.getName())) {
            if (clicked.getType().equals(Material.CHEST)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_CHEST));
                event.setCancelled(true);
            } else if ((clicked.getType().equals(Material.WOODEN_DOOR)) || (clicked.getType().equals(Material.IRON_DOOR))) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_DOOR));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.STONE_BUTTON)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_BUTTON));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.LEVER)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_LEVER));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.FURNACE)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_FURNACE));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.BED)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_BED));
                event.setCancelled(true);
            } else if ((clicked.getType().equals(Material.WOOD_PLATE)) || (clicked.getType().equals(Material.STONE_PLATE))) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_PRESSURE_PLATE));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.WORKBENCH)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_WORKBENCH));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.TRAP_DOOR)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_TRAP_DOOR));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.DISPENSER)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_DISPENSER));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.NOTE_BLOCK)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_NOTE_BLOCK));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.JUKEBOX)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_JUKEBOX));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.BOAT)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_BOAT));
                event.setCancelled(true);
            } else if (clicked.getType().equals(Material.MINECART)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_MINECART));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity eAttacker = event.getDamager();
        Entity eDefender = event.getEntity();
        if (eDefender instanceof Projectile) {
            Projectile proj = (Projectile) eAttacker;
            eAttacker = proj.getShooter();
        }
        if (eAttacker instanceof Player && eDefender instanceof Player) {
            Player pAttacker = (Player) eAttacker;
            Player pDefender = (Player) eDefender;
            DeityChunk dAttackerChunk = ProtectionManager.getChunk(pAttacker.getLocation());
            DeityChunk dDefenderChunk = ProtectionManager.getChunk(pDefender.getLocation());
            if (!dAttackerChunk.canPvp(pAttacker.getName()) || !dDefenderChunk.canPvp(pDefender.getName())) {
                event.setDamage(0);
                event.setCancelled(true);
                DeityProtect.plugin.chat.sendPlayerMessage(pAttacker, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_FIGHT));
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) { return; }
        Chunk chunk = event.getLocation().getWorld().getChunkAt(event.getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(event.getLocation().getWorld().getName(), chunk.getX(), chunk.getZ());
        if (!dChunk.canMobSpawn(event.getEntity().getType().getName())) {
            event.setCancelled(true);
        }
    }
    
    private boolean verifyBlockMove(Block block, BlockFace direction) {
        Block blockTo = block.getRelative(direction);
        Location loc = block.getLocation();
        Location locTo = blockTo.getLocation();
        DeityChunk chunkFrom = ProtectionManager.getChunk(block.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ());
        DeityChunk chunkTo = ProtectionManager.getChunk(block.getWorld().getName(), locTo.getChunk().getX(), locTo.getChunk().getZ());
        if (!chunkFrom.isChunk(chunkTo)) {
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