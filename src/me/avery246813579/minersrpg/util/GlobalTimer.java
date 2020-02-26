package me.avery246813579.minersrpg.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.entities.MinerEntity;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.QuestDialog;
import me.avery246813579.minersrpg.spells.SpellTypes;

public class GlobalTimer implements Runnable {
	/** Classes **/
	MinersRpg plugin;

	public GlobalTimer(MinersRpg plugin) {
		this.plugin = plugin;

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
	}

	public void run() {
		/** Checks if entities are NPCS **/
		for (MinerEntity me : EntityUtil.entities) {
			if (me.isNPC()) {
				LivingEntity entity = me.getEntity();
				if (me.getSpawnLocation().getBlockX() != entity.getLocation().getBlockX() || me.getSpawnLocation().getBlockY() != entity.getLocation().getBlockY() || me.getSpawnLocation().getBlockZ() != entity.getLocation().getBlockZ()) {
					entity.teleport(me.getSpawnLocation());
				}
			}

			if (me.isHostile()) {
				if (me.getTargetPlayer() != null) {
					if (me.isOutsideTrackingRange()) {
						Monster monster = (Monster) me.getEntity();
						monster.setTarget(null);
						me.setTargetPlayer(null);
					}
				}
			}

			if (me.isOutsideSquare()) {
				if (me.getTargetPlayer() == null) {
					EntityUtil.livingEntityMoveTo(me.getEntity(), me.getSpawnLocation(), 1.0F);
				}
			}
		}

		/** Checks all the quest dialogs **/
		if (!EntityUtil.getDialogs().isEmpty()) {
			for (QuestDialog qd : EntityUtil.getDialogs()) {
				qd.setTimeBetween(qd.getTimeBetween() - 1);

				if (qd.getTimeBetween() != 0) {
					return;
				}

				if (qd.getCurrentLine() + 1 >= qd.getDialog().size()) {
					EntityUtil.getDialogs().remove(qd);
				}

				qd.getPlayer().sendMessage(ChatColor.GRAY + "[" + (qd.getCurrentLine() + 1) + "/" + qd.getDialog().size() + "] " + qd.getSpeaker() + ": " + ChatColor.BLUE + qd.getDialog().get(qd.getCurrentLine()));
				qd.setCurrentLine(qd.getCurrentLine() + 1);
				qd.setTimeBetween(qd.getMaxTimeBetween());
			}
		}

		/** Miner Check Look **/
		for (Miner miner : EntityUtil.getMiners()) {
			for (SpellTypes st : SpellTypes.values()) {
				int i = miner.getMinerSpells().getCooldowns().get(st);

				if (i != 0) {
					i--;
					miner.getMinerSpells().getCooldowns().remove(st);
					miner.getMinerSpells().getCooldowns().put(st, i);

					if (miner.getMinerSpells().getCurrentSpell() == st) {
						miner.getMinerSpells().createWandName();
					}
				}
			}
			
			/** Checks for the essential items in inventory **/
			if(miner.getPlayer().getInventory().getItem(6) == null){
				if(miner.getExp() != 0) miner.getPlayer().getInventory().setItem(6, miner.questBook());
			}else if(miner.getPlayer().getInventory().getItem(6).getType() != Material.BOOK){
				if(miner.getPlayer().getInventory().getItem(6).getItemMeta().getDisplayName().contains("Quest")){
					return;
				}
				
				if(miner.getExp() != 0) miner.getPlayer().getInventory().setItem(6, miner.questBook());
			}
		}
	}
}
