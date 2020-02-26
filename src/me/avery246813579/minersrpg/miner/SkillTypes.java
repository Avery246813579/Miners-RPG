package me.avery246813579.minersrpg.miner;

public enum SkillTypes {
	FISHING("Fishing"),
	MINING("Mining"),
	COOKING("Cooking"),
	PRAYER("Prayer"),
	SMITHING("Smithing"),
	WOODCUTTING("WoodCutting"),
	CRAFTING("Crafting"),
	MAGIC("Magic");
	
	/** Variables **/
	private String name;

	SkillTypes(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
