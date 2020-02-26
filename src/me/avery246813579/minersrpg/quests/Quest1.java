package me.avery246813579.minersrpg.quests;

import java.util.ArrayList;
import java.util.List;

import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.Quest;
import me.avery246813579.minersrpg.util.InventoryUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Quest1 extends Quest {
	public Quest1(Miner miner) {
		super(miner);
	}

	public Quest1(Miner miner, int i) {
		super(miner, i);
	}

	public void init() {
		setStep(getStep() + 1);

		List<String> dialog = new ArrayList<String>();
		dialog.add("Friend!! Are you alright?");
		dialog.add("Phew, I just saw you survive that wagon crash with no harm to you.");
		dialog.add("Maybe it's the god's giving you another chance?");
		dialog.add("Hey Ill tell you what, go to the pub and get me a drink.");
		dialog.add("I will reward you with some emeralds so you can start a new life.");
		readDialog(getPlayer(), ChatColor.GREEN + "John", 3, dialog);

		final Quest1 quest = this;
		new BukkitRunnable() {
			public void run() {
				quest.sendObjectiveUpdate(quest.getPlayer(), "Talk to Josh at the pub.");
			}
		}.runTaskLater(MinersRpg.getPlugin(), 300);
	}

	@Override
	protected void checkPlayerInteract(LivingEntity entity, Player player) {
		if (this.getStep() == 1 && entity.getCustomName().startsWith(ChatColor.GREEN + "Josh")) {
			if (InventoryUtil.hasOpenSpace(player.getInventory())) {
				setStep(getStep() + 1);

				List<String> dialog = new ArrayList<String>();
				dialog.add("Who are you?");
				dialog.add("A friend of Johns? Well then you are a friend of me!");
				dialog.add("You need a drink for John. Well of course! Here is some water");
				readDialog(player, ChatColor.GREEN + "John", 3, dialog);

				final Quest1 quest = this;
				new BukkitRunnable() {
					public void run() {
						quest.sendObjectiveUpdate(quest.getPlayer(), "Bring John his drink!");
						quest.getPlayer().getInventory().addItem(new ItemStack(Material.POTION));
					}
				}.runTaskLater(MinersRpg.getPlugin(), 180);
			} else {
				this.sendQuestMessage(player, ChatColor.RED + "You need to have space for an item!");
			}
		}

		if (this.getStep() == 2 && entity.getCustomName().startsWith(ChatColor.GREEN + "John")) {
			if (!player.getInventory().contains(Material.POTION)) {
				player.sendMessage(ChatColor.GRAY + "[1/1] " + ChatColor.GREEN + "John: " + ChatColor.BLUE + "Where is my drink?");
				return;
			}

			player.getInventory().removeItem(new ItemStack(Material.POTION, 1));
			setStep(getStep() + 1);

			List<String> dialog = new ArrayList<String>();
			dialog.add("Thanks for the drink.");
			dialog.add("Here is some emeralds to start your adventure");

			readDialog(player, ChatColor.GREEN + "Josh", 2, dialog);

			final Quest1 quest = this;
			new BukkitRunnable() {
				public void run() {
					quest.sendQuestMessage(quest.getPlayer(), ChatColor.YELLOW + "You have earned 20 Emeralds!");
				}
			}.runTaskLater(MinersRpg.getPlugin(), 80);
		}
	}

	@Override
	protected void checkKilledEntity(Entity entity, Player player) {

	}
}
