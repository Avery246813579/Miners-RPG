package me.avery246813579.minersrpg.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MinerZombie extends MinerEntity{

	public MinerZombie() {
		super("Zombie", new Location(Bukkit.getWorld("world"), 1468, 34, 36), EntityType.ZOMBIE);
		this.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
		this.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
		this.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		this.setBoots(new ItemStack(Material.IRON_BOOTS));
		this.setHand(new ItemStack(Material.RED_ROSE));
		this.setHostile(true);
		
		this.setMaxHealth(10);
		this.setDamage(10);
		this.setMinXp(50);
		this.setMaxXp(100);
		this.setArmor(5);
		this.setLevel(5);
		this.setRespawnTime(20);
		
		this.setItemDropRate(40);
		this.getItemDrops().add(new ItemStack(Material.DIAMOND));
		this.getItemDrops().add(new ItemStack(Material.GOLD_INGOT));
		this.getItemDrops().add(new ItemStack(Material.IRON_INGOT));
		this.getItemDrops().add(new ItemStack(Material.EMERALD));

		
		this.spawnEntity();
	}

}
