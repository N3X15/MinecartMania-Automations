package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.inventory.MinecartManiaInventory;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoTimberObserver extends BlockObserver {
    
    public AutoTimberObserver() {
        super("AutoTimber");
    }
    
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        boolean gdirty = false;
        if (minecart.getDataValue("AutoTimber") == null)
            return false;
        final World w = minecart.minecart.getWorld();
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        {
            boolean dirty = false;
            // Grow stems via bonemeal, if the materials are present
            if (minecart.getDataValue("AutoFertilize") != null) {
                final int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
                if (id == Material.SAPLING.getId()) {
                    // Do we even HAVE bonemeal?
                    if (minecart.amount(Item.BONEMEAL.getId(), (short) Item.BONEMEAL.getData()) > 0) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            final int treeSubtype = data & 3;
                            // Remove 1 unit of bonemeal and try to dump a tree
                            final int rand = ((new Random()).nextInt(10));
                            TreeType t = null;
                            switch (treeSubtype) {
                                case 1:
                                    if (rand == 0) {
                                        t = TreeType.TALL_REDWOOD;
                                    } else {
                                        t = TreeType.REDWOOD;
                                    }
                                    break;
                                case 2:
                                    t = TreeType.BIRCH;
                                    break;
                                default:
                                    if (rand == 0) {
                                        t = TreeType.BIG_TREE;
                                    } else {
                                        t = TreeType.TREE;
                                    }
                                    break;
                            }
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), 0, x, y, z);
                            if (!w.generateTree(new Location(w, x, y, z), t)) {
                                MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.SAPLING.getId(), x, y, z);
                                MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), data, x, y, z);
                            }
                            gdirty = dirty = true;
                        }
                    }
                }
            }
            //update data
            if (dirty) {
                id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
                dirty = false;
            }
        }
        if (id == Material.LOG.getId()) {
            int down = 1;
            while (MinecartManiaWorld.getBlockIdAt(w, x, y - down, z) == Material.LOG.getId()) {
                down++;
            }
            final int baseId = MinecartManiaWorld.getBlockIdAt(w, x, y - down, z);
            //base of tree
            if ((baseId == Material.DIRT.getId()) || (baseId == Material.GRASS.getId()) || (baseId == Material.LEAVES.getId())) {
                final Block base = w.getBlockAt(x, (y - down) + 1, z);
                //Attempt to replant the tree
                if (removeLogs(x, (y - down) + 1, z, w, minecart, false) && (minecart.getDataValue("AutoForest") != null)) {
                    final short saplingType = (short) (base.getData() & 8);
                    if (minecart.amount(Material.SAPLING.getId(), saplingType) > 0) {
                        if (minecart.removeItem(Material.SAPLING.getId(), saplingType)) {
                            w.getBlockAt(x, (y - down) + 1, z).setTypeIdAndData(Material.SAPLING.getId(), (byte) saplingType, true);
                            gdirty = true;
                        }
                    }
                }
            }
        }
        return gdirty;
    }
    
    private boolean removeLogs(final int posx, final int posy, final int posz, final World w, final MinecartManiaInventory inventory, final boolean recursing) {
        final boolean action = false;
        final int range = 1;
        for (int dx = -(range); dx <= range; dx++) {
            for (int dy = -(range); dy <= range; dy++) {
                for (int dz = -(range); dz <= range; dz++) {
                    //Setup data
                    final int x = posx + dx;
                    final int y = posy + dy;
                    final int z = posz + dz;
                    final int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
                    final int data = MinecartManiaWorld.getBlockData(w, x, y, z);
                    if (id == Material.LOG.getId()) {
                        final ItemStack logstack = Item.getItem(id, data).toItemStack();
                        if (!inventory.addItem(logstack)) {
                            if (recursing) {
                                MinecartManiaWorld.spawnDrop(w, x, y, z, logstack);
                            } else
                                return false;
                        }
                        MinecartManiaWorld.setBlockAt(w, 0, x, y, z);
                        removeLogs(x, y, z, w, inventory, true);
                    }
                }
            }
        }
        return action;
    }
}
