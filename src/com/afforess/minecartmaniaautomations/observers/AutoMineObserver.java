package com.afforess.minecartmaniaautomations.observers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
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
                    if (slot.getType().isBlock() && !((id == Material.SAND.getId()) || (id == Material.GRAVEL.getId()))) {
                        staticReplacement = slot.clone();
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
                if (minecart.removeItem(staticReplacement.getTypeId(), 1, staticReplacement.getDurability())) {
                    final ItemStack is = AutomationsUtils.getDropsForBlock(random, id, data, 0);
                    if (is != null) {
                        if (minecart.addItem(is)) {
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), staticReplacement.getTypeId(), x, y, z);
                            MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), x, y, z, staticReplacement.getDurability());
                            return true;
                        }
                    } else {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), staticReplacement.getTypeId(), x, y, z);
                        MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), x, y, z, staticReplacement.getDurability());
                        return true;
                    }
                } else
                    return false;
                
            }
            return false;
        }
        
        final List<Integer> blocks = getAdjacentBlockTypes(minecart, x, y, z);
        for (final int type : blocks) {
            if ((type == Material.SIGN.getId()) || (type == Material.WALL_SIGN.getId()) || (type == Material.TORCH.getId()) || (type == Material.REDSTONE_TORCH_ON.getId()) || (type == Material.REDSTONE_TORCH_OFF.getId()) || (type == Material.STONE_BUTTON.getId()) || (type == Material.LEVER.getId()) || (type == Material.WATER.getId()) || (type == Material.STATIONARY_WATER.getId()) || (type == Material.LAVA.getId()) || (type == Material.STATIONARY_LAVA.getId()))
                return false;
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
    
    private List<Integer> getAdjacentBlockTypes(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        final ArrayList<Integer> l = new ArrayList<Integer>();
        final int types[] = new int[4];
        types[0] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x + 1, y, z);
        types[1] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x - 1, y, z);
        types[2] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y + 1, z);
        types[3] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y - 1, z);
        for (final int type : types) {
            if (!l.contains(type)) {
                l.add(type);
            }
        }
        return l;
    }
}
