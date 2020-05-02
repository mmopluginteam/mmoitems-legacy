package net.Indyuce.mmoitems.version.nms;

public class Attribute {
	private String name;
	private float value;
	private String slot;

	public Attribute(String name, double value, String slot) {
		this.name = name;
		this.value = (float) value;
		this.slot = slot;
	}

	public String getName() {
		return name;
	}

	public float getValue() {
		return value;
	}

	public String getSlot() {
		return slot;
	}
}
