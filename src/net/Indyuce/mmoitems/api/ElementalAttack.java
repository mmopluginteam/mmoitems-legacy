package net.Indyuce.mmoitems.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public class ElementalAttack {
	private Map<Element, Double> absolute = new HashMap<>();
	private AttackResult result;
	private LivingEntity target;

	public ElementalAttack(ItemStack item, AttackResult result) {
		this.result = result;

		for (Element element : Element.values()) {
			double damage = MMOItems.plugin.getNMS().getDoubleTag(item, element.name() + "_DAMAGE");
			if (damage > 0) {
				double abs = damage / 100 * result.getDamage();
				result.addDamage(-abs);
				absolute.put(element, abs);
			}
		}
	}

	public ElementalAttack applyElementalArmor(LivingEntity target) {
		this.target = target;

		for (ItemStack equip : target.getEquipment().getArmorContents()) {
			if (Type.get(equip) != null)
				for (Element element : absolute.keySet()) {
					double defense = MMOItems.plugin.getNMS().getDoubleTag(equip, element.name() + "_DEFENSE") / 100;
					if (defense > 0) 
						absolute.put(element, absolute.get(element) * (1 - defense));
				}
		}

		return this;
	}

	public void apply(PlayerStats stats) {

		for (Element element : absolute.keySet()) {
			double damage = absolute.get(element);
			if (damage > 0) {
				result.addDamage(damage);
				element.getParticle().displayParticle(target);
			}
		}
	}
}
