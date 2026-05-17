package team.bytephoria.byteclanshomes.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclanshomes.PaperPlugin;

public final class InternalCommands {

    private final PaperPlugin paperPlugin;
    public InternalCommands(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @Command("byteclanshome reload")
    @Permission("byteclanshome.command.reload")
    @CommandDescription("Reload the configuration.")
    public void reload(final @NotNull Player player) {
        this.paperPlugin.reload();
        player.sendMessage(Component.text("The configuration was reloaded successfully!", NamedTextColor.GREEN));
    }

}
