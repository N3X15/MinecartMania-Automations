package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class CropObserver extends BlockObserver {
    
    private final int crop;
    private final int seedType;
    private int baseBlockType;
    private int minHeight;
    private int maxHeight;
    private ItemStack drop = null;
    private final Random random;
    private final int controlBlockId;
    
    public CropObserver(final String name, final Material crop, final Material seed, final Material controlBlockID) {
        super(name);
        this.crop = crop.getId();
        seedType = seed.getId();
        baseBlockType = Material.SOIL.getId();
        minHeight = 0;
        maxHeight = 7;
        controlBlockId = controlBlockID.getId();
        random = new Random();
    }
    
    public CropObserver setBase(final Material block) {
        baseBlockType = block.getId();
        return this;
    }
    
    public CropObserver setHeightRange(final int min, final int max) {
        minHeight = min;
        maxHeight = max;
        return this;
    }
    
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        if (minecart.getDataValue(name) == null)
            return false;
        
        final World w = minecart.getWorld();
        final int baseBlock = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
        final int stalk = MinecartManiaWorld.getBlockIdAt(w, x, y + 1, z);
        final int stalkData = MinecartManiaWorld.getBlockData(w, x, y + 1, z);
        final int controlBlock = MinecartManiaWorld.getBlockIdAt(w, x, y - 1, z);
        
        if (baseBlock != baseBlockType)
            return false;
        
        if (stalk != crop) {
            if (minecart.getDataValue("SmartForest") != null) {
                if ((controlBlockId != 0) && (controlBlock != controlBlockId))
                    return false;
            }
            if (minecart.contains(seedType)) {
                if (minecart.removeItem(seedType)) {
                    MinecartManiaWorld.setBlockAt(w, crop, x, y + 1, z);
                    MinecartManiaWorld.setBlockData(w, minHeight, x, y + 1, z);
                    return true;
                }
            }
        }
        
        if ((stalk == crop) && (stalkData == maxHeight)) {
            minecart.addItem(getDrop(w, stalkData));
            MinecartManiaWorld.setBlockAt(w, 0, x, y + 1, z);
            MinecartManiaWorld.setBlockData(w, 0, x, y + 1, z);
            return true;
        }
        return true;
    }
    
    private ItemStack getDrop(final World w, final int data) {
        if (drop == null)
            return AutomationsUtils.getDropsForBlock(random, crop, data, 0);
        else
            return drop;
    }
    
    public BlockObserver setDrop(final ItemStack is) {
        drop = is;
        return this;
    }
    
}
