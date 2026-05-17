package team.bytephoria.byteclanshomes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.bytephoria.byteclans.bukkitapi.access.BukkitByteClans;
import team.bytephoria.byteclanshomes.command.ClanHomeCommands;
import team.bytephoria.byteclanshomes.command.InternalCommands;
import team.bytephoria.byteclanshomes.data.ConfigurationData;
import team.bytephoria.byteclanshomes.messenger.Messenger;
import team.bytephoria.byteclanshomes.teleport.TeleportCountdown;

import java.io.File;

public final class PaperPlugin extends JavaPlugin {

    private ConfigurationData configurationData;
    private ConfigurationNode messageConfigurationNode;
    private Messenger messenger;

    private TeleportCountdown teleportCountdown;

    @Override
    public void onEnable() {
        this.configurationData = this.createConfigurationData();

        final AnnotationParser<Player> annotationParser = BukkitByteClans.getAPI().annotationParser();

        annotationParser.parse(
                new InternalCommands(this),
                new ClanHomeCommands(this)
        );

        this.messageConfigurationNode = this.loadConfigurationNode("messages.yml");
        this.messenger = new Messenger(this.messageConfigurationNode);
        this.teleportCountdown = new TeleportCountdown(this, this.messenger);
    }

    @Override
    public void onDisable() {
        this.configurationData = null;
        this.messenger = null;
        this.messageConfigurationNode = null;
        this.teleportCountdown = null;
    }

    public void reload() {
        this.configurationData = this.createConfigurationData();
        this.teleportCountdown.clear();
    }

    public void runMainThread(final @NotNull Runnable runnable) {
        this.getServer().getScheduler().runTask(this, runnable);
    }

    public Messenger messenger() {
        return this.messenger;
    }

    public TeleportCountdown teleportCountdown() {
        return this.teleportCountdown;
    }

    public @NotNull ConfigurationNode loadConfigurationNode(final @NotNull String fileName) {
        final File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) {
            this.saveResource(fileName, false);
        }

        final YamlConfigurationLoader yamlConfigurationLoader = YamlConfigurationLoader.builder()
                .file(file)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .defaultOptions(ConfigurationOptions.defaults())
                .build();

        try {
            return yamlConfigurationLoader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

    }

    private @NotNull ConfigurationData createConfigurationData() {
        final FileConfiguration fileConfiguration = this.loadFileConfiguration();
        return ConfigurationData.fromFileConfiguration(fileConfiguration);
    }

    private @NotNull FileConfiguration loadFileConfiguration() {
        final File configurationFile = new File(this.getDataFolder(), "config.yml");
        if (!configurationFile.exists()) {
            this.saveResource("config.yml", false);
            this.getSLF4JLogger().info("Default configuration file was created!");
        }

        return YamlConfiguration.loadConfiguration(configurationFile);
    }

    public ConfigurationData configurationData() {
        return this.configurationData;
    }
}
