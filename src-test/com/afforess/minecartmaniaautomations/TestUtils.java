package com.afforess.minecartmaniaautomations;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.minecraft.server.AchievementList;
import net.minecraft.server.Item;

import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class TestUtils {
    
    @Test
    public void test_getDropsForBlocks() {
        final Random rand = new Random();
        AchievementList.a(); //REQUIRED for successful test.
        final int isid = Item.INK_SACK.id;
        final ItemStack results = AutomationsUtils.getDropsForBlock(rand, 21, 0, 0); // Lapis Ore
        assertEquals("getDropsForBlocks: Failed to return correct ID", isid, results.getTypeId());
        assertEquals("getDropsForBlocks: Failed to return correct Data", 4, results.getDurability());
    }
    
}
