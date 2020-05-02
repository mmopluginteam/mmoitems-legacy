package net.Indyuce.mmoitems.ability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Shadow_Veil extends Ability implements Listener {
	private List<UUID> shadowVeil = new ArrayList<UUID>();

	public Shadow_Veil() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 35);
		addModifier("duration", 5);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration");

		shadowVeil.add(stats.getPlayer().getUniqueId());
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 3, 0);
		for (Player online : Bukkit.getOnlinePlayers())
			online.hidePlayer(stats.getPlayer());
		new BukkitRunnable() {
			double ti = 0;
			double y = 0;
			Location loc = stats.getPlayer().getLocation();

			public void run() {
				ti++;
				if (ti > duration * 20) {
					for (Player online : Bukkit.getOnlinePlayers())
						online.showPlayer(stats.getPlayer());
					shadowVeil.remove(stats.getPlayer().getUniqueId());
					ParticleEffect.SMOKE_LARGE.display(0, 0, 0, .13f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 3, 0);
					cancel();
					return;
				}
				if (!shadowVeil.contains(stats.getPlayer().getUniqueId())) {
					for (Player online : Bukkit.getOnlinePlayers())
						online.showPlayer(stats.getPlayer());
					ParticleEffect.SMOKE_LARGE.display(0, 0, 0, .13f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 3, 0);
					cancel();
				}
				if (y < 4)
					for (int j1 = 0; j1 < 5; j1++) {
						y += .04;
						for (int j = 0; j < 4; j++) {
							double xz = y * Math.PI * .8 + (j * Math.PI / 2);
							ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, loc.clone().add(Math.cos(xz) * 2.5, y, Math.sin(xz) * 2.5));
						}
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}

	@EventHandler
	public void a(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();
		if (shadowVeil.contains(player.getUniqueId()))
			shadowVeil.remove(player.getUniqueId());
	}
}
