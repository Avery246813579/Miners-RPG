package me.avery246813579.minersrpg.weapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import me.avery246813579.minersrpg.MinersRpg;

public class WeaponHandler {
	/** Classes **/
	private MinersRpg plugin;
	
	/** Variables **/
	private static List<MinerWeapon> weapons = new ArrayList<MinerWeapon>();
	
	public WeaponHandler(MinersRpg plugin){
		this.plugin = plugin;
	}
	
	/** Weapons **/
	public static MinerWeapon oakWoodSword = new MinerWeapon("Oak Wood Sword", 1, 3, Material.WOOD_SWORD, 501);
	
	public MinersRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(MinersRpg plugin) {
		this.plugin = plugin;
	}
}
