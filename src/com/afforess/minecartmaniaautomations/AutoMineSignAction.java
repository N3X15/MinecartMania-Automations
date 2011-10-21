package com.afforess.minecartmaniaautomations;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.world.AbstractItem;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;

public class AutoMineSignAction implements SignAction {
    
    public AbstractItem items[] = null;
    private Player player;
    
    public AutoMineSignAction(Sign sign, Player player) {
        this.player = player;
    }
    
    public boolean execute(MinecartManiaMinecart minecart) {
        if(minecart.getDataValue("AutoMine")==null) {
            String stuff="";
            boolean first=true;
            for(AbstractItem i : items) {
                if(first) {
                    first=false;
                } else {
                    stuff+=", ";
                }
                stuff+=i.toMaterial().name();
            }
            player.sendMessage(ChatColor.GREEN+"Now mining for "+stuff);
            minecart.setDataValue("AutoMine", items);
        }
        return true;
    }
    
    public boolean async() {
        return true;
    }
    
    public boolean valid(Sign sign) {
        if (sign.getLine(0).toLowerCase().contains("mine blocks")) {
            Protection p = LWC.getInstance().findProtection(sign.getBlock());
            if (p != null) {
                Player pl = p.getBukkitOwner();
                if (pl != null) {
                    player = pl;
                    //Logger.getLogger("Minecraft").info("Located owner of sign "+sign.toString()+": "+player.getName());
                }
            }
            if (player != null) {
                sign.setLine(0, "[Mine Blocks]");
                this.items = ItemUtils.getItemStringListToMaterial(sign.getLines());
                if(!checkItems()) {
                    items=null;
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean checkItems() {
        for (AbstractItem item : items) {
            if (item == null)
                continue;
            if (player != null) {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item) || !player.hasPermission("minecartmania.automine.everything")) {
                    if (player != null)
                        player.sendMessage(ChatColor.RED + "You don't have permission to automine " + item.toMaterial().name() + "!");
                    return false;
                }
            } else {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "autominesign";
    }
    
    public String getFriendlyName() {
        // TODO Auto-generated method stub
        return "Automatic Mining Sign";
    }
    
}
