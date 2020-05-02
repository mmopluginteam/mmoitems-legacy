package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class AuraParticles extends ParticleRunnable {
	private float speed, height, radius, r_speed, y_offset, y_speed;
	private int amount;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		radius = (float) data.getModifier("radius");
		r_speed = (float) data.getModifier("rotation-speed");
		y_speed = (float) data.getModifier("y-speed");
		y_offset = (float) data.getModifier("y-offset");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		for (int k = 0; k < amount; k++) {
			double a = j + Math.PI * 2 * k / amount;
			data.display(loc.clone().add(Math.cos(a) * radius, Math.sin(j * y_speed * 3) * y_offset + height, Math.sin(a) * radius), speed);
		}

		j += Math.PI / 48 * r_speed;
		j -= j > Math.PI * 2 / y_speed ? Math.PI * 2 / y_speed : 0;
	}
}
