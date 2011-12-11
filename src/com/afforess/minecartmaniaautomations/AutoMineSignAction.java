package com.afforess.minecartmaniaautomations;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniacore.utils.ItemMatcher;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;

public class AutoMineSignAction implements SignAction {
    
    public ItemMatcher[] matchers = null;
    private Player player;
    
    public AutoMineSignAction(Sign sign) {
        this.valid(sign);
    }
    
    public AutoMineSignAction(Sign sign, Player player) {
        this.player = player;
    }
    
    public boolean execute(MinecartManiaMinecart minecart) {
        if (minecart.getDataValue("AutoMine") == null) {
            minecart.setDataValue("AutoMine", matchers);
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
                    Logger.getLogger("Minecraft").info("Located owner of sign @"+sign.getLocation()+": "+player.getName());
                }
            }
            if (player != null) {
                sign.setLine(0, "[Mine Blocks]");
                this.matchers = ItemUtils.getItemStringListToMatchers(sign.getLines());
                if (!checkItems()) {
                    matchers = null;
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean checkItems() {
        
        if (player != null)
            if (player.hasPermission("minecartmania.automine.everything"))
                return true;
        for (ItemMatcher item : matchers) {
            if (item == null)
                continue;
            ItemStack itemstack = item.toItemStack();
            if(itemstack==null) continue;
            if (player != null) {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item.toItemStack())) {
                    if (player != null)
                        player.sendMessage(ChatColor.RED + "You don't have permission to automine " + itemstack.getType().name() + "!");
                    return false;
                }
            } else {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item.toItemStack())) {
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
