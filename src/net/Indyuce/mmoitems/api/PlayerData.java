package net.Indyuce.mmoitems.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability.AbilityData;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.ItemSet.SetBonuses;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.comp.rpgplugin.RPGProfile;

public class PlayerData {
	private static Map<UUID, PlayerData> playerDatas = new HashMap<>();
	private static final boolean noOffhand = MMOItems.plugin.getVersion().isBelowOrEqual(1, 8);

	/*
	 * refreshes the player instance if the player has gone offline since or
	 * else just return it. the offline player does not have to be refreshed, it
	 * is just cached at the beginning
	 */
	private UUID uuid;
	private Player player;
	private OfflinePlayer offlinePlayer;

	/*
	 * the inventory is all the items the player can actually use. items are
	 * cached here to check if the player's items changed, if so just update
	 * inventory
	 */
	private List<ItemStack> playerInventory = new ArrayList<>();
	private ItemStack helmet = null, chestplate = null, leggings = null, boots = null, hand = null, offhand = null;
	private Map<String, Long> commands = new HashMap<>();
	private Map<String, Long> consumables = new HashMap<>();
	private Map<Ability, Long> abilities = new HashMap<>();
	private Map<CooldownType, Long> cooldowns = new HashMap<>();

	/*
	 * specific stat calculation
	 */
	private Map<PotionEffectType, PotionEffect> permanentEffects = new HashMap<>();
	private Set<ParticleData> itemParticles = new HashSet<>();
	private ParticleData overridingItemParticles = null;
	private SetBonuses setBonuses = null;
	private Set<AbilityData> itemAbilities = new HashSet<>();

	private boolean fullHands = false;

