package net.Indyuce.mmoitems.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.Stat;

public class DamageManager implements Listener {
	private Map<Integer, Double> customDamage = new HashMap<>();

	public boolean isCustomDamaged(Entity entity) {
		return customDamage.containsKey(entity.getEntityId());
	}

	public void setCustomDamaged(Entity entity, double damage) {
		if (!customDamage.containsKey(entity.getEntityId()))
			customDamage.put(entity.getEntityId(), damage);
	}

	public double getCustomDamage(Entity entity) {
		return customDamage.get(entity.getEntityId());
	}

	public void removeCustomDamaged(Entity entity) {
		customDamage.remove(Integer.valueOf(entity.getEntityId()));
	}

	public void damage(PlayerStats playerStats, LivingEntity target, double value, DamageType damageType) {
		damage(playerStats, target, value, damageType, true);
	}

	public void damage(PlayerStats playerStats, LivingEntity target, double value, DamageType damageType, boolean knockback) {
		if (target.hasMetadata("NPC") || playerStats.getPlayer().hasMetadata("NPC"))
			return;

		setCustomDamaged(target, value);

		/*
		 * calculate extra damage depending on the type of attack and the entity
		 * that is being damaged
		 */
		if (damageType != DamageType.WEAPON) {
			value *= 1 + (damageType == DamageType.MAGIC ? playerStats.getStat(Stat.MAGIC_DAMAGE) / 100 : 0);
			value *= 1 + (isUndead(target) ? playerStats.getStat(Stat.UNDEAD_DAMAGE) / 100 : 0);
			value *= 1 + (playerStats.getStat(target instanceof Player ? Stat.PVP_DAMAGE : Stat.PVE_DAMAGE) / 100);
		}

		/*
		 * if the knockback is disabled just add a super high value to the base
		 * knockback resistance attribute, this way the entity does not feel the
		 * knockback. might not be perfect for compatibility with rpg plugins if
		 * they use this attribute but works just fine without any
		 */
		if (!knockback) {
			final double baseKnockbackValue = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getBaseValue();
			target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(63);
			try {
				MMOItems.plugin.getNMS().damageEntity(playerStats.getPlayer(), target, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(baseKnockbackValue);
			return;
		}
		MMOItems.plugin.getNMS().damageEntity(playerStats.getPlayer(), target, value);
	}

	public boolean isUndead(Entity entity) {
		return entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Wither;
	}

	public enum DamageType {

		// damage dealt by weapons & weapon passives
		WEAPON,

		// damage dealt by physical skills
		PHYSICAL,

		// damage dealt by magical abilities
		MAGIC;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void a(EntityDamageByEntityEvent event) {
		removeCustomDamaged(event.getEntity());
	}
}
