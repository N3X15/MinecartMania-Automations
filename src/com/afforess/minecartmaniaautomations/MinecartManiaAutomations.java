package com.afforess.minecartmaniaautomations;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
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
import com.afforess.minecartmaniacore.world.AbstractItem;

public class MinecartManiaAutomations extends JavaPlugin {
	public static MinecartManiaLogger log = MinecartManiaLogger.getInstance();
	public static Server server;
	public static PluginDescriptionFile description;
	public static MinecartManiaActionListener listener = new MinecartManiaActionListener();
	public static ArrayList<AbstractItem> unrestrictedBlocks = new ArrayList<AbstractItem>();

	public void onEnable() {
		server = this.getServer();
		description = this.getDescription();
		MinecartManiaConfigurationParser.read(description.getName()
				+ "Configuration.xml",
				MinecartManiaCore.getDataDirectoryRelativePath(),
				new AutomationsSettingParser());
		getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT,
				listener, Priority.Normal, this);
		log.info(description.getName() + " version " + description.getVersion()
				+ " is enabled!");

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
		listener.blockObservers.add(new CropObserver("AutoWart",
				Material.NETHER_WARTS.getId(), Material.NETHER_STALK.getId(),
				Material.WOOL.getId())
		.setBase(Material.SOUL_SAND.getId())
				.setHeightRange(0, 3)
				.setDrop(new ItemStack(Material.NETHER_STALK, 3)));
	}

	public void onDisable() {
		listener.blockObservers.clear();
	}

}
