/**
 * 
 */
package com.afforess.minecartmaniaautomations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.server.Block;

import org.bukkit.inventory.ItemStack;

/**
 * @author Rob
 * 
 */
public class AutomationsUtils {
    
    public static ItemStack getDropsForBlock(final Random random, final int id, final int data, final int fortune) {
        final Block b = net.minecraft.server.Block.byId[id];
        final int numDrops = b.getDropCount(0, random);
        final int dropId = b.getDropType(fortune, random, 0);
        final int dropData = getDropData(b.getClass(), b, random, id, data, fortune);
        if (dropId <= 0)
            return null;
        return new ItemStack(dropId, numDrops, (short) dropData);
    }
    
    /**
     * Get the getDropData method (if it's declared) and invoke it.
     * @param b
     * @param random
     * @param id
     * @param data
     * @param fortune
     * @return
     */
    @SuppressWarnings("unchecked")
    private static int getDropData(final Class<? extends Block> b, final Block block, final Random random, final int id, final int data, final int fortune) {
        Method m = null;
        try {
            m = b.getDeclaredMethod("getDropData", int.class);
        } catch (final NoSuchMethodException e) {
            // If not declared, try the super class.
            if (b.getSuperclass().equals(Block.class))
                // High as we can go; Since Block just returns 0, do the same.
                return 0;
            else
                return getDropData((Class<? extends Block>) b.getSuperclass(), block, random, id, data, fortune);
        }
        m.setAccessible(true);
        try {
            return (Integer) m.invoke(block, fortune);
        } catch (final IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
}
