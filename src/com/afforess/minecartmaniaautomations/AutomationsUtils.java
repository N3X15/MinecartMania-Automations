/**
 * 
 */
package com.afforess.minecartmaniaautomations;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.server.Block;

import org.bukkit.inventory.ItemStack;

/**
 * @author Rob
 * 
 */
public class AutomationsUtils {
    
    public static ItemStack getDropsForBlock(Random random, int id, int data,
            int miningWithTool) {
        Block b = net.minecraft.server.Block.byId[id];
        int numDrops = b.a(random);
        int dropId = b.a(miningWithTool, random);
        int dropData = data;
        if (dropId <= 0)
            return null;
        try {
            Method m = b.getClass().getMethod("a_", int.class);
            m.setAccessible(true);
            dropData = (Integer) m.invoke(b, miningWithTool);
        } catch (Exception e) {
        }
        return new ItemStack(dropId, numDrops, (short) dropData);
    }
    
}
