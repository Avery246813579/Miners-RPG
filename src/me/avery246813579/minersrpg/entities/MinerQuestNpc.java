package me.avery246813579.minersrpg.entities;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class MinerQuestNpc extends MinerEntity{

	public MinerQuestNpc(String s, Location l, EntityType e) {
		super(s, l, e);
		
		this.setNPC(true);
		this.spawnEntity();
	}
}
