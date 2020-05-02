package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Hoearthquake extends Ability {
	public Hoearthquake() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		if (!stats.getPlayer().isOnGround())
			return new AttackResult(false);

		new BukkitRunnable() {
			Vector vec = stats.getPlayer().getEyeLocation().getDirection().setY(0);
			Location loc = stats.getPlayer().getLocation();
			int ti = 0;

			public void run() {
				if (ti++ > 20)
					cancel();

				loc.add(vec);
				loc.getWorld().playSound(loc, VersionSound.BLOCK_GRAVEL_BREAK.getSound(), 2, 1);
				ParticleEffect.CLOUD.display(.5f, 0, .5f, 0, 1, loc);

				for (int x = -1; x < 2; x++)
					for (int z = -1; z < 2; z++) {
						Block block = loc.clone().add(x, -1, z).getBlock();
						if (block.getType() == Material.GRASS || block.getType() == Material.DIRT)
							block.setType(Material.SOIL);
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
