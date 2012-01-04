package com.afforess.minecartmaniaautomations;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.afforess.minecartmaniaautomations.observers.AutoCactusObserver;
import com.afforess.minecartmaniaautomations.observers.AutoFarmObserver;
import com.afforess.minecartmaniaautomations.observers.AutoMelonObserver;
import com.afforess.minecartmaniaautomations.observers.AutoMineObserver;
import com.afforess.minecartmaniaautomations.observers.AutoPumpkinObserver;
import com.afforess.minecartmaniaautomations.observers.AutoSugarObserver;
import com.afforess.minecartmaniaautomations.observers.AutoTimberObserver;
import com.afforess.minecartmaniaautomations.observers.CropObserver;
import com.afforess.minecartmaniaautomations.observers.DefoliatorObserver;
import com.afforess.minecartmaniaautomations.observers.SmartForestObserver;
import com.afforess.minecartmaniacore.MinecartManiaCore;
import com.afforess.minecartmaniacore.config.MinecartManiaConfigurationParser;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;
import com.afforess.minecartmaniacore.world.SpecificMaterial;

public class MinecartManiaAutomations extends JavaPlugin {
    public static MinecartManiaLogger log = MinecartManiaLogger.getInstance();
    public static MinecartManiaActionListener listener = new MinecartManiaActionListener();
    public static ArrayList<SpecificMaterial> unrestrictedBlocks = new ArrayList<SpecificMaterial>();
    
    public void onEnable() {
        MinecartManiaConfigurationParser.read(getDescription().getName() + "Configuration.xml", MinecartManiaCore.getDataDirectoryRelativePath(), new AutomationsSettingParser());
        getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, listener, Priority.Normal, this);
        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
        
        listener.blockObservers.clear();
        listener.blockObservers.add(new AutoPumpkinObserver());
        listener.blockObservers.add(new AutoMelonObserver());
        listener.blockObservers.add(new DefoliatorObserver());
        listener.blockObservers.add(new AutoFarmObserver());
        listener.blockObservers.add(new AutoCactusObserver());
        listener.blockObservers.add(new AutoTimberObserver());
        listener.blockObservers.add(new AutoSugarObserver());
        listener.blockObservers.add(new SmartForestObserver());
        listener.blockObservers.add(new AutoMineObserver());
        listener.blockObservers.add(new CropObserver("AutoWart", Material.NETHER_WARTS, Material.NETHER_STALK, Material.WOOL).setBase(Material.SOUL_SAND).setHeightRange(0, 3).setDrop(new ItemStack(Material.NETHER_STALK, 3)));
    }
    
    public void onDisable() {
        listener.blockObservers.clear();
    }
    
}
