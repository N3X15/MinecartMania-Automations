package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoFarmObserver extends BlockObserver {
    
    private Random random;
    
    public AutoFarmObserver() {
        super("AutoFarm");
    }
    
    @Override
    public boolean lookingForBlock(final int type, final int data) {
        return ((type == Material.CROPS.getId()) || (type == Material.AIR.getId()));
    }
    
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        if (random == null) {
            random = new Random();
        }
        if ((minecart.getDataValue("AutoHarvest") == null) && (minecart.getDataValue("AutoTill") == null) && (minecart.getDataValue("AutoSeed") == null))
            return false;
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        //int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
        int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
        boolean dirty = false; //set when the data gets changed
        boolean gdirty = false;
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        {
            // Grow crops via bonemeal, if the materials are present
            if (minecart.getDataValue("AutoFertilize") != null) {
                if (id == Material.CROPS.getId()) {
                    // NOT fully grown
                    if (data != 0x7) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.CROPS.getId(), x, y, z);
                            MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), x, y, z, 0x7);
                            gdirty = dirty = true;
                        } else {
                            //System.out.println("Can't remove bonemeal");
                        }
                    } else {
                        //System.out.println("Can't find enough bonemeal");
                    }
                }
            }
            //update data
            if (dirty) {
                id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
                data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
                //aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
                dirty = false;
            }
        }
        //Harvest fully grown crops first
        if (minecart.getDataValue("AutoHarvest") != null) {
            if (id == Material.CROPS.getId()) {
                //fully grown
                if (data == 0x7) {
                    minecart.addItem(Material.WHEAT.getId());
                    minecart.addItem(Material.SEEDS.getId());
                    if (random.nextBoolean()) { //Randomly add second seed.
                        minecart.addItem(Material.SEEDS.getId());
                    }
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    gdirty = dirty = true;
                }
            }
        }
        //update data
        if (dirty) {
            id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
            //aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
            dirty = false;
        }
        //till soil
        if (minecart.getDataValue("AutoTill") != null) {
            if ((belowId == Material.GRASS.getId()) || (belowId == Material.DIRT.getId())) {
                if (id == Material.AIR.getId()) {
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.SOIL.getId(), x, y - 1, z);
                    gdirty = dirty = true;
                }
            }
        }
        
        //update data
        if (dirty) {
            id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
            //aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
            belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 1, z);
            dirty = false;
        }
        //Seed tilled land 
        if (minecart.getDataValue("AutoSeed") != null) {
            if (belowId == Material.SOIL.getId()) {
                if (id == Material.AIR.getId()) {
                    if (minecart.removeItem(Material.SEEDS.getId())) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.CROPS.getId(), x, y, z);
                        gdirty = dirty = true;
                    }
                }
            }
        }
        
        return gdirty;
    }
    
}
