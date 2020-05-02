package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class FirefliesParticles extends ParticleRunnable {
	private float speed, height, radius, r_speed;
	private int amount;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		radius = (float) data.getModifier("radius");
		r_speed = (float) data.getModifier("rotation-speed");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		for (int k = 0; k < amount; k++) {
			double a = j + Math.PI * 2 * k / amount;
			data.display(loc.clone().add(Math.cos(a) * radius, height, Math.sin(a) * radius), speed);
		}

		j += Math.PI / 48 * r_speed;
		j -= j > Math.PI * 2 ? Math.PI * 2 : 0;
	}
}
