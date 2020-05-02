package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.ParticleData.ParticleRunnable;

public class OffsetParticles extends ParticleRunnable {
	private float speed, h_offset, v_offset, height;
	private int amount;

	@Override
	public ParticleRunnable applyModifiers() {
		speed = (float) data.getModifier("speed");
		height = (float) data.getModifier("height");
		h_offset = (float) data.getModifier("horizontal-offset");
		v_offset = (float) data.getModifier("vertical-offset");
		amount = (int) data.getModifier("amount");
		return this;
	}

	@Override
	public void run() {
		data.display(data.getPlayerData().getPlayer().getLocation().add(0, height, 0), amount, h_offset, v_offset, h_offset, speed);
	}
}
