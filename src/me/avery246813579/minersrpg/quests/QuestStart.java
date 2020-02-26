package me.avery246813579.minersrpg.quests;

import java.util.ArrayList;
import java.util.List;

import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.Quest;
import me.avery246813579.minersrpg.weapons.WeaponHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestStart extends Quest{

	public QuestStart(Miner miner) {
		super(miner);
		
		this.questName = "Welcome to the Kingdom";
		this.startLocation = "First Join";
		this.setMinLevel(1);
		
		List<String> lore = new ArrayList<String>();
		lore.add("Listen to Avery and begin your adventure!");
		lore.add("Go to the stable and get 3 Raw Beef, and 2 Eggs.");
		this.stringSteps = lore;
	}
	
	@Override
	protected void init() {
		/** Teleports Player **/
		getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1532, 101, 183, 0, 0));

		/** Freezes the player **/
		this.getMiner().setFrozen(true);
		
		/** Gives the player blindness **/
		this.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 480, 5));
		
		/** Loads first message **/
		List<String> firstMessage = new ArrayList<String>();
		firstMessage.add("Hello Adventurer. My name is Avery. Who I am does not matter at the moment.");
		firstMessage.add("What really matters is that you were chosen to live.");
		firstMessage.add("At the moment you are unconsious. You will wake up with no harm to yourself.");
		firstMessage.add("Once you regain consiousness, find a man named Merek and talk to him.");
		firstMessage.add("I will contact you again when I am need.");
		firstMessage.add("Good luck Young Adventurer.");
		
		/** Reads the player the first message **/
		readDialog(this.getPlayer(), "Avery", 4, firstMessage);
		
		/** Waits for the message to end **/
		final Quest quest = this;
		new BukkitRunnable(){
			public void run(){
				/** Sends player objective update **/
				quest.sendObjectiveUpdate(getPlayer(), "Find Merek the farmer");
				
				/** Teleports player to start **/
				getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1477, 34, 152, 145, 0));
				
				/** Unfreezes player **/
				getMiner().setFrozen(false);
				
				/** Gives players starting items **/
				getPlayer().getInventory().setItem(7, getMiner().getMinerSpells().wand());
				
				/** Sets next step **/
				setStep(getStep() + 1);
			}
		}.runTaskLater(MinersRpg.getPlugin(), 480);
	}

	@Override
	protected void checkPlayerInteract(LivingEntity entity, Player player) {
		if(getStep() == 1){
			if(entity.getCustomName().startsWith(ChatColor.GREEN + "Merek")){
				/** Plays sound indicating sucess **/
				getPlayer().playSound(getPlayer().getLocation(), Sound.SUCCESSFUL_HIT, 5F, 5F);
				
				/** Loads Mereks first message **/
				List<String> firstMessage = new ArrayList<String>();
				firstMessage.add("Well hello Young Adventurer. I am Merek.");
				firstMessage.add("I have been waiting for you to wake up for some time now.");
				firstMessage.add("You were just on your way to Stonegate castle when your transport got overrun by the undead.");
				firstMessage.add("You were unconcious for a few hours, I moved you away from the crash and brought you to my barn.");
				firstMessage.add("Since it seems God has spared your life, I am going to help you get back on your feet");
				firstMessage.add("Ill tell you what! If you can bring me 3 Raw Beef, and 2 Eggs I will give you some Emeralds!");
				firstMessage.add("Here is a sword so that you can kill the animals.");
				firstMessage.add("The stable is located at [1234, 233, 2342]. Now off with you!");
				
				/** Reads Messages **/
				readDialog(getPlayer(), "Merek", 4, firstMessage);
				
				new BukkitRunnable(){
					public void run(){
						/** Sends Quest Update **/
						sendObjectiveUpdate(getPlayer(), "Head to Mereks Stable at [1234, 123,234] and get 3 Raw Beef, and 2 Eggs.");
					
						/** Gives player basic sword **/
						getPlayer().getInventory().addItem(WeaponHandler.oakWoodSword.getItem());
						
						/** Sets next step **/
						setStep(getStep() + 1);
						setStringStep(getStringStep() + 1);
					}
				}.runTaskLater(MinersRpg.getPlugin(), 640);
			}
		}if(getStep() == 2 && entity.getCustomName().startsWith(ChatColor.GREEN + "Merek")){
			if(!getPlayer().getInventory().contains(Material.EGG, 3) && !getPlayer().getInventory().contains(Material.RAW_BEEF, 2)){
				getPlayer().sendMessage(ChatColor.GRAY + "[1/1] Merek: " + ChatColor.BLUE + "Where is my food? Get to work!");
				return;
			}
			
			/** Plays sound indicating sucess **/
			getPlayer().playSound(getPlayer().getLocation(), Sound.SUCCESSFUL_HIT, 5F, 5F);
			
			/** Loads Mereks first message **/
			List<String> firstMessage = new ArrayList<String>();
			firstMessage.add("You did it!");
			firstMessage.add("Test Quest Over");

			/** Reads Messages **/
			readDialog(getPlayer(), "Merek", 4, firstMessage);
			
			new BukkitRunnable(){
				public void run(){
					/** Sends Quest Update **/
					sendObjectiveUpdate(getPlayer(), "Quest Complete");
				
					/** Gives player basic sword **/
					getMiner().sendExpMessage("100");
					
					/** Adds Exp **/
					getMiner().setExp(getMiner().getExp() + 100);
					
					/** Adds Emeralds **/
					getMiner().addEmeralds(10);
					getMiner().sendEmeraldMessage("10");
					
					/** Sets next step **/
					setStep(getStep() + 1);
					setStringStep(getStringStep() + 1);
					
					/** Updates the quest finished **/
					setFinshed(true);
				}
			}.runTaskLater(MinersRpg.getPlugin(), 160);
		}
	}

	@Override
	protected void checkKilledEntity(Entity entity, Player player) {
		final Quest quest = this;
		new BukkitRunnable() {
			public void run() {
				quest.sendQuestMessage(quest.getPlayer(), ChatColor.YELLOW + "You have earned 20 Emeralds!");
			}
		}.runTaskLater(MinersRpg.getPlugin(), 80);

	}
}
