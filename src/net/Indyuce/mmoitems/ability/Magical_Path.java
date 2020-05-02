package net.Indyuce.mmoitems.ability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Magical_Path extends Ability implements Listener {
	private Map<UUID, Long> fallDamage = new HashMap<UUID, Long>();

	public Magical_Path() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 3);
		addModifier("cooldown", 15);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration");

		final boolean flight = stats.getPlayer().getAllowFlight();
		stats.getPlayer().setAllowFlight(true);
		stats.getPlayer().setFlying(true);
		stats.getPlayer().setVelocity(stats.getPlayer().getVelocity().setY(.5));
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 1, 1);

		new BukkitRunnable() {
			double j = 0;

			public void run() {
				j++;
				if (j > duration * 10) {
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 1, 1);
					stats.getPlayer().setAllowFlight(flight);
					cancel();
					return;
				}

				ParticleEffect.SPELL.display(.5f, 0, .5f, .1f, 8, stats.getPlayer().getLocation().add(0, .1, 0));
				ParticleEffect.SPELL_INSTANT.display(.5f, 0, .5f, .1f, 16, stats.getPlayer().getLocation().add(0, .1, 0));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 2);

		fallDamage.put(stats.getPlayer().getUniqueId(), (long) (System.currentTimeMillis() + duration * 3000));
		return new AttackResult(true);
	}

	@EventHandler
	public void a(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player) || event.getCause() != DamageCause.FALL)
			return;

		Player player = (Player) event.getEntity();
		if (!fallDamage.containsKey(player.getUniqueId()))
			return;

		if (fallDamage.get(player.getUniqueId()) > System.currentTimeMillis()) {
			ParticleEffect.SPELL.display(.5f, 0, .5f, .1f, 16, player.getLocation().add(0, .1, 0));
			ParticleEffect.SPELL_INSTANT.display(.5f, 0, .5f, .1f, 32, player.getLocation().add(0, .1, 0));
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ENDERMEN_HURT.getSound(), 1, 2);
			event.setCancelled(true);
			return;
		}

		// clear player from map not to overload memory
		fallDamage.remove(player.getUniqueId());
	}
}
