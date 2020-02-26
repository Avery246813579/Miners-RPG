package me.avery246813579.minersrpg.spells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.avery246813579.minersfortune.sql.tables.MinerTable;
import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.miner.Miner;

public class MinerSpells {
	/** Variales **/
	private Map<SpellTypes, Integer> cooldowns = new HashMap<SpellTypes, Integer>();
	private SpellTypes currentSpell = SpellTypes.Fireball;
	private String stickName;
	private Miner miner;

	public MinerSpells(Miner miner) {
		this.miner = miner;

		loadCooldowns();
		new BukkitRunnable() {
			public void run() {
				createWandName();
			}
		}.runTaskLater(MinersRpg.getPlugin(), 10);
	}

	public void loadCooldowns() {
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(miner.getPlayer()));

		for (SpellTypes st : SpellTypes.values()) {
			cooldowns.put(st, 0);
		}

		if (minerTable.getSpells().equalsIgnoreCase("none")) {
			return;
		}

		String[] spells = minerTable.getSpells().split(",");
		for (String s : spells) {
			String[] split = s.split("-");
			SpellTypes st = SpellTypes.findSpellType(Integer.parseInt(split[0]));
			cooldowns.remove(st);
			cooldowns.put(st, Integer.parseInt(split[1]));

			if (Integer.parseInt(split[2]) == 1) {
				this.currentSpell = st;
			}
		}

