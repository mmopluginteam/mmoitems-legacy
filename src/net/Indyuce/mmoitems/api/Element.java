package net.Indyuce.mmoitems.api;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.ParticleEffect;

public enum Element {
	FIRE(Material.BLAZE_POWDER, "Fire", ChatColor.DARK_RED, new ElementParticle(ParticleEffect.FLAME, .05f, 8)),
	ICE(Material.SNOW_BALL, "Ice", ChatColor.AQUA, new ElementParticle(ParticleEffect.BLOCK_CRACK, .1f, 24, Material.ICE)),
	WIND(Material.FEATHER, "Wind", ChatColor.GRAY, new ElementParticle(ParticleEffect.EXPLOSION_NORMAL, .05f, 8)),
	EARTH(Material.SAPLING, "Earth", ChatColor.GREEN, new ElementParticle(ParticleEffect.BLOCK_CRACK, .05f, 24, Material.DIRT)),
	THUNDER(Material.SULPHUR, "Thunder", ChatColor.YELLOW, new ElementParticle(ParticleEffect.FIREWORKS_SPARK, .07f, 8)),
	WATER(Material.WATER_LILY, "Water", ChatColor.BLUE, new ElementParticle(ParticleEffect.BLOCK_CRACK, .07f, 24, Material.STATIONARY_WATER)),

	LIGHT(Material.GLOWSTONE_DUST, "Light", ChatColor.WHITE, new ElementParticle(ParticleEffect.FIREWORKS_SPARK, .05f, 8)),
	DARKNESS(Material.COAL, 1, "Darkness", ChatColor.DARK_GRAY, new ElementParticle(ParticleEffect.SMOKE_NORMAL, .05f, 24)),;

	private ItemStack item;
	private String name;
	private ChatColor color;
	private ElementParticle particle;

	private Element(Material material, String name, ChatColor color, ElementParticle particle) {
		this(material, 0, name, color, particle);
	}

	private Element(Material material, int durability, String name, ChatColor color, ElementParticle particle) {
		this.item = new ItemStack(material, 1, (short) durability);
		this.name = name;
		this.color = color;
		this.particle = particle;
	}

	public ItemStack getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public ChatColor getPrefix() {
		return color;
	}

	public ElementParticle getParticle() {
		return particle;
	}

	public enum StatType {
		DAMAGE,
		DEFENSE;
	}

	public static class ElementParticle {
		public enum Type {
			BLOCK,
			NORMAL;
		}

		private Type type;
		private ParticleEffect eff;
		private Material m;
		private float speed;
		private int n;

		public ElementParticle(ParticleEffect eff, float speed, int n) {
			this.type = ElementParticle.Type.NORMAL;
			this.eff = eff;
			this.speed = speed;
			this.n = n;
		}

		public ElementParticle(ParticleEffect eff, float speed, int n, Material m) {
			this.type = ElementParticle.Type.BLOCK;
			this.eff = eff;
			this.m = m;
			this.speed = speed;
			this.n = n;
		}

		public void displayParticle(Entity t) {
			if (type == ElementParticle.Type.BLOCK)
				eff.display(new ParticleEffect.BlockData(m), 0, 0, 0, speed, n, t.getLocation().add(0, 1, 0));
			else
				eff.display(0, 0, 0, speed, n, t.getLocation().add(0, 1, 0));
		}
	}
}