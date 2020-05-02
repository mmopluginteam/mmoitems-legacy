package net.Indyuce.mmoitems.ability.arcane;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Arcane_Rift extends Ability {
	public Arcane_Rift() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 5);
		addModifier("duration", 2);
		addModifier("amplifier", 2);
		addModifier("cooldown", 10);
		addModifier("speed", 1);
		addModifier("duration", 1.5);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		if (!stats.getPlayer().isOnGround())
			return new AttackResult(false);

		double damage1 = data.getModifier("damage");
		double slowDuration = data.getModifier("duration");
		double slowAmplifier = data.getModifier("amplifier");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_DEATH.getSound(), 2, .5f);
		new BukkitRunnable() {
			Vector vec = getTargetDirection(stats.getPlayer(), target).setY(0).normalize().multiply(.5 * data.getModifier("speed"));
			Location loc = stats.getPlayer().getLocation();
			int ti = 0, duration = (int) (20 * Math.min(data.getModifier("duration"), 10.));
			List<Integer> hit = new ArrayList<>();

			public void run() {
				if (ti++ > duration)
					cancel();

				loc.add(vec);
				ParticleEffect.SPELL_WITCH.display(.5f, 0, .5f, 0, 5, loc);

				for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
					if (MMOUtils.canDamage(stats.getPlayer(), entity) && loc.distanceSquared(entity.getLocation()) < 2 && !hit.contains(entity.getEntityId())) {
						hit.add(entity.getEntityId());
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);
						((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (slowDuration * 20), (int) slowAmplifier));
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
