package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniaautomations.WoolColors;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoPumpkinObserver extends BlockObserver {
    
    private Random random;
    
    public AutoPumpkinObserver() {
        super("AutoPumpkin");
    }
    
    public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
            int z) {
        if (random == null) {
            random = new Random(x * y + z);
        }
        int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y+1, z);
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y+1, z);
        int baseId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y-1, z);
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        // Grow stems via bonemeal, if the materials are present
        if (minecart.getDataValue("AutoFertilize") != null) {
            if (id == Material.PUMPKIN_STEM.getId()) {
                // NOT fully grown
                if (data != 0x7) {
                    // Do we even HAVE bonemeal?
                    if (minecart.amount(Item.BONEMEAL.getId(),(short) Item.BONEMEAL.getData()) > 0) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.PUMPKIN_STEM.getId(), x, y+1, z);
                            MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), x, y+1, z, 0x7);
                            return true;
                        }
                    }
                }
            }
        }
        if (id == Material.PUMPKIN.getId() && (baseId == Material.DIRT.getId() || baseId == Material.SOIL.getId() || baseId == Material.GRASS.getId())) {
            minecart.addItem(Material.PUMPKIN.getId());
            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y+1, z);
            return true;
        }
        if (minecart.getDataValue("Stems Too") != null || minecart.getDataValue("SmartForest") != null) {
            if (id == Material.PUMPKIN_STEM.getId()) {
                boolean removeStem = false;
                if (minecart.getDataValue("Stems Too") != null) {
                    removeStem = (data == 0x7); // Fully Grown
                }
                if (minecart.getDataValue("SmartForest") != null && !removeStem) {
                    int belowData = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y-1, z);
                    removeStem = !(belowId == Material.WOOL.getId() && belowData == WoolColors.ORANGE.ordinal());
                }
                if (removeStem) {
                    minecart.addItem(AutomationsUtils.getDropsForBlock(random, id, data, 0));
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y+1, z);
                    return true;
                }
            }
        }
        return false;
    }
    
}
