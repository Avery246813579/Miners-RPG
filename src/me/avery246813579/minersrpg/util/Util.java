package me.avery246813579.minersrpg.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Util {
	public static Integer getBooleanByteType(boolean b) {
		if (b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String inventoryToString(Inventory invInventory) {
		String s = "";
		
		for(int i = 0; i < invInventory.getSize(); i++){
			if(invInventory.getItem(i) != null){
				ItemStack is = invInventory.getItem(i);
				
				if(EntityUtil.findWeapon(is) != null){
					s = s + "," + EntityUtil.findWeapon(is).getWeaponID() + ":" + is.getAmount();
				}else{
					s = s + "," + is.getTypeId() + ":" + is.getAmount();
				}
			}else{
				s = s + ",0:0";
			}
		}
		
		s = s.substring(1, s.length());
		
		return s;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack[] inventoryToItem(String s){
		String[] split = s.split(",");
		int i = 0;
		
		ItemStack[] itemStack = new ItemStack[split.length];
		for(String string : split){
			String[] secondSplit = string.split(":");
			
			if(secondSplit[0].equalsIgnoreCase("0")){
				itemStack[i] = new ItemStack(Material.CROPS);
			}else{
				if(EntityUtil.findWeaponWithNumber(Integer.parseInt(secondSplit[0])) != null){
					ItemStack is = EntityUtil.findWeaponWithNumber(Integer.parseInt(secondSplit[0])).getItem();
					is.setAmount(Integer.parseInt(secondSplit[1]));
					itemStack[i] = is;
				}else{
					ItemStack is = new ItemStack(Material.getMaterial(Integer.parseInt(secondSplit[0])));
					is.setAmount(Integer.parseInt(secondSplit[1]));
					itemStack[i] = is;
				}
			}
			
			i++;
		}
		
		return itemStack;
	}
}
