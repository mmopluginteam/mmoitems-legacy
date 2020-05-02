package net.Indyuce.mmoitems.comp.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.Indyuce.mmoitems.MMOItems;

// WorldGuard for MC Legacy
public class WorldGuardFlags implements FlagPlugin {
	private WorldGuardPlugin worldguard;
	private Map<CustomFlag, StateFlag> flags = new HashMap<>();

	public WorldGuardFlags() {
		worldguard = (WorldGuardPlugin) MMOItems.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

		FlagRegistry registry = worldguard.getFlagRegistry();
		for (CustomFlag customFlag : CustomFlag.values()) {
			StateFlag flag = new StateFlag(customFlag.getPath(), true);
			try {
				registry.register(flag);
				flags.put(customFlag, flag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isPvpAllowed(Location loc) {
		ApplicableRegionSet set = worldguard.getRegionContainer().createQuery().getApplicableRegions(loc);
		return set.queryState(null, DefaultFlag.PVP) != StateFlag.State.DENY;
	}

	@Override
	public boolean isFlagAllowed(Player player, CustomFlag flag) {
		ApplicableRegionSet regions = worldguard.getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		return regions.queryValue(worldguard.wrapPlayer(player), flags.get(flag)) != StateFlag.State.DENY;
	}
}
