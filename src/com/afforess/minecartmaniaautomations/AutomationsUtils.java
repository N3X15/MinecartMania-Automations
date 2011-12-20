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
    
    public static ItemStack getDropsForBlock(final Random random, final int id, final int data, final int miningWithTool) {
        final Block b = net.minecraft.server.Block.byId[id];
        final int numDrops = b.a(random);
        final int dropId = b.getDropType(miningWithTool, random, 0);
        int dropData = data;
        if (dropId <= 0)
            return null;
        try {
            final Method m = b.getClass().getMethod("getDropData", int.class);
            m.setAccessible(true);
            dropData = (Integer) m.invoke(b, miningWithTool);
        } catch (final Exception e) {
        }
        return new ItemStack(dropId, numDrops, (short) dropData);
    }
    
}
