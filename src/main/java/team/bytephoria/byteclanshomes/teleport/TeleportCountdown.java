package team.bytephoria.byteclanshomes.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclanshomes.PaperPlugin;
import team.bytephoria.byteclanshomes.messenger.Messenger;
import team.bytephoria.byteclanshomes.teleport.scheduler.TeleportScheduledTask;
import team.bytephoria.byteclanshomes.util.BukkitUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TeleportCountdown {

    private final Map<UUID, TeleportEntry> teleports = new HashMap<>();

    private final PaperPlugin paperPlugin;
    private final Messenger messenger;

    public TeleportCountdown(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull Messenger messenger
    ) {
        this.paperPlugin = paperPlugin;
        this.messenger = messenger;
    }

    public void ticking(final @NotNull TeleportEntry teleportEntry) {
        final int currentCountdown = teleportEntry.getAndDecreaseCountdown();
        if (currentCountdown > 0) {
            teleportEntry.getBukkitPlayer()
                    .ifPresent(player -> {
                        final BlockVector initialPosition = teleportEntry.initialPosition();
                        final Location currentLocation = player.getLocation();

                        if (BukkitUtil.comparePosition(initialPosition, currentLocation)) {
                            this.messenger.sendPathMessage(player, "teleport.countdown", Map.of("seconds", Integer.toString(currentCountdown)));
                        } else {
                            this.cancelledTask(teleportEntry);
                        }

                    });

            return;
        }

        this.completedTask(teleportEntry);
    }

    public void completedTask(final @NotNull TeleportEntry teleportEntry) {
        this.unregister(teleportEntry.userId());
        teleportEntry.getBukkitPlayer().ifPresent(player ->
                player.teleportAsync(teleportEntry.location())
                        .thenAccept(aBoolean ->
                                this.paperPlugin.runMainThread(() -> {
                                    final String path = teleportEntry.defaultHome() ? "default" : "home";
                                    this.messenger.sendPathMessage(player, "teleport.success-" + path, Map.of("home", teleportEntry.homeName()));
                                })
                        )
        );
    }

    public void cancelledTask(final @NotNull TeleportEntry teleportEntry) {
        this.unregister(teleportEntry.userId());
        teleportEntry.getBukkitPlayer().ifPresent(player ->
                this.messenger.sendPathMessage(player, "teleport.cancelled")
        );

    }

    public void register(
            final @NotNull Player player,
            final @NotNull String home,
            final boolean defaultHome,
            final @NotNull Location location,
            final int teleportCountdown
    ) {
        final UUID userId = player.getUniqueId();
        this.teleports.put(userId, new TeleportEntry(userId, home, defaultHome, player.getLocation(), location, teleportCountdown));

        final String path = defaultHome ? "default" : "home";
        this.paperPlugin.messenger().sendPathMessage(player, "teleport.starting-" + path, Map.of("home", home));
        if (this.teleports.size() == 1) {
            this.paperPlugin.getServer().getScheduler().runTaskTimer(
                    this.paperPlugin,
                    new TeleportScheduledTask(this),
                    0,
                    20
            );
        }

    }

    public void unregister(final @NotNull UUID userId) {
        this.teleports.remove(userId);
        if (this.teleports.isEmpty()) {
            this.paperPlugin.getServer().getScheduler().cancelTasks(this.paperPlugin);
        }

    }

    public void clear() {
        this.teleports.clear();
        this.paperPlugin.getServer().getScheduler().cancelTasks(this.paperPlugin);
    }

    public @NotNull Collection<TeleportEntry> entries() {
        return this.teleports.values();
    }

}
