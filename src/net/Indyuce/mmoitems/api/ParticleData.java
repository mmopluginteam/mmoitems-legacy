package net.Indyuce.mmoitems.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.ParticleEffect.OrdinaryColor;
import net.Indyuce.mmoitems.ParticleEffect.ParticleProperty;
import net.Indyuce.mmoitems.particle.AuraParticles;
import net.Indyuce.mmoitems.particle.DoubleRingsParticles;
import net.Indyuce.mmoitems.particle.FirefliesParticles;
import net.Indyuce.mmoitems.particle.GalaxyParticles;
import net.Indyuce.mmoitems.particle.HelixParticles;
import net.Indyuce.mmoitems.particle.OffsetParticles;
import net.Indyuce.mmoitems.particle.VortexParticles;

public class ParticleData {
	private PlayerData playerData;

	private ParticleType type;
	private ParticleEffect particle;
	private OrdinaryColor color;
	private boolean isColored;
	private Map<String, Double> modifiers = new HashMap<>();

	private ParticleRunnable runnable;
	private Random random = new Random();

	private boolean valid = true;

	public ParticleData(PlayerData playerData, String formatted) {
		this.playerData = playerData;

		String[] split = formatted.split("\\|");
		if (split.length < 2) {
			valid = false;
			return;
		}

		particle = ParticleEffect.valueOf(split[0]);
		type = ParticleType.valueOf(split[1]);

		for (int j = 2; j < split.length; j++) {
			String[] split1 = split[j].split("\\=");
			String modifier = split1[0];
			if (modifier.equals("color")) {
				String[] split2 = split1[1].split("\\,");
				color = new OrdinaryColor(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]), Integer.parseInt(split2[2]));
				continue;
			}

			modifiers.put(split1[0], Double.parseDouble(split1[1]));
		}
		isColored = particle.hasProperty(ParticleProperty.COLORABLE) && color != null;
	}

	public double getModifier(String path) {
		return modifiers.containsKey(path) ? modifiers.get(path) : type.getModifier(path);
	}

	/*
	 * depending on if the particle is colorable or not, display with colors or
	 * not
	 */

	public void display(Location location, float speed) {
		display(location, 1, 0, 0, 0, speed);
	}

	public void display(Location location, int amount, float offsetX, float offsetY, float offsetZ, float speed) {
		if (isColored) {
			for (int j = 0; j < amount; j++)
				particle.display(color, location.clone().add(rdm() * offsetX, rdm() * offsetY, rdm() * offsetZ));
			return;
		}

		particle.display(offsetX, offsetY, offsetZ, speed, amount, location);
	}

	public void display(Location location, Vector direction, float speed) {
		particle.display(direction, speed, location);
	}

	public boolean isValid() {
		return valid;
	}

	private double rdm() {
		return 2 * (random.nextDouble() - .5);
	}

	public ParticleType getType() {
		return type;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public ParticleRunnable getRunnable() {
		return runnable;
	}

	public ParticleData start() {
		(runnable = type.getRunnable().setParticleData(this).applyModifiers()).runTaskTimer(MMOItems.plugin, 0, type.getTime());
		return this;
	}

	public enum ParticleType {
		OFFSET(OffsetParticles.class, false, 5, "Some particles randomly spawning around your body.", new StringValue("amount", 5), new StringValue("vertical-offset", .5), new StringValue("horizontal-offset", .3), new StringValue("speed", 0), new StringValue("height", 1)),
		// WEAPON_OFFSET(false, 5, "Particles randomly spawning around your
		// weapon.", "amount", "offset"),
		FIREFLIES(FirefliesParticles.class, true, 1, "Particles dashing around you at the same height.", new StringValue("amount", 3), new StringValue("speed", 0), new StringValue("rotation-speed", 1), new StringValue("radius", 1.3), new StringValue("height", 1)),
		VORTEX(VortexParticles.class, true, 1, "Particles flying around you in a cone shape.", new StringValue("radius", 1.5), new StringValue("height", 2.4), new StringValue("speed", 0), new StringValue("y-speed", 1), new StringValue("rotation-speed", 1), new StringValue("amount", 3)),
		GALAXY(GalaxyParticles.class, true, 1, "Particles flying around you in spiral arms.", new StringValue("height", 1), new StringValue("speed", 1), new StringValue("y-coord", 0), new StringValue("rotation-speed", 1), new StringValue("amount", 6)),
		DOUBLE_RINGS(DoubleRingsParticles.class, true, 1, "Particles drawing two rings around you.", new StringValue("radius", .8), new StringValue("y-offset", .4), new StringValue("height", 1), new StringValue("speed", 0), new StringValue("rotation-speed", 1)),
		HELIX(HelixParticles.class, true, 1, "Particles drawing a sphere around you.", new StringValue("radius", .8), new StringValue("height", .6), new StringValue("rotation-speed", 1), new StringValue("y-speed", 1), new StringValue("amount", 4), new StringValue("speed", 0)),
		AURA(AuraParticles.class, true, 1, "Particles dashing around you (height can differ).", new StringValue("amount", 3), new StringValue("speed", 0), new StringValue("rotation-speed", 1), new StringValue("y-speed", 1), new StringValue("y-offset", .7), new StringValue("radius", 1.3), new StringValue("height", 1));

		/*
		 * if override is set to true, only one particle effect can play at a
		 * time, and the particle which overrides has priority
		 */
		private boolean override;

		/*
		 * list of double modifiers that allow to configurate the particle
		 * effects, they'll be displayed in the effect editor once the particle
		 * type is chosen.
		 */
		private Map<String, Double> modifiers = new HashMap<>();

		private long time;
		private Class<? extends ParticleRunnable> runnable;
		private String lore;

		private ParticleType(Class<? extends ParticleRunnable> runnable, boolean override, long time, String lore, StringValue... modifiers) {
			this.runnable = runnable;
			this.override = override;
			this.time = time;
			this.lore = lore;

			for (StringValue modifier : modifiers)
				this.modifiers.put(modifier.getName(), modifier.getValue());
		}

		public String getDefaultName() {
			return MMOUtils.caseOnWords(name().toLowerCase().replace("_", " "));
		}

		public double getModifier(String path) {
			return modifiers.get(path);
		}

		public Set<String> getModifiers() {
			return modifiers.keySet();
		}

		public String getDescription() {
			return lore;
		}

		public boolean hasPriority() {
			return override;
		}

		public long getTime() {
			return time;
		}

		public ParticleRunnable getRunnable() {
			try {
				return runnable.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class ParticleRunnable extends BukkitRunnable {
		protected ParticleData data;

		public ParticleRunnable setParticleData(ParticleData data) {
			this.data = data;
			return this;
		}

		public ParticleRunnable applyModifiers() {
			return this;
		}

		@Override
		public void run() {
			cancel();
		}
	}
}
