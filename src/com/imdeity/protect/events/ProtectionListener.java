package com.imdeity.protect.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityListener;
import com.imdeity.protect.DeityProtect;
import com.imdeity.protect.DeityProtectLangHelper;
import com.imdeity.protect.ProtectionManager;
import com.imdeity.protect.api.DeityChunk;

public class ProtectionListener extends DeityListener {
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
        Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        
        if (!dChunk.hasEditPemission(player.getName())) {
            DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BLOCK_BREAK));
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
        Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (!dChunk.hasEditPemission(player.getName())) {
            DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BLOCK_PLACE));
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
        Chunk chunk = player.getWorld().getChunkAt(event.getBlock().getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        
        if (!dChunk.hasEditPemission(player.getName())) {
            DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_SIGN_EDIT));
            event.setCancelled(true);
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
        if (DeityAPI.getAPI().getUtilAPI().fixLocation(event.getFrom()).distance(DeityAPI.getAPI().getUtilAPI().fixLocation(event.getTo())) == 0.0) { return; }
        Player player = event.getPlayer();
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
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
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
        Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        
        if (!dChunk.hasEditPemission(player.getName())) {
            DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BUCKET_EMPTY));
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
        Chunk chunk = player.getWorld().getChunkAt(event.getBlockClicked().getLocation());
        DeityChunk dChunk = ProtectionManager.getChunk(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        
        if (!dChunk.hasEditPemission(player.getName())) {
            DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_BUCKET_FILL));
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        if (clicked == null) { return; }
        if ((player == null) || !(player instanceof Player) || DeityProtect.hasOverride(player)) { return; }
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
            } else if (clicked.getType().equals(Material.TRIPWIRE)) {
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_TRIPWIRE));
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
        if (eAttacker instanceof Player && !DeityProtect.hasOverride((Player) eAttacker) && eDefender instanceof Player) {
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
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
            if (event.getEntity().getShooter() instanceof Player && !DeityProtect.hasOverride((Player) event.getEntity().getShooter())) {
                Player player = (Player) event.getEntity().getShooter();
                DeityProtect.plugin.chat.sendPlayerMessage(player, DeityProtect.plugin.language.getNode(DeityProtectLangHelper.INVALID_USE_ENDERPEARL));
                DeityAPI.getAPI().getPlayerAPI().getInventoryAPI().addItemToInventory(player.getInventory(), new ItemStack(Material.ENDER_PEARL, 1));
                event.setCancelled(true);
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
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Integer> blocksToRemove = new ArrayList<Integer>();
        for (int i = 0; i < event.blockList().size(); i++) {
            Block b = event.blockList().get(i);
            DeityChunk dChunk = ProtectionManager.getChunk(b.getLocation().getWorld().getName(), b.getLocation().getChunk().getX(), b.getLocation().getChunk().getZ());
            if (!dChunk.canExplode(event.getEntityType().getName())) {
                blocksToRemove.add(i);
            }
        }
        int numRemoved = 0;
        for (int i : blocksToRemove) {
            event.blockList().remove(i - numRemoved);
            numRemoved++;
        }
    }
}
