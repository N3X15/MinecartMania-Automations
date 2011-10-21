package com.afforess.minecartmaniaautomations.observers;

import org.bukkit.Material;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoCactusObserver extends BlockObserver {
    
    public AutoCactusObserver() {
        super("AutoCactus");
    }
    
    @Override
    public boolean lookingForBlock(int type, int data) {
        return (type == Material.CACTUS.getId());
    }
    
    @Override
    public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
            int z) {
        if ((minecart.getDataValue("AutoCactus") == null) && (minecart.getDataValue("AutoReCactus") == null)) {
            return false;
        }
        
        boolean dirty = false;
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
        
        //Harvest Cacti
        if (minecart.getDataValue("AutoCactus") != null) {
            
            // Like sugar, we need to break this from the top first. 
            if (id == Material.CACTUS.getId() && aboveId != Material.CACTUS.getId()) {
                if (belowId == Material.SAND.getId()) {
                    if (minecart.getDataValue("AutoReCactus") == null) {
                        // Only harvest the bottom if we're not replanting. 
                        minecart.addItem(Material.CACTUS.getId());
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                } else {
                    minecart.addItem(Material.CACTUS.getId());
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    dirty = true;
                }
            }
        }
        
        //update data
        id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        
        //Replant Cactus
        if (minecart.getDataValue("AutoReCactus") != null) {
            if (id == Material.SAND.getId()) {
                if (aboveId == Material.AIR.getId()) {
                    
                    // Need to check for blocks to the sides of the cactus position 
                    // as this would normally block planting.
                    
                    int sidemx = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x - 1, y + 1, z);
                    int sidepx = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x + 1, y + 1, z);
                    int sidemz = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z - 1);
                    int sidepz = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z + 1);
                    
                    boolean blockcactus = false;
                    
                    if (sidemx != Material.AIR.getId()) {
                        blockcactus = true;
                    }
                    if (sidepx != Material.AIR.getId()) {
                        blockcactus = true;
                    }
                    if (sidemz != Material.AIR.getId()) {
                        blockcactus = true;
                    }
                    if (sidepz != Material.AIR.getId()) {
                        blockcactus = true;
                    }
                    
                    if (blockcactus == false && minecart.removeItem(Material.CACTUS.getId())) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.CACTUS.getId(), x, y + 1, z);
                        dirty = true;
                    }
                }
            }
        }
        
        return dirty;
    }
    
}
