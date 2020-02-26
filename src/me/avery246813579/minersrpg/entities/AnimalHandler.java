package me.avery246813579.minersrpg.entities;

import java.util.ArrayList;
import java.util.Arrays;

import me.avery246813579.minersrpg.MinersRpg;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class AnimalHandler {
	/** Classes **/
	MinersRpg plugin;
	
	public AnimalHandler(MinersRpg plugin){
		this.plugin = plugin;
		
		/** Loads Animals **/
		loadAnimals();
	}
	
	public void loadAnimals(){
		/** Loading Merek's Cows **/
		new MinerAnimal("Merek's Cow", new Location(Bukkit.getWorld("world"), 1479.5, 34, 99.5), EntityType.COW, new ItemStack(Material.RAW_BEEF), 100);
		new MinerAnimal("Merek's Cow", new Location(Bukkit.getWorld("world"), 1477.5, 34, 98.5), EntityType.COW, new ItemStack(Material.RAW_BEEF), 100);
		new MinerAnimal("Merek's Cow", new Location(Bukkit.getWorld("world"), 1474.5, 34, 98.5), EntityType.COW, new ItemStack(Material.RAW_BEEF), 100);
		new MinerAnimal("Merek's Cow", new Location(Bukkit.getWorld("world"), 1472.5, 34, 99.5), EntityType.COW, new ItemStack(Material.RAW_BEEF), 100);
		
		/** Loading Merek's Chickens **/
		new MinerAnimal("Merek's Chicken", new Location(Bukkit.getWorld("world"), 1476.1, 34, 87.3), EntityType.CHICKEN, new ItemStack(Material.EGG), 100);
		new MinerAnimal("Merek's Chicken", new Location(Bukkit.getWorld("world"), 1472.5, 34, 87.5), EntityType.CHICKEN, new ItemStack(Material.EGG), 100);
		new MinerAnimal("Merek's Chicken", new Location(Bukkit.getWorld("world"), 1474.5, 34, 88.5), EntityType.CHICKEN, new ItemStack(Material.EGG), 100);
		new MinerAnimal("Merek's Chicken", new Location(Bukkit.getWorld("world"), 1477.5, 34, 88.5), EntityType.CHICKEN, new ItemStack(Material.EGG), 100);
		new MinerAnimal("Merek's Chicken", new Location(Bukkit.getWorld("world"), 1479.5, 34, 87.5), EntityType.CHICKEN, new ItemStack(Material.EGG), 100);

	}
}
