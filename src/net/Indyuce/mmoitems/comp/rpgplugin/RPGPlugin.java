package net.Indyuce.mmoitems.comp.rpgplugin;

import java.util.logging.Level;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PlayerData;

public interface RPGPlugin {
	public int getLevel(Player player);

	public String getClass(Player player);

	public double getMana(Player player);

	public void setMana(Player player, double value);

	public double getStamina(Player player);

	public void setStamina(Player player, double value);

	public boolean canBeDamaged(Entity entity);

	public void refreshStats(PlayerData data);
	
	public RPGProfile getProfile(PlayerData data);

	public enum PluginEnum {
		HEROES("Heroes", HeroesHook.class),
		SKILLAPI("SkillAPI", SkillAPIHook.class),
		RPGPLAYERLEVELING("RPGPlayerLeveling", RPGPlayerLevelingHook.class),
		BATTLELEVELS("BattleLevels", BattleLevelsHook.class),
		MCMMO("mcMMO", McMMOHook.class),
		SKILLS("Skills", SkillsHook.class);

		private Class<? extends RPGPlugin> pluginClass;
		private String name;

		private PluginEnum(String name, Class<? extends RPGPlugin> pluginClass) {
			this.pluginClass = pluginClass;
			this.name = name;
		}

		public RPGPlugin getInstance() {
			try {
				return pluginClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				MMOItems.plugin.getLogger().log(Level.WARNING, "Couldn't load compatibility for " + name);
				return null;
			}
		}

		public String getName() {
			return name;
		}
	}
}
