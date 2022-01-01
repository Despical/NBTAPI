package me.despical.mininbt.api;

import me.despical.mininbt.api.util.VersionResolver;
import org.bukkit.inventory.ItemStack;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NBT {

	private static final String version = "net.minecraft.server." + VersionResolver.VERSION, cbVersion = "org.bukkit.craftbukkit." + VersionResolver.VERSION;
	public static Class<?> tagCompoundClass, nbtBaseClass, nmsItemstackClass, craftItemstackClass, mojangsonParserClass;

	static {
		try {
			if (VersionResolver.supports(17)) {
				tagCompoundClass = Class.forName("net.minecraft.nbt.NBTTagCompound");
				nbtBaseClass = Class.forName("net.minecraft.nbt.NBTBase");
				nmsItemstackClass = Class.forName("net.minecraft.world.item.ItemStack");
				mojangsonParserClass = Class.forName("net.minecraft.nbt.MojangsonParser");
			} else {
				tagCompoundClass = Class.forName(version + ".NBTTagCompound");
				nbtBaseClass = Class.forName(version + ".NBTBase");
				nmsItemstackClass = Class.forName(version + ".ItemStack");
				mojangsonParserClass = Class.forName(version + ".MojangsonParser");
			}

			craftItemstackClass = Class.forName(cbVersion + ".inventory.CraftItemStack");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private final Object tagCompound;

	public NBT() {
		this(null);
	}

	public NBT(Object tagCompound) {
		Object toSet = tagCompound;

		if (tagCompound == null) {
			try {
				toSet = tagCompoundClass.newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		this.tagCompound = toSet;
	}

	public static NBT get(ItemStack item) {
		try {
			Method asNMSCopy = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
			asNMSCopy.setAccessible(true);

			Object nmsStack = asNMSCopy.invoke(null, item);
			asNMSCopy.setAccessible(false);

			Method getCompound = nmsItemstackClass.getMethod("getTag");
			getCompound.setAccessible(true);

			Object nbtCompound = getCompound.invoke(nmsStack);
			getCompound.setAccessible(false);

			return new NBT(nbtCompound);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Object getTagCompound() {
		return tagCompound;
	}

	public NBT getCompoundNullable(String key) {
		try {
			return getCompoundThrows(key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public NBT getCompoundThrows(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getCompound = tagCompoundClass.getMethod("getCompound", String.class);
		getCompound.setAccessible(true);

		Object compoundValue = getCompound.invoke(this.tagCompound, key);
		getCompound.setAccessible(false);
		return compoundValue == null ? null : new NBT(compoundValue);
	}

	public NBT getCompound(String key) {
		return getCompoundNullable(key) == null ? null : new NBT();
	}

	public NBTList getListNullable(String key) {
		try {
			return getListThrows(key);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public NBTList getListThrows(String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method get = tagCompoundClass.getMethod("get", String.class);
		get.setAccessible(true);

		Object nbtListValue = get.invoke(this.tagCompound, key);
		get.setAccessible(false);
		return nbtListValue == null ? null : new NBTList(nbtListValue);
	}

	public void setObject(String key, Object o) {
		if (o instanceof String) {
			setString(key, (String) o);
		} else if (o instanceof Integer) {
			setInt(key, (Integer) o);
		} else if (o instanceof Double) {
			setDouble(key, (Double) o);
		} else if (o instanceof Long) {
			setLong(key, (Long) o);
		} else if (o instanceof List) {
			NBTList list = new NBTList();

			for (Object e : (List) o) {
				if (e instanceof Map) {
					NBT mapNBT = new NBT();

					for (Object k : ((Map) e).keySet()) {
						if (k instanceof String) {
							Object v = ((Map) e).get(k);
							mapNBT.setObject((String) k, v);
						}
					}

					list.add(mapNBT);
				} else {
					list.addGeneric(e);
				}
			}

			set(key, list);
		}
	}

	public NBTList getList(String key) {
		NBTList nbt = getListNullable(key);
		return nbt == null ? null : new NBTList();
	}

	public String getString(String key) {
		try {
			Method getString = tagCompoundClass.getMethod("getString", String.class);
			getString.setAccessible(true);

			Object stringValue = getString.invoke(this.tagCompound, key);
			getString.setAccessible(false);
			return stringValue instanceof String ? (String) stringValue : null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void setString(String key, String value) {
		try {
			Method setString = tagCompoundClass.getMethod("setString", String.class, String.class);
			setString.setAccessible(true);
			setString.invoke(this.tagCompound, key, value);
			setString.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Integer getInt(String key) {
		try {
			Method getInt = tagCompoundClass.getMethod("getInt", String.class);
			getInt.setAccessible(true);

			Object intValue = getInt.invoke(this.tagCompound, key);
			getInt.setAccessible(false);
			return intValue instanceof Integer ? (Integer) intValue : null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void setInt(String key, Integer value) {
		try {
			Method setInt = tagCompoundClass.getMethod("setInt", String.class, int.class);
			setInt.setAccessible(true);
			setInt.invoke(this.tagCompound, key, value);
			setInt.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setShort(String key, Short value) {
		try {
			Method setShort = tagCompoundClass.getMethod("setShort", String.class, short.class);
			setShort.setAccessible(true);
			setShort.invoke(this.tagCompound, key, value);
			setShort.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setBoolean(String key, Boolean value) {
		try {
			Method setBoolean = tagCompoundClass.getMethod("setBoolean", String.class, boolean.class);
			setBoolean.setAccessible(true);
			setBoolean.invoke(this.tagCompound, key, value);
			setBoolean.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setDouble(String key, Double value) {
		try {
			Method setDouble = tagCompoundClass.getMethod("setDouble", String.class, double.class);
			setDouble.setAccessible(true);
			setDouble.invoke(this.tagCompound, key, value);
			setDouble.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Long getLong(String key) {
		try {
			Method getLong = tagCompoundClass.getMethod("getLong", String.class);
			getLong.setAccessible(true);

			Object longValue = getLong.invoke(this.tagCompound, key);
			getLong.setAccessible(false);
			return longValue instanceof Long ? (Long) longValue : null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void setLong(String key, Long value) {
		try {
			Method m = tagCompoundClass.getMethod("setLong", String.class, long.class);
			m.setAccessible(true);
			m.invoke(this.tagCompound, key, value);
			m.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void set(String key, NBT value) {
		try {
			Method m = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
			m.setAccessible(true);
			m.invoke(this.tagCompound, key, value.tagCompound);
			m.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void set(String key, NBTList value) {
		try {
			Method setMethod = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
			setMethod.setAccessible(true);
			setMethod.invoke(this.tagCompound, key, value.getTagList());
			setMethod.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void set(String key, NBTBaseType type, Object value) {
		try {
			Object toPut = type.make(value);
			Method setMethod = tagCompoundClass.getMethod("set", String.class, nbtBaseClass);
			setMethod.setAccessible(true);
			setMethod.invoke(this.tagCompound, key, toPut);
			setMethod.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setStrings(Map<String, String> map) {
		try {
			Method m = tagCompoundClass.getMethod("setString", String.class, String.class);
			m.setAccessible(true);
			map.forEach((key, value) -> {
				try {
					m.invoke(this.tagCompound, key, value);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			m.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean hasKey(String key) {
		try {
			Method m = tagCompoundClass.getMethod("hasKey", String.class);
			m.setAccessible(true);
			Object o = m.invoke(this.tagCompound, key);
			m.setAccessible(false);

			return o instanceof Boolean && (Boolean) o;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public ItemStack apply(ItemStack item) {
		try {
			Method nmsGet = craftItemstackClass.getMethod("asNMSCopy", ItemStack.class);
			nmsGet.setAccessible(true);
			Object nmsStack = nmsGet.invoke(null, item);
			nmsGet.setAccessible(false);

			Method nbtSet = nmsItemstackClass.getMethod("setTag", tagCompoundClass);
			nbtSet.setAccessible(true);
			nbtSet.invoke(nmsStack, this.tagCompound);
			nbtSet.setAccessible(false);

			Method m = craftItemstackClass.getMethod("asBukkitCopy", nmsItemstackClass);
			m.setAccessible(true);
			Object o = m.invoke(null, nmsStack);
			m.setAccessible(false);

			return o instanceof ItemStack ? (ItemStack) o : null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Set<String> getKeys() {
		try {
			Map m = null;

			if (VersionResolver.supports(17)) {
				try {
					Field f = tagCompoundClass.getDeclaredField("x");
					f.setAccessible(true);
					m = (Map) f.get(tagCompound);
					f.setAccessible(false);
				} catch (Exception ignore) {

				}

				for (Field field : tagCompoundClass.getDeclaredFields()) {
					if (field.getType() == Map.class) {
						field.setAccessible(true);
						m = (Map) field.get(tagCompound);
						field.setAccessible(false);
						break;
					}
				}
			} else {
				Field field = tagCompoundClass.getDeclaredField("map");
				field.setAccessible(true);
				m = (Map) field.get(tagCompound);
				field.setAccessible(false);
			}

			return (Set<String>) m.keySet();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new HashSet<>();
		}
	}

	public String toString() {
		return "NBT(" + compoundString() + ")";
	}

	public String compoundString() {
		return Objects.toString(tagCompound);
	}
}