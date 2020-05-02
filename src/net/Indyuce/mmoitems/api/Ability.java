package net.Indyuce.mmoitems.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.event.AbilityUseEvent;

public class Ability {
	private String name, id;
	private Map<String, Double> mods = new HashMap<>();
	private List<CastingMode> allowedModes;
	private boolean enabled = true;

	protected static final Random random = new Random();

	public Ability(CastingMode... allowedModes) {
		id = getClass().getSimpleName().toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z_]", "");
		name = getClass().getSimpleName().replace("_", " ");
		this.allowedModes = Arrays.asList(allowedModes);
	}

	public Ability(String abilityId, String abilityName, CastingMode... allowedModes) {
		this.allowedModes = Arrays.asList(allowedModes);
		name = abilityName;
		id = abilityId.toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z_]", "");
	}

	public void addModifier(String modifierPath, double defaultValue) {
		mods.put(modifierPath, defaultValue);
	}

	public String getID() {
		return id;
	}

	public String getLowerCaseID() {
		return id.toLowerCase().replace("_", "-");
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAllowedMode(CastingMode castingMode) {
		return allowedModes.contains(castingMode);
	}

	public List<CastingMode> getSupportedCastingModes() {
		return allowedModes;
	}

	public double getDefaultValue(String path) {
		return mods.get(path);
	}

	public Set<String> getModifiers() {
		return mods.keySet();
	}

	/*
	 * shall only be used with right click abilites since the on-hit abilities
	 * also requires the initial damage value and a target to be successfully
	 * cast
	 */
	public AttackResult cast(PlayerData playerData, AbilityData data) {
		return cast(playerData, null, 0, data, true);
	}

	public AttackResult cast(PlayerData playerData, LivingEntity target, double damage, AbilityData data, boolean message) {
		AbilityUseEvent event = new AbilityUseEvent(playerData, this, target);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return new AttackResult(false);

		/*
		 * check if the player can cast the ability, if he can't just return a
		 * new instance of of AttackResult with false boolean
		 */
		if (!playerData.canCast(data, message))
			return new AttackResult(false);

		/*
		 * cast the actual ability and see if it was successfully cast
		 */
		PlayerStats playerStats = playerData.getStats(Stat.MAGIC_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVE_DAMAGE, Stat.PVP_DAMAGE);
		AttackResult result = whenCast(playerStats, target, data, damage);
		if (!result.isSuccessful())
			return result;

		/*
		 * the player can cast the ability, and it was successfully cast on its
		 * target, removes resources needed from the player
		 */
		if (data.hasModifier("mana"))
			MMOItems.getRPG().setMana(playerData.getPlayer(), MMOItems.getRPG().getMana(playerData.getPlayer()) - data.getModifier("mana"));
		if (data.hasModifier("stamina"))
			MMOItems.getRPG().setStamina(playerData.getPlayer(), MMOItems.getRPG().getStamina(playerData.getPlayer()) - data.getModifier("stamina"));

		double cooldown = data.getModifier("cooldown");
		if (cooldown > 0)
			playerData.applyAbilityCooldown(this, cooldown);

		return result;
	}

	/*
	 * when that boolean is set to false, the ability will not register when the
	 * plugin enables which prevents it from being registered in the ability
	 * manager from MMOItems
	 */
	public void disable() {
		enabled = false;
	}

	/*
	 * these methods need to be overriden by ability classes depending on their
	 * ability type
	 */
	public AttackResult whenCast(PlayerStats playerStats, LivingEntity target, AbilityData data, double damage) {
		return new AttackResult(false);
	}

	/*
	 * util methods for abilities
	 */
	protected Location getTargetLocation(Player player, LivingEntity entity) {
		return getTargetLocation(player, entity, 50);
	}

	protected Location getTargetLocation(Player player, LivingEntity entity, int length) {
		if (entity != null)
			return entity.getLocation();

		Location loc = player.getTargetBlock((Set<Material>) null, length).getLocation();
		return loc.getBlock().getType() == Material.AIR ? null : loc.add(.5, 1, .5);
	}

	protected Vector getTargetDirection(Player player, LivingEntity target) {
		return target == null ? player.getEyeLocation().getDirection() : target.getLocation().add(0, MMOUtils.getHeight(target) / 2, 0).subtract(player.getLocation().add(0, 1.3, 0)).toVector().normalize();
	}

	public enum CastingMode {

		// when the player hits another entity.
		ON_HIT(false),

		// when the player is hit by another entity
		WHEN_HIT(false),

		// when the player performs a simple click
		LEFT_CLICK(true),
		RIGHT_CLICK(true),

		// when the player performs a simple click while sneaking
		SHIFT_LEFT_CLICK(true),
		SHIFT_RIGHT_CLICK(true);

		private boolean message;

		private CastingMode(boolean message) {
			this.message = message;
		}

		public boolean displaysMessage() {
			return message;
		}

		public String getName() {
			return MMOUtils.caseOnWords(name().toLowerCase().replace("_", " "));
		}

		public String getLowerCaseID() {
			return name().toLowerCase().replace("_", "-");
		}

		public static CastingMode safeValueOf(String path) {
			try {
				return CastingMode.valueOf(path.toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z_]", ""));
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static class AbilityData {
		private Ability ability;
		private CastingMode castMode;
		private Map<String, Double> modifiers = new HashMap<>();

		/*
		 * the valid boolean is used to check if the ability is formatted the
		 * right way in the item nbt compound, or if it could be successfully
		 * loaded from the specified configuration section
		 */
		private boolean valid = true;

		public AbilityData() {
		}

		public AbilityData(Ability ability) {
			this.ability = ability;
		}

		public AbilityData load(ConfigurationSection config) {

			String abilityFormat = config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
			if (!MMOItems.plugin.getAbilities().hasAbility(abilityFormat)) {
				valid = false;
				return this;
			}

			ability = MMOItems.plugin.getAbilities().getAbility(abilityFormat);
			try {
				castMode = CastingMode.valueOf(config.getString("mode").toUpperCase().replace("-", "_").replace(" ", "_"));
				for (String key : config.getKeys(false))
					if (!key.equalsIgnoreCase("mode") && !key.equalsIgnoreCase("type"))
						modifiers.put(key, config.getDouble(key));
			} catch (Exception e) {
				valid = false;
				return this;
			}
			return this;
		}

		public AbilityData load(String formatted) {
			String[] split = formatted.split("\\|");
			if (split.length < 2) {
				valid = false;
				return this;
			}

			ability = MMOItems.plugin.getAbilities().getAbility(split[0]);
			castMode = CastingMode.valueOf(split[1]);

			for (int j = 2; j < split.length; j++) {
				String[] split1 = split[j].split("\\=");
				modifiers.put(split1[0], Double.parseDouble(split1[1]));
			}
			return this;
		}

		public boolean isValid() {
			return valid;
		}

		public Ability getAbility() {
			return ability;
		}

		public CastingMode getCastingMode() {
			return castMode;
		}

		public void setModifier(String path, double value) {
			modifiers.put(path, value);
		}

		public boolean hasModifier(String path) {
			return modifiers.containsKey(path);
		}

		public double getModifier(String path) {
			return modifiers.containsKey(path) ? modifiers.get(path) : ability.getDefaultValue(path);
		}
	}
}
