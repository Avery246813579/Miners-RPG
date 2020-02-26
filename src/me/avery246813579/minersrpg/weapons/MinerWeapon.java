package me.avery246813579.minersrpg.weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.avery246813579.minersrpg.entities.MinerEntity;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.util.EnchantmentType;
import me.avery246813579.minersrpg.util.EntityUtil;

public class MinerWeapon implements Listener {
	/** Variables **/
	private List<EnchantmentType> enchantments = new ArrayList<EnchantmentType>();
	private int minDamage, maxDamage, critChance, armorPen, minLevel, weaponID, sellPrice;
	private ItemStack item;
	private String name;

	/** TODO When crit, add crit particle effect **/

	public MinerWeapon(String name, int minDamage, int maxDamage, Material m, int weaponId) {
		this.name = name;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.critChance = 0;
		this.minLevel = 0;
		this.weaponID = weaponId;
		
		createItem(m);
	}

	public void createItem(Material m) {
		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);

		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RED + "Dam: " + minDamage + "-" + maxDamage);
		lore.add(ChatColor.GOLD + "Lev. Min: " + minLevel);
		lore.add("");
		if (critChance != 0)
			lore.add(ChatColor.YELLOW + "Crit Chance: " + critChance + "%");
		if (armorPen != 0)
			lore.add(ChatColor.YELLOW + "Armor Pen: " + armorPen);
		if (!enchantments.isEmpty()) {
			String enchant = ChatColor.YELLOW + "Enchants: " + enchantments.get(0).getName();

			for (EnchantmentType et : enchantments) {
				if (et == enchantments.get(0)) {
					return;
				}

				enchant = enchant + ", " + et.getName();
			}

			lore.add(enchant);
		}

		im.setLore(lore);
		is.setItemMeta(im);

		this.item = is;
	}

	public int findDamage(int armor) {
		Random r = new Random();
		int startingDamage = r.nextInt((maxDamage - minDamage) + 1) + minDamage;

		if (armorPen == 0) {
			return startingDamage;
		}

		if (armor - armorPen >= 0) {
			return startingDamage + armorPen;
		}

		return startingDamage + (armorPen - armor);
	}

	public boolean isCritStrike() {
		Random r = new Random();
		int i = (r.nextInt((100 - 1) + 1) + 1);

		if (i <= critChance) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (EntityUtil.findEntity(e.getEntity()) == null) {
			return;
		}

		MinerEntity me = EntityUtil.findEntity(e.getEntity());

		if (me.isNPC()) {
			e.setCancelled(true);
			return;
		}
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

			e.setCancelled(true);
			event.setCancelled(true);
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				int damage = 1;
				int armor = me.getArmor();
				if (EntityUtil.findWeapon(player.getItemInHand()) != null) {
					if (EntityUtil.findWeapon(player.getItemInHand()) == this) {
						damage = findDamage(armor);

						if (isCritStrike()) {
							damage = damage * 2;
							player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
							player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 10L, 10L);
						}
					}
				}

				double d = me.findDamageWithArmor(damage);
				if ((me.getEntity().getHealth() - d) <= 0) {
					if (me.getKiller() != null) {
						int exp = me.getEntityXp();
						me.showEntityXp(me.getKiller(), exp);

						Miner miner = EntityUtil.findMiner(Bukkit.getPlayer(me.getKiller()));
						miner.setExp(miner.getExp() + exp);
					} else {
						me.showEntityXp("The Wind", me.getEntityXp());
					}
				} else {
					me.showEntityHealth(d);
					EntityUtil.showEntityTagTimer(me, d);
				}

				LivingEntity le = (LivingEntity) event.getEntity();
				le.damage(d);
			}
		}
	}

	public List<EnchantmentType> getEnchantments() {
		return enchantments;
	}

	public void setEnchantments(List<EnchantmentType> enchantments) {
		this.enchantments = enchantments;
	}

	public int getMinDamage() {
		return minDamage;
	}

	public void setMinDamage(int minDamage) {
		this.minDamage = minDamage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public int getCritChance() {
		return critChance;
	}

	public void setCritChance(int critChance) {
		this.critChance = critChance;
	}

	public int getArmorPen() {
		return armorPen;
	}

	public void setArmorPen(int armorPen) {
		this.armorPen = armorPen;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeaponID() {
		return weaponID;
	}

	public void setWeaponID(int weaponID) {
		this.weaponID = weaponID;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}
}
