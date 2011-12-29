package com.afforess.minecartmaniaautomations.observers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.utils.ItemMatcher;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoMineObserver extends BlockObserver {
    
    private Random random;
    
    /**
     * @param name
     */
    public AutoMineObserver() {
        super("AutoMine");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.afforess.minecartmaniaautomations.BlockObserver#onBlockSeen(com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart, int, int, int)
     */
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        if (minecart.getDataValue("AutoMine") == null)
            return false;
        if (random == null) {
            random = new Random(x * y);
        }
        boolean dirty = false;
        //update data
        final int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        final int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
        final int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        
        ItemStack staticReplacement = null;
        // Don't remove sand or gravel if we don't have anything solid (in case there's rails on top)
        if ((id == Material.SAND.getId()) || (id == Material.GRAVEL.getId())) {
            for (final ItemStack slot : minecart.getContents().clone()) {
                if (slot != null) {
                    if (isStaticBlock(slot.getType())) {
                        staticReplacement = slot.clone();
                        MinecartManiaLogger.getInstance().info(String.format("[AutoMine] Static replacement set to %s", slot.getType().name()));
                        break;
                    }
                }
            }
            if (staticReplacement == null)
                return false;
        }
        // Don't mess with stuff underneath rails or redstone wire
        if ((aboveId == Material.RAILS.getId()) || (aboveId == Material.POWERED_RAIL.getId()) || (aboveId == Material.DETECTOR_RAIL.getId()) || (aboveId == Material.REDSTONE_WIRE.getId())) {
            // ... Unless we're on top of sand or gravel.  Then
            // remove it and replace with a solid block, if possible.
            if ((id == Material.SAND.getId()) || (id == Material.GRAVEL.getId())) {
                return fixLooseBlocks(minecart, minecart.minecart.getWorld(), id, data, x, y, z, staticReplacement);
            }
            return false;
        }
        
        final List<Material> blocks = getAdjacentBlockTypes(minecart, x, y, z);
        for (final Material type : blocks) {
            if (isTypeGnome(type)) {
                // ... Unless we're on top of sand or gravel.  Then
                // remove it and replace with a solid block, if possible.
                if ((id == Material.SAND.getId()) || (id == Material.GRAVEL.getId())) {
                    return fixLooseBlocks(minecart, minecart.minecart.getWorld(), id, data, x, y, z, staticReplacement);
                }
                return false;
            }
        }
        if ((id == Material.BEDROCK.getId()) || (id == Material.RAILS.getId()) || (id == Material.LAVA.getId()) || (id == Material.STATIONARY_LAVA.getId()) || (id == Material.WATER.getId()) || (id == Material.STATIONARY_WATER.getId()))
            return false;
        // Otherwise, if it's in the list, mine it.
        final ItemMatcher[] matchers = (ItemMatcher[]) minecart.getDataValue("AutoMine");
        
        for (final ItemMatcher matcher : matchers) {
            if (matcher.match(new ItemStack(id, 0, (short) data))) {
                final ItemStack is = AutomationsUtils.getDropsForBlock(random, id, data, 0);
                if (is != null) {
                    if (minecart.addItem(is)) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                } else {
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    dirty = true;
                }
            }
        }
        
        return dirty;
    }
    
    private boolean isTypeGnome(Material type) {
        switch (type) {
            case SIGN:
            case WALL_SIGN:
            case TORCH:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
            case STONE_BUTTON:
            case LEVER:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return true;
        }
        return false;
    }
    
    private boolean isStaticBlock(Material type) {
        switch (type) {
            case STONE:
            case DIRT:
            case COBBLESTONE:
            case WOOD:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case LOG:
            case LAPIS_ORE:
            case LAPIS_BLOCK:
            case SANDSTONE:
            case WOOL:
            case GOLD_BLOCK:
            case IRON_BLOCK:
            case DOUBLE_STEP:
            case BRICK:
            case TNT:
            case BOOKSHELF:
            case MOSSY_COBBLESTONE:
            case OBSIDIAN:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
            case SOIL:
            case SNOW_BLOCK:
            case CLAY:
            case PUMPKIN:
            case NETHERRACK:
            case JACK_O_LANTERN:
            case MELON_BLOCK:
            case MYCEL:
            case NETHER_BRICK:
            case ENDER_STONE:
                return true;
        }
        return false;
    }
    
    private List<Material> getAdjacentBlockTypes(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        final ArrayList<Material> l = new ArrayList<Material>();
        final Material types[] = new Material[4];
        types[0] = MinecartManiaWorld.getBlockAt(minecart.getWorld(), x + 1, y, z).getType();
        types[1] = MinecartManiaWorld.getBlockAt(minecart.getWorld(), x - 1, y, z).getType();
        types[2] = MinecartManiaWorld.getBlockAt(minecart.getWorld(), x, y + 1, z).getType();
        types[3] = MinecartManiaWorld.getBlockAt(minecart.getWorld(), x, y - 1, z).getType();
        for (final Material type : types) {
            if (!l.contains(type)) {
                l.add(type);
            }
        }
        return l;
    }
    
    private boolean fixLooseBlocks(MinecartManiaStorageCart cart, World world, int id, int data, int x, int y, int z, ItemStack staticReplacement) {
        ItemStack drop = AutomationsUtils.getDropsForBlock(random, id, data, 0);
        staticReplacement.setAmount(1);
        if (!cart.removeItem(staticReplacement.getTypeId(), 1, staticReplacement.getDurability())) {
            return false;
        }
        if (drop != null) {
            if (!cart.addItem(drop)) {
                return false;
            }
        }
        MinecartManiaWorld.setBlockAt(world, staticReplacement.getTypeId(), x, y, z);
        MinecartManiaWorld.setBlockData(world, staticReplacement.getDurability(), x, y, z);
        return true;
    }
}
