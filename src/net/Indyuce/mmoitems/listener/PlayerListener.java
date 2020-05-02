package net.Indyuce.mmoitems.listener;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.ability.Magical_Shield;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.SoulboundInfo;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.version.VersionSound;

public class PlayerListener implements Listener {
	private static final Random random = new Random();
	private static final List<DamageCause> mitigationCauses = Arrays.asList(DamageCause.PROJECTILE, DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_EXPLOSION);

	@EventHandler
	public void a(EntityDamageByEntityEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player) || !mitigationCauses.contains(event.getCause()) || event.getEntity().hasMetadata("NPC"))
			return;

		Player player = (Player) event.getEntity();
		PlayerData playerData = PlayerData.get(player);
		PlayerStats stats = playerData.getStats(Stat.DODGE_RATING, Stat.DODGE_COOLDOWN_REDUCTION, Stat.BLOCK_RATING, Stat.BLOCK_POWER, Stat.BLOCK_COOLDOWN_REDUCTION, Stat.PARRY_RATING, Stat.PARRY_COOLDOWN_REDUCTION);

		// dodging
		double dodgeRating = Math.min(stats.getStat(Stat.DODGE_RATING), MMOItems.plugin.getConfig().getDouble("mitigation.dodge.rating-max")) / 100;
		if (random.nextDouble() < dodgeRating && !playerData.isOnCooldown(CooldownType.DODGE)) {
			playerData.applyCooldown(CooldownType.DODGE, stats.getStat(Stat.DODGE_COOLDOWN_REDUCTION));
			event.setCancelled(true);

			Message.ATTACK_DODGED.format(ChatColor.RED).send(player, "mitigation");
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ENDERDRAGON_FLAP.getSound(), 2, 1);
			ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .06f, 16, player.getLocation());
			player.setVelocity(getVector(player, event).multiply(.85).setY(.3));
			return;
		}

		// parrying
		double parryRating = Math.min(stats.getStat(Stat.PARRY_RATING), MMOItems.plugin.getConfig().getDouble("mitigation.parry.rating-max")) / 100;
		if (random.nextDouble() < parryRating && !playerData.isOnCooldown(CooldownType.PARRY)) {
			playerData.applyCooldown(CooldownType.PARRY, stats.getStat(Stat.PARRY_COOLDOWN_REDUCTION));
			event.setCancelled(true);

			Message.ATTACK_PARRIED.format(ChatColor.RED).send(player, "mitigation");
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ENDERDRAGON_FLAP.getSound(), 2, 1);
			ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .06f, 16, player.getLocation());
			if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof LivingEntity) {
				LivingEntity attacker = (LivingEntity) ((EntityDamageByEntityEvent) event).getDamager();
				attacker.setVelocity(attacker.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().setY(.35).multiply(MMOItems.plugin.getConfig().getDouble("mitigation.parry.knockback-force")));
			}
			return;
		}

		// blocking
		double blockRating = Math.min(stats.getStat(Stat.BLOCK_RATING), MMOItems.plugin.getConfig().getDouble("mitigation.block.rating-max")) / 100;
		if (random.nextDouble() < blockRating && !playerData.isOnCooldown(CooldownType.BLOCK)) {
			double blockPower = Math.min(MMOItems.plugin.getConfig().getDouble("mitigation.block.power.default") + stats.getStat(Stat.BLOCK_POWER), MMOItems.plugin.getConfig().getDouble("mitigation.block.power.max")) / 100;
			playerData.applyCooldown(CooldownType.BLOCK, stats.getStat(Stat.BLOCK_COOLDOWN_REDUCTION));
			event.setDamage(event.getDamage() * (1 - blockPower));

			Message.ATTACK_BLOCKED.format(ChatColor.RED, "#percent#", new DecimalFormat("0.#").format(blockPower * 100)).send(player, "mitigation");
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 2, 1);

			double yaw = getYaw(player, getVector(player, event)) + 95;
			for (double j = yaw - 90; j < yaw + 90; j += 5)
				for (double y = 0; y < 2; y += .1)
					ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.GRAY), player.getLocation().clone().add(Math.cos(Math.toRadians(j)) * .7, y, Math.sin(Math.toRadians(j)) * .7));
		}
	}

	private Vector getVector(Player player, EntityDamageEvent event) {
		return event instanceof EntityDamageByEntityEvent ? event.getEntity().getLocation().subtract(player.getLocation()).toVector().normalize() : player.getEyeLocation().getDirection();
	}

	private double getYaw(Entity player, Vector vec) {
		return new Location(player.getWorld(), vec.getX(), vec.getY(), vec.getZ()).setDirection(vec).getYaw();
	}

	@EventHandler
	public void b(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player) || event.isCancelled() || event.getEntity().hasMetadata("NPC"))
			return;

		Player player = (Player) event.getEntity();

		// magical shield ability
		for (Location loc : Magical_Shield.magicalShield.keySet())
			if (loc.getWorld().equals(player.getWorld())) {
				Double[] values = Magical_Shield.magicalShield.get(loc);
				if (loc.distanceSquared(player.getLocation()) <= values[0])
					event.setDamage(event.getDamage() * (1 - values[1]));
			}

		// damage reduction stats
		PlayerData playerData = PlayerData.get(player);
		for (DamageCause damageCause : new DamageCause[] { DamageCause.FIRE, DamageCause.MAGIC, DamageCause.FALL })
			if (event.getCause() == damageCause) {
				event.setDamage(event.getDamage() * (1 - playerData.getStat(damageCause.name() + "_DAMAGE_REDUCTION") / 100));
				break;
			}

		event.setDamage(event.getDamage() * (1 - playerData.getStat(Stat.DAMAGE_REDUCTION) / 100));
	}

	// regeneration
	@EventHandler
	public void c(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player)
			event.setAmount(event.getAmount() * (1 + PlayerData.get((Player) event.getEntity()).getStat(Stat.REGENERATION) / 100));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void d(PlayerJoinEvent event) {
		PlayerData.setup(event.getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void e(PlayerQuitEvent event) {
		PlayerData playerData = PlayerData.get(event.getPlayer());
		playerData.cancelRunnables();
		playerData.resetPlayer();
	}

	// apply on-hit abilities from armor pieces.
	@EventHandler(priority = EventPriority.HIGH)
	public void f(EntityDamageByEntityEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof LivingEntity) || event.getEntity().hasMetadata("NPC") || event.getDamager().hasMetadata("NPC"))
			return;

		LivingEntity damager = (LivingEntity) event.getDamager();
		Player player = (Player) event.getEntity();
		PlayerData.get(player).castAbilities(damager, new AttackResult(true, event.getDamage()), CastingMode.WHEN_HIT);
	}

	@EventHandler
	public void g(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL)
			return;

		Player player = event.getPlayer();
		PlayerData playerData = PlayerData.get(player);
		boolean left = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;

		playerData.castAbilities(null, new AttackResult(true, 0), player.isSneaking() ? (left ? CastingMode.SHIFT_LEFT_CLICK : CastingMode.SHIFT_RIGHT_CLICK) : (left ? CastingMode.LEFT_CLICK : CastingMode.RIGHT_CLICK));
	}

	/*
	 * prevent players from droping items which are bound to them with a
	 * soulbound. items are cached inside a map waiting for the player to
	 * respawn. if he does not respawn the items are dropped on the ground, this
	 * way there don't get lost
	 */
	@EventHandler
	public void h(PlayerDeathEvent event) {
		if (event.getKeepInventory())
			return;

		Player player = event.getEntity();
		SoulboundInfo soulboundInfo = new SoulboundInfo(player);

		ItemStack item;
		Iterator<ItemStack> iterator = event.getDrops().iterator();
		while (iterator.hasNext())
			if (MMOItems.plugin.getNMS().getStringTag(item = iterator.next(), "MMOITEMS_SOULBOUND").equals(player.getUniqueId().toString())) {
				iterator.remove();
				soulboundInfo.add(item);
			}

		soulboundInfo.setup();
	}

	@EventHandler
	public void i(PlayerRespawnEvent event) {
		SoulboundInfo.read(event.getPlayer());
	}
}