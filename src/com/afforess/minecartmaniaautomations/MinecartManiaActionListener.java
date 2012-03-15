package com.afforess.minecartmaniaautomations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class MinecartManiaActionListener implements Listener {
    
    public List<BlockObserver> blockObservers = new ArrayList<BlockObserver>();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecartActionEvent(final MinecartActionEvent event) {
        if (!event.isActionTaken()) {
            final MinecartManiaMinecart minecart = event.getMinecart();
            if (minecart.isStorageMinecart()/* && minecart.isMoving() */) {
                final MinecartManiaStorageCart cart = (MinecartManiaStorageCart) minecart;
                checkSigns(cart);
                //Efficiency. Don't farm overlapping tiles repeatedly, waste of time
                final int interval = minecart.getDataValue("Farm Interval") == null ? -1 : (Integer) minecart.getDataValue("Farm Interval");
                if (interval > 0) {
                    minecart.setDataValue("Farm Interval", interval - 1);
                } else {
                    minecart.setDataValue("Farm Interval", minecart.getRange() / 2);
                    
                    if (minecart.getRange() < 1)
                        return;
                    
                    final Location loc = minecart.minecart.getLocation().clone();
                    final int range = minecart.getRange();
                    final int rangeY = minecart.getRangeY();
                    for (int dx = -(range); dx <= range; dx++) {
                        for (int dy = -(rangeY); dy <= rangeY; dy++) {
                            for (int dz = -(range); dz <= range; dz++) {
                                //Setup data
                                final int x = loc.getBlockX() + dx;
                                final int y = loc.getBlockY() + dy;
                                final int z = loc.getBlockZ() + dz;
                                int type = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                                final int data = MinecartManiaWorld.getBlockData(minecart.getWorld(), x, y, z);
                                for (final BlockObserver bo : blockObservers) {
                                    if (bo.lookingForBlock(type, data)) {
                                        if (bo.blockSeen(cart, x, y, z)) {
                                            type = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkSigns(final MinecartManiaStorageCart cart) {
        for (final Sign sign : SignUtils.getAdjacentMinecartManiaSignList(cart.getLocation(), 1)) {
            for (final AutomationsSign type : AutomationsSign.values()) {
                final SignAction action = type.getSignAction(sign);
                if (action.valid(sign)) {
                    sign.addSignAction(action);
                }
            }
        }
    }
}
