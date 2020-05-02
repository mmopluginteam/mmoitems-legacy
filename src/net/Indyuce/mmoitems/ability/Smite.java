package net.Indyuce.mmoitems.ability;

import org.bukkit.entity.LivingEntity;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Smite extends Ability {
	public Smite() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("damage", 8);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		MMOItems.plugin.getDamage().damage(stats, target, data.getModifier("damage"), DamageType.MAGIC);
		target.getWorld().strikeLightningEffect(target.getLocation());
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_FIREWORK_LARGE_BLAST.getSound(), 3, 1);
		return new AttackResult(true);
	}
}
