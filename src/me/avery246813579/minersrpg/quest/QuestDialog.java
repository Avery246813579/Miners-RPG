package me.avery246813579.minersrpg.quest;

import java.util.List;

import me.avery246813579.minersrpg.util.EntityUtil;

import org.bukkit.entity.Player;

public class QuestDialog {
	/** Variables **/
	private Player player;
	private String speaker;
	private List<String> dialog;
	private int timeBetween, maxTimeBetween, currentLine;

	public QuestDialog(Player player, String speaker, int timeBetween, List<String> dialog){
		this.player = player;
		this.speaker = speaker; 
		this.timeBetween = 1;
		this.setMaxTimeBetween(timeBetween);
		this.dialog = dialog;
		
		EntityUtil.getDialogs().add(this);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTimeBetween() {
		return timeBetween;
	}

	public void setTimeBetween(int timeBetween) {
		this.timeBetween = timeBetween;
	}

	public List<String> getDialog() {
		return dialog;
	}

	public void setDialog(List<String> dialog) {
		this.dialog = dialog;
	}

	public int getMaxTimeBetween() {
		return maxTimeBetween;
	}

	public int setMaxTimeBetween(int maxTimeBetween) {
		this.maxTimeBetween = maxTimeBetween;
		return maxTimeBetween;
	}

	public int getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
}
