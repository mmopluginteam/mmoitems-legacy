package net.Indyuce.mmoitems.api;

import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.ParticleEffect.OrdinaryColor;

public class ArrowParticles extends BukkitRunnable {
	private Arrow arrow;

	private ParticleEffect particle;
	private int amount;
	private float offset, speed;
	private OrdinaryColor color;

	private boolean valid = false, colored = false;
	
	private static final Random random = new Random();

	public ArrowParticles(Arrow arrow) {
		this.arrow = arrow;
	}

	public ArrowParticles load(ItemStack item) {
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ARROW_PARTICLES");
		String[] split = tag.split("\\|");

		if (split.length == 4) {
			particle = ParticleEffect.valueOf(split[0]);
			amount = Integer.parseInt(split[1]);
			offset = (float) Double.parseDouble(split[2]);
			speed = (float) Double.parseDouble(split[3]);

			valid = true;
		}

		if (split.length == 6) {
			particle = ParticleEffect.valueOf(split[0]);
			amount = Integer.parseInt(split[1]);
			offset = (float) Double.parseDouble(split[2]);
			color = new ParticleEffect.OrdinaryColor(Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]));

			colored = true;
			valid = true;
		}

		return this;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public void run() {
		if (arrow.isDead() || arrow.isOnGround()) {
			cancel();
			return;
		}

		if (colored) {
			for (int j = 0; j < amount; j++)
				particle.display(color, arrow.getLocation().add(rdm() * offset, .25 + rdm() * offset, rdm() * offset));
			return;
		}

		particle.display(offset, offset, offset, speed, amount, arrow.getLocation().add(0, .25, 0));
	}

	private double rdm() {
		return 2 * (random.nextDouble() - .5);
	}
}
