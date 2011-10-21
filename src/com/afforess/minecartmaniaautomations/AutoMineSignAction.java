package com.afforess.minecartmaniaautomations;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.world.AbstractItem;

public class AutoMineSignAction implements SignAction {
    
    public AbstractItem items[]=null;
    private Player player;
    
    public AutoMineSignAction(Sign sign, Player player) {
        this.player = player;
    }
    
    public boolean execute(MinecartManiaMinecart minecart) {
        if(player==null) return false;
        if(items==null) return false;
        for (AbstractItem item : items) {
            if(item==null) continue;
            if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item) || player.hasPermission("minecartmania.automine.everything")) {
                if (player != null)
                    player.sendMessage(ChatColor.RED + "You don't have permission to automine " + item.toMaterial().name() + "!");
                return false;
            }
        }
        minecart.setDataValue("AutoMine", items);
        return true;
    }
    
    public boolean async() {
        return true;
    }
    
    public boolean valid(Sign sign) {
        if (sign.getLine(0).toLowerCase().contains("mine blocks")) {
            sign.setLine(0, "[Mine Blocks]");
            this.items = ItemUtils.getItemStringListToMaterial(sign.getLines());
            return true;
        }
        return false;
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
