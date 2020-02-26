package me.avery246813579.minersrpg.spells;

public enum SpellTypes {
	Fireball("Fireball", 2, 1, 1),
	Spawn("Spawn Teleport", 900, 2, 5),
	Speed("Speed Buff", 600, 3, 10);
	
	/** Varialbes **/
	private int cooldown, spellNumber, minLevel;
	private String wandName;
	
	SpellTypes(String wandName, int cooldown, int spellNumber, int minLevel){
		this.setWandName(wandName);
		this.setCooldown(cooldown);
		this.spellNumber = spellNumber;
		this.minLevel = minLevel;
	}
	
	public static SpellTypes findSpellType(int i){
		for(SpellTypes st : SpellTypes.values()){
			if(st.getSpellNumber() == i){
				return st;
			}
		}
		
		return null;
	}

	public String getWandName() {
		return wandName;
	}

	public void setWandName(String wandName) {
		this.wandName = wandName;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getSpellNumber() {
		return spellNumber;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public void setSpellNumber(int spellNumber) {
		this.spellNumber = spellNumber;
	}
}