		createWandName();
	}

	public void saveCooldowns() {
		String cooldownString = "";

		for (SpellTypes st : SpellTypes.values()) {
			int i = cooldowns.get(st);

			cooldownString = cooldownString + "," + st.getSpellNumber() + "-" + i;
			if (st == currentSpell)
				cooldownString = cooldownString + "-1";
			else
				cooldownString = cooldownString + "-0";
		}

		cooldownString = cooldownString.substring(1, cooldownString.length());

		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(miner.getPlayer()));
		minerTable.setSpells(cooldownString);
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);
	}

	public void createWandName() {
		if (cooldowns.get(currentSpell) != 0) {
			stickName = ChatColor.YELLOW + currentSpell.getWandName() + ChatColor.BLACK + " | " + ChatColor.RED + "Cooldown: " + cooldowns.get(currentSpell) + "s";
		} else {
			stickName = ChatColor.GOLD + "Magic Wand" + ChatColor.BLACK + " | " + ChatColor.YELLOW + currentSpell.getWandName();
		}

		miner.getPlayer().getInventory().setItem(7, wand());
	}

	public void openSpellInventory() {
		Inventory inventory = Bukkit.createInventory(null, 54, "Spell Inventory");
		inventory.setItem(11, projectiles());
		inventory.setItem(15, teleports());
		inventory.setItem(19, fireball());
		inventory.setItem(23, spawn());
		inventory.setItem(42, pageNavigation("Next"));
		miner.getPlayer().openInventory(inventory);
	}

	public void openSecondSpellInventory() {
		Inventory inventory = Bukkit.createInventory(null, 54, "Spell Inventory");
		inventory.setItem(11, buffs());
		inventory.setItem(15, debuffs());
		inventory.setItem(19, speed());
		inventory.setItem(38, pageNavigation("Last"));
		miner.getPlayer().openInventory(inventory);
	}

	public void performSpell() {
		if (currentSpell == SpellTypes.Fireball) {
			if (cooldowns.get(SpellTypes.Fireball) == 0) {
				miner.getPlayer().launchProjectile(Snowball.class);
			} else {
				sendCooldownMessage(currentSpell);
				return;
			}
		}
		
		if(currentSpell == SpellTypes.Spawn){
			if(cooldowns.get(SpellTypes.Spawn) == 0){
				miner.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1464.5, 34, 135, 180, 0));
				sendSpellMessage(ChatColor.YELLOW + "You have teleported to spawn.");
			}else{
				sendCooldownMessage(currentSpell);
				return;
			}
		}
		
		if(currentSpell == SpellTypes.Speed){
			if(cooldowns.get(SpellTypes.Speed) == 0){
				miner.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 1));
				sendSpellMessage(ChatColor.YELLOW + "You applied speed effects.");
			}else{
				sendCooldownMessage(currentSpell);
				return;
			}
		}

		cooldowns.remove(currentSpell);
		cooldowns.put(currentSpell, currentSpell.getCooldown());
		createWandName();
	}

	public void selectSpell(ItemStack currentItem) {
		if (currentItem == null) {
			return;
		}

		if (currentItem.getType() == Material.FIREBALL) {
			if (currentSpell == SpellTypes.Fireball) {
				sendSpellMessage("You are already using " + SpellTypes.Fireball.getWandName() + "!");
				return;
			} else {
				sendSpellMessage("" + ChatColor.YELLOW + SpellTypes.Fireball.getWandName() + " has been selected!");
				currentSpell = SpellTypes.Fireball;
			}
		}

		if (currentItem.getType() == Material.MAP) {
			if (currentItem.getItemMeta().getDisplayName().contains("Spawn")) {
				if (currentSpell == SpellTypes.Spawn) {
					sendSpellMessage("You are already using " + SpellTypes.Spawn.getWandName() + "!");
					return;
				} else {
					sendSpellMessage("" + ChatColor.YELLOW + SpellTypes.Spawn.getWandName() + " has been selected!");
					currentSpell = SpellTypes.Spawn;
				}
			}
		}
		
		if(currentItem.getType() == Material.POTION){
			if(currentItem.getItemMeta().getDisplayName().contains("Speed")){
				if (currentSpell == SpellTypes.Speed) {
					sendSpellMessage("You are already using " + SpellTypes.Speed.getWandName() + "!");
					return;
				} else {
					sendSpellMessage("" + ChatColor.YELLOW + SpellTypes.Speed.getWandName() + " has been selected!");
					currentSpell = SpellTypes.Speed;
				}
			}
		}

	}

	public void sendCooldownMessage(SpellTypes st) {
		miner.getPlayer().sendMessage(ChatColor.GREEN + "Spells> " + ChatColor.RED + st.getWandName() + " is on cooldown for " + cooldowns.get(st) + " more seconds!");
	}

	public void sendSpellMessage(String message) {
		miner.getPlayer().sendMessage(ChatColor.GREEN + "Spell> " + ChatColor.GRAY + message);
	}

	/***************************************
	 * 
	 * Item Stacks
	 * 
	 ***************************************/

	public ItemStack projectiles() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Projectiles");
		is.setItemMeta(im);

		return is;
	}

	public ItemStack teleports() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Teleports");
		is.setItemMeta(im);

		return is;
	}

	public ItemStack debuffs() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Debuffs");
		is.setItemMeta(im);

		return is;
	}

	public ItemStack buffs() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Buffs");
		is.setItemMeta(im);

		return is;
	}

	public ItemStack pageNavigation(String way) {
		ItemStack is = new ItemStack(Material.ARROW);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + way + " Page");
		is.setItemMeta(im);

		return is;
	}

	public ItemStack fireball() {
		ItemStack is = new ItemStack(Material.FIREBALL);
		ItemMeta im = is.getItemMeta();
		if (currentSpell != SpellTypes.Fireball)
			im.setDisplayName(ChatColor.GREEN + "Fireball");
		else
			im.setDisplayName(ChatColor.YELLOW + "Fireball");
		List<String> lore = new ArrayList<String>();
		if (cooldowns.get(SpellTypes.Fireball) != 0)
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.RED + "Cooling down");
		else
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.GREEN + "Ready to use");
		lore.add(ChatColor.BLUE + "Cooldown: " + ChatColor.GRAY + SpellTypes.Fireball.getCooldown());
		if (cooldowns.get(SpellTypes.Fireball) != 0)
			lore.add(ChatColor.BLUE + "Cooldown Left: " + ChatColor.GRAY + cooldowns.get(SpellTypes.Fireball));
		im.setLore(lore);
		is.setItemMeta(im);

		return is;
	}

	public ItemStack spawn() {
		ItemStack is = new ItemStack(Material.MAP);
		ItemMeta im = is.getItemMeta();
		if (currentSpell != SpellTypes.Spawn)
			im.setDisplayName(ChatColor.GREEN + "Spawn Teleport");
		else
			im.setDisplayName(ChatColor.YELLOW + "Spawn Teleport");
		List<String> lore = new ArrayList<String>();
		if (cooldowns.get(SpellTypes.Spawn) != 0)
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.RED + "Cooling down");
		else
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.GREEN + "Ready to use");
		lore.add(ChatColor.BLUE + "Cooldown: " + ChatColor.GRAY + SpellTypes.Spawn.getCooldown());
		if (cooldowns.get(SpellTypes.Spawn) != 0)
			lore.add(ChatColor.BLUE + "Cooldown Left: " + ChatColor.GRAY + cooldowns.get(SpellTypes.Spawn));
		im.setLore(lore);
		is.setItemMeta(im);

		return is;
	}

	public ItemStack wand() {
		ItemStack is = new ItemStack(Material.STICK);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Magic wand");
		im.setLore(lore);
		im.setDisplayName(stickName);
		is.setItemMeta(im);

		return is;
	}
	
	public ItemStack speed(){
		ItemStack is = new ItemStack(Material.POTION);
		ItemMeta im = is.getItemMeta();
		if (currentSpell != SpellTypes.Speed)
			im.setDisplayName(ChatColor.GREEN + "Speed Buff");
		else
			im.setDisplayName(ChatColor.YELLOW + "Speed Buff");
		List<String> lore = new ArrayList<String>();
		if (cooldowns.get(SpellTypes.Speed) != 0)
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.RED + "Cooling down");
		else
			lore.add(ChatColor.BLUE + "Status: " + ChatColor.GREEN + "Ready to use");
		lore.add(ChatColor.BLUE + "Cooldown: " + ChatColor.GRAY + SpellTypes.Speed.getCooldown());
		if (cooldowns.get(SpellTypes.Speed) != 0)
			lore.add(ChatColor.BLUE + "Cooldown Left: " + ChatColor.GRAY + cooldowns.get(SpellTypes.Speed));
		im.setLore(lore);
		is.setItemMeta(im);

		return is;
	}

	public String getStickName() {
		return stickName;
	}

	public void setStickName(String stickName) {
		this.stickName = stickName;
	}

	public Miner getMiner() {
		return miner;
	}

	public void setMiner(Miner miner) {
		this.miner = miner;
	}

	public Map<SpellTypes, Integer> getCooldowns() {
		return cooldowns;
	}

	public void setCooldowns(Map<SpellTypes, Integer> cooldowns) {
		this.cooldowns = cooldowns;
	}

	public SpellTypes getCurrentSpell() {
		return currentSpell;
	}

	public void setCurrentSpell(SpellTypes currentSpell) {
		this.currentSpell = currentSpell;
	}
}
