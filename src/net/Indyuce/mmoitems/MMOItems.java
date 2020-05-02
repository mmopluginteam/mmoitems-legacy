package net.Indyuce.mmoitems;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.SoulboundInfo;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpdaterData;
import net.Indyuce.mmoitems.command.AdvancedWorkbenchCommand;
import net.Indyuce.mmoitems.command.GemStonesCommand;
import net.Indyuce.mmoitems.command.MMOItemsCommand;
import net.Indyuce.mmoitems.command.SoulboundCommand;
import net.Indyuce.mmoitems.command.UpdateItemCommand;
import net.Indyuce.mmoitems.command.completion.MMOItemsCompletion;
import net.Indyuce.mmoitems.command.completion.UpdateItemCompletion;
import net.Indyuce.mmoitems.comp.MMOItemsMetrics;
import net.Indyuce.mmoitems.comp.MMOItemsRewardTypes;
import net.Indyuce.mmoitems.comp.RealDualWieldHook;
import net.Indyuce.mmoitems.comp.flags.DefaultFlags;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin;
import net.Indyuce.mmoitems.comp.flags.ResidenceFlags;
import net.Indyuce.mmoitems.comp.flags.WorldGuardFlags;
import net.Indyuce.mmoitems.comp.holograms.HologramSupport;
import net.Indyuce.mmoitems.comp.holograms.HologramsPlugin;
import net.Indyuce.mmoitems.comp.holograms.HolographicDisplaysPlugin;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory_v1_8;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory_v1_9;
import net.Indyuce.mmoitems.comp.inventory.RPGPlayerInventory;
import net.Indyuce.mmoitems.comp.mythicmobs.MMDrops;
import net.Indyuce.mmoitems.comp.placeholder.DefaultParser;
import net.Indyuce.mmoitems.comp.placeholder.PlaceholderAPIParser;
import net.Indyuce.mmoitems.comp.placeholder.PlaceholderParser;
import net.Indyuce.mmoitems.comp.rpgplugin.DefaultHook;
import net.Indyuce.mmoitems.comp.rpgplugin.RPGPlugin;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.listener.GuiListener;
import net.Indyuce.mmoitems.listener.AdvancedWorkbenchListener;
import net.Indyuce.mmoitems.listener.CustomDurability;
import net.Indyuce.mmoitems.listener.DisableInteractions;
import net.Indyuce.mmoitems.listener.ItemUpdater;
import net.Indyuce.mmoitems.listener.ItemUse;
import net.Indyuce.mmoitems.listener.PlayerListener;
import net.Indyuce.mmoitems.listener.version.Version_v1_12;
import net.Indyuce.mmoitems.listener.version.Version_v1_8;
import net.Indyuce.mmoitems.listener.version.Version_v1_9;
import net.Indyuce.mmoitems.manager.AbilityManager;
import net.Indyuce.mmoitems.manager.ConfigManager;
import net.Indyuce.mmoitems.manager.DamageManager;
import net.Indyuce.mmoitems.manager.DropTableManager;
import net.Indyuce.mmoitems.manager.EntityManager;
import net.Indyuce.mmoitems.manager.RecipeManager;
import net.Indyuce.mmoitems.manager.StatManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.version.ServerVersion;
import net.Indyuce.mmoitems.version.SpigotPlugin;
import net.Indyuce.mmoitems.version.nms.NMSHandler;

public class MMOItems extends JavaPlugin {

	public static MMOItems plugin;
	private ServerVersion version;
	private AbilityManager abilityManager;
	private RecipeManager recipeManager;
	private static ConfigManager configManager;
	private StatManager statManager;
	private EntityManager entityManager;
	private DamageManager damageManager;
	private DropTableManager dropTableManager;
	private TypeManager typeManager;
	private static RPGPlugin rpgPlugin;

	private PlaceholderParser placeholderParser = new DefaultParser();
	private HologramSupport hologramSupport;
	private FlagPlugin flagPlugin = new DefaultFlags();
	private PlayerInventory inventory;
	private NMSHandler nms;

	public void onLoad() {
		plugin = this;
		version = new ServerVersion(Bukkit.getServer().getClass());

		try {
			if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
				flagPlugin = new WorldGuardFlags();
				getLogger().log(Level.INFO, "Hooked onto WorldGuard");
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not initialize support with WorldGuard, make sure you are using the latest version available for your current spigot build.");
		}
	}

