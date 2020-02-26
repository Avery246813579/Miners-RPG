package me.avery246813579.minersrpg.quest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.avery246813579.minersrpg.MinersRpg;

public class QuestHandler implements Listener {
	/** Classes **/
	MinersRpg plugin;

	/** Variables **/
	private static Map<Player, List<Quest>> quests = new HashMap<Player, List<Quest>>();

	public QuestHandler(MinersRpg plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof LivingEntity) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				List<Quest> questList = quests.get(player);
				
				for(Quest quest : questList){
					quest.checkPlayerInteract((LivingEntity) event.getRightClicked(), event.getPlayer());
				}
			}
		}
	}
	
	public static Quest getQuestByName(Player player, String name){
		List<Quest> quest = quests.get(player);
		
		for(Quest q : quest){
			if(ChatColor.stripColor(name).equalsIgnoreCase(q.getQuestName())){
				return q;
			}
		}
		
		return null;
	}

	public static Map<Player, List<Quest>> getQuests() {
		return quests;
	}

	public static void setQuests(Map<Player, List<Quest>> quests) {
		QuestHandler.quests = quests;
	}
}
