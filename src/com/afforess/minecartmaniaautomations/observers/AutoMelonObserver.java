package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;

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
        if (minecart.getDataValue("AutoMelon") == null)
            return false;
        if (random == null) {
            random = new Random(x * y + z);
        }
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
        boolean dirty = false; //set when the data gets changed
        boolean gdirty = false;
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        // Grow stems via bonemeal, if the materials are present
        if (minecart.getDataValue("AutoFertilize") != null) {
            int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
            if (id == Material.MELON_STEM.getId()) {
                // NOT fully grown
                if (data != 0x7) {
                    // Do we even HAVE bonemeal?
                    if (minecart.amount(Item.BONEMEAL) > 0) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.MELON_STEM.getId(), x, y, z);
                            MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), x, y, z, 0x7);
                            gdirty = dirty = true;
                        }
                    }
                }
            }
        }
        //update data
        if (dirty) {
            id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
            belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
            dirty = false;
        }
        int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
        if (id == Material.MELON.getId() && (belowId == Material.DIRT.getId() || belowId == Material.SOIL.getId() || belowId == Material.GRASS.getId())) {
            minecart.addItem(AutomationsUtils.getDropsForBlock(random, id, data, 0));
            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
            gdirty = dirty = true;
        }
        if (minecart.getDataValue("Stems Too") != null || minecart.getDataValue("SmartForest") != null) {
            //update data
            if (dirty) {
                data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
                id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
                belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
                dirty = false;
            }
            if (id == Material.MELON_STEM.getId()) {
                boolean removeStem = false;
                int controlBlock=MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 2, z);
                int controlBlockData=MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y - 2, z);
                if (minecart.getDataValue("Stems Too") != null) {
                    removeStem = (data == 0x7); // Fully Grown
                }
                if (minecart.getDataValue("SmartForest") != null && !removeStem) {
                    removeStem = !(controlBlock == Material.WOOL.getId() && controlBlockData == WoolColors.LIME.ordinal());
                }
                if (removeStem) {
                            minecart.addItem(AutomationsUtils.getDropsForBlock(random, id, data, 0));
                    
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    gdirty = dirty = true;
                }
            }
        }
        return gdirty;
    }
}
