package me.avery246813579.minersrpg.listeners;

import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.util.EntityUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpellListener implements Listener{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		/** Finds player and gives them their stick **/
		Miner miner = EntityUtil.findMiner(player);
		event.getPlayer().getInventory().setItem(7, miner.getMinerSpells().wand());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory().getName().equalsIgnoreCase("Spell Inventory")){
			if(event.getCurrentItem() == null && event.getCursor() == null){
				return;
			}
			
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
			Miner miner = EntityUtil.findMiner((Player) event.getWhoClicked());
			if(event.getCurrentItem().getType() == Material.ARROW && event.getInventory().contains(miner.getMinerSpells().projectiles())){
				miner.getMinerSpells().openSecondSpellInventory();
				return;
			}
			
			if(event.getCurrentItem().getType() == Material.ARROW && event.getInventory().contains(miner.getMinerSpells().buffs())){
				miner.getMinerSpells().openSpellInventory();
				return;
			}			
			
			miner.getMinerSpells().selectSpell(event.getCurrentItem());
			miner.getMinerSpells().createWandName();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		
		/** Checks if the item is null **/
		if(event.getItem() == null){
			return;
		}
		
		/** Checks if the material is the magic wand **/
		if((event.getItem().getType() != Material.STICK)){
			return;
		}
		
		Miner miner = EntityUtil.findMiner(player);
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
			miner.getMinerSpells().performSpell();
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			miner.getMinerSpells().openSpellInventory();
		}
	}
}
