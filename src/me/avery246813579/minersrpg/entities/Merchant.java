package me.avery246813579.minersrpg.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.avery246813579.minersfortune.sql.tables.MinerTable;
import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.util.EntityUtil;
import me.avery246813579.minersrpg.util.InventoryUtil;
import me.avery246813579.minersrpg.weapons.MinerWeapon;

public class Merchant extends MinerEntity implements Listener {
	private List<Player> views = new ArrayList<Player>();

	private Map<Player, ItemStack> sellDrop = new HashMap<Player,ItemStack>();
	private Map<ItemStack, Integer> buyItems = new HashMap<ItemStack, Integer>();
	private List<Inventory> buyInventorys = new ArrayList<Inventory>();
	private Inventory merchantInventory;
	private String shopName;

	public Merchant(String name, Location l, String shopName, Profession p) {
		super(name, l, EntityType.VILLAGER);

		this.setNPC(true);
		this.spawnEntity();
		this.setMaxHealth(10);
		this.setSuffix("Merchant");
		EntityUtil.addEntity(this);

		this.shopName = shopName;
	}

	public void loadItems() {
		buyInventorys.clear();
		if (!buyItems.isEmpty()) {
			createInventorys(buyItems, buyInventorys);
		}
	}

	public void createInventorys(Map<ItemStack, Integer> items, List<Inventory> inventorys) {
		int itemSize = items.size();
		for (int i = 0; i < getInventories(items); i++) {
			inventorys.add(Bukkit.createInventory(null, getInventorySize(itemSize), shopName));
			itemSize = itemSize - 11;
		}

		addItems(items, inventorys);
	}

	public void openMerchantInventory(Player player) {
		/** Creates Inventory **/
		merchantInventory = Bukkit.createInventory(null, 27, shopName);

		/** Adds the items **/
		merchantInventory.setItem(11, buyIcon());
		merchantInventory.setItem(13, sellIcon());
		merchantInventory.setItem(15, tradeIcon());

		/** Opens the Inventory **/
		player.openInventory(merchantInventory);
	}

	public void openSellInventory(Player player) {
		/** Creates Inventory **/
		Inventory sellInventory = Bukkit.createInventory(null, 9, "Sell Items");

		/** Opens up the inventory **/
		player.openInventory(sellInventory);
	}

	public void addItems(Map<ItemStack, Integer> items, List<Inventory> inventorys) {
		List<ItemStack> rawItems = new ArrayList<ItemStack>();

		Iterator<Entry<ItemStack, Integer>> itemZ = items.entrySet().iterator();
		while (itemZ.hasNext()) {
			Map.Entry<ItemStack, Integer> pairs = (Map.Entry<ItemStack, Integer>) itemZ.next();
			rawItems.add(pairs.getKey());
		}

		int itemzT = 0;
		int location = 10;

		for (ItemStack itemStack : rawItems) {
			itemzT++;

			if (location == 18) {
				location = location + 2;
			}

			if (location == 26) {
				location = location + 2;
			}

			int prize = items.get(itemStack);

			ItemStack is = itemStack.clone();
			ItemMeta im = is.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if (im.getLore() != null) {
				lore = im.getLore();
			}
			lore.add("");
			lore.add(ChatColor.GREEN + "Cost: " + prize);
			im.setLore(lore);
			is.setItemMeta(im);

			if (itemzT > 0 && itemzT <= 11) {
				inventorys.get(0).setItem(location, is);
				location = location + 2;
			}
			if (itemzT > 11 && itemzT <= 22) {
				if (itemzT == 12) {
					location = 11;
				}

				inventorys.get(1).setItem(location, is);
				location = location + 2;
			}
		}

		if (buyInventorys.size() > 1) {
			ItemStack nextItem = new ItemStack(Material.ARROW);
			ItemMeta im = nextItem.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Next Page");
			nextItem.setItemMeta(im);
			buyInventorys.get(0).setItem(buyInventorys.get(0).getSize() - 12, nextItem);
		}

		for (Inventory i : buyInventorys) {
			ItemStack is = new ItemStack(Material.BED);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Back");
			is.setItemMeta(im);
			i.setItem(i.getSize() - 14, is);
		}
	}

