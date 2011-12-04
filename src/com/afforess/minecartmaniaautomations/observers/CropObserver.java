package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class CropObserver extends BlockObserver {

	private int crop;
	private int seedType;
	private int baseBlockType;
	private int minHeight;
	private int maxHeight;
	private ItemStack drop=null;
	private Random random;
	private int controlBlockId;

	public CropObserver(String name, int crop, int seed, int controlBlockID) {
		super(name);
		this.crop=crop;
		this.seedType=seed;
		this.baseBlockType=Material.SOIL.getId();
		this.minHeight=0;
		this.maxHeight=7;
		this.controlBlockId=controlBlockID;
		random=new Random();
	}
	
	public CropObserver setBase(int blockID) {
		baseBlockType=blockID;
		return this;
	}
	
	public CropObserver setHeightRange(int min, int max) {
		minHeight=min;
		maxHeight=max;
		return this;
	}
	
	@Override
	public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
			int z) {
		if(minecart.getDataValue(this.name)==null) return false;
		
		World w = minecart.getWorld();
		int baseBlock = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
		int stalk = MinecartManiaWorld.getBlockIdAt(w, x, y+1, z);
		int stalkData = MinecartManiaWorld.getBlockData(w, x, y+1, z);
		int controlBlock  = MinecartManiaWorld.getBlockIdAt(w, x, y-1, z);
		
		if(baseBlock != baseBlockType) return false;
		
		if(stalk!=crop) {
	        if (minecart.getDataValue("SmartForest") != null) {
	        	if(controlBlockId!=0 && controlBlock!=controlBlockId) 
	        		return false;
	        }
			if(minecart.contains(seedType)) {
				if(minecart.removeItem(seedType)){
					MinecartManiaWorld.setBlockAt(w, crop, x, y+1, z);
					MinecartManiaWorld.setBlockData(w, minHeight, x, y+1, z);
					return true;
				}
			}
		}
		
		if(stalk==crop && stalkData==maxHeight) {
			if(minecart.addItem(getDrop(w,stalkData))) {
				if(minecart.removeItem(seedType)){
					MinecartManiaWorld.setBlockAt(w, crop, x, y+1, z);
					MinecartManiaWorld.setBlockData(w, minHeight, x, y+1, z);
					return true;
				}
			}
		}
		return true;
	}

	private ItemStack getDrop(World w, int data) {
		if(drop==null) {
			return AutomationsUtils.getDropsForBlock(random, crop, data, 0);
		} else {
			return drop;
		}
	}

	public BlockObserver setDrop(ItemStack is) {
		drop=is;
		return this;
	}

}
