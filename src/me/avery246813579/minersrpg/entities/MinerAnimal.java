package me.avery246813579.minersrpg.entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MinerAnimal extends MinerEntity{
	public MinerAnimal(String s, Location l, EntityType e, ItemStack d, int dropRate) {
		super(s, l, e);
		
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(d);
		
		this.setItemDrops(drops);
		this.setItemDropRate(dropRate);
		this.setHostile(false);
		this.setRespawnTime(15);
		this.setMoveDistance(5);
		this.setSuffix("Mob");
		this.setMaxHealth(10);
		
		spawnEntity();
	}

}
