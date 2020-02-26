package me.avery246813579.minersrpg.miner;

import java.util.HashMap;
import java.util.Map;

import me.avery246813579.minersfortune.sql.tables.MinerTable;

public class MinerSkills {
	/** Variables **/
	private Map<SkillTypes, Integer> skills = new HashMap<SkillTypes, Integer>();
	private Miner miner;

	public MinerSkills(Miner miner) {
		this.miner = miner;

		loadSkills();
	}

	public void loadSkills() {
		/** Gets Skill String and splits at ',' **/
		String[] skillString = miner.getMinerTable().getSkills().split(",");

		/** Creates int for finding array **/
		int arrayLocation = 0;

		/** Loops and create new Skills **/
		for (SkillTypes skillTypes : SkillTypes.values()) {
			if (miner.getMinerTable().getSkills().equalsIgnoreCase("none")) {
				skills.put(skillTypes, 1);
			} else {
				int level = 1;

				if (skillString[arrayLocation] != null) {
					level = Integer.parseInt(skillString[arrayLocation]);
				}

				skills.put(skillTypes, level);
				arrayLocation++;
			}
		}
	}

	public void saveSkills() {
		/** Gets MinerTable and Creates skill string **/
		MinerTable minerTable = miner.getMinerTable();
		String skillString = "";

		/** Loops threw Skill Types and adds them to skill string **/
		for (SkillTypes skillTypes : SkillTypes.values()) {
			int level = skills.get(skillTypes);

			skillString = skillString + "," + level;
		}

		/** Sub Strings to remove extra ',' **/
		skillString = skillString.substring(1, skillString.length());

		/** Sets and Saves Miner Table **/
		minerTable.setSkills(skillString);
		miner.saveMinerTable(minerTable);
	}

	public Miner getMiner() {
		return miner;
	}

	public void setMiner(Miner miner) {
		this.miner = miner;
	}

	public Map<SkillTypes, Integer> getSkills() {
		return skills;
	}

	public void setSkills(Map<SkillTypes, Integer> skills) {
		this.skills = skills;
	}
}
