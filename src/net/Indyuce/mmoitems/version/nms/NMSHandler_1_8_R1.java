package net.Indyuce.mmoitems.version.nms;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.Blocks;
import net.minecraft.server.v1_8_R1.ChatMessage;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.Container;
import net.minecraft.server.v1_8_R1.ContainerAnvil;
import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.NBTTagFloat;
import net.minecraft.server.v1_8_R1.NBTTagInt;
import net.minecraft.server.v1_8_R1.NBTTagList;
import net.minecraft.server.v1_8_R1.NBTTagString;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_8_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;

public class NMSHandler_1_8_R1 implements NMSHandler {
	@Override
	public ItemStack addTag(ItemStack item, List<ItemTag> tags) {
		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();

		for (ItemTag tag : tags) {
			if (tag.getValue() instanceof Boolean) {
				compound.setBoolean(tag.getPath(), (boolean) tag.getValue());
				continue;
			}
			if (tag.getValue() instanceof Double) {
				compound.setDouble(tag.getPath(), (double) tag.getValue());
				continue;
			}
			if (tag.getValue() instanceof String) {
				compound.setString(tag.getPath(), (String) tag.getValue());
				continue;
			}
			if (tag.getValue() instanceof Integer) {
				compound.setInt(tag.getPath(), (int) tag.getValue());
				continue;
			}
		}

		nmsi.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsi);
	}

	@Override
	public ItemStack removeTag(ItemStack item, String... paths) {
		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		if (!nmsi.hasTag())
			return item;

		NBTTagCompound compound = nmsi.getTag();
		for (String path : paths)
			compound.remove(path);
		nmsi.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsi);
	}

	@Override
	public ItemStack addAttribute(ItemStack item, List<Attribute> attributes) {
		net.minecraft.server.v1_8_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsStack.getTag();
		if (compound == null) {
			nmsStack.setTag(new NBTTagCompound());
			compound = nmsStack.getTag();
		}

		/*
		 * when the item has temporarily saved its attribute modifiers in
		 * another tag, the attributes must be added there.
		 */
		String tagPath = compound.hasKey("MMOItems_SavedAttributeModifiers") ? "MMOItems_SavedAttributeModifiers" : "AttributeModifiers";

		/*
		 * attributes can stack. two attributes may have the same attribute
		 * name, amount, etc but their uuid's must differ.
		 */
		NBTTagList modifiers = compound.hasKey(tagPath) ? compound.getList(tagPath, 10) : new NBTTagList();
		for (Attribute attribute : attributes) {
			NBTTagCompound added = new NBTTagCompound();
			added.set("AttributeName", new NBTTagString("generic." + attribute.getName()));
			added.set("Name", new NBTTagString("generic." + attribute.getName()));
			added.set("Amount", new NBTTagFloat(attribute.getValue()));
			added.set("Operation", new NBTTagInt(0));
			added.set("UUIDLeast", new NBTTagInt(UUID.randomUUID().hashCode()));
			added.set("UUIDMost", new NBTTagInt(UUID.randomUUID().hashCode()));
			added.set("Slot", new NBTTagString(attribute.getSlot()));
			modifiers.add(added);
		}

		compound.set(tagPath, modifiers);
		nmsStack.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getTags(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return new HashSet<String>();

		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.c();
	}

	@Override
	public boolean getBooleanTag(ItemStack item, String path) {
		if (item == null || item.getType() == Material.AIR)
			return false;

		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.getBoolean(path);
	}

	@Override
	public double getDoubleTag(ItemStack item, String path) {
		if (item == null || item.getType() == Material.AIR)
			return 0;

		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.getDouble(path);
	}

	@Override
	public String getStringTag(ItemStack item, String path) {
		if (item == null || item.getType() == Material.AIR)
			return "";

		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.getString(path);
	}

	@Override
	public Map<String, Double> requestDoubleTags(ItemStack item, Map<String, Double> map) {
		if (item == null || item.getType() == Material.AIR)
			return map;

		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		for (String s : map.keySet())
			map.put(s, map.get(s) + compound.getDouble("MMOITEMS_" + s));

		return map;
	}

	@Override
	public double[] getBoundingBox(Entity entity, double offset) {
		net.minecraft.server.v1_8_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		AxisAlignedBB boundingBox = nmsEntity.getBoundingBox();
		return new double[] { boundingBox.a, boundingBox.b, boundingBox.c, boundingBox.d, boundingBox.e, boundingBox.f };
	}

	@Override
	public void damageEntity(Player p, LivingEntity t, double value) {
		((CraftLivingEntity) t).getHandle().damageEntity(DamageSource.playerAttack((EntityHuman) ((CraftPlayer) p).getHandle()), (float) value);
	}

	@Override
	public void sendJson(Player p, String msg) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(msg));
		PlayerConnection co = ((CraftPlayer) p).getHandle().playerConnection;
		co.sendPacket(packet);
	}

	@Override
	public void sendTitle(Player player, String msgTitle, String msgSubTitle, int fadeIn, int ticks, int fadeOut) {
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + msgTitle + "\"}");
		IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + msgSubTitle + "\"}");

		PacketPlayOutTitle p = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle p2 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle p3 = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, ticks, fadeOut);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(p);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(p2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(p3);
	}

	@Override
	public void sendActionBar(Player player, String message) {
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
	}

	@Override
	public ItemStack saveAttributes(ItemStack item) {
		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		compound.set("MMOItems_SavedAttributeModifiers", compound.getList("AttributeModifiers", 10));
		compound.set("AttributeModifiers", new NBTTagList());
		nmsi.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsi);
	}

	@Override
	public ItemStack loadAttributes(ItemStack item) {
		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		compound.set("AttributeModifiers", compound.getList("MMOItems_SavedAttributeModifiers", 10));
		compound.remove("MMOItems_SavedAttributeModifiers");
		nmsi.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsi);
	}

	@Override
	public boolean hasTag(ItemStack item, String path) {
		net.minecraft.server.v1_8_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.hasKey(path);
	}

	@Override
	public int getNextContainerId(Player player) {
		return ((CraftPlayer) player).getHandle().nextContainerCounter();
	}

	@Override
	public void handleInventoryCloseEvent(Player player) {
		CraftEventFactory.handleInventoryCloseEvent(((CraftPlayer) player).getHandle());
	}

	@Override
	public void sendPacketOpenWindow(Player player, int containerId) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage(Blocks.ANVIL.a() + ".name")));
	}

	@Override
	public void sendPacketCloseWindow(Player player, int containerId) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
	}

	@Override
	public void setActiveContainerDefault(Player player) {
		((CraftPlayer) player).getHandle().activeContainer = ((CraftPlayer) player).getHandle().defaultContainer;
	}

	@Override
	public void setActiveContainer(Player player, Object container) {
		((CraftPlayer) player).getHandle().activeContainer = (Container) container;
	}

	@Override
	public void setActiveContainerId(Object container, int containerId) {
		((Container) container).windowId = containerId;
	}

	@Override
	public void addActiveContainerSlotListener(Object container, Player player) {
		((Container) container).addSlotListener(((CraftPlayer) player).getHandle());
	}

	@Override
	public Inventory toBukkitInventory(Object container) {
		return ((Container) container).getBukkitView().getTopInventory();
	}

	@Override
	public Object newContainerAnvil(Player player) {
		return new AnvilContainer(((CraftPlayer) player).getHandle());
	}

	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entityhuman) {
			super(entityhuman.inventory, entityhuman.world, new BlockPosition(0, 0, 0), entityhuman);
			this.checkReachable = false;
		}
	}
}
