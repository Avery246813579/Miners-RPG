package me.avery246813579.minersrpg;

import me.avery246813579.minersfortune.MinersFortune;
import me.avery246813579.minersfortune.sql.tables.MinerTable;
import me.avery246813579.minersrpg.entities.AnimalHandler;
import me.avery246813579.minersrpg.entities.Merchant;
import me.avery246813579.minersrpg.entities.MinerQuestNpc;
import me.avery246813579.minersrpg.entities.MinerZombie;
import me.avery246813579.minersrpg.listeners.PlayerListener;
import me.avery246813579.minersrpg.listeners.SpellListener;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.QuestHandler;
import me.avery246813579.minersrpg.util.EntityUtil;
import me.avery246813579.minersrpg.util.GlobalTimer;
import me.avery246813579.minersrpg.weapons.MinerWeapon;
import me.avery246813579.minersrpg.weapons.WeaponHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MinersRpg extends JavaPlugin implements Listener {
	/** Plugins **/
	private static MinersFortune minersFortune;
	private static MinersRpg plugin;

	
	/** http://medieval.stormthecastle.com/medieval-names.htm **/
	
	MinerWeapon mw = new MinerWeapon(ChatColor.RED + "Killer", 5, 7, Material.DIAMOND_SWORD, 500);

	public void onEnable() {
		/** Deletes all the old mobs **/
		for(World world : Bukkit.getWorlds()){
			for(Entity entity : world.getEntities()){
				if(!(entity instanceof Player)){
					entity.remove();
				}
			}
		}
		
		/** Creating Static Classes **/
		new EntityUtil(this);
		new AnimalHandler(this);
		new WeaponHandler(this);
		new QuestHandler(this);
		new GlobalTimer(this);

		/** Enables Listeners **/
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new SpellListener(), this);

		/** Loads Miners Fortune Plugin **/
		try {
			setMinersFortune((MinersFortune) Bukkit.getPluginManager().getPlugin("MinersFortune"));
			plugin = this;
		} catch (Exception ex) {
			Bukkit.getPluginManager().disablePlugin(this);
		}

		mw.setArmorPen(1);
		mw.setSellPrice(3);
		mw.setCritChance(25);
		mw.createItem(Material.DIAMOND_SWORD);

		new MinerZombie();
		Merchant m = new Merchant("Micheal", new Location(Bukkit.getWorld("world"), 1452, 34, 137), "Random Shop", Profession.LIBRARIAN);
		m.getBuyItems().put(mw.getItem(), 5);
		m.getBuyItems().put(new ItemStack(Material.ACACIA_STAIRS), 2);
		m.getBuyItems().put(new ItemStack(Material.ACTIVATOR_RAIL), 4);
		m.getBuyItems().put(new ItemStack(Material.ANVIL), 2);
		m.getBuyItems().put(new ItemStack(Material.BIRCH_WOOD_STAIRS), 2);
		m.getBuyItems().put(new ItemStack(Material.BAKED_POTATO), 2);
		m.getBuyItems().put(new ItemStack(Material.BOW), 2);
		m.getBuyItems().put(new ItemStack(Material.CARROT), 2);
		m.loadItems();

		EntityUtil.addEntity(new MinerQuestNpc("Josh", new Location(Bukkit.getWorld("world"), 1468.5, 35, 115.5), EntityType.VILLAGER));
		EntityUtil.addEntity(new MinerQuestNpc("Merek", new Location(Bukkit.getWorld("world"), 1462, 34, 141), EntityType.VILLAGER));
		EntityUtil.addWeapon(mw);

		if (Bukkit.getOnlinePlayers().length != 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
				Miner miner = new Miner(plugin, player, minerTable.getPlayer_id(), minerTable.getExp(), minerTable.getEmeralds(), minerTable.getLastLocation(), minerTable.getInventory(), minerTable.getClassType(), minerTable.getMasteries(), minerTable.getRelics(), minerTable.getQuests(),
						minerTable.getVaultStorage());
				EntityUtil.getMiners().add(miner);
			}
		}
	}

	public void onDisable() {
		if (Bukkit.getOnlinePlayers().length != 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				/** Saves a Miner **/
				Miner miner = EntityUtil.findMiner(player);
				EntityUtil.getMiners().remove(player);
				miner.savePlayer();
			}
		}

		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (!(entity instanceof Player)) {
					entity.remove();
				}
			}
		}
	}

	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.SPAWNER_EGG && event.getSpawnReason() != SpawnReason.CUSTOM) {
			event.getEntity().remove();
		}
	}

	public static MinersFortune getMinersFortune() {
		return minersFortune;
	}

	public static void setMinersFortune(MinersFortune minersFortune) {
		MinersRpg.minersFortune = minersFortune;
	}

	public static MinersRpg getPlugin() {
		return plugin;
	}

	public static void setPlugin(MinersRpg plugin) {
		MinersRpg.plugin = plugin;
	}
}
