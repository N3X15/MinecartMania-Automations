package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 2, z);
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
            belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 2, z);
            dirty = false;
        }
        int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
        if (id == Material.MELON_BLOCK.getId() && (belowId == Material.DIRT.getId() || belowId == Material.SOIL.getId() || belowId == Material.GRASS.getId())) {
            ItemStack toAdd = new ItemStack(Material.MELON_BLOCK.getId());
            if (!minecart.addItem(toAdd)) {
                minecart.minecart.getWorld().dropItem(new Location(minecart.minecart.getWorld(), x, y, z), toAdd);
            }
            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
            gdirty = dirty = true;
        }
        if (minecart.getDataValue("Stems Too") != null || minecart.getDataValue("SmartForest") != null) {
            //update data
            if (dirty) {
                id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
                belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 2, z);
                dirty = false;
            }
            if (id == Item.MELON_STEM.getId()) {
                boolean removeStem = false;
                if (minecart.getDataValue("Stems Too") != null) {
                    removeStem = (data == 0x7); // Fully Grown
                }
                if (minecart.getDataValue("SmartForest") != null && !removeStem) {
                    int belowData = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y - 2, z);
                    removeStem = !(belowId == Material.WOOL.getId() && belowData == WoolColors.LIME.ordinal());
                }
                if (removeStem) {
                    for (int i = 0; i < 3; i++) {
                        if (random.nextInt(15) <= data) {
                            minecart.addItem(Item.MELON_SEED.getId());
                        }
                    }
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    gdirty = dirty = true;
                }
            }
        }
        return gdirty;
    }
}
