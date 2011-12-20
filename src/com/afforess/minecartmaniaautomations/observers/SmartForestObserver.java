package com.afforess.minecartmaniaautomations.observers;

import org.bukkit.Material;
import org.bukkit.World;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniaautomations.WoolColors;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

/**
 * SmartForest functionality
 * 
 * @author Rob "N3X15" Nelson <nexis@7chan.org>
 * 
 */
public class SmartForestObserver extends BlockObserver {
    public SmartForestObserver() {
        super("SmartForest");
    }
    
    public static String SMARTFOREST_ON = "SmartForest";
    public static String SMARTFOREST_OFF = "Forest Off";
    
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        if ((minecart.getDataValue("SmartForest") == null))
            return false;
        
        boolean dirty = false;
        
        final World w = minecart.minecart.getWorld();
        
        final int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
        final int aboveId = MinecartManiaWorld.getBlockIdAt(w, x, y + 1, z);
        final int belowId = MinecartManiaWorld.getBlockIdAt(w, x, y - 1, z);
        
        if (minecart.getDataValue("SmartForest") != null) {
            final Material mat = Material.getMaterial(belowId);
            Item sapling = null;
            if (aboveId == 0) {
                if ((id == Material.DIRT.getId()) || (id == Material.GRASS.getId())) {
                    switch (mat) {
                        case WOOL:
                            final WoolColors data = WoolColors.getWoolColor(MinecartManiaWorld.getBlockData(w, x, y - 1, z));
                            switch (data) {
                                case GREEN:
                                    sapling = Item.CACTUS;
                                    break;
                                case RED:
                                    sapling = Item.RED_ROSE;
                                    break;
                                case YELLOW:
                                    sapling = Item.YELLOW_FLOWER;
                                    break;
                                default:
                                    sapling = Item.SPRUCE_SAPLING;
                                    break;
                            }
                            break;
                        case WOOD:
                            sapling = Item.BIRCH_SAPLING;
                            break;
                        case STONE:
                            sapling = Item.SAPLING;
                            break;
                        case COBBLESTONE:
                            sapling = Item.RED_ROSE;
                            break;
                        case SANDSTONE:
                            sapling = Item.YELLOW_FLOWER;
                            break;
                    }
                }
                if (id == Material.SOIL.getId()) {
                    switch (mat) {
                        case WOOL:
                            final WoolColors data = WoolColors.getWoolColor(MinecartManiaWorld.getBlockData(w, x, y - 1, z));
                            switch (data) {
                                case LIME:
                                    sapling = Item.MELON_SEED;
                                    break;
                                case ORANGE:
                                    sapling = Item.PUMPKIN_SEED;
                                    break;
                            }
                            break;
                    }
                }
                if (id == Material.SAND.getId()) {
                    switch (mat) {
                        case WOOL:
                            final WoolColors data = WoolColors.getWoolColor(MinecartManiaWorld.getBlockData(w, x, y - 1, z));
                            switch (data) {
                                case GREEN:
                                    sapling = Item.CACTUS;
                                    break;
                            }
                            break;
                    }
                }
            }
            if (sapling != null) {
                if (minecart.contains(sapling.getId(), (short) sapling.getData())) {
                    minecart.removeItem(sapling.getId(), 1, (short) sapling.getData());
                    // Switch to sapling version, if needed
                    switch (sapling) {
                        case MELON_SEED:
                            sapling = Item.MELON_STEM;
                            break;
                        case PUMPKIN_SEED:
                            sapling = Item.PUMPKIN_STEM;
                            break;
                    }
                    w.getBlockAt(x, y + 1, z).setTypeIdAndData(sapling.getId(), (byte) sapling.getData(), true);
                    dirty = true;
                }
            }
        }
        return dirty;
    }
}
