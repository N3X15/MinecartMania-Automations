package com.afforess.minecartmaniaautomations;

import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;

/**
 * Register with Minecart Mania to observe blocks seen by carts
 * 
 * @author Rob "N3X15" Nelson <nexisentertainment@gmail.com>
 * 
 */
public abstract class BlockObserver {
    public final String name;
    public Item blockType = Item.AIR; // AIR == any
    
    public BlockObserver(final String name) {
        this.name = name;
    }
    
    public BlockObserver byBlockType(Item type) {
        this.blockType = type;
        return this;
    }
    
    /**
     * 
     * @param minecart
     * @param x
     * @param y
     * @param z
     * @return Whether the block or anything else was changed (dirty flag)
     */
    public abstract boolean onBlockSeen(MinecartManiaStorageCart minecart,
            int x, int y, int z);
}
