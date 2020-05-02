package net.Indyuce.mmoitems;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class MMOUtils {
	private static final Random random = new Random();

	public static String getSkullTextureURL(ItemStack item) {
		try {
			ItemMeta meta = item.getItemMeta();
			Field profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			Collection<Property> properties = ((GameProfile) profileField.get(item.getItemMeta())).getProperties().get("textures");
			Property property = properties.toArray(new Property[properties.size()])[0];
			return new String(Base64.decodeBase64(property.getValue())).replace("{textures:{SKIN:{url:\"", "").replace("\"}}}", "");
		} catch (Exception e) {
			return "";
		}
	}

	// random offset between -a and a
	public static double rdm(double a) {
		return (random.nextDouble() - .5) * 2 * a;
	}

	public static int getEffectDuration(PotionEffectType type) {

		// confusion takes a lot of time to decay
		// night vision flashes your screen for the last 10sec of effect
		if (type.equals(PotionEffectType.NIGHT_VISION) || type.equals(PotionEffectType.CONFUSION))
			return 260;

		// takes some time to decay
		if (type.equals(PotionEffectType.BLINDNESS))
			return 140;

		// otherwise 4sec is high enough to maintain the effect even when the
		// server laggs
		return 80;
	}

	public static String getDisplayName(ItemStack item) {
		if (!item.hasItemMeta())
			return caseOnWords(item.getType().name().toLowerCase().replace("_", " "));
		return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : caseOnWords(item.getType().name().toLowerCase().replace("_", " "));
	}

	public static String caseOnWords(String s) {
		StringBuilder builder = new StringBuilder(s);
		boolean isLastSpace = true;
		for (int i = 0; i < builder.length(); i++) {
			char ch = builder.charAt(i);
			if (isLastSpace && ch >= 'a' && ch <= 'z') {
				builder.setCharAt(i, (char) (ch + ('A' - 'a')));
				isLastSpace = false;
			} else if (ch != ' ')
				isLastSpace = false;
			else
				isLastSpace = true;
		}
		return builder.toString();
	}

	public static boolean isPluginItem(ItemStack item, boolean lore) {
		if (item != null && item.getType() != Material.AIR)
			if (item.getItemMeta() != null)
				if (item.getItemMeta().getDisplayName() != null)
					return !lore || item.getItemMeta().getLore() != null;
		return false;
	}

	public static void saturate(Player player, double saturation) {
		if (saturation <= 0)
			return;

		float saturated = player.getSaturation() + (float) saturation;
		player.setSaturation(saturated > 20 ? 20f : saturated);
	}

	public static void feed(Player player, int feed) {
		if (feed <= 0)
			return;

		int food = player.getFoodLevel() + feed;
		player.setFoodLevel(food > 20 ? 20 : food);
	}

	@SuppressWarnings("deprecation")
	public static void heal(Player player, double heal) {
		if (heal <= 0)
			return;

		double health = player.getHealth() + heal;
		player.setHealth(health > player.getMaxHealth() ? player.getMaxHealth() : health);
	}

	public static boolean canDamage(Player player, Entity target) {
		return canDamage(player, null, target);
	}

	public static boolean canDamage(Entity target) {
		return canDamage(null, null, target);
	}

	public static boolean canDamage(Player player, Location loc, Entity target) {

		/*
		 * cannot hit himself or non-living entities. careful, some entities are
		 * weirdly considered as livingEntities like the armor stand. also check
		 * if the entity is dead since a dying entity (dying effect takes some
		 * time) can still be targeted but we dont want that
		 */
		if (target.equals(player) || !(target instanceof LivingEntity) || target instanceof ArmorStand || target.isDead())
			return false;

		/*
		 * can spam your console - an error message is sent each time an NPC
		 * gets damaged since it is considered a player.
		 */
		if (target.hasMetadata("NPC"))
			return false;

		/*
		 * the ability player damage option is cached for quicker access in the
		 * config manager instance since it is used in runnables
		 */
		if (target instanceof Player && (!MMOItems.getLanguage().abilityPlayerDamage || !MMOItems.plugin.getFlags().isPvpAllowed(target.getLocation())))
			return false;

		return loc == null ? true : isInBoundingBox(target, loc);
	}

	public static PotionEffect getPotionEffect(Player player, PotionEffectType type) {
		for (PotionEffect effect : player.getActivePotionEffects())
			if (effect.getType() == type)
				return effect;
		return null;
	}

	public static String intToRoman(int input) {
		if (input < 1)
			return "0";
		if (input > 499)
			return ">499";

		String s = "";
		while (input >= 1000) {
			s += "M";
			input -= 1000;
		}
		while (input >= 900) {
			s += "DM";
			input -= 900;
		}
		while (input >= 500) {
			s += "D";
			input -= 500;
		}
		while (input >= 400) {
			s += "CD";
			input -= 400;
		}
		while (input >= 100) {
			s += "C";
			input -= 100;
		}
		while (input >= 90) {
			s += "XC";
			input -= 90;
		}
		while (input >= 50) {
			s += "L";
			input -= 50;
		}
		while (input >= 40) {
			s += "XL";
			input -= 40;
		}
		while (input >= 10) {
			s += "X";
			input -= 10;
		}
		while (input >= 9) {
			s += "IX";
			input -= 9;
		}
		while (input >= 5) {
			s += "V";
			input -= 5;
		}
		while (input >= 4) {
			s += "IV";
			input -= 4;
		}
		while (input >= 1) {
			s += "I";
			input -= 1;
		}
		return s;
	}

	public static double truncation(double x, int n) {
		double pow = Math.pow(10.0, n);
		return Math.floor(x * pow) / pow;
	}

	// returns random value if supported OR single value
	public static double randomValue(String string) {
		String[] split = string.split("\\=");
		if (split.length == 2)
			try {
				double[] values = new double[] { Double.parseDouble(split[0]), Double.parseDouble(split[1]) };
				return random.nextDouble() * (values[1] - values[0]) + values[0];
			} catch (Exception e) {
			}
		return Double.parseDouble(string);
	}

	public static Vector rotAxisX(Vector v, double a) {
		double y = v.getY() * Math.cos(a) - v.getZ() * Math.sin(a);
		double z = v.getY() * Math.sin(a) + v.getZ() * Math.cos(a);
		return v.setY(y).setZ(z);
	}

	public static Vector rotAxisY(Vector v, double b) {
		double x = v.getX() * Math.cos(b) + v.getZ() * Math.sin(b);
		double z = v.getX() * -Math.sin(b) + v.getZ() * Math.cos(b);
		return v.setX(x).setZ(z);
	}

	public static Vector rotAxisZ(Vector v, double c) {
		double x = v.getX() * Math.cos(c) - v.getY() * Math.sin(c);
		double y = v.getX() * Math.sin(c) + v.getY() * Math.cos(c);
		return v.setX(x).setY(y);
	}

	public static Vector rotateFunc(Vector v, Location loc) {
		double yaw = loc.getYaw() / 180 * Math.PI;
		double pitch = loc.getPitch() / 180 * Math.PI;
		v = rotAxisX(v, pitch);
		v = rotAxisY(v, -yaw);
		return v;
	}

	public static boolean isInBoundingBox(Entity entity, Location loc) {
		double[] bb = MMOItems.plugin.getNMS().getBoundingBox(entity, .2);
		return loc.getX() > bb[0] && loc.getX() < bb[3] && loc.getY() > bb[1] && loc.getY() < bb[4] && loc.getZ() > bb[2] && loc.getZ() < bb[5];
	}

	public static double distanceFromBoundingBox(Entity entity, Location loc) {
		return Math.sqrt(distanceSquaredFromBoundingBox(entity, loc));
	}

	public static double distanceSquaredFromBoundingBox(Entity entity, Location loc) {
		double[] bb = MMOItems.plugin.getNMS().getBoundingBox(entity, .2);

		double dx = loc.getX() > bb[0] && loc.getX() < bb[3] ? 0 : Math.min(Math.abs(bb[0] - loc.getX()), Math.abs(bb[3] - loc.getX()));
		double dy = loc.getY() > bb[1] && loc.getY() < bb[4] ? 0 : Math.min(Math.abs(bb[1] - loc.getY()), Math.abs(bb[4] - loc.getY()));
		double dz = loc.getZ() > bb[2] && loc.getZ() < bb[5] ? 0 : Math.min(Math.abs(bb[2] - loc.getZ()), Math.abs(bb[5] - loc.getZ()));

		return Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2);
	}

	public static double getHeight(Entity entity) {
		double[] bb = MMOItems.plugin.getNMS().getBoundingBox(entity, 0);
		return bb[4] - bb[1];
	}

	/*
	 * method to get all entities surrounding a location. this method does not
	 * take every entity in the world but rather takes all the entities from the
	 * 9 chunks around the entity, so even if the location is at the border of a
	 * chunk (worst case border of 4 chunks), the entity will still be included
	 */
	public static List<Entity> getNearbyChunkEntities(Location loc) {

		/*
		 * another method to save performance is if an entit bounding box
		 * calculation is made twice in the same tick then the method does not
		 * need to be called twice, it can utilize the same entity list since
		 * the entities have not moved (e.g fireball which does 2+ calculations
		 * per tick)
		 */
		List<Entity> entities = new ArrayList<Entity>();

		int cx = loc.getChunk().getX();
		int cz = loc.getChunk().getZ();

		for (int x = -1; x < 2; x++)
			for (int z = -1; z < 2; z++)
				for (Entity entity : loc.getWorld().getChunkAt(cx + x, cz + z).getEntities())
					entities.add(entity);

		return entities;
	}
}
