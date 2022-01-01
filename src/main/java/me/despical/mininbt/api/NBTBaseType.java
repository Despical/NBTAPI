package me.despical.mininbt.api;

import me.despical.mininbt.api.util.VersionResolver;

import java.lang.reflect.Constructor;

public enum NBTBaseType {

	BYTE (byte.class, "Byte"),
	BYTE_ARRAY (byte[].class, "ByteArray"),
	DOUBLE (double.class, "Double"),
	FLOAT (float.class, "Float"),
	INT (int.class, "Int"),
	INT_ARRAY (int[].class, "IntArray"),
	LONG (long.class, "Long"),
	SHORT (short.class, "Short"),
	STRING (String.class, "String");

	private String name;
	private Class<?> innerClazz, nbtBaseClass;

	<T> NBTBaseType(Class<T> innerClazz, String name) {
		try {
			this.name = name;
			this.innerClazz = innerClazz;
			this.nbtBaseClass = VersionResolver.supports(17) ? Class.forName("net.minecraft.nbt.NBTBase") : Class.forName("net.minecraft.server." + VersionResolver.VERSION + ".NBTTag" + name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static NBTBaseType get(Class<?> clazz) {
		for (NBTBaseType type : values()) {
			if (type.innerClazz.equals(clazz)) {
				return type;
			}
		}

		if (clazz == Float.class) {
			return FLOAT;
		} else if (clazz == Integer.class) {
			return INT;
		}

		return null;
	}

	public static NBTBaseType getByClass(Class<?> clazz) {
		for (NBTBaseType type : values()) {
			if (type.innerClazz.equals(clazz)) {
				return type;
			}
		}

		return null;
	}

	public static NBTBaseType getByNBTBaseClass(Class<?> clazz) {
		for (NBTBaseType type : values()) {
			if (type.nbtBaseClass.equals(clazz)) {
				return type;
			}
		}

		return null;
	}

	public static NBTBaseType getFromObject(Object o) {
		NBTBaseType type = getByClass(o.getClass());

		if (type != null) {
			return type;
		}

		try {
			return getByClass((Class) o.getClass().getField("TYPE").get(null));
		} catch (Exception ignored) {

		}

		return null;
	}

	public String getName() {
		return this.name;
	}

	public <T> Object make(T value) {
		try {
			Constructor innerConst = nbtBaseClass.getConstructor(innerClazz);
			innerConst.setAccessible(true);

			Object instance = innerConst.newInstance(value);
			innerConst.setAccessible(false);

			return instance;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
