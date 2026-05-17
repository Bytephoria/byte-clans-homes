package team.bytephoria.byteclanshomes.util;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public final class BukkitUtil {

    private BukkitUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static boolean comparePosition(final @NotNull BlockVector first, final @NotNull Location second) {
        return first.getBlockX() == second.getBlockX() && first.getBlockY() == second.getBlockY() && first.getBlockZ() == second.getBlockZ();
    }

}
