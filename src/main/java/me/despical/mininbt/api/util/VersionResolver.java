package me.despical.mininbt.api.util;

import org.bukkit.Bukkit;

import static me.despical.mininbt.api.util.VersionResolver.ServerVersion.*;

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

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	/**
	 * Current version of server.
	 */
	public static final ServerVersion CURRENT_VERSION;

	static {
		CURRENT_VERSION = resolveVersion();
	}

	private VersionResolver() {
	}

	/**
	 * Get the server's version.
	 *
	 * @return version of the server in split NMS format enum
	 */
	private static ServerVersion resolveVersion() {
		try {
			return ServerVersion.valueOf(VERSION);
		} catch (IllegalArgumentException exception) {
			return OTHER;
		}
	}

	public static boolean isRemappedVersion() {
		return isCurrentEqualOrHigher(v1_17_R1);
	}

	/**
	 * Checks if current version equals or higher than the given version.
	 *
	 * @param version given version
	 * @return true if current version equals or higher than given one
	 */
	public static boolean isCurrentEqualOrHigher(ServerVersion version) {
		return CURRENT_VERSION.version >= version.version;
	}

	/**
	 * Enum values of the each Minecraft version in NMS format.
	 */
	public enum ServerVersion {
		v1_8_R1, v1_8_R2, v1_8_R3, v1_9_R1, v1_9_R2, v1_10_R1, v1_11_R1, v1_12_R1,
		v1_13_R1, v1_13_R2, v1_14_R1, v1_15_R1, v1_16_R1, v1_16_R2, v1_16_R3, v1_17_R1,
		v1_18_R1, OTHER;

		private final int version;

		ServerVersion() {
			this.version = Integer.parseInt(name().replaceAll("[v_R]", ""));
		}

		int versionAsInt() {
			return version;
		}
	}
}