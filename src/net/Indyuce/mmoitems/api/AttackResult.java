package net.Indyuce.mmoitems.api;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.UntargetedWeapon;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class AttackResult {
	private double damage, initialDamage;
	private boolean successful, untargetedWeapon;

	private static final Random random = new Random();

	public AttackResult(boolean successful) {
		this(successful, 0);
	}

	/*
	 * this constructor allows the
	 */
	public AttackResult(Weapon weapon, double damage) {
		this(true, damage);

		this.untargetedWeapon = weapon != null && weapon instanceof UntargetedWeapon;
	}

	public AttackResult(boolean successful, double damage) {
		this.successful = successful;
		this.damage = damage;
		this.initialDamage = damage;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public AttackResult setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}

	public double getDamage() {
		return damage;
	}

	// not used in MMOItems
	public boolean isDamageModified() {
		return initialDamage != damage;
	}

	public void addDamage(double value) {
		damage += value;
	}

	public void addRelativeDamage(double coef) {
		multiplyDamage(1 + coef);
	}

	public void multiplyDamage(double coef) {
		damage *= coef;
	}

	public void applyEffectsAndDamage(PlayerStats stats, ItemStack item, LivingEntity target) {
		applyEffects(stats, item, target);
		MMOItems.plugin.getDamage().damage(stats, target, damage, DamageType.WEAPON);
	}

	public AttackResult applyEffects(PlayerStats stats, ItemStack item, LivingEntity target) {

		// elemental damage
		new ElementalAttack(item, this).applyElementalArmor(target).apply(stats);

		// abilities only if the weapon attacking is an untargeted weapon
		if (untargetedWeapon)
			stats.getPlayerData().castAbilities(target, this, CastingMode.ON_HIT);

		// extra damage
		addRelativeDamage(stats.getStat(target instanceof Player ? Stat.PVP_DAMAGE : Stat.PVE_DAMAGE) / 100);
		addRelativeDamage(stats.getStat(Stat.WEAPON_DAMAGE) / 100);
		if (MMOItems.plugin.getDamage().isUndead(target))
			addRelativeDamage(stats.getStat(Stat.UNDEAD_DAMAGE) / 100);

		// critical strikes
		if (random.nextDouble() <= stats.getStat(Stat.CRITICAL_STRIKE_CHANCE) / 100) {
			multiplyDamage(MMOItems.plugin.getConfig().getDouble("crit-coefficient") + stats.getStat(Stat.CRITICAL_STRIKE_POWER) / 100);
			target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_PLAYER_ATTACK_CRIT.getSound(), 1, 1);
			ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .1f, 16, target.getLocation().add(0, 1, 0));
		}

		return this;
	}
}
