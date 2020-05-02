package net.Indyuce.mmoitems.ability;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Present_Throw extends Ability {
	private ItemStack present = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

	public Present_Throw() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("radius", 4);
		addModifier("force", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);

		ItemMeta presentMeta = present.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/47e55fcc809a2ac1861da2a67f7f31bd7237887d162eca1eda526a7512a64910").getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

		try {
			Field profileField = presentMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(presentMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			MMOItems.plugin.getLogger().log(Level.WARNING, "Could not load the skull texture for the Present Throw item ability.");
		}

		present.setItemMeta(presentMeta);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damaged) {
		double damage1 = data.getModifier("damage");
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);

		final Item item = stats.getPlayer().getWorld().dropItem(stats.getPlayer().getLocation().add(0, 1.2, 0), present);
		item.setPickupDelay(10000000);
		item.setVelocity(stats.getPlayer().getEyeLocation().getDirection().multiply(1.5 * data.getModifier("force")));

		/*
		 * when items are moving through the air, they loose a percent of their
		 * velocity proportionally to their coordinates in each axis. this means
		 * that if the trajectory is not affected, the ratio of x/y will always
		 * be the same. check for any change of that ratio to check for a
		 * trajectory change
		 */
		final double trajRatio = item.getVelocity().getX() / item.getVelocity().getZ();
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_SNOWBALL_THROW.getSound(), 1, 0);
		new BukkitRunnable() {
			double ti = 0;

			public void run() {
				ti++;
				if (ti > 70 || item.isDead() || item == null) {
					item.remove();
					cancel();
				}

				double currentTrajRatio = item.getVelocity().getX() / item.getVelocity().getZ();
				ParticleEffect.SPELL_INSTANT.display(0, 0, 0, 0, 1, item.getLocation().add(0, .1, 0));
				if (item.isOnGround() || Math.abs(trajRatio - currentTrajRatio) > .1) {
					ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .25f, 128, item.getLocation().add(0, .1, 0));
					item.getWorld().playSound(item.getLocation(), VersionSound.ENTITY_FIREWORK_TWINKLE.getSound(), 2, 1.5f);
					for (Entity entity : item.getWorld().getEntities())
						if (entity.getLocation().distanceSquared(item.getLocation()) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity))
							MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);
					item.remove();
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);

		return new AttackResult(true);
	}
}
