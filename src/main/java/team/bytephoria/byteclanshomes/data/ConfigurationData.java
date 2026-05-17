package team.bytephoria.byteclanshomes.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public record ConfigurationData(
        int homesPerClan,
        boolean autoDefault,
        int teleportCountdown
) {

    public static @NotNull ConfigurationData fromFileConfiguration(final @NotNull FileConfiguration fileConfiguration) {
        final int homePerClan = fileConfiguration.getInt("settings.limit", 1);
        final boolean autoDefault = fileConfiguration.getBoolean("settings.auto-default", true);
        final int teleportCountdown = fileConfiguration.getInt("settings.teleport-countdown", 1);

        return new ConfigurationData(homePerClan, autoDefault, teleportCountdown);
    }

}