	public PlayerData(Player player) {
		this.uuid = player.getUniqueId();
		this.offlinePlayer = player;
		updateInventory();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public Player getPlayer() {
		return player == null ? player = Bukkit.getPlayer(uuid) : player;
	}

	public void resetPlayer() {
		player = null;
	}

	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	/*
	 * returns all the usable MMOItems in the player inventory, this can be used
	 * to calculate stats. this list updates each time a player equips a new
	 * item.
	 */
	public List<ItemStack> getMMOItems() {
		return playerInventory;
	}

	@SuppressWarnings("deprecation")
	public void checkForInventoryUpdate() {
		PlayerInventory inv = getPlayer().getInventory();
		if (!areItemsEqual(helmet, inv.getHelmet()) || !areItemsEqual(chestplate, inv.getChestplate()) || !areItemsEqual(leggings, inv.getLeggings()) || !areItemsEqual(boots, inv.getBoots()) || !areItemsEqual(hand, inv.getItemInHand()) || (!noOffhand && !areItemsEqual(offhand, inv.getItemInOffHand())))
			updateInventory();
	}

	public void scheduleDelayedInventoryUpdate() {
		new BukkitRunnable() {
			public void run() {
				updateInventory();
			}
		}.runTaskLater(MMOItems.plugin, 1);
	}

	private boolean areItemsEqual(ItemStack item, ItemStack item1) {
		return item == null ? item1 == null : item.equals(item1);
	}

	public void cancelRunnables() {
		for (ParticleData data : itemParticles)
			data.getRunnable().cancel();

		/*
		 * in versions below 1.10 the isCancelled() method doesn't exist for
		 * bukkitRunnables, so the plugin can't check if they're cancelled - can
		 * just cancel them anyway
		 */
		if (overridingItemParticles != null)
			overridingItemParticles.getRunnable().cancel();
	}

	/*
	 * returns true if the player hands are full, i.e if the player is holding
	 * one two handed item and one other item at the same time. this will
	 */
	public boolean areHandsFull() {
		if (noOffhand)
			return false;

		ItemStack main = getPlayer().getInventory().getItemInMainHand();
		ItemStack off = player.getInventory().getItemInOffHand();
		return (MMOItems.plugin.getNMS().getBooleanTag(main, "MMOITEMS_TWO_HANDED") && (off != null && off.getType() != Material.AIR)) || (MMOItems.plugin.getNMS().getBooleanTag(off, "MMOITEMS_TWO_HANDED") && (main != null && main.getType() != Material.AIR));
	}

	@SuppressWarnings("deprecation")
	public void updateInventory() {

		/*
		 * very important, clear particle data AFTER canceling the runnable
		 * otherwise it cannot cancel and the runnable keeps going (severe)
		 */
		playerInventory.clear();
		permanentEffects.clear();
		itemAbilities.clear();
		cancelRunnables();
		itemParticles.clear();
		overridingItemParticles = null;

		/*
		 * updates the full-hands boolean, this way it can be cached and used in
		 * the updateEffects() method
		 */
		fullHands = areHandsFull();

		// find all the items the player can actually use.
		RPGProfile rpgProfile = MMOItems.getRPG().getProfile(this);
		for (ItemStack item : MMOItems.plugin.getInventory().getInventory(getPlayer())) {
			Type type = Type.get(item);
			if (type == null)
				continue;

			/*
			 * if the player cannot use the item, the item will lose its
			 * attributes (attributes will be saved in another NBTTag that can't
			 * be accessed by vanilla minecraft). this allows players to still
			 * wear and hold items they can't use, but they won't get the
			 * effects from attributes -> very powerful
			 */
			if (!rpgProfile.canUse(item, false)) {
				if (!MMOItems.plugin.getNMS().hasTag(item, "MMOItems_SavedAttributeModifiers"))
					item.setItemMeta(MMOItems.plugin.getNMS().saveAttributes(item).getItemMeta());
				continue;
			}

			if (MMOItems.plugin.getNMS().hasTag(item, "MMOItems_SavedAttributeModifiers"))
				item.setItemMeta(MMOItems.plugin.getNMS().loadAttributes(item).getItemMeta());

			if (!noOffhand)
				if (item.equals(player.getInventory().getItemInOffHand()) && !type.hasSlot("offhand"))
					continue;
			if (item.equals(player.getItemInHand()) && !type.hasSlot("mainhand"))
				continue;

			playerInventory.add(item);
		}

		for (ItemStack item : getMMOItems()) {
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_PERM_EFFECTS");
			itemLoop: for (String format : tag.split("\\;")) {
				if (format.equals(""))
					continue;

				String[] split = format.split("\\:");
				PotionEffectType effect = PotionEffectType.getByName(split[0].toUpperCase().replace("-", "_"));
				int amplifier = (int) (Double.parseDouble(split[1]) - 1);
				for (PotionEffect loadedEffect : getPermanentPotionEffects())
					if (loadedEffect.getType() == effect && loadedEffect.getAmplifier() >= amplifier)
						continue itemLoop;

				permanentEffects.put(effect, new PotionEffect(effect, MMOUtils.getEffectDuration(effect), amplifier));
			}
		}

		for (ItemStack item : getMMOItems()) {
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_PARTICLES");
			if (tag.equals(""))
				continue;

			ParticleData particleData = new ParticleData(this, tag);
			if (!particleData.isValid())
				return;

			if (particleData.getType().hasPriority()) {
				if (overridingItemParticles == null)
					overridingItemParticles = particleData.start();
				continue;
			}

			itemParticles.add(particleData.start());
		}

		for (ItemStack item : getMMOItems()) {
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ABILITIES");
			if (tag.equals(""))
				continue;

			for (String abilityTag : tag.split("\\%")) {
				AbilityData abilityData = new AbilityData().load(abilityTag);
				if (abilityData.isValid())
					itemAbilities.add(abilityData);
			}
		}

		/*
		 * calculate the player's item set and add the bonus permanent effects /
		 * bonus abilities to the playerdata maps
		 */
		int max = 0;
		ItemSet set = null;
		Map<ItemSet, Integer> sets = new HashMap<>();
		for (ItemStack item : getMMOItems()) {
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_SET");
			ItemSet itemSet = MMOItems.getLanguage().getItemSet(tag);
			if (itemSet == null)
				continue;

			int nextInt = (sets.containsKey(itemSet) ? sets.get(itemSet) : 0) + 1;
			sets.put(itemSet, nextInt);
			if (nextInt >= max) {
				max = nextInt;
				set = itemSet;
			}
		}
		setBonuses = set == null ? null : set.getBonuses(max);

		if (hasSetBonuses()) {
			itemAbilities.addAll(setBonuses.getAbilities());
			for (PotionEffect effect : setBonuses.getPotionEffects())
				if (getPermanentPotionEffectAmplifier(effect.getType()) < effect.getAmplifier())
					permanentEffects.put(effect.getType(), effect);
		}

		/*
		 * update stuff from the external RPG plugins. the 'max mana' stat
		 * currently only supports Heroes since other APIs do not allow other
		 * plugins to easily increase this type of stat.
		 */
		MMOItems.getRPG().refreshStats(this);

		/*
		 * actually update the player inventory so the task doesn't infinitely
		 * loop on updating
		 */
		helmet = player.getInventory().getHelmet();
		chestplate = player.getInventory().getChestplate();
		leggings = player.getInventory().getLeggings();
		boots = player.getInventory().getBoots();
		hand = player.getItemInHand();
		offhand = noOffhand ? null : player.getInventory().getItemInOffHand();
	}

	public SetBonuses getSetBonuses() {
		return setBonuses;
	}

	public boolean hasSetBonuses() {
		return setBonuses != null;
	}

	public int getPermanentPotionEffectAmplifier(PotionEffectType type) {
		for (PotionEffect effect : permanentEffects.values())
			if (effect.getType() == type)
				return effect.getAmplifier();
		return -1;
	}

	public void updateEffects() {

		// perm effects
		for (PotionEffect effect : getPermanentPotionEffects()) {
			player.removePotionEffect(effect.getType());
			player.addPotionEffect(effect);
		}

		// two handed
		if (fullHands) {
			player.removePotionEffect(PotionEffectType.SLOW);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3));
		}
	}

	public Collection<PotionEffect> getPermanentPotionEffects() {
		return permanentEffects.values();
	}

	public PlayerStats getStats(Stat... requested) {
		return new PlayerStats(this, requested);
	}

	public Set<AbilityData> getItemAbilities(CastingMode castMode) {
		return itemAbilities.stream().filter(abilityData -> abilityData.getCastingMode() == castMode).collect(Collectors.toSet());
	}

	public AttackResult castAbilities(LivingEntity target, AttackResult result, CastingMode castMode) {
		if (!MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_ABILITIES))
			return result.setSuccessful(false);

		if (target != null && !MMOUtils.canDamage(player, target))
			return result.setSuccessful(false);

		boolean message = castMode.displaysMessage();
		for (AbilityData abilityData : getItemAbilities(castMode))
			result.addDamage(abilityData.getAbility().cast(this, target, result.getDamage(), abilityData, message).getDamage());
		return result;
	}

	public boolean canCast(AbilityData data, boolean message) {
		double remaining = getRemainingAbilityCooldown(data.getAbility());
		if (remaining > 0) {
			if (message) {
				String progressBar = ChatColor.YELLOW + "";
				double cooldown = data.getModifier("cooldown");
				double progress = (cooldown - remaining) / cooldown * 10.;

				String barChar = MMOItems.plugin.getConfig().getString("cooldown-progress-bar-char");
				for (int j = 0; j < 10; j++)
					progressBar += (progress >= j ? ChatColor.GREEN : ChatColor.WHITE) + barChar;
				Message.SPELL_ON_COOLDOWN.format(ChatColor.RED, "#left#", "" + new DecimalFormat("0.#").format(remaining), "#progress#", progressBar, "#s#", (remaining >= 2 ? "s" : "")).send(getPlayer(), "ability-cooldown");
			}
			return false;
		}

		if (MMOItems.plugin.getConfig().getBoolean("permissions.abilities") && !getPlayer().hasPermission("mmoitems.ability." + data.getAbility().getLowerCaseID()) && !getPlayer().hasPermission("mmoitems.bypass.ability"))
			return false;

		if (data.hasModifier("mana"))
			if (MMOItems.getRPG().getMana(getPlayer()) < data.getModifier("mana")) {
				Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(player, "not-enough-mana");
				return false;
			}

		if (data.hasModifier("stamina"))
			if (MMOItems.getRPG().getStamina(getPlayer()) < data.getModifier("stamina")) {
				Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(player, "not-enough-stamina");
				return false;
			}

		return true;
	}

	public double getStat(Stat stat) {
		double value = hasSetBonuses() ? getSetBonuses().getStat(stat) : 0;
		for (ItemStack item : getMMOItems())
			value += MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_" + stat.name());
		return value;
	}

	public double getStat(String stat) {
		double value = 0;
		stat = stat.toUpperCase().replace("-", "_").replace(" ", "_");
		for (ItemStack item : getMMOItems())
			value += MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_" + stat);
		return value;
	}

	public boolean isOnCooldown(CooldownType type) {
		return (cooldowns.containsKey(type) ? cooldowns.get(type) : 0) > System.currentTimeMillis();
	}

	public void applyCooldown(CooldownType type, double value) {
		String mitigation = type.name().toLowerCase();
		long extra = (long) (1000 * (type.isMitigation ? getMitigationCooldown(mitigation) * (1 - Math.min(getMaxMitigationCooldownReduction(mitigation), value) / 100) : value));
		cooldowns.put(type, System.currentTimeMillis() + extra);
	}

	public boolean canUseCommand(String command) {
		return (commands.containsKey(command) ? commands.get(command) : 0) < System.currentTimeMillis();
	}

	public boolean canUseAbility(Ability ability) {
		return (abilities.containsKey(ability) ? abilities.get(ability) : 0) < System.currentTimeMillis();
	}

	public boolean canUseConsumable(String id) {
		return (consumables.containsKey(id) ? consumables.get(id) : 0) < System.currentTimeMillis();
	}

	public void applyCommandCooldown(String command, double value) {
		commands.put(command, (long) (System.currentTimeMillis() + value * 1000));
	}

	public void applyConsumableCooldown(String id, double value) {
		consumables.put(id, (long) (System.currentTimeMillis() + value * 1000));
	}

	public void applyAbilityCooldown(Ability ability, double value) {
		abilities.put(ability, (long) (System.currentTimeMillis() + value * 1000));
	}

	public double getRemainingAbilityCooldown(Ability ability) {
		return (double) ((abilities.containsKey(ability) ? abilities.get(ability) : 0) - System.currentTimeMillis()) / 1000;
	}

	private double getMaxMitigationCooldownReduction(String path) {
		return MMOItems.plugin.getConfig().getDouble("mitigation." + path + ".cooldown.max-reduction") / 100;
	}

	private double getMitigationCooldown(String path) {
		return MMOItems.plugin.getConfig().getDouble("mitigation." + path + ".cooldown.base");
	}

	public static PlayerData get(OfflinePlayer player) {
		return playerDatas.get(player.getUniqueId());
	}

	public static PlayerData setup(Player player) {
		if (playerDatas.containsKey(player.getUniqueId()))
			return playerDatas.get(player.getUniqueId());

		PlayerData playerData = new PlayerData(player);
		playerDatas.put(player.getUniqueId(), playerData);
		return playerData;
	}

	public static Collection<PlayerData> getAll() {
		return playerDatas.values();
	}

	public enum CooldownType {

		// simple attack cooldown
		ATTACK(false),

		// item type special effects
		SPECIAL_ATTACK(false),

		// piercing / blunt / slashing passive effects
		SET_TYPE_ATTACK(false),

		// mitigation cooldowns
		PARRY(true),
		BLOCK(true),
		DODGE(true);

		public boolean isMitigation;

		private CooldownType(boolean isMitigation) {
			this.isMitigation = isMitigation;
		}
	}
}
