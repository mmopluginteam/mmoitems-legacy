package net.Indyuce.mmoitems.ability;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Item_Bomb extends Ability implements Listener {
	public Item_Bomb() {
		super("ITEM_BOMB", "Item Bomb", CastingMode.RIGHT_CLICK);

		addModifier("damage", 7);
		addModifier("radius", 6);
		addModifier("slow-duration", 4);
		addModifier("slow-amplifier", 1);
		addModifier("cooldown", 15);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	@SuppressWarnings("deprecation")
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		ItemStack itemStack = stats.getPlayer().getItemInHand().clone();
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return new AttackResult(false);

		itemStack.setAmount(1);
		Item item = stats.getPlayer().getWorld().dropItem(stats.getPlayer().getLocation().add(0, 1.2, 0), itemStack);
		item.setVelocity(stats.getPlayer().getEyeLocation().getDirection().multiply(1.3));
		item.setPickupDelay(10000000);
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_SNOWBALL_THROW.getSound(), 2, 0);

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > 40) {
					double radius = data.getModifier("radius");
					double damage = data.getModifier("damage");
					double slowDuration = data.getModifier("slow-duration");
					double slowAmplifier = data.getModifier("slow-amplifier");

					for (Entity entity : item.getNearbyEntities(radius, radius, radius))
						if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
							LivingEntity living = (LivingEntity) entity;
							MMOItems.plugin.getDamage().damage(stats, living, damage, DamageType.MAGIC);
							living.removePotionEffect(PotionEffectType.SLOW);
							living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (slowDuration * 20), (int) slowAmplifier));
						}

					ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 0, 24, item.getLocation());
					ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .2f, 48, item.getLocation());
					item.getWorld().playSound(item.getLocation(), VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 3, 0);

					item.remove();
					cancel();
					return;
				}

				ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, item.getLocation().add(0, .2, 0));
				ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .1f, 1, item.getLocation().add(0, .2, 0));
				item.getWorld().playSound(item.getLocation(), VersionSound.BLOCK_NOTE_HAT.getSound(), 2, (float) (.5 + (j / 40. * 1.5)));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
