package com.afforess.minecartmaniaautomations;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.signs.Sign;
import com.afforess.minecartmaniacore.signs.SignAction;
import com.afforess.minecartmaniacore.utils.ItemMatcher;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.world.SpecificMaterial;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;

public class AutoMineSignAction implements SignAction {
    
    public ItemMatcher[] matchers = null;
    private Player player;
    private ItemStack solidReplacer = null;
    
    public AutoMineSignAction(final Sign sign) {
        valid(sign);
    }
    
    public AutoMineSignAction(final Sign sign, final Player player) {
        this.player = player;
    }
    
    public boolean execute(final MinecartManiaMinecart minecart) {
        minecart.setDataValue("AutoMine", matchers);
        minecart.setDataValue("StaticReplacer", solidReplacer);
        return true;
    }
    
    public boolean async() {
        return true;
    }
    
    public boolean valid(final Sign sign) {
        solidReplacer = null;
        matchers = null;
        if (sign.getLine(0).toLowerCase().contains("mine blocks")) {
            final Protection p = LWC.getInstance().findProtection(sign.getBlock());
            if (p != null) {
                final Player pl = p.getBukkitOwner();
                if (pl != null) {
                    player = pl;
                    //Logger.getLogger("Minecraft").info("Located owner of sign @" + sign.getLocation() + ": " + player.getName());
                }
            }
            if (player != null) {
                sign.setLine(0, "[Mine Blocks]");
                if (sign.getLine(1).startsWith("solid:")) {
                    final SpecificMaterial[] mats = ItemUtils.getItemStringToMaterial(sign.getLine(1).substring(6));
                    if (mats.length == 1) {
                        solidReplacer = new ItemStack(mats[0].getId(), 1, (short) mats[0].getData());
                    }
                }
                matchers = ItemUtils.getItemStringListToMatchers(sign.getLines());
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
        for (final ItemMatcher item : matchers) {
            if (item == null) {
                continue;
            }
            final ItemStack itemstack = item.toItemStack();
            if (itemstack == null) {
                continue;
            }
            if (player != null) {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item.toSpecificMaterial())) {
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to automine " + itemstack.getType().name() + "!");
                    }
                    return false;
                }
            } else {
                if (!MinecartManiaAutomations.unrestrictedBlocks.contains(item.toSpecificMaterial()))
                    return false;
            }
        }
        return true;
    }
    
    public String getName() {
        return "autominesign";
    }
    
    public String getFriendlyName() {
        return "Automatic Mining Sign";
    }
    
}
