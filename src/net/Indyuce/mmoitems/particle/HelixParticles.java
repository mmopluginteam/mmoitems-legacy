package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class HelixParticles extends ParticleRunnable {
	private float speed, height, radius, r_speed, y_speed;
	private int amount;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		radius = (float) data.getModifier("radius");
		r_speed = (float) data.getModifier("rotation-speed");
		y_speed = (float) data.getModifier("y-speed");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		for (double k = 0; k < amount; k++) {
			double a = j + k * Math.PI * 2 / amount;
			data.display(loc.clone().add(Math.cos(a) * Math.cos(j * y_speed) * radius, 1 + Math.sin(j * y_speed) * height, Math.sin(a) * Math.cos(j * y_speed) * radius), speed);
		}

		j += Math.PI / 24 * r_speed;
		j -= j > Math.PI * 2 / y_speed ? Math.PI * 2 / y_speed : 0;
	}
}
