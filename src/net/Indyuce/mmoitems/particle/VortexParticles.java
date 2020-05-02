package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class VortexParticles extends ParticleRunnable {
	private float speed, height, radius, r_speed, y_speed;
	private int amount;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		radius = (float) data.getModifier("radius");
		y_speed = (float) data.getModifier("y-speed");
		r_speed = (float) data.getModifier("rotation-speed");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		double r = j / Math.PI / 2;
		for (int k = 0; k < amount; k++) {
			double a = j + Math.PI * 2 * k / amount;
			data.display(loc.clone().add(Math.cos(a) * radius * (1 - r * y_speed), r * y_speed * height, Math.sin(a) * radius * (1 - r * y_speed)), speed);
		}

		j += Math.PI / 24 * r_speed;
		j -= j > Math.PI * 2 / y_speed ? Math.PI * 2 / y_speed : 0;
	}
}
