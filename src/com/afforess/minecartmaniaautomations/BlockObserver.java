package com.afforess.minecartmaniaautomations;

import java.util.Calendar;

import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;

/**
 * Register with Minecart Mania to observe blocks seen by carts
 * 
 * @author Rob "N3X15" Nelson <nexisentertainment@gmail.com>
 * 
 */
public abstract class BlockObserver {
    public final String name;
    private long totalMS = 0L;
    private long numTests = 0L;
    
    public BlockObserver(final String name) {
        this.name = name;
    }
    
    public boolean lookingForBlock(final int type, final int data) {
        return true;
    }
    
    public boolean blockSeen(MinecartManiaStorageCart cart, int x, int y, int z) {
        // Record start time
        long start = Calendar.getInstance().getTimeInMillis();
        
        // Actually do shit
        boolean o = onBlockSeen(cart, x, y, z);
        
        // Record how long it took
        totalMS += (Calendar.getInstance().getTimeInMillis() - start);
        numTests++;
        
        return o;
    }
    
    /**
     * Process seen blocks
     * @param minecart
     * @param x
     * @param y
     * @param z
     * @return Whether the block or anything else was changed (dirty flag)
     */
    protected abstract boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y, int z);
    
    public long getAverageMSPerOp() {
        return totalMS / numTests;
    }
}