	public void buyItem(Player player, ItemStack rawIs) {
		/** Removing cost from lore **/
		ItemStack is = rawIs.clone();
		ItemMeta im = is.getItemMeta();
		List<String> lore;
		if(im.getLore() != null){
			lore = is.getItemMeta().getLore();
		}else{
			lore = new ArrayList<String>();
		}
		lore.remove(lore.size() - 1);
		lore.remove(lore.size() - 1);
		im.setLore(lore);
		is.setItemMeta(im);

		/** Gets cost **/
		int cost = buyItems.get(is);

		/** Gets and Checks if player has enough money **/
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		if (minerTable.getEmeralds() < cost) {
			player.closeInventory();
			sendMessage(player, ChatColor.RED + "Not enough money to purchase this item!");
			return;
		}

		/** Checks if player has space in there inventory **/
		if (!InventoryUtil.hasOpenSpace(player.getInventory())) {
			player.closeInventory();
			sendMessage(player, ChatColor.RED + "You don't have enough inventory space to store this item!");
			return;
		}

		/** Takes money and saves Miner **/
		minerTable.setEmeralds(minerTable.getEmeralds() - cost);
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);

		/** Gives player item and thanks them **/
		player.closeInventory();
		player.getInventory().addItem(is);
		sendMessage(player, ChatColor.YELLOW + "Purchase successful! Have a nice day!");
	}

	public void sellItem(Player player, ItemStack is, Inventory i) {
		/** Checks if player can sell item **/
		if ((EntityUtil.findWeapon(is) == null)) {
			player.closeInventory();
			sendMessage(player, ChatColor.RED + "You can not sell this item");
			return;
		}

		/** Gets Item **/
		MinerWeapon mw = EntityUtil.findWeapon(is);

		/** Loading and saving credits earned **/
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		minerTable.setEmeralds(minerTable.getEmeralds() + mw.getSellPrice());
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);

		/** Deletes item and saves player **/
		sellDrop.put(player, is);
		player.closeInventory();
		sendMessage(player, ChatColor.YELLOW + "Selling seccessful! You have sold your item for " + mw.getSellPrice() + " emeralds.");
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.VILLAGER) {
			event.setCancelled(true);
		}

		if (event.getRightClicked() == this.getEntity()) {
			openMerchantInventory(event.getPlayer());
			views.add(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		
		if(sellDrop.containsKey(player)){
			if(!sellDrop.containsValue(event.getItemDrop().getItemStack())){
				return;
			}
			
			event.setCancelled(true);
			sellDrop.remove(player);
			player.getInventory().removeItem(event.getItemDrop().getItemStack());
			event.getItemDrop().getItemStack().setType(Material.AIR);
			event.getItemDrop().getItemStack().setAmount(0);
		}
	}

	@EventHandler
	public void onMerchantInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getName().equalsIgnoreCase(shopName) && views.contains(player)) {
			event.setCancelled(true);
			if (event.getCurrentItem().isSimilar(buyIcon())) {
				loadItems();
				player.closeInventory();
				player.openInventory(buyInventorys.get(0));
				views.add(player);
				return;
			}

			if (event.getCurrentItem().isSimilar(backIcon())) {
				loadItems();
				player.closeInventory();
				openMerchantInventory(player);
				views.add(player);
				return;
			}

			if (event.getCurrentItem().isSimilar(sellIcon())) {
				loadItems();
				player.closeInventory();
				openSellInventory(player);
				views.add(player);
				return;
			}

			buyItem(player, event.getCurrentItem());
		}

		if (event.getInventory().getName().equalsIgnoreCase("Sell Items") && views.contains(player) && event.getRawSlot() < event.getInventory().getSize()) {
			if (event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_SOME) {
				if (event.getCurrentItem() == null) {
					return;
				}
				
				event.setCancelled(true);
				ItemStack is = event.getCursor();
				sellItem(player, is, event.getInventory());
				player.getInventory().remove(is);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		if (views.contains(player) && (event.getInventory().getName().equalsIgnoreCase(shopName) || event.getInventory().getName().equalsIgnoreCase("Sell Items"))) {
			views.remove(player);
		}

		if (event.getInventory().getName().equalsIgnoreCase("Sell Items")) {
			event.getInventory().clear();
		}
	}

	public int getInventorySize(int i) {
		if (i <= 4) {
			return 27;
		} else if (i <= 7) {
			return 36;
		} else if (i <= 11) {
			return 54;
		}

		return 0;
	}

	public int getInventories(Map<ItemStack, Integer> items) {
		if (items.size() <= 11) {
			return 1;
		} else if (items.size() <= 22) {
			return 2;
		} else if (items.size() <= 33) {
			return 3;
		} else if (items.size() <= 44) {
			return 4;
		}

		return 0;
	}

	public boolean isSameInventory(Inventory i, Inventory q, ItemStack is) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : q.getContents()) {
			items.add(item);
		}

		int errors = 0;

		for (ItemStack item : i.getContents()) {
			if (!items.contains(item) && item != is) {
				errors++;
			}
		}

		if (errors >= 2) {
			return false;
		}

		if (!i.getTitle().equalsIgnoreCase(q.getTitle())) {
			return false;
		}

		if (!(i.getSize() == q.getSize())) {
			return false;
		}

		return true;
	}

	public int findBuyInventory(Inventory inv, ItemStack is) {
		for (int i = 0; i > (buyInventorys.size() - 1); i++) {
			if (isSameInventory(buyInventorys.get(i), inv, is)) {
				return i;
			}
		}

		return 0;
	}

	/***************************************
	 * 
	 * General Methods
	 * 
	 ***************************************/

	public void sendMessage(Player player, String message) {
		player.sendMessage(ChatColor.GREEN + "Shop> " + ChatColor.GRAY + message);
	}

	/***************************************
	 * 
	 * Item Stacks
	 * 
	 ***************************************/

	public ItemStack buyIcon() {
		List<String> lore = new ArrayList<String>();
		ItemStack is = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Buy Items");
		lore.add(ChatColor.GRAY + "Click on this item to");
		lore.add(ChatColor.GRAY + "gain access to this");
		lore.add(ChatColor.GRAY + "merchants buy menu.");
		im.setLore(lore);
		is.setItemMeta(im);

		return is;
	}

	public ItemStack sellIcon() {
		List<String> sellItemLore = new ArrayList<String>();
		ItemStack sellIS = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta sellIM = sellIS.getItemMeta();
		sellIM.setDisplayName(ChatColor.RED + "Sell Items");
		sellItemLore.add(ChatColor.GRAY + "Click on this item to");
		sellItemLore.add(ChatColor.GRAY + "gain access to this");
		sellItemLore.add(ChatColor.GRAY + "merchants sell menu.");
		sellIM.setLore(sellItemLore);
		sellIS.setItemMeta(sellIM);

		return sellIS;
	}

	public ItemStack tradeIcon() {
		List<String> tradeItemLore = new ArrayList<String>();
		ItemStack tradeIS = new ItemStack(Material.SKULL_ITEM);
		ItemMeta tradeIM = tradeIS.getItemMeta();
		tradeIM.setDisplayName(ChatColor.YELLOW + "Trade Items");
		tradeItemLore.add(ChatColor.GRAY + "This current menu is");
		tradeItemLore.add(ChatColor.GRAY + "still getting created!");
		tradeIM.setLore(tradeItemLore);
		tradeIS.setItemMeta(tradeIM);

		return tradeIS;
	}

	public ItemStack backIcon() {
		ItemStack is = new ItemStack(Material.BED);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Back");
		is.setItemMeta(im);

		return is;
	}

	public Inventory getMerchantInventory() {
		return merchantInventory;
	}

	public void setMerchantInventory(Inventory merchantInventory) {
		this.merchantInventory = merchantInventory;
	}

	public Map<ItemStack, Integer> getBuyItems() {
		return buyItems;
	}

	public void setBuyItems(Map<ItemStack, Integer> buyItems) {
		this.buyItems = buyItems;
	}

	public List<Inventory> getBuyInventorys() {
		return buyInventorys;
	}

	public void setBuyInventorys(List<Inventory> buyInventorys) {
		this.buyInventorys = buyInventorys;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public List<Player> getViews() {
		return views;
	}

	public void setViews(List<Player> views) {
		this.views = views;
	}
}
