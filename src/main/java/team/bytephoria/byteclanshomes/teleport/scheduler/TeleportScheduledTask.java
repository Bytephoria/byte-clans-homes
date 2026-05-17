package team.bytephoria.byteclanshomes.teleport.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclanshomes.teleport.TeleportEntry;
import team.bytephoria.byteclanshomes.teleport.TeleportCountdown;

import java.util.function.Consumer;

public final class TeleportScheduledTask implements Consumer<BukkitTask> {

    private final TeleportCountdown teleportCountdown;
    public TeleportScheduledTask(final @NotNull TeleportCountdown teleportCountdown) {
        this.teleportCountdown = teleportCountdown;
    }

    @Override
    public void accept(final @NotNull BukkitTask bukkitTask) {
        for (final TeleportEntry teleportEntry : this.teleportCountdown.entries()) {
            this.teleportCountdown.ticking(teleportEntry);
        }
    }
}
