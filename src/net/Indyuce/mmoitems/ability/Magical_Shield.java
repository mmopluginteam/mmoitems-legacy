package net.Indyuce.mmoitems.ability;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Magical_Shield extends Ability {
	public static Map<Location, Double[]> magicalShield = new HashMap<Location, Double[]>();

	public Magical_Shield() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("power", 40);
		addModifier("radius", 5);
		addModifier("duration", 5);
		addModifier("cooldown", 35);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		if (!stats.getPlayer().isOnGround())
			return new AttackResult(false);

		double duration = data.getModifier("duration");
		double radius = Math.pow(data.getModifier("radius"), 2);
		double power = data.getModifier("power") / 100;

		final Location loc = stats.getPlayer().getLocation().clone();
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 3, 0);
		magicalShield.put(loc, new Double[] { radius, power });
		new BukkitRunnable() {
			int ti = 0;

			public void run() {
				ti++;
				for (double j = 0; j < Math.PI / 2; j += Math.PI / (28 + random.nextInt(5)))
					for (double i = 0; i < Math.PI * 2; i += Math.PI / (14 + random.nextInt(5))) {
						Location loc1 = stats.getPlayer().getLocation().clone().add(2.5 * Math.cos(i + j) * Math.sin(j), 2.5 * Math.cos(j), 2.5 * Math.sin(i + j) * Math.sin(j));
						ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.PURPLE), loc1);
					}
				if (ti > duration * 20 / 3) {
					magicalShield.remove(loc);
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 3);
		return new AttackResult(true);
	}
}
