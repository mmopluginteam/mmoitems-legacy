package net.Indyuce.mmoitems.ability;

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

public class Ice_Crystal extends Ability {
	public Ice_Crystal() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("duration", 3);
		addModifier("amplifier", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 1, 1);
		new BukkitRunnable() {
			Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(.7);
			Location loc = stats.getPlayer().getEyeLocation();
			int ti = 0;

			public void run() {
				ti++;
				if (ti > 25)
					cancel();

				loc.getWorld().playSound(loc, VersionSound.BLOCK_GLASS_BREAK.getSound(), 2, 1);
				List<Entity> entities = MMOUtils.getNearbyChunkEntities(loc);
				for (int j = 0; j < 3; j++) {
					loc.add(vec);
					if (loc.getBlock().getType().isSolid())
						cancel();

					for (int i = 3; i < 6; i++)
						ParticleEffect.SNOW_SHOVEL.display(new Vector(0, .7, 0), .07f * i, loc);
					ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .04f, 1, loc);

					for (Entity target : entities)
						if (MMOUtils.canDamage(stats.getPlayer(), loc, target)) {
							ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
							ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, .13f, 48, loc);
							ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .2f, 24, loc);
							loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 1);
							MMOItems.plugin.getDamage().damage(stats, (LivingEntity) target, data.getModifier("damage"), DamageType.MAGIC);
							((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) data.getModifier("duration") * 20, (int) data.getModifier("amplifier")));
							cancel();
							return;
						}
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
