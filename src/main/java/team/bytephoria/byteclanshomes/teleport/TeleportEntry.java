package team.bytephoria.byteclanshomes.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public final class TeleportEntry {

    private final UUID userId;
    private WeakReference<Player> playerWeakReference;

    private final String homeName;
    private final boolean defaultHome;

    private final BlockVector initialPosition;
    private final Location location;

    private int currentCountdown;

    public TeleportEntry(
            final @NotNull UUID userId,
            final @NotNull String homeName,
            final boolean defaultHome,
            final @NotNull Location initialPosition,
            final @NotNull Location location,
            final int initialCountdown
    ) {
        this.userId = userId;
        this.playerWeakReference = new WeakReference<>(null);

        this.homeName = homeName;
        this.defaultHome = defaultHome;

        this.initialPosition = new BlockVector(
                initialPosition.getBlockX(),
                initialPosition.getBlockY(),
                initialPosition.getBlockZ()
        );

        this.location = location;
        this.currentCountdown = initialCountdown;
    }

    public @NotNull Optional<Player> getBukkitPlayer() {
        final Player player = this.playerWeakReference.get();
        if (player == null) {
            final Player findPlayer = Bukkit.getPlayer(this.userId);
            if (findPlayer != null) {
                this.playerWeakReference = new WeakReference<>(findPlayer);
                return Optional.of(findPlayer);
            }

            return Optional.empty();
        }

        return Optional.of(player);
    }

    public String homeName() {
        return this.homeName;
    }

    public boolean defaultHome() {
        return this.defaultHome;
    }

    public BlockVector initialPosition() {
        return this.initialPosition;
    }

    public int getAndDecreaseCountdown() {
        return this.currentCountdown--;
    }

    public UUID userId() {
        return this.userId;
    }

    public Location location() {
        return this.location;
    }
}
