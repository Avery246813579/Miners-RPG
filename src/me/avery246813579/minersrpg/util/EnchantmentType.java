package me.avery246813579.minersrpg.util;

public enum EnchantmentType {
	
	Knockback("Knockback");
	
	String name;
	
	EnchantmentType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
