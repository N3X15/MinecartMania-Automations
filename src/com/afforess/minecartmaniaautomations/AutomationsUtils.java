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
    
    public static ItemStack getDropsForBlock(final Random random, final int id, final int data, final int miningWithTool) {
        final Block b = net.minecraft.server.Block.byId[id];
        final int numDrops = b.getDropCount(0, random);
        final int dropId = b.getDropType(miningWithTool, random, 0);
        int dropData = data;
        if (dropId <= 0)
            return null;
        Method m = null;
        try {
            m = b.getClass().getDeclaredMethod("getDropData", int.class);
        } catch (final NoSuchMethodException e) {
            try {
                m = b.getClass().getMethod("getDropData", int.class);
            } catch (final NoSuchMethodException e2) {
                e.printStackTrace();
                for (Method method : b.getClass().getMethods()) {
                    if (method.getName().startsWith("get") && method.getReturnType().equals(int.class)) {
                        System.out.println(" * " + method);
                    }
                }
                return null;
            }
        }
        m.setAccessible(true);
        try {
            dropData = (Integer) m.invoke(b, miningWithTool);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ItemStack(dropId, numDrops, (short) dropData);
    }
    
}
