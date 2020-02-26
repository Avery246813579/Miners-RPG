package me.avery246813579.minersrpg.miner;

import java.util.ArrayList;
import java.util.List;

import me.avery246813579.minersfortune.sql.tables.MinerTable;
import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.quest.Quest;
import me.avery246813579.minersrpg.quest.QuestHandler;
import me.avery246813579.minersrpg.quests.Quest1;
import me.avery246813579.minersrpg.quests.QuestStart;
import me.avery246813579.minersrpg.spells.MinerSpells;
import me.avery246813579.minersrpg.util.EntityUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Miner {
	/** Classes **/
	private MinerSkills minerSkills;
	private MinerSpells minerSpells;
	MinersRpg plugin;

	/** Variables **/
	private int player_id, exp, level, emeralds;
	private ItemStack[] inventory, vaultStorage;
	private boolean isFrozen = false;
	private Location lastLocation;
	private String classType;
	private Player player;

	public Miner(MinersRpg plugin, Player player, int player_id, int exp, int emeralds, String lastLocation, String inventory, String classType, String masteries, String relics, String quests, String vaultStorage) {
		this.plugin = plugin;
		this.player = player;
		this.player_id = player_id;
		this.exp = exp;
		this.emeralds = emeralds;
		this.classType = classType;

		/** Loads String to Methods **/
		if (!lastLocation.equalsIgnoreCase("none"))
			this.lastLocation = stringToLocation(lastLocation);
		if (!inventory.equalsIgnoreCase("none"))
			this.inventory = stringToItemArray(inventory);
		if (!vaultStorage.equalsIgnoreCase("none"))
			this.vaultStorage = stringToItemArray(vaultStorage);
		else
			this.vaultStorage = new ItemStack[27];

		minerSpells = new MinerSpells(this);
		minerSkills = new MinerSkills(this);
		loadPlayer();
	}

	public void checkLevelUp() {
		if (level == 0) {
			level = getLevelFromExp(exp);
		} else {
			int nextLevel = getExpFromLevel((getLevel() + 1));
			if (nextLevel < exp) {
				player.sendMessage(ChatColor.RED + "Level up");
				level++;
			}
		}
		float min = getExpFromLevel(level) + getExpFromLevel(level + 1);
		float max = getExpFromLevel(level + 1) * 2;
		float newMax = max - min;
		float newMin = exp - getExpFromLevel(level);
		float xp = (newMin / newMax);

		player.setLevel(level);
		player.setExp(xp);
	}

	public void openQuestBook() {
		/** Creates Inventory **/
		Inventory inventory = Bukkit.createInventory(null, 54, "Quest List");

		/** Loads Items **/
		int location = 9;

		for (Quest quest : QuestHandler.getQuests().get(player)) {
			location++;
			
			
			ItemStack questItem;
			String status;

			if(quest.canDoQuest()){
				if(quest.isFinshed()){
					questItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 9);
					status = "Complete";
				}else{
					questItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
					status = "Available";
				}
			}else{
				questItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 15);
				status = "Unavailable";
			}
			
			ItemMeta im = questItem.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + quest.getQuestName());
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Status: " + ChatColor.YELLOW + status);
			lore.add(ChatColor.GRAY + "Min Level: " + ChatColor.YELLOW + quest.getMinLevel());
			lore.add(ChatColor.GRAY + "Start Location: " + ChatColor.YELLOW + quest.getStartLocation());
			im.setLore(lore);
			questItem.setItemMeta(im);
			
			inventory.setItem(location, questItem);
			
			if (location == 16) {
				location = 19;
				return;
			}
			
			if(location == 25){
				location = 28;
				return;
			}
			
			if(location == 34){
				location = 16;
				return;
			}
		}

		/** Opens Inventory **/
		player.openInventory(inventory);
	}

	public void loadPlayer() {
		/** Clears and sets players inventory **/
		if (inventory != null) {
			player.getInventory().clear();
			player.getInventory().setContents(inventory);
			player.getInventory().remove(Material.CROPS);
		}

		/** Teleports Player to last location **/
		if (lastLocation != null) {
			player.teleport(lastLocation);
		}

		/** Sets players level **/
		checkLevelUp();

		/** Loads Quests **/
		createQuests();

		/** Checks if they just joined **/
		if (exp == 0) {
			exp = 1;
			level = 1;
			QuestHandler.getQuests().get(player).get(0).initializeQuest();
		}
	}

	public void savePlayer() {
		/** Creates a fake inventory for vault storage **/
		Inventory vaultInventory = Bukkit.createInventory(null, vaultStorage.length);
		vaultInventory.setContents(vaultStorage);
		
		/** Save Quests **/
		saveQuests();
		
		/** Saves Miner Subclasses **/
		getMinerSpells().saveCooldowns();
		getMinerSkills().saveSkills();

		/** Creates string names for variables **/
		String stringLocation = locationToString(player.getLocation());
		String stringInventory = inventoryToString(player.getInventory());
		String stringVaultStorage = inventoryToString(vaultInventory);

		/** Saves Miner Table Account **/
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		minerTable.setExp(exp);
		minerTable.setEmeralds(emeralds);
		minerTable.setLastLocation(stringLocation);
		minerTable.setInventory(stringInventory);
		minerTable.setVaultStorage(stringVaultStorage);
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);
	}

	public void createQuests() {
		List<Quest> quests = new ArrayList<Quest>();

		quests.add(new QuestStart(this));
		quests.add(new Quest1(this));

		updateQuests(quests);
	}

	public void updateQuests(List<Quest> quests) {
		if (MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player)).getQuests().equalsIgnoreCase("none")) {
			QuestHandler.getQuests().put(player, quests);
			return;
		}
		
		int i = 0;
		String[] questStatus = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player)).getQuests().split(",");
		for (Quest quest : quests) {
			String[] questStep = questStatus[i].split("=");
			quest.setStep(Integer.parseInt(questStep[0]));
			quest.setFinshed(getBooleanFromString(questStep[1]));
			i++;
		}

		QuestHandler.getQuests().put(player, quests);
	}

	public void saveQuests() {
		if (QuestHandler.getQuests().get(player) == null) {
			return;
		}

		String questString = "";
		List<Quest> quests = QuestHandler.getQuests().get(player);
		for (Quest quest : quests) {
			questString = questString + "," + quest.getStep() + "=" + getBooleanByteType(quest.isFinshed());
		}

		questString = questString.substring(1, questString.length());

		/** Saves String **/
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		minerTable.setQuests(questString);
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);
	}
	
	public MinerTable getMinerTable(){
		return MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
	}
	
	public void saveMinerTable(MinerTable minerTable){
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);
	}
	
	/**********************************
	 * 
	 * Send Exp Level
	 * 
	 **********************************/
	
	public void addEmeralds(int amount){
		MinerTable minerTable = MinersRpg.getMinersFortune().getSqlHandler().getMiner(MinersRpg.getMinersFortune().getSqlHandler().getPlayerId(player));
		minerTable.setEmeralds(minerTable.getEmeralds() + amount);
		MinersRpg.getMinersFortune().getSqlHandler().saveMiner(minerTable);
	}

	public void sendEmeraldMessage(String message){
		player.sendMessage(ChatColor.GRAY + "[Emerald Reward] " + ChatColor.YELLOW + message);
	}
	
	public void sendExpMessage(String message){
		player.sendMessage(ChatColor.GRAY + "[Exp Reward] " + ChatColor.YELLOW + message);
	}

	/**********************************
	 * 
	 * Itemstacks
	 * 
	 **********************************/
	
	public ItemStack questBook(){
		int finishedQuests = 0;
		
		for(Quest quest : QuestHandler.getQuests().get(player)){
			if(quest.isFinshed()){
				finishedQuests++;
			}
		}
		
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		im.setDisplayName(ChatColor.GREEN + "Quest Book");
		lore.add(ChatColor.GREEN + "Completed: " + ChatColor.GRAY + "(" + finishedQuests + "/" + QuestHandler.getQuests().get(player).size() + ")");
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	/**********************************
	 * 
	 * Helpers for Sql to Java
	 * 
	 **********************************/

	public String locationToString(Location l) {
		return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getYaw() + "," + l.getPitch();
	}

	public Location stringToLocation(String stringLocation) {
		String[] splitLocation = stringLocation.split(",");
		return new Location(Bukkit.getWorld(splitLocation[0]), Integer.parseInt(splitLocation[1]), Integer.parseInt(splitLocation[2]), Integer.parseInt(splitLocation[3]), Float.parseFloat(splitLocation[4]), Float.parseFloat(splitLocation[5]));
	}

	@SuppressWarnings("deprecation")
	public static String inventoryToString(Inventory invInventory) {
		String s = "";

		for (int i = 0; i < invInventory.getSize(); i++) {
			if (invInventory.getItem(i) != null) {
				ItemStack is = invInventory.getItem(i);

				if (EntityUtil.findWeapon(is) != null) {
					s = s + "," + EntityUtil.findWeapon(is).getWeaponID() + ":" + is.getAmount();
				} else {
					s = s + "," + is.getTypeId() + ":" + is.getAmount();
				}
			} else {
				s = s + ",0:0";
			}
		}

		s = s.substring(1, s.length());

		return s;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack[] stringToItemArray(String s) {
		String[] split = s.split(",");
		int i = 0;

		ItemStack[] itemStack = new ItemStack[split.length];
		for (String string : split) {
			String[] secondSplit = string.split(":");

			if (secondSplit[0].equalsIgnoreCase("0")) {
				itemStack[i] = new ItemStack(Material.CROPS);
			} else {
				if (EntityUtil.findWeaponWithNumber(Integer.parseInt(secondSplit[0])) != null) {
					ItemStack is = EntityUtil.findWeaponWithNumber(Integer.parseInt(secondSplit[0])).getItem();
					is.setAmount(Integer.parseInt(secondSplit[1]));
					itemStack[i] = is;
				} else {
					ItemStack is = new ItemStack(Material.getMaterial(Integer.parseInt(secondSplit[0])));
					is.setAmount(Integer.parseInt(secondSplit[1]));
					itemStack[i] = is;
				}
			}

			i++;
		}

		return itemStack;
	}

	public static Integer getBooleanByteType(boolean b) {
		if (b) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean getBooleanFromString(String s) {
		int i = Integer.parseInt(s);

		if (i == 1) {
			return true;
		} else {
			return false;
		}
	}

	public int getLevelFromExp(int exp) {
		if (exp <= 0) {
			return 0;
		} else if (exp <= 90) {
			return 1;
		} else if (exp <= 188) {
			return 2;
		} else if (exp <= 293) {
			return 3;
		} else if (exp <= 406) {
			return 4;
		} else if (exp <= 854) {
			return 5;
		} else if (exp <= 1330) {
			return 6;
		} else if (exp <= 1834) {
			return 7;
		} else if (exp <= 2366) {
			return 8;
		} else if (exp <= 2926) {
			return 9;
		} else if (exp <= 3976) {
			return 10;
		} else if (exp <= 5076) {
			return 11;
		} else if (exp <= 6226) {
			return 12;
		} else if (exp <= 7426) {
			return 13;
		} else if (exp <= 8676) {
			return 14;
		} else if (exp <= 9976) {
			return 15;
		} else if (exp <= 11326) {
			return 16;
		} else if (exp <= 12726) {
			return 17;
		} else if (exp <= 14176) {
			return 18;
		} else if (exp <= 15676) {
			return 19;
		} else if (exp <= 17807) {
			return 20;
		} else if (exp <= 20007) {
			return 21;
		} else if (exp <= 22276) {
			return 22;
		} else if (exp <= 24614) {
			return 23;
		} else if (exp <= 27020) {
			return 24;
		} else if (exp <= 29495) {
			return 25;
		} else if (exp <= 32039) {
			return 26;
		} else if (exp <= 34652) {
			return 27;
		} else if (exp <= 37333) {
			return 28;
		} else if (exp <= 40084) {
			return 29;
		}

		return 0;
	}

	public int getExpFromLevel(int level) {
		if (level == 1) {
			return 0;
		} else if (level == 2) {
			return 90;
		} else if (level == 3) {
			return 188;
		} else if (level == 4) {
			return 293;
		} else if (level == 5) {
			return 406;
		} else if (level == 6) {
			return 854;
		} else if (level == 7) {
			return 1330;
		} else if (level == 8) {
			return 1834;
		} else if (level == 9) {
			return 2366;
		} else if (level == 10) {
			return 2926;
		} else if (level == 11) {
			return 3976;
		} else if (level == 12) {
			return 5076;
		} else if (level == 13) {
			return 6226;
		} else if (level == 14) {
			return 7426;
		} else if (level == 15) {
			return 8676;
		} else if (level == 16) {
			return 9976;
		} else if (level == 17) {
			return 11326;
		} else if (level == 18) {
			return 12726;
		} else if (level == 19) {
			return 14176;
		} else if (level == 20) {
			return 15676;
		} else if (level == 21) {
			return 17807;
		} else if (level == 22) {
			return 20007;
		} else if (level == 23) {
			return 22276;
		} else if (level == 24) {
			return 24614;
		} else if (level == 25) {
			return 27020;
		} else if (level == 26) {
			return 29495;
		} else if (level == 27) {
			return 32039;
		} else if (level == 28) {
			return 34652;
		} else if (level == 29) {
			return 37333;
		} else if (level == 30) {
			return 40084;
		}

		return 0;
	}

	public MinersRpg getPlugin() {
		return plugin;
	}

	public void setPlugin(MinersRpg plugin) {
		this.plugin = plugin;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
		checkLevelUp();
	}

	public int getEmeralds() {
		return emeralds;
	}

	public void setEmeralds(int emeralds) {
		this.emeralds = emeralds;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

	public ItemStack[] getVaultStorage() {
		return vaultStorage;
	}

	public void setVaultStorage(ItemStack[] vaultStorage) {
		this.vaultStorage = vaultStorage;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MinerSpells getMinerSpells() {
		return minerSpells;
	}

	public void setMinerSpells(MinerSpells minerSpells) {
		this.minerSpells = minerSpells;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public MinerSkills getMinerSkills() {
		return minerSkills;
	}

	public void setMinerSkills(MinerSkills minerSkills) {
		this.minerSkills = minerSkills;
	}
}


