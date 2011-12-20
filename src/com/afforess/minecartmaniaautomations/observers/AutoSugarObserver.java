package com.afforess.minecartmaniaautomations.observers;

import org.bukkit.Material;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoSugarObserver extends BlockObserver {
    
    public AutoSugarObserver() {
        super("AutoSugar");
    }
    
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        boolean gdirty = false;
        if ((minecart.getDataValue("AutoSugar") == null) && (minecart.getDataValue("AutoPlant") == null))
            return false;
        
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
        
        //Harvest Sugar
        if (minecart.getDataValue("AutoSugar") != null) {
            
            // Check for sugar blocks and ensure they're the top one in the stack. 
            // Breaking sugar below the top will result in cane on the track which can stop the cart
            // until autocollection is turned back on.
            
            if ((id == Material.SUGAR_CANE_BLOCK.getId()) && (aboveId != Material.SUGAR_CANE_BLOCK.getId())) {
                if ((belowId == Material.GRASS.getId()) || (belowId == Material.DIRT.getId())) {
                    if (minecart.getDataValue("AutoPlant") == null) {
                        minecart.addItem(Material.SUGAR_CANE.getId());
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    }
                } else {
                    minecart.addItem(Material.SUGAR_CANE.getId());
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                }
            }
        }
        
        //update data
        id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
        
        //Replant cane
        if (minecart.getDataValue("AutoPlant") != null) {
            if ((id == Material.GRASS.getId()) || (id == Material.DIRT.getId())) {
                if (aboveId == Material.AIR.getId()) {
                    
                    // Need to check for water or the cane will not plant.
                    final int water1 = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x + 1, y, z);
                    final int water2 = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x - 1, y, z);
                    final int water3 = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z + 1);
                    final int water4 = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z - 1);
                    
                    boolean foundwater = false;
                    
                    if ((water1 == Material.WATER.getId()) || (water1 == Material.STATIONARY_WATER.getId())) {
                        foundwater = true;
                    }
                    if ((water2 == Material.WATER.getId()) || (water2 == Material.STATIONARY_WATER.getId())) {
                        foundwater = true;
                    }
                    if ((water3 == Material.WATER.getId()) || (water3 == Material.STATIONARY_WATER.getId())) {
                        foundwater = true;
                    }
                    if ((water4 == Material.WATER.getId()) || (water4 == Material.STATIONARY_WATER.getId())) {
                        foundwater = true;
                    }
                    
                    if (foundwater == true) {
                        
                        if (minecart.removeItem(Material.SUGAR_CANE.getId())) {
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.SUGAR_CANE_BLOCK.getId(), x, y + 1, z);
                            gdirty = true;
                        }
                    }
                }
            }
        }
        return gdirty;
    }
    
}