	public void onEnable() {
		new SpigotPlugin(60876, this).checkForUpdate();

		try {
			getLogger().log(Level.INFO, "Detected Bukkit Version: " + version.toString());
			nms = (NMSHandler) Class.forName("net.Indyuce.mmoitems.version.nms.NMSHandler_" + version.toString().substring(1)).newInstance();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Your server version is not compatible.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		new MMOItemsMetrics();

		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		int registeredAbilities = (abilityManager = new AbilityManager()).registerAbilities();
		if (registeredAbilities > 0)
			getLogger().log(Level.INFO, "Successfully registered " + registeredAbilities + " extra abilit" + (registeredAbilities > 1 ? "ies" : "y") + ".");

		saveDefaultConfig();
		configManager = new ConfigManager();
		statManager = new StatManager();
		typeManager = new TypeManager();

		Bukkit.getServer().getPluginManager().registerEvents(entityManager = new EntityManager(), this);
		Bukkit.getServer().getPluginManager().registerEvents(damageManager = new DamageManager(), this);
		Bukkit.getServer().getPluginManager().registerEvents(dropTableManager = new DropTableManager(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ItemUse(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CustomDurability(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DisableInteractions(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ItemUpdater(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);

		/*
		 * this class implements the Listener, if the option
		 * perm-effects-apply-on-move is enabled the loop will not apply perm
		 * effects and this class will be registered as a listener. starts with
		 * a 5s delay to let the other plugins time to load nicely
		 */
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					PlayerData.get(player).updateEffects();
			}
		}.runTaskTimer(this, 100, 20);

		/*
		 * this tasks updates twice a second player inventories on the server.
		 * allows now to use a glitchy itemEquipEvent. must be called after
		 * loading the config since it checks for a config option
		 */
		new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers())
					PlayerData.get(player).checkForInventoryUpdate();
			}
		}.runTaskTimer(this, 100, getConfig().getInt("inventory-update-delay"));

		// depending on config options
		if (!getConfig().getBoolean("disable-craftings.advanced"))
			Bukkit.getServer().getPluginManager().registerEvents(new AdvancedWorkbenchListener(), this);

		// depending on server version
		if (version.isStrictlyHigher(1, 11))
			Bukkit.getServer().getPluginManager().registerEvents(new Version_v1_12(), this);
		Bukkit.getServer().getPluginManager().registerEvents(version.isStrictlyHigher(1, 8) ? new Version_v1_9() : new Version_v1_8(), this);

		if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
			flagPlugin = new ResidenceFlags();
			getLogger().log(Level.INFO, "Hooked onto Residence");
		}

		if (Bukkit.getPluginManager().getPlugin("RPGInventory") != null) {
			inventory = new RPGPlayerInventory(this);
			getLogger().log(Level.INFO, "Hooked onto RPGInventory");
		} else
			inventory = version.isBelowOrEqual(1, 8) ? new PlayerInventory_v1_8() : new PlayerInventory_v1_9();

		if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
			hologramSupport = new HolographicDisplaysPlugin();
			getLogger().log(Level.INFO, "Hooked onto HolographicDisplays");
		} else if (Bukkit.getPluginManager().getPlugin("Holograms") != null) {
			hologramSupport = new HologramsPlugin();
			getLogger().log(Level.INFO, "Hooked onto Holograms");
		}

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
			placeholderParser = new PlaceholderAPIParser();
		}

		if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
			Bukkit.getServer().getPluginManager().registerEvents(new MMDrops(), this);
			getLogger().log(Level.INFO, "Hooked onto MythicMobs");
		}

		if (Bukkit.getPluginManager().getPlugin("RealDualWield") != null) {
			Bukkit.getServer().getPluginManager().registerEvents(new RealDualWieldHook(), this);
			getLogger().log(Level.INFO, "Hooked onto RealDualWield");
		}

		if (Bukkit.getPluginManager().getPlugin("BossShopPro") != null) {

			/*
			 * runs async because of plugin loading order issues, this way it
			 * only registers after BossShop is initialized
			 */
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
				new MMOItemsRewardTypes().register();
				getLogger().log(Level.INFO, "Hooked onto BossShopPro (async)");
			});
		}

		// leveling plugins compatibility
		findRpgPlugin();

		// load item updater data
		FileConfiguration updater = ConfigData.getCD(this, "/dynamic", "updater");
		for (String type : updater.getKeys(false))
			for (String id : updater.getConfigurationSection(type).getKeys(false)) {
				String path = type + "." + id;
				ItemUpdater.enableUpdater(path, UpdaterData.load(path, UUID.fromString(updater.getString(path + ".uuid")), updater));
			}

		// compatibility with /reload
		// setups playerdatas for online players
		Bukkit.getOnlinePlayers().forEach(player -> PlayerData.setup(player));

		// item recipes
		getLogger().log(Level.INFO, "Loading recipes, please wait...");
		recipeManager = new RecipeManager();

		// commands
		getCommand("mmoitems").setExecutor(new MMOItemsCommand());
		getCommand("gemstones").setExecutor(new GemStonesCommand());
		getCommand("advancedworkbench").setExecutor(new AdvancedWorkbenchCommand());
		getCommand("updateitem").setExecutor(new UpdateItemCommand());
		getCommand("soulbound").setExecutor(new SoulboundCommand());

		// tab completion
		getCommand("mmoitems").setTabCompleter(new MMOItemsCompletion());
		getCommand("updateitem").setTabCompleter(new UpdateItemCompletion());
	}

	public void onDisable() {

		// save item updater data
		getLogger().log(Level.INFO, "Saving item updater data, please wait...");
		FileConfiguration updater = ConfigData.getCD(this, "/dynamic", "updater");
		for (String s : updater.getKeys(false))
			updater.set(s, null);
		for (UpdaterData data : ItemUpdater.getDatas())
			data.save(updater);
		ConfigData.saveCD(this, updater, "/dynamic", "updater");

		// drop abandonned soulbound items
		SoulboundInfo.getAbandonnedInfo().forEach(info -> info.dropItems());

		// save uuids
		configManager.saveConfigFiles();

		// close inventories
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getOpenInventory() != null)
				if (player.getOpenInventory().getTopInventory().getHolder() instanceof PluginInventory)
					player.closeInventory();
	}

	public static String getPrefix() {
		return ChatColor.YELLOW + "MI" + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY;
	}

	public static File getJarFile() {
		return plugin.getFile();
	}

	public NMSHandler getNMS() {
		return nms;
	}

	public FlagPlugin getFlags() {
		return flagPlugin;
	}

	public static RPGPlugin getRPG() {
		return rpgPlugin;
	}

	public static void setRPG(RPGPlugin value) {
		rpgPlugin = value;
	}

	public void setPlayerInventory(PlayerInventory value) {
		inventory = value;
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	public ServerVersion getVersion() {
		return version;
	}

	public StatManager getStats() {
		return statManager;
	}

	public EntityManager getEntities() {
		return entityManager;
	}

	public DamageManager getDamage() {
		return damageManager;
	}

	public DropTableManager getDropTables() {
		return dropTableManager;
	}

	public AbilityManager getAbilities() {
		return abilityManager;
	}

	public RecipeManager getRecipes() {
		return recipeManager;
	}

	public static ConfigManager getLanguage() {
		return configManager;
	}

	public TypeManager getTypes() {
		return typeManager;
	}

	public PlaceholderParser getPlaceholderParser() {
		return placeholderParser;
	}

	public HologramSupport getHolograms() {
		return hologramSupport;
	}

	// get uuid from updater map
	// or from uuids file
	public static UUID getItemUUID(Type type, String id) {
		String path = type.getId() + "." + id;
		if (ItemUpdater.hasData(path))
			return ItemUpdater.getData(path).getUUID();

		if (getLanguage().getItemUUIDs().contains(path))
			return UUID.fromString(getLanguage().getItemUUIDs().getString(path));

		UUID uuid = UUID.randomUUID();
		getLanguage().getItemUUIDs().set(path, uuid.toString());
		return uuid;
	}

	public static UUID generateRandomUUID(Type type, String id) {
		UUID uuid = UUID.randomUUID();
		String path = type.getId() + "." + id;
		if (ItemUpdater.hasData(path))
			ItemUpdater.getData(path).setUUID(uuid);
		return uuid;
	}

	public static ItemStack getItem(Type type, String id) {
		id = id.toUpperCase().replace("-", "_").replace(" ", "_");

		FileConfiguration items = type.getConfigFile();
		if (!items.contains(id))
			return null;

		MMOItem mmoitem = new MMOItem(type, id);

		for (Stat stat : Stat.values()) {
			if (!stat.c().isEnabled())
				continue;

			ConfigurationSection section = items.getConfigurationSection(id);
			if (mmoitem.canHaveStat(section, stat))
				if (!stat.c().readStatInfo(mmoitem, section))
					return null;
		}

		return mmoitem.toItemStack();

	}

	public void findRpgPlugin() {
		for (RPGPlugin.PluginEnum plugin : RPGPlugin.PluginEnum.values())
			if (Bukkit.getPluginManager().getPlugin(plugin.getName()) != null) {
				if ((rpgPlugin = plugin.getInstance()) instanceof Listener)
					Bukkit.getPluginManager().registerEvents((Listener) rpgPlugin, this);
				getLogger().log(Level.INFO, "Hooked onto " + plugin.getName());
				return;
			}
		Bukkit.getPluginManager().registerEvents((Listener) (rpgPlugin = new DefaultHook()), this);
	}

	public boolean isBlacklisted(Material material) {
		return getConfig().getStringList("block-blacklist").contains(material.name());
	}
}