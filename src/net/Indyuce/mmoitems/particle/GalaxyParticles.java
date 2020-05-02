package net.Indyuce.mmoitems.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class GalaxyParticles extends ParticleRunnable {
	private float speed, height, r_speed, y_coord;
	private int amount;

	private double j = 0;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		r_speed = (float) data.getModifier("rotation-speed");
		y_coord = (float) data.getModifier("y-coord");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		Location loc = data.getPlayerData().getPlayer().getLocation();
		for (int k = 0; k < amount; k++) {
			double a = j + Math.PI * 2 * k / amount;
			data.display(loc.clone().add(0, height, 0), new Vector(Math.cos(a), y_coord, Math.sin(a)), speed * .2f);
		}

		j += Math.PI / 24 * r_speed;
		j -= j > Math.PI * 2 ? Math.PI * 2 : 0;
	}
}
