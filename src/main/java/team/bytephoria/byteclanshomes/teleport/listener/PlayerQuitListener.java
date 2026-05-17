package team.bytephoria.byteclanshomes.teleport.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclanshomes.teleport.TeleportCountdown;

public final class PlayerQuitListener implements Listener {

    private final TeleportCountdown teleportCountdown;
    public PlayerQuitListener(final @NotNull TeleportCountdown teleportCountdown) {
        this.teleportCountdown = teleportCountdown;
    }

    @EventHandler
    public void onPlayerQuitEvent(final @NotNull PlayerQuitEvent quitEvent) {
        this.teleportCountdown.unregister(quitEvent.getPlayer().getUniqueId());
    }

}
