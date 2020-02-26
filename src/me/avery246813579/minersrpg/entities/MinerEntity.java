package me.avery246813579.minersrpg.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.util.EntityUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class MinerEntity implements Listener {
	private int maxHealth, damage, armor, level, itemDropRate, minXp, maxXp, respawnTime, moveDistance, hostileZone, unlockDistance;
	private List<ItemStack> itemDrops = new ArrayList<ItemStack>();
	private ItemStack helmet, chestplate, leggings, boots, hand;
	private String name, killer, suffix;
	private boolean isNPC, isHostile;
	private Location spawnLocation;
	private EntityType entityType;
	private LivingEntity entity;
	private Player targetPlayer;

	public MinerEntity(String s, Location l, EntityType e) {
		this.name = s;
		this.spawnLocation = l;
		this.entityType = e;
		this.moveDistance = 15;
		this.hostileZone = 15;
		this.unlockDistance = 15;

		level = 1;
		maxHealth = 1;
		
		EntityUtil.addEntity(this);
	}

	public void spawnEntity() {
		/** Creating the Entity **/
		Entity e = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
		entity = (LivingEntity) e;
		
		if(isNPC){
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 300));
		}
		
		/** Giving Entity Name **/
		entity.setRemoveWhenFarAway(false);
		entity.setMaxHealth(maxHealth);
		entity.setHealth(maxHealth);
		showEntityName(-101);

		giveItems();
	}

	public void showEntityName(double d) {
		if(d != -101 && entity.getHealth() != d){
			return;
		}
		
		ChatColor color = ChatColor.RED;
		String level = ChatColor.GOLD + "[Lv. " + this.level + "]";

		if (entityType == EntityType.VILLAGER) {
			color = ChatColor.GREEN;
		}

		if (isNPC) {
			level = ChatColor.GOLD + "[NPC]";
		}
		
		if(suffix != null){
			level = ChatColor.GOLD + "[" + suffix + "]";
		}

		entity.setCustomName(color + name + " " + level);
		entity.setCustomNameVisible(true);
	}

	public void showEntityHealth(double damage) {
		double i = (entity.getHealth() - damage);
		if (i < 0) {
			i = 0.0;
		}

		entity.setCustomName(ChatColor.RED + "♥ " + ChatColor.YELLOW + (Math.round(i * 10.0) / 10.0) + ChatColor.RED + " ♥");
		entity.setCustomNameVisible(true);
	}

	public void showEntityXp(String killer, int xp) {
		entity.setCustomName(ChatColor.RED + "☠  " + killer + " ☠ " + ChatColor.GOLD + "[" + xp + "]");
		entity.setCustomNameVisible(true);
		entity.damage(30000000);;

		Miner miner = EntityUtil.findMiner(Bukkit.getPlayer(killer));
		miner.setExp(miner.getExp() + xp);
	}

	public void giveItems() {
		EntityEquipment ee = entity.getEquipment();
		ee.setHelmet(helmet);
		ee.setChestplate(chestplate);
		ee.setLeggings(leggings);
		ee.setBoots(boots);
		ee.setItemInHand(hand);
	}

	public void tryDropItems(Location l) {
		Random r = new Random();
		int i = (r.nextInt((100 - 0) + 1) + 0);

		if (i <= itemDropRate || itemDropRate == 100) {
			if(itemDrops.size() == 1){
				l.getWorld().dropItem(l, itemDrops.get(0));
				return;
			}
			
			l.getWorld().dropItem(l, itemDrops.get(r.nextInt(itemDrops.size() - 1)));
		}
	}

	public int getEntityXp() {
		Random r = new Random();
		return r.nextInt((maxXp - minXp) + 1) + minXp;
	}
	
	public boolean isOutsideSquare(){
		if(spawnLocation.distance(entity.getLocation()) > moveDistance) {
			return true;
		}
		
		return false;
	}
	
	public boolean isInsideHostileZone(Player player){
		if(entity.getLocation().distance(player.getLocation()) < hostileZone) {
			return true;
		}
		
		return false;
	}
	
	public boolean isOutsideTrackingRange(){
		if(entity.getLocation().distance(targetPlayer.getLocation()) > unlockDistance) {
			return true;
		}
		
		return false;
	}

	public double findDamageWithArmor(double d) {
		if ((d - armor) <= 0) {
			return 0;
		}

		return (d - armor);
	}

	/*****************************************
	 * 
	 * Listeners
	 * 
	 ****************************************/

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() != entity) {
			return;
		}

		setTargetPlayer(null);
		event.getDrops().clear();
		tryDropItems(event.getEntity().getLocation());
		EntityUtil.startEntityDeathTimer(this);
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() != entity) {
			return;
		}
		
		if(isNPC){
			event.setCancelled(true);
			return;
		}
		
		if(event instanceof EntityDamageByEntityEvent){
			return;
		}

		event.setCancelled(true);
		double d = findDamageWithArmor(event.getDamage());
		if ((entity.getHealth() - d) <= 0) {
			if (killer != null) {
				int exp = getEntityXp();
				showEntityXp(killer, exp);
			} else {
				showEntityXp("The Wind", getEntityXp());
			}
		} else {
			showEntityHealth(d);
			EntityUtil.showEntityTagTimer(this, d);
		}

		entity.damage(d);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() == entity) {
			event.setDamage(damage);
			return;
		}

		if (event.getEntity() == entity) {
			if(isNPC){
				return;
			}
			
			if (event.getDamager() instanceof Player) {
				killer = ((Player) event.getDamager()).getName();
				entity.setVelocity(event.getDamager().getLocation().getDirection().multiply(0.5D));
			} else {
				if (event.getDamager() instanceof Arrow) {
					if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
						killer = ((Player) ((Arrow) event.getDamager()).getShooter()).getName();
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event){
		if(!(event.getTarget() instanceof Player)){
			event.setCancelled(true);
			return;
		}
		
		if(!(event.getEntity() == entity)){
			return;
		}
		
		if(event.getTarget() == getTargetPlayer()){
			return;
		}
		
		if(isInsideHostileZone((Player) event.getTarget())){
			if(getTargetPlayer() == null){
				Monster monster = (Monster) event.getEntity();
				Player player = (Player) event.getTarget();
				
				monster.setTarget(player);
				setTargetPlayer(player);
			}
		}
		
		if(!(event.getReason() == TargetReason.CUSTOM)){
			event.setCancelled(true);
			return;
		}
	}
	
	/*****************************************
	 * 
	 * Getters & Setters
	 * 
	 ****************************************/

	public ItemStack getHelmet() {
		return helmet;
	}

	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
	}

	public ItemStack getChestplate() {
		return chestplate;
	}

	public void setChestplate(ItemStack chestplate) {
		this.chestplate = chestplate;
	}

	public ItemStack getLeggings() {
		return leggings;
	}

	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
	}

	public ItemStack getBoots() {
		return boots;
	}

	public void setBoots(ItemStack boots) {
		this.boots = boots;
	}

	public ItemStack getHand() {
		return hand;
	}

	public void setHand(ItemStack hand) {
		this.hand = hand;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public boolean isNPC() {
		return isNPC;
	}

	public void setNPC(boolean isNPC) {
		this.isNPC = isNPC;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinXp() {
		return minXp;
	}

	public void setMinXp(int minXp) {
		this.minXp = minXp;
	}

	public int getMaxXp() {
		return maxXp;
	}

	public void setMaxXp(int maxXp) {
		this.maxXp = maxXp;
	}

	public List<ItemStack> getItemDrops() {
		return itemDrops;
	}

	public void setItemDrops(List<ItemStack> itemDrops) {
		this.itemDrops = itemDrops;
	}

	public int getItemDropRate() {
		return itemDropRate;
	}

	public void setItemDropRate(int itemDropRate) {
		this.itemDropRate = itemDropRate;
	}

	public String getKiller() {
		return killer;
	}

	public void setKiller(String killer) {
		this.killer = killer;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getMoveDistance() {
		return moveDistance;
	}

	public void setMoveDistance(int moveDistance) {
		this.moveDistance = moveDistance;
	}

	public Player getTargetPlayer() {
		return targetPlayer;
	}

	public void setTargetPlayer(Player targetPlayer) {
		this.targetPlayer = targetPlayer;
	}

	public boolean isHostile() {
		return isHostile;
	}

	public void setHostile(boolean isHostile) {
		this.isHostile = isHostile;
	}

	public int getHostileZone() {
		return hostileZone;
	}

	public void setHostileZone(int hostileZone) {
		this.hostileZone = hostileZone;
	}

	public int getUnlockDistance() {
		return unlockDistance;
	}

	public void setUnlockDistance(int unlockDistance) {
		this.unlockDistance = unlockDistance;
	}
}
