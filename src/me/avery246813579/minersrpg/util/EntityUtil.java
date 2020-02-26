package me.avery246813579.minersrpg.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R3.EntityInsentient;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.avery246813579.minersrpg.MinersRpg;
import me.avery246813579.minersrpg.entities.MinerEntity;
import me.avery246813579.minersrpg.miner.Miner;
import me.avery246813579.minersrpg.quest.QuestDialog;
import me.avery246813579.minersrpg.weapons.MinerWeapon;

public class EntityUtil {
	/** Classes **/
	static MinersRpg plugin;

	/** Variables **/

	/** List & Maps **/
	public static Map<MinerEntity, Integer> timerEntities = new HashMap<MinerEntity, Integer>();
	public static Map<MinerEntity, Integer> entityDeathTimer = new HashMap<MinerEntity, Integer>();
	public static List<MinerEntity> entities = new ArrayList<MinerEntity>();
	public static List<MinerWeapon> weapons = new ArrayList<MinerWeapon>();
	private static List<Miner> miners = new ArrayList<Miner>();
	private static List<QuestDialog> dialogs = new ArrayList<QuestDialog>();

	public EntityUtil(MinersRpg plugin) {
		EntityUtil.plugin = plugin;
	}

	public static void showEntityTagTimer(MinerEntity entity, final double damage) {
		final MinerEntity e = entity;
		final double i = e.getEntity().getHealth();
		new BukkitRunnable(){
			public void run(){
				e.showEntityName((i - damage));
			}
		}.runTaskLater(plugin, 60);
	}

	public static void startEntityDeathTimer(MinerEntity entity) {
		final MinerEntity e = entity;
		new BukkitRunnable(){
			public void run(){
				e.spawnEntity();
			}
		}.runTaskLater(plugin, e.getRespawnTime() * 20);
	}

	public static void addEntity(MinerEntity e) {
		Bukkit.getPluginManager().registerEvents(e, plugin);
		entities.add(e);
	}

	public static void addWeapon(MinerWeapon w) {
		Bukkit.getPluginManager().registerEvents(w, plugin);
		weapons.add(w);
	}

	public static void removeEntity(MinerEntity e) {
		entities.remove(e);
	}

	public static void neuterVillager(Entity entity) {
		try {
			Method getHandleMethod = entity.getClass().getDeclaredMethod("getHandle", new Class[0]);
			Object nmsVillager = getHandleMethod.invoke(entity, new Object[0]);

			Field goalSelectorField = findField(nmsVillager.getClass(), "goalSelector");
			Field targetSelectorField = findField(nmsVillager.getClass(), "targetSelector");
			goalSelectorField.setAccessible(true);
			targetSelectorField.setAccessible(true);

			Object goalSelector = goalSelectorField.get(nmsVillager);
			Object targetSelector = targetSelectorField.get(nmsVillager);

			Field bField = goalSelector.getClass().getDeclaredField("b");
			bField.setAccessible(true);

			bField.set(goalSelector, new ArrayList<Object>());
			bField.set(targetSelector, new ArrayList<Object>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field findField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			if (clazz != Object.class)
				return findField(clazz.getSuperclass(), name);
		}
		return null;
	}

	public static boolean livingEntityMoveTo(LivingEntity livingEntity, Location target, float speed) {
		return  ((EntityInsentient)((CraftLivingEntity) livingEntity).getHandle()).getNavigation().a(target.getX(), target.getY(), target.getZ(), speed);
	}

	public static MinerEntity findEntity(Entity e) {
		for (MinerEntity me : entities) {
			if (me.getEntity().equals(e)) {
				return me;
			}
		}

		return null;
	}

	public static MinerWeapon findWeapon(ItemStack is) {
		if(is == null){
			return null;
		}
		
		for (MinerWeapon mw : weapons) {
			if (mw.getItem().isSimilar(is)) {
				return mw;
			}
		}

		return null;
	}
	
	public static MinerWeapon findWeaponWithNumber(int i){
		for(MinerWeapon mw : weapons){
			if(mw.getWeaponID() == i){
				return mw;
			}
		}
		
		return null;
	}
	
	public static Miner findMiner(Player player){
		for(Miner miner : miners){
			if(miner.getPlayer() == player){
				return miner;
			}
		}
		
		return null;
	}

	public static List<Miner> getMiners() {
		return miners;
	}

	public static void setMiners(List<Miner> miners) {
		EntityUtil.miners = miners;
	}

	public static List<QuestDialog> getDialogs() {
		return dialogs;
	}

	public static void setDialogs(List<QuestDialog> dialogs) {
		EntityUtil.dialogs = dialogs;
	}
}
