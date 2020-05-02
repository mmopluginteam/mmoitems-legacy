package net.Indyuce.mmoitems.api;

import java.util.HashMap;
import java.util.Map;

public class StringValue {
	private String name;
	private double value, extraValue;

	public StringValue(String name, double value) {
		this(name, value, -1);
	}

	public StringValue(String name, double value, double extraValue) {
		this.name = name;
		this.value = value;
		this.extraValue = extraValue;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public boolean hasExtraValue() {
		return extraValue != -1;
	}

	public double getExtraValue() {
		return extraValue;
	}

	@Deprecated
	public static Map<String, Double> readFromArray(StringValue... array) {
		Map<String, Double> map = new HashMap<String, Double>();
		for (StringValue mod : array)
			map.put(mod.getName(), mod.getValue());
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StringValue))
			return false;

		StringValue couple = (StringValue) obj;
		return couple.getName() == name && couple.getValue() == value && couple.getExtraValue() == extraValue;
	}
}
