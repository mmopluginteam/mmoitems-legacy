package net.Indyuce.mmoitems.api.item.weapon;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Gauntlet extends Weapon {
	public Gauntlet(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	public void specialAttack(LivingEntity target) {
		if (!MMOItems.plugin.getConfig().getBoolean("item-ability.gauntlet.enabled"))
			return;
		if (!hasEnoughResources(MMOItems.plugin.getConfig().getDouble("item-ability.gauntlet.cooldown"), CooldownType.SPECIAL_ATTACK, false))
			return;

		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, target.getLocation().add(0, 1, 0));
		target.getWorld().playSound(target.getLocation(), VersionSound.BLOCK_ANVIL_LAND.getSound(), 1, 0);
		target.removePotionEffect(PotionEffectType.BLINDNESS);
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
		target.setVelocity(player.getEyeLocation().getDirection().setY(0).normalize().setY(.8));
		target.setVelocity(player.getEyeLocation().getDirection().setY(0).normalize().multiply(2).setY(.3));
	}
}
