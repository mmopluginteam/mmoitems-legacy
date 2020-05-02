package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class DoubleRingsParticles extends ParticleRunnable {
	private float speed, height, radius, r_speed, y_offset;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		radius = (float) data.getModifier("radius");
		r_speed = (float) data.getModifier("rotation-speed");
		y_offset = (float) data.getModifier("y-offset");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		for (double k = 0; k < 2; k++)
			data.display(loc.clone().add(radius * Math.cos(j + k * Math.PI), height + Math.sin(j) * y_offset, radius * Math.sin(j + k * Math.PI)), speed);

		j += Math.PI / 16 * r_speed;
		j -= j > Math.PI * 2 ? Math.PI * 2 : 0;
	}
}
