package com.afforess.minecartmaniaautomations;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class TestUtils {
    
    @Test
    public void test_getDropsForBlocks() {
        Random rand = new Random();
        ItemStack results = AutomationsUtils.getDropsForBlock(rand, 21, 0, 0); // Lapis Ore
        assertEquals("getDropsForBlocks: Failed to return correct ID", Material.INK_SACK.getId(), results.getTypeId());
        assertEquals("getDropsForBlocks: Failed to return correct Data", 4, results.getDurability());
    }
    
}
