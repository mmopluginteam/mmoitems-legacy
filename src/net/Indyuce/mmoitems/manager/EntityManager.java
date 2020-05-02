package net.Indyuce.mmoitems.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ArrowParticles;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.ProjectileData;
import net.Indyuce.mmoitems.api.Stat;

public class EntityManager implements Listener {

	/*
	 * entity data used by abilities or staff attacks that utilize entities like
	 * evoker fangs or shulker missiles. it can correspond to the damage the
	 * entity is supposed to deal, etc
	 */
	private Map<Integer, Object[]> entities = new HashMap<>();

	private Map<Integer, ProjectileData> projectiles = new HashMap<>();

	public void registerCustomProjectile(ItemStack sourceItem, PlayerStats playerStats, Entity entity) {
		registerCustomProjectile(sourceItem, playerStats, entity, 1);
	}

	public void registerCustomProjectile(ItemStack sourceItem, PlayerStats playerStats, Entity entity, double damageCoefficient) {
		/*
		 * if damage is null, then it uses the default minecraft bow damage.
		 * it's then multiplied by the damage coefficient which corresponds for
		 * bows to the pull force just like vanilla. it does not work with
		 * tridents
		 */
		double damage = playerStats.getStat(Stat.ATTACK_DAMAGE);
		playerStats.setStat(Stat.ATTACK_DAMAGE, (damage == 0 ? 7 : damage) * damageCoefficient);

		/*
		 * load arrow particles if the entity is an arrow and if the item has
		 * arrow particles. currently projectiles are only arrows so there is no
		 * problem with other projectiles like snowballs etc.
		 */
		if (entity instanceof Arrow) {
			ArrowParticles particles = new ArrowParticles((Arrow) entity).load(sourceItem);
			if (particles.isValid())
				particles.runTaskTimer(MMOItems.plugin, 0, 1);
		}

		projectiles.put(entity.getEntityId(), new ProjectileData(sourceItem, playerStats));
	}

	public void registerCustomEntity(Entity entity, Object... data) {
		entities.put(entity.getEntityId(), data);
	}

	public boolean isCustomProjectile(Projectile projectile) {
		return projectiles.containsKey(projectile.getEntityId());
	}

	public boolean isCustomEntity(Entity entity) {
		return entities.containsKey(entity.getEntityId());
	}

	public ProjectileData getProjectileData(Projectile projectile) {
		return projectiles.get(projectile.getEntityId());
	}

	public Object[] getEntityData(Entity entity) {
		return entities.get(entity.getEntityId());
	}

	public void unregisterCustomProjectile(Projectile projectile) {
		projectiles.remove(projectile.getEntityId());
	}

	public void unregisterCustomEntity(Entity entity) {
		entities.remove(entity.getEntityId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void b(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		new BukkitRunnable() {
			public void run() {
				unregisterCustomEntity(entity);
			}
		}.runTaskLater(MMOItems.plugin, 0);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void c(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof LivingEntity) || event.getEntity().hasMetadata("NPC"))
			return;

		Arrow arrow = (Arrow) event.getDamager();
		if (!isCustomProjectile(arrow))
			return;

		ProjectileData data = getProjectileData(arrow);
		PlayerStats stats = data.getPlayerStats();
		LivingEntity target = (LivingEntity) event.getEntity();

		AttackResult result = new AttackResult(true, stats.getStat(Stat.ATTACK_DAMAGE)).applyEffects(stats, data.getSourceItem(), target);

		if (data.getSourceItem().hasItemMeta())
			if (data.getSourceItem().getItemMeta().getEnchants().containsKey(Enchantment.ARROW_DAMAGE))
				result.addRelativeDamage(.25 + (.25 * data.getSourceItem().getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE)));

		event.setDamage(result.getDamage());
		MMOItems.plugin.getEntities().unregisterCustomProjectile(arrow);
		return;
	}
}