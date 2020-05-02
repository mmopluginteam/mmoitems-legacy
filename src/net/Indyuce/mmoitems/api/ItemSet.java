package net.Indyuce.mmoitems.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability.AbilityData;

public class ItemSet {
	private Map<Integer, SetBonuses> bonuses = new HashMap<>();
	private List<String> loreTag;
	private String name, id;

	public ItemSet(ConfigurationSection section) {
		this.id = section.getName().toUpperCase().replace("-", "_");
		this.loreTag = section.getStringList("lore-tag");
		this.name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));

		if (section.contains("bonuses"))
			for (int j = 2; j < 7; j++) {
				if (!section.getConfigurationSection("bonuses").contains("" + j))
					continue;

				SetBonuses bonuses = new SetBonuses(this);

				for (String key : section.getConfigurationSection("bonuses." + j).getKeys(false)) {
					String format = key.toUpperCase().replace("-", "_").replace(" ", "_");

					// stat
					Stat stat = Stat.safeValueOf(format);
					if (stat != null) {
						bonuses.addStat(stat, section.getDouble("bonuses." + j + "." + key));
						continue;
					}

					// potion effect
					PotionEffectType potionEffectType = PotionEffectType.getByName(format);
					if (potionEffectType != null) {
						bonuses.addPotionEffect(new PotionEffect(potionEffectType, MMOUtils.getEffectDuration(potionEffectType), section.getInt("bonuses." + j + "." + key) - 1));
						continue;
					}

					// ability
					if (key.startsWith("ability-")) {
						AbilityData ability = new AbilityData().load(section.getConfigurationSection("bonuses." + j + "." + key));
						if (!ability.isValid()) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not load the set bonus ability, path: " + id + ".bonuses." + j + "." + key);
							continue;
						}

						bonuses.addAbility(ability);
					}
				}

				this.bonuses.put(j, bonuses);
			}
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public SetBonuses getBonuses(int items) {
		SetBonuses bonuses = new SetBonuses(this);
		for (int j = 2; j <= Math.min(items, 6); j++)
			if (this.bonuses.containsKey(j))
				bonuses.add(this.bonuses.get(j));
		return bonuses;
	}

	public List<String> getLoreTag() {
		return loreTag;
	}

	public class SetBonuses {
		private Map<Stat, Double> stats = new HashMap<>();
		private Map<PotionEffectType, PotionEffect> permEffects = new HashMap<>();
		private Set<AbilityData> abilities = new HashSet<>();
		private ItemSet itemSet;

		public SetBonuses(ItemSet itemSet) {
			this.itemSet = itemSet;
		}

		public SetBonuses(SetBonuses bonuses) {
			this.stats = new HashMap<>(bonuses.stats);
			this.permEffects = new HashMap<>(bonuses.permEffects);
			this.abilities = new HashSet<>(bonuses.abilities);
			this.itemSet = bonuses.itemSet;
		}

		public void addStat(Stat stat, double value) {
			stats.put(stat, value);
		}

		public void addPotionEffect(PotionEffect effect) {
			permEffects.put(effect.getType(), effect);
		}

		public void addAbility(AbilityData ability) {
			abilities.add(ability);
		}

		public ItemSet getItemSet() {
			return itemSet;
		}

		public double getStat(Stat stat) {
			return stats.containsKey(stat) ? stats.get(stat) : 0;
		}

		public Set<Entry<Stat, Double>> getStats() {
			return stats.entrySet();
		}

		public Collection<PotionEffect> getPotionEffects() {
			return permEffects.values();
		}

		public Set<AbilityData> getAbilities() {
			return abilities;
		}

		public void add(SetBonuses bonuses) {
			for (Entry<Stat, Double> stat : bonuses.getStats())
				stats.put(stat.getKey(), (stats.containsKey(stat.getKey()) ? stats.get(stat.getKey()) : 0) + stat.getValue());

			for (PotionEffect effect : bonuses.getPotionEffects())
				if (!permEffects.containsKey(effect.getType()) || permEffects.get(effect.getType()).getAmplifier() < effect.getAmplifier())
					permEffects.put(effect.getType(), effect);

			for (AbilityData ability : bonuses.getAbilities())
				abilities.add(ability);
		}
	}
}
