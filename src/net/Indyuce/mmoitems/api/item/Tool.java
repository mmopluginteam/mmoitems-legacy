package net.Indyuce.mmoitems.api.item;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.version.VersionSound;

public class Tool extends UseItem {
	public Tool(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public boolean canBeUsed() {
		return MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_TOOLS) && profile.canUse(item, true);
	}

	public boolean miningEffects(Block block) {
		if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_AUTOSMELT"))
			if (block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE) {
				ItemStack item = new ItemStack(Material.valueOf(block.getType().name().replace("_ORE", "") + "_INGOT"));

				Location loc = block.getLocation().add(.5, 0, .5);
				block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(loc, item);
				ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, loc.add(0, .5, 0));
				return true;
			}

		if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_BOUNCING_CRACK"))
			new BukkitRunnable() {
				Vector v = player.getEyeLocation().getDirection().multiply(.5);
				Location loc = block.getLocation().clone().add(.5, .5, .5);
				int j = 0;

				@SuppressWarnings("deprecation")
				public void run() {
					if (j++ > 10)
						cancel();

					loc.add(v);
					Block block = loc.getBlock();
					if (block.getType() == Material.AIR || MMOItems.plugin.isBlacklisted(block.getType()))
						return;

					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
					block.breakNaturally(item);
					loc.getWorld().playSound(loc, VersionSound.BLOCK_GRAVEL_BREAK.getSound(), 1, 1);
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
		return false;
	}
}
