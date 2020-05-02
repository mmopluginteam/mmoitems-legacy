package net.Indyuce.mmoitems.api;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public enum TypeSet {

	/*
	 * slashing weapons deal damage in a cone behind the player's initial
	 * target, which makes it a deadly AoE weapon for warriors
	 */
	SLASHING((stats, target, weapon, result) -> {
		if (!MMOItems.plugin.getConfig().getBoolean("item-ability.slashing.enabled") || stats.getPlayerData().isOnCooldown(CooldownType.SET_TYPE_ATTACK))
			return null;

		stats.getPlayerData().applyCooldown(CooldownType.SET_TYPE_ATTACK, MMOItems.plugin.getConfig().getDouble("item-ability.slashing.cooldown"));
		Location loc = stats.getPlayer().getLocation().clone().add(0, 1.3, 0);

		final double a1 = (loc.getYaw() + 90) / 180 * Math.PI, p = -loc.getPitch() / 180 * Math.PI;
		for (double r = 1; r < 5; r += .3)
			for (double a = -Math.PI / 6; a < Math.PI / 6; a += Math.PI / 8 / r)
				ParticleEffect.CRIT.display(0, 0, 0, 0, 1, loc.clone().add(Math.cos(a + a1) * r, Math.sin(p) * r, Math.sin(a + a1) * r));

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(loc) < 40 && stats.getPlayer().getEyeLocation().getDirection().angle(entity.getLocation().subtract(stats.getPlayer().getLocation()).toVector()) < Math.PI / 3 && MMOUtils.canDamage(stats.getPlayer(), entity) && !entity.equals(target))
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, .4 * result.getDamage(), DamageType.WEAPON);
		return null;
	}),

	/*
	 * piercing weapons deal damage in a line behind the initial target, which
	 * is harder to land than a slashing weapon but the AoE damage ratio is
	 * increased which makes it a perfect 'double or nothing' weapon for
	 * assassins
	 */
	PIERCING((stats, target, weapon, result) -> {
		if (!MMOItems.plugin.getConfig().getBoolean("item-ability.piercing.enabled") || stats.getPlayerData().isOnCooldown(CooldownType.SET_TYPE_ATTACK))
			return null;

		stats.getPlayerData().applyCooldown(CooldownType.SET_TYPE_ATTACK, MMOItems.plugin.getConfig().getDouble("item-ability.piercing.cooldown"));
		Location loc = stats.getPlayer().getLocation().clone().add(0, 1.3, 0);

		final double a1 = (loc.getYaw() + 90) / 180 * Math.PI, p = -loc.getPitch() / 180 * Math.PI;
		for (double r = 1; r < 5; r += .3)
			for (double a = -Math.PI / 12; a < Math.PI / 12; a += Math.PI / 16 / r)
				ParticleEffect.CRIT.display(0, 0, 0, 0, 1, loc.clone().add(Math.cos(a + a1) * r, Math.sin(p) * r, Math.sin(a + a1) * r));

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(stats.getPlayer().getLocation()) < 40 && stats.getPlayer().getEyeLocation().getDirection().angle(entity.getLocation().toVector().subtract(stats.getPlayer().getLocation().toVector())) < Math.PI / 18)
				if (MMOUtils.canDamage(stats.getPlayer(), entity) && !entity.equals(target))
					MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, .4 * result.getDamage(), DamageType.WEAPON);
		return null;
	}),

	/*
	 * blunt weapons are like 1.9 sweep attacks. they damage all enemies nearby
	 * and apply a slight knockback
	 */
	BLUNT((stats, target, weapon, result) -> {
		final Random random = new Random();

		if (MMOItems.plugin.getConfig().getBoolean("item-ability.blunt.aoe.enabled") && !stats.getPlayerData().isOnCooldown(CooldownType.SPECIAL_ATTACK)) {
			stats.getPlayerData().applyCooldown(CooldownType.SPECIAL_ATTACK, MMOItems.plugin.getConfig().getDouble("item-ability.blunt.aoe.cooldown"));
			target.getWorld().playSound(target.getLocation(), VersionSound.BLOCK_ANVIL_LAND.getSound(), 1, 2);
			ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, target.getLocation().add(0, 1, 0));
			double bluntPower = stats.getStat(Stat.BLUNT_POWER);
			if (bluntPower > 0) {
				double bluntRating = weapon.getValue(stats.getStat(Stat.BLUNT_RATING), MMOItems.plugin.getConfig().getDouble("default.blunt-rating")) / 100;
				for (Entity entity : target.getNearbyEntities(bluntPower, bluntPower, bluntPower))
					if (MMOUtils.canDamage(stats.getPlayer(), entity) && !entity.equals(target))
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, result.getDamage() * bluntRating, DamageType.WEAPON);
			}
		}

		if (MMOItems.plugin.getConfig().getBoolean("item-ability.blunt.stun.enabled") && !stats.getPlayerData().isOnCooldown(CooldownType.SPECIAL_ATTACK) && random.nextDouble() < MMOItems.plugin.getConfig().getDouble("item-ability.blunt.stun.chance") / 100) {
			stats.getPlayerData().applyCooldown(CooldownType.SPECIAL_ATTACK, MMOItems.plugin.getConfig().getDouble("item-ability.blunt.stun.cooldown"));
			target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD.getSound(), 1, 2);
			target.removePotionEffect(PotionEffectType.SLOW);
			target.removePotionEffect(PotionEffectType.BLINDNESS);
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (30 * MMOItems.plugin.getConfig().getDouble("item-ability.blunt.stun.power")), 1));
			Location loc = target.getLocation();
			loc.setYaw((float) (loc.getYaw() + 2 * (random.nextDouble() - .5) * 90));
			loc.setPitch((float) (loc.getPitch() + 2 * (random.nextDouble() - .5) * 30));
		}
		return null;
	}),

	/*
	 * any item type can may apply their stats even when worn in offhand.
	 * they're the only items with that specific property
	 */
	OFFHAND,

	/*
	 * ranged attacks based weapons. when the player is too squishy to fight in
	 * the middle of the battle-field, these weapons allow him to take some
	 * distance and still deal some good damage
	 */
	RANGE,

	/*
	 * any other item type, like armor, consumables, etc. they all have their
	 * very specific passive depending on their item type
	 */
	EXTRA;

	private SetAttackHandler<PlayerStats, LivingEntity, Weapon, AttackResult, Void> attackHandler;

	private TypeSet() {

		this((playerStats, target, weapon, result) -> {
			return null;
		});
	}

	private TypeSet(SetAttackHandler<PlayerStats, LivingEntity, Weapon, AttackResult, Void> attackHandler) {
		this.attackHandler = attackHandler;
	}

	public void applyAttackEffect(PlayerStats playerStats, LivingEntity target, Weapon weapon, AttackResult result) {
		attackHandler.apply(playerStats, target, weapon, result);
	}

	public String getName() {
		return MMOUtils.caseOnWords(name().toLowerCase());
	}

	@FunctionalInterface
	interface SetAttackHandler<A, B, C, D, R> {
		R apply(A a, B b, C c, D d);
	}
}
