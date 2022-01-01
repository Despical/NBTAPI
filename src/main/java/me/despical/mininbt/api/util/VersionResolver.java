package me.despical.mininbt.api.util;

/**
 * An utility class for getting server's version in split NSM format.
 * This class is taken from Commons library by Despical.
 *
 * @author Despical
 * @since 1.0.0
 * <p>
 * Created at 30.05.2020
 */
public class VersionResolver {

	public static final String VERSION = parseVersion();
	public static final int VER = Integer.parseInt(VERSION.substring(1).split("_")[1]);

	private VersionResolver() {
	}

	private static String parseVersion() {
		String found = null;

		for (Package pack : Package.getPackages()) {
			if (pack.getName().startsWith("org.bukkit.craftbukkit.v")) {
				found = pack.getName().split("\\.")[3];
				break;
			}
		}

		if (found == null) {
			throw new IllegalArgumentException("Failed to parse server version. Could not find any package starting with name: 'org.bukkit.craftbukkit.v'");
		}

		return found;
	}

	/**
	 * Checks whether the server version is equal or greater than the given version.
	 *
	 * @param version the version to compare the server version with.
	 * @return true if the version is equal or newer, otherwise false.
	 */
	public static boolean supports(int version) {
		return VER >= version;
	}
}