package me.avery246813579.minersrpg.listeners;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.avery246813579.minersfortune.sql.tables.MinerTable;
import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.Quest;
import me.avery246813579.minersrpg.quest.QuestHandler;
import me.avery246813579.minersrpg.util.EntityUtil;

public class PlayerListener implements Listener {
	/** Classes **/
	MinersRpg plugin;

	public PlayerListener(MinersRpg plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		/** Creates and Loads a Miner **/
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		EntityUtil.getMiners().add(
				new Miner(plugin, player, minerTable.getPlayer_id(), minerTable.getExp(), minerTable.getEmeralds(), minerTable.getLastLocation(), minerTable.getInventory(), minerTable.getClassType(), minerTable.getMasteries(), minerTable.getRelics(), minerTable.getQuests(), minerTable
						.getVaultStorage()));
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 3));

		player.getInventory().setItem(8, vanity());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getItem() == null) {
			return;
		}
		
		if (event.getItem().getType() == Material.BOOK){
			if(!event.getItem().getItemMeta().getDisplayName().contains("Quest")){
				return;
			}
			
			Miner miner = EntityUtil.findMiner(player);
			if (miner.getExp() != 0) {
				miner.openQuestBook();
			}
		}
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		
		if(event.getCurrentItem() == null){
			return;
		}
		
		if(!(event.getCurrentItem().getType() == Material.STAINED_CLAY)){
			return;
		}
		
		if(event.getInventory().getName().equalsIgnoreCase("Quest List")){
			event.setCancelled(true);
			player.closeInventory();
			
			Quest quest = QuestHandler.getQuestByName(player, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
			quest.openQuestInventory();
		}
		
	}

	public ItemStack vanity() {
		ItemStack is = new ItemStack(Material.QUARTZ);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Vanity Menu");
		is.setItemMeta(im);

		return is;
	}

	@EventHandler
	public void onQuitLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		/** Saves a Miner **/
		Miner miner = EntityUtil.findMiner(player);
		EntityUtil.getMiners().remove(player);
		miner.savePlayer();

		/** Deletes players data **/
		final Player playa = player;

		new BukkitRunnable() {
			public void run() {
				File f = new File("world/players", playa + ".dat");
				if (f.exists()) {
					f.delete();
				}
			}
		}.runTaskLater(MinersRpg.getPlugin(), 5L);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		/** Gets Miner **/
		Miner miner = EntityUtil.findMiner(player);

		/** Checks and freezes player **/
		if (miner.isFrozen()) {
			if (((event.getTo().getX() != event.getFrom().getX()) || (event.getTo().getZ() != event.getFrom().getZ()))) {
				event.setTo(event.getFrom());
				return;
			}
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();

			if (event.getEntityType() == EntityType.EGG) {
				player.sendMessage(ChatColor.GREEN + "Egg> " + ChatColor.GRAY + "I don't like getting touched that way!");
				event.setCancelled(true);
			}
		}

	}
}
