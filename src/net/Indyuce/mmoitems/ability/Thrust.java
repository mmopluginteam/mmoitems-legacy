package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Thrust extends Ability {
	public Thrust() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("damage", 6);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 1, 0);
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 3));

		Location loc = stats.getPlayer().getEyeLocation().clone();
		Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(.5);
		for (int j = 0; j < 14; j++) {
			loc.add(vec);
			for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
				if (MMOUtils.canDamage(stats.getPlayer(), loc, entity))
					MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.PHYSICAL);
			ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, loc);
		}
		return new AttackResult(true);
	}
}
