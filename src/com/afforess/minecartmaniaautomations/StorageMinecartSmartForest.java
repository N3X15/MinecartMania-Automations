package com.afforess.minecartmaniaautomations;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

/**
 * SmartForest functionality
 * @author Rob "N3X15" Nelson <nexis@7chan.org>
 *
 */
public class StorageMinecartSmartForest {
    public static String SMARTFOREST_ON = "SmartForest";
    public static String SMARTFOREST_OFF = "Forest Off";
    
    public static void doSmartForest(MinecartManiaStorageCart minecart) {
        if((minecart.getDataValue("SmartForest") == null)) {
            return;
        }

        if (minecart.getRange() < 1) {
            return;
        }

        Location loc = minecart.minecart.getLocation().clone();
        int range = minecart.getRange();
        int rangeY = minecart.getRangeY();
        for (int dx = -(range); dx <= range; dx++){
            for (int dy = -(rangeY); dy <= rangeY; dy++){
                for (int dz = -(range); dz <= range; dz++){
                    //Setup data
                    int x = loc.getBlockX() + dx;
                    int y = loc.getBlockY() + dy;
                    int z = loc.getBlockZ() + dz;
                    World w = minecart.minecart.getWorld();

                    int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
                    int aboveId = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z); 
                    int belowId = MinecartManiaWorld.getBlockIdAt(w, x, y-1, z); 
                    
                    if(minecart.getDataValue("SmartForest")!=null) {
                        if((id == Material.DIRT.getId() || id == Material.GRASS.getId())&&aboveId==0) {
                            Material mat = Material.getMaterial(belowId);
                            Item sapling = Item.SAPLING;
                            switch(mat) {
                                case WOOL:
                                    sapling = Item.SPRUCE_SAPLING;
                                    break;
                                case WOOD:
                                    sapling = Item.BIRCH_SAPLING;
                                    break;
                                    default: break;
                            }
                            if (minecart.contains(sapling)) {
                                minecart.removeItem(sapling.getId(), sapling.getData());
                                w.getBlockAt(x, y, z).setTypeIdAndData(sapling.getId(), (byte) sapling.getData(), true);
                            }
                        }
                    }
                }
            }
        }
    }
}
