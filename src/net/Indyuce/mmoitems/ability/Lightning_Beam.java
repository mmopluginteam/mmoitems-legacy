package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Lightning_Beam extends Ability {
	public Lightning_Beam() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 8);
		addModifier("radius", 5);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);
		loc = getFirstNonSolidBlock(loc);

		double damage1 = data.getModifier("damage");
		double radius = data.getModifier("radius");
		
		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc) <= radius * radius)
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 1, 0);
		ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .2f, 64, loc);
		ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .2f, 32, loc);
		Vector vec = new Vector(0, .3, 0);
		for (double j = 0; j < 40; j += .3)
			ParticleEffect.FIREWORKS_SPARK.display(.1f, .1f, .1f, .01f, 6, loc.add(vec));
		return new AttackResult(true);
	}

	private Location getFirstNonSolidBlock(Location loc) {
		Location initial = loc.clone();
		for (int j = 0; j < 5; j++)
			if (!loc.add(0, 1, 0).getBlock().getType().isSolid())
				return loc;
		return initial;
	}
}
