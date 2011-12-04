package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniaautomations.WoolColors;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoMelonObserver extends BlockObserver {
    
    private Random random;
    
    public AutoMelonObserver() {
        super("AutoMelon");
    }
    
    public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
            int z) {
        if (random == null) {
            random = new Random(x * y + z);
        }
        World w = minecart.getWorld();
        int data = MinecartManiaWorld.getBlockData(w, x, y+1, z);
        int id = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z);
        int baseId = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(w, x, y-1, z);
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        // Grow stems via bonemeal, if the materials are present
        if (minecart.getDataValue("AutoFertilize") != null) {
            if (id == Material.MELON_STEM.getId()) {
                // NOT fully grown
                if (data != 0x7) {
                    // Do we even HAVE bonemeal?
                    if (minecart.amount(Item.BONEMEAL) > 0) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            MinecartManiaWorld.setBlockAt(w, Material.MELON_STEM.getId(), x, y+1, z);
                            MinecartManiaWorld.setBlockData(w, x, y+1, z, 0x7);
                            return true;
                        }
                    }
                }
            }
        }
        if (id == Material.MELON_BLOCK.getId() && (baseId == Material.DIRT.getId() || baseId == Material.SOIL.getId() || baseId == Material.GRASS.getId())) {
            w.dropItemNaturally(new Location(w,x,y+1,z), AutomationsUtils.getDropsForBlock(random, id, data, 0));
            MinecartManiaWorld.setBlockAt(w, Material.AIR.getId(), x, y+1, z);
            return true;
        }
        if (minecart.getDataValue("Stems Too") != null || minecart.getDataValue("SmartForest") != null) {
            if (id == Material.MELON_STEM.getId()) {
                boolean removeStem = false;
                if (minecart.getDataValue("Stems Too") != null) {
                    removeStem = (data == 0x7); // Fully Grown
                }
                if (minecart.getDataValue("SmartForest") != null && !removeStem) {
                    int belowData = MinecartManiaWorld.getBlockData(w, x, y-1, z);
                    removeStem = !(belowId == Material.WOOL.getId() && belowData == WoolColors.LIME.ordinal());
                }
                if (removeStem) {
                    minecart.addItem(AutomationsUtils.getDropsForBlock(random, id, data, 0));
                    MinecartManiaWorld.setBlockAt(w, Material.AIR.getId(), x, y+1, z);
                    return true;
                }
            }
        }
        return false;
    }
    
}
