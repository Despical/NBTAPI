package me.despical.mininbt.api;

import me.despical.mininbt.api.util.VersionResolver;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class NBTList {

	private static final String version = "net.minecraft.server." + VersionResolver.VERSION;
	private static final List<Method> getMethods = new ArrayList<>();
	private static Class<?> tagListClass;

	static {
		try {
			if (VersionResolver.supports(17)) {
				tagListClass = Class.forName("net.minecraft.nbt.NBTTagList");
			} else {
				tagListClass = Class.forName(version + ".NBTTagList");
			}

			for (Method method : tagListClass.getDeclaredMethods()) {
				if (method.getReturnType().equals(Void.TYPE) || method.getParameterCount() != 1 || !method.getParameterTypes()[0].equals(int.class)) {
					continue;
				}

				if (method.getName().equalsIgnoreCase("remove")) {
					continue;
				}

				getMethods.add(method);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private final Object tagList;

	public NBTList() {
		this(null);
	}

	public NBTList(Object tagCompound) {
		Object toSet = tagCompound;

		if (tagCompound == null) {
			try {
				toSet = tagListClass.newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		this.tagList = toSet;
	}

	public Object getTagList() {
		return tagList;
	}

	public boolean isEmpty() {
		try {
			Method isEmpty = tagListClass.getMethod("isEmpty");
			isEmpty.setAccessible(true);

			Object empty = isEmpty.invoke(this.tagList);
			isEmpty.setAccessible(false);
			return empty instanceof Boolean ? (Boolean) empty : true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return true;
		}
	}

	public int size() {
		try {
			Method sizeMethod = tagListClass.getMethod("size");
			sizeMethod.setAccessible(true);

			Object size = sizeMethod.invoke(this.tagList);
			sizeMethod.setAccessible(false);
			return (Integer) size;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public List<Object> values() {
		try {
			List<Object> res = new ArrayList<>();

			for (int i = 0; i < size(); i++) {
				Object o = null;

				for (Method method : getMethods) {
					method.setAccessible(true);
					o = method.invoke(this.tagList, i);
					method.setAccessible(false);

					if (o != null) {
						if (o instanceof Number && ((Number) o).intValue() == 0) {
							continue;
						}

						if (o instanceof String && ((String) o).length() == 0) {
							continue;
						}

						if (NBT.tagCompoundClass.isInstance(o)) {
							NBT nbt = new NBT(o);

							if (nbt.getKeys().isEmpty()) {
								continue;
							}
						}

						if (tagListClass.isInstance(o)) {
							NBTList s = new NBTList(o);

							if (s.isEmpty()) {
								continue;
							}
						}

						if (o.getClass().isArray()) {
							if (Array.getLength(o) == 0) {
								continue;
							}
						}

						break;
					}
				}

				if (NBT.tagCompoundClass.isInstance(o)) {
					o = new NBT(o);
				} else if (tagListClass.isInstance(o)) {
					o = new NBTList(o);
				}

				res.add(o);
			}

			return res;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void add(NBT value) {
		add(value.getTagCompound());
	}

	public <T> void add(NBTBaseType type, T value) {
		add(type.make(value));
	}

	public <T> void add(NBTBaseType type, T... values) {
		for (T value : values) {
			add(type, value);
		}
	}

	public <T> void addGeneric(T value) {
		if (value == null) {
			return;
		}

		NBTBaseType type = NBTBaseType.get(value.getClass());

		if (type == null) {
			return;
		}

		add(type, value);
	}

	public <T> void add(T... values) {
		NBTBaseType type = values.length > 0 ? NBTBaseType.getByClass(values[0].getClass()) : null;

		if (type != null) {
			add(type, values);
		}
	}

	private void add(Object nbt) {
		try {
			Method add = AbstractList.class.getMethod("add", Object.class);
			add.setAccessible(true);
			add.invoke(tagList, nbt);
			add.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}