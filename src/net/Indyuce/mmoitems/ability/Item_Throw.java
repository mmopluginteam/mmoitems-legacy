package net.Indyuce.mmoitems.ability;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Item_Throw extends Ability implements Listener {
	public Item_Throw() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("force", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		ItemStack itemStack = stats.getPlayer().getInventory().getItemInMainHand().clone();
		if (itemStack == null || itemStack.getType() == Material.AIR)
			return new AttackResult(false);

		itemStack.setAmount(1);
		Item item = stats.getPlayer().getWorld().dropItem(stats.getPlayer().getLocation().add(0, 1.2, 0), itemStack);
		item.setPickupDelay(10000000);
		item.setVelocity(stats.getPlayer().getEyeLocation().getDirection().multiply(1.5 * data.getModifier("force")));
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_SNOWBALL_THROW.getSound(), 1, 0);
		new BukkitRunnable() {
			double ti = 0;

			public void run() {
				ti++;
				if (ti > 20 || item.isDead() || item == null) {
					item.remove();
					cancel();
				}

				ParticleEffect.CRIT.display(0, 0, 0, 0, 1, item.getLocation());
				for (Entity entity : item.getNearbyEntities(1, 1, 1))
					if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
						item.remove();
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, data.getModifier("damage"), DamageType.PHYSICAL);
						cancel();
						return;
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
