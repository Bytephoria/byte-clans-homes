package team.bytephoria.byteclanshomes.messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Map;

public final class Messenger {

    private final ConfigurationNode configurationNode;
    public Messenger(final @NotNull ConfigurationNode configurationNode) {
        this.configurationNode = configurationNode;
    }

    public void sendPathMessage(
            final @NotNull Player player,
            final @NotNull String path,
            final @NotNull Map<String, String> placeholders
    ) {

        final String message = this.resolvePath(path);
        if (message == null || message.isBlank()) {
            return;
        }

        String replacing = message;
        for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            replacing = replacing.replace("{" + placeholder.getKey() + "}", placeholder.getValue());
        }

        this.sendMessage(player, replacing);
    }

    public void sendPathMessage(final @NotNull Player player, final @NotNull String path) {
        final String message = this.resolvePath(path);
        if (message == null || message.isBlank()) {
            return;
        }

        this.sendMessage(player, message);
    }

    private void sendMessage(final @NotNull Player player, final @Nullable String message) {
        if (message != null && !message.isBlank()) {
            final Component component = MiniMessage.miniMessage().deserialize(message);
            player.sendMessage(component);
        }
    }

    private String resolvePath(final @NotNull String path) {
        final String[] paths = path.split("\\.");
        return this.configurationNode.node((Object[]) paths).getString();
    }

}
