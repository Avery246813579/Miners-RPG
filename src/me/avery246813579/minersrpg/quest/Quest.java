package me.avery246813579.minersrpg.quest;

import java.util.ArrayList;
import java.util.List;

import me.avery246813579.minersrpg.miner.Miner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Quest implements Listener {
	/** Private Variables **/
	private int step, minLevel, stringStep;
	private boolean finshed;
	private Player player;
	private Miner miner;

	/** Protected Variables **/
	protected List<String> stringSteps = new ArrayList<String>();
	protected String questName, startLocation;

	public Quest(Miner miner) {
		player = miner.getPlayer();
		this.miner = miner;
		step = -1;

		/** Settings basics if not set **/
		questName = "Unnamed Quest";
		startLocation = "1, 1, 1";
	}

	public Quest(Miner miner, int i) {
		player = miner.getPlayer();
		this.miner = miner;
		step = i;
	}

	public void initializeQuest() {
		if (miner.getLevel() >= minLevel) {
			init();
			step++;
		} else {
			sendQuestMessage(player, ChatColor.RED + "You have to be at least level " + minLevel + " to do this quest.");
		}
	}

	public void openQuestInventory() {
		/** Create Inventory **/
		Inventory i = Bukkit.createInventory(null, getInventorySize(), questName);

		int step = 0;
		int location = 9;
		for (String s : stringSteps) {
			location++;
			ItemStack is;

			String status;
			if (stringStep > step) {
				is = new ItemStack(Material.STAINED_CLAY, 1, (byte) 9);
				status = "Complete";
			} else if (stringStep == step) {
				is = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
				status = "Current";
			} else {
				is = new ItemStack(Material.STAINED_CLAY, 1, (byte) 7);
				status = "Upcoming";
			}

			List<String> lore = new ArrayList<String>();
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Quest Step " + (step + 1));
			lore.add(ChatColor.GRAY + "Step status: " + ChatColor.YELLOW + status);
			lore.add(ChatColor.GRAY + "Description: " + ChatColor.YELLOW + getQuestList(s).get(0));
			List<String> sub1 = getQuestList(s);
			sub1.remove(getQuestList(s).get(0));

			for (String sub : sub1) {
				lore.add(ChatColor.YELLOW + sub);
			}

			im.setLore(lore);
			is.setItemMeta(im);

			i.setItem(location, is);

			if (location == 16) {
				location = 19;
				return;
			}

			if (location == 25) {
				location = 28;
				return;
			}

			if (location == 34) {
				location = 16;
				return;
			}

			step++;
		}

		player.openInventory(i);
	}

	public List<String> getQuestList(String step) {
		String rawStep = step;

		List<String> list = new ArrayList<String>();
		if (rawStep.length() > 30) {
			if (!(rawStep.charAt(30) == ' ')) {
				list.add(rawStep.substring(0, 30));
			} else {
				list.add(rawStep.substring(0, 30));
			}

			rawStep = rawStep.substring(30, rawStep.length());

			list.add(rawStep);
		} else {
			list.add(rawStep);
		}

		return list;
	}

	public int getInventorySize() {
		if (stringSteps.size() <= 4) {
			return 27;
		} else if (stringSteps.size() <= 7) {
			return 36;
		} else if (stringSteps.size() <= 12) {
			return 45;
		} else if (stringSteps.size() <= 15) {
			return 54;
		}

		return 0;
	}

	public void finishQuest() {
		miner.getPlayer().getInventory().setItem(6, miner.questBook());
	}

	public boolean canDoQuest() {
		if (minLevel <= miner.getLevel()) {
			return true;
		}

		return false;
	}

	/**********************************
	 * 
	 * Sending Messages
	 * 
	 **********************************/

	public void readDialog(Player player, String speaker, int timeBetween, List<String> dialog) {
		new QuestDialog(player, speaker, timeBetween, dialog);
	}

	public void sendObjectiveUpdate(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[Quest Objective Update] " + message);
	}

	public void sendQuestMessage(Player player, String message) {
		player.sendMessage(ChatColor.GREEN + "Quest> " + ChatColor.GRAY + message);
	}

	/**********************************
	 * 
	 * Abstract Methods
	 * 
	 **********************************/

	protected abstract void checkPlayerInteract(LivingEntity entity, Player player);

	protected abstract void checkKilledEntity(Entity entity, Player player);

	protected abstract void init();

	/**********************************
	 * 
	 * Getters & Setters
	 * 
	 **********************************/

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public boolean isFinshed() {
		return finshed;
	}

	public void setFinshed(boolean finshed) {
		this.finshed = finshed;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public Miner getMiner() {
		return miner;
	}

	public void setMiner(Miner miner) {
		this.miner = miner;
	}

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public List<String> getStringSteps() {
		return stringSteps;
	}

	public void setStringSteps(List<String> stringSteps) {
		this.stringSteps = stringSteps;
	}

	public int getStringStep() {
		return stringStep;
	}

	public void setStringStep(int stringStep) {
		this.stringStep = stringStep;
	}
}
