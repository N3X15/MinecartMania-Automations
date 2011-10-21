package com.afforess.minecartmaniaautomations;

import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;

/**
 * Register with Minecart Mania to observe blocks seen by carts
 * 
 * @author Rob "N3X15" Nelson <nexisentertainment@gmail.com>
 * 
 */
public abstract class BlockObserver {
    public final String name;
    
    public BlockObserver(final String name) {
        this.name = name;
        System.out.println("INIT BlockListener "+name);
    }
    
    public boolean lookingForBlock(int type, int data) {
        return true;
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
