package me.avery246813579.minersrpg.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
	/** Checks if a inventory has space for a item **/
	public static boolean hasOpenSpace(Inventory inventory){
		for(ItemStack is : inventory.getContents()){
			if(is == null){
				return true;
			}
		}
		
		return false;
	}
	
	/** Gets the amount of open space **/
	public static int getOpenSpace(Inventory inventory){
		int openSpace = 0;
		
		for(ItemStack is : inventory.getContents()){
			if(is.equals(null)){
				openSpace++;
			}
		}
		
		return openSpace;
	}
}
