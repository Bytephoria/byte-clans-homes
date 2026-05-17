package team.bytephoria.byteclanshomes.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclanshomes.DataKeys;
import team.bytephoria.byteclanshomes.PaperPlugin;
import team.bytephoria.byteclanshomes.data.ConfigurationData;
import team.bytephoria.byteclanshomes.util.ByteClansUtil;
import team.bytephoria.datacontainer.api.DataContainer;

import java.util.*;

public final class ClanHomeCommands {

    private final PaperPlugin paperPlugin;
    public ClanHomeCommands(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    /**
     *
     * - /clan home
     * - /clan home tp <home_name>
     * - /clan home default <home_name>
     * - /clan home set <home_name>
     * - /clan home delete <home_name>
     * - /clan home rename <current_name< <new_name>
     *
     */

    @Command("clan home list")
    public void homeList(final @NotNull Player player) {
        final Collection<String> homes = this.clanHomes(player);

        if (ByteClans.getAPI().getMember(player.getUniqueId()).isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        if (homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        final String homesString = String.join("<gray>, <green>", homes);
        this.paperPlugin.messenger()
                .sendPathMessage(
                        player,
                        "list.homes",
                        Map.of("homes", homesString)
                );
    }

    @Command("clan home")
    public void teleportDefaultHome(final @NotNull Player player) {
        final Optional<Clan> clanOptional = ByteClansUtil.getClanByMember(player);
        if (clanOptional.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanOptional.get();
        final DataContainer dataContainer = clan.dataContainer();

        final String defaultHome = dataContainer.get(DataKeys.DEFAULT_HOME);
        if (defaultHome == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-default-home");
            return;
        }

        final Map<String, Location> homes = dataContainer.getOrDefault(DataKeys.HOMES, Collections.emptyMap());
        if (homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        final Location location = homes.get(defaultHome);
        if (location == null) {
            return;
        }

        final int teleportCountdown = this.paperPlugin.configurationData().teleportCountdown();
        this.paperPlugin.teleportCountdown().register(player, defaultHome, true, location, teleportCountdown);
    }

    @Command("clan home default <home>")
    public void setDefaultHome(
            final @NotNull Player player,
            final @NotNull @Argument(value = "home", suggestions = "clan_homes") String home
    ) {
        final Optional<Clan> clanOptional = ByteClansUtil.getClanByMember(player);
        if (clanOptional.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanOptional.get();
        final DataContainer container = clan.dataContainer();
        final Map<String, Location> homes = container.get(DataKeys.HOMES);

        if (homes == null || homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        container.set(DataKeys.DEFAULT_HOME, home);
        ByteClans.getAPI().dataContainerManager().save(clan);

        this.paperPlugin.messenger().sendPathMessage(player, "default-home.set", Map.of("home", home));
    }

    @Command("clan home tp <home>")
    public void teleportHome(
            final @NotNull Player player,
            final @NotNull @Argument(value = "home", suggestions = "clan_homes") String home
    ) {

        final ClanMember clanMember = ByteClans.getAPI().getMemberOrNull(player.getUniqueId());
        if (clanMember == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanMember.clan();
        final DataContainer container = clan.dataContainer();
        final Map<String, Location> homes = container.get(DataKeys.HOMES);

        if (homes == null || homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        final Location location = homes.get(home);

        if (location == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.not-found", Map.of("home", home));
            return;
        }

        final int teleportCountdown = this.paperPlugin.configurationData().teleportCountdown();
        this.paperPlugin.teleportCountdown().register(player, home, false, location, teleportCountdown);
    }

    @Command("clan home set <home>")
    public void setHome(
            final @NotNull Player player,
            final @NotNull @Argument("home") String home
    ) {

        final ClanMember clanMember = ByteClans.getAPI().getMemberOrNull(player.getUniqueId());
        if (clanMember == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanMember.clan();
        final DataContainer container = clan.dataContainer();

        final ConfigurationData configurationData = this.paperPlugin.configurationData();
        final Map<String, Location> homes = container.getOrDefault(DataKeys.HOMES, new HashMap<>(configurationData.homesPerClan()));

        if (homes.containsKey(home)) {
            this.paperPlugin.messenger()
                    .sendPathMessage(player,
                            "homes.already-exists",
                            Map.of("home", home)
                    );

            return;
        }

        if (homes.size() >= configurationData.homesPerClan()) {
            this.paperPlugin.messenger()
                    .sendPathMessage(player,
                            "homes.limit-reached",
                            Map.of("limit", String.valueOf(configurationData.homesPerClan()))
                    );

            return;
        }

        homes.put(home, player.getLocation());
        container.set(DataKeys.HOMES, homes);

        if (configurationData.autoDefault() && homes.size() == 1) {
            container.set(DataKeys.DEFAULT_HOME, home);

            this.paperPlugin.messenger().sendPathMessage(player, "homes.auto-default-set");
        }

        ByteClans.getAPI()
                .dataContainerManager()
                .save(clan)
                .thenAccept(ignored ->
                        this.paperPlugin.runMainThread(() ->
                                this.paperPlugin.messenger()
                                        .sendPathMessage(player,
                                                "homes.created",
                                                Map.of("home", home)
                                        )
                        ));
    }

    @Command("clan home delete <home>")
    public void deleteHome(
            final @NotNull Player player,
            final @NotNull @Argument(value = "home", suggestions = "clan_homes") String home
    ) {

        final ClanMember clanMember = ByteClans.getAPI().getMemberOrNull(player.getUniqueId());
        if (clanMember == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanMember.clan();
        final DataContainer container = clan.dataContainer();
        final Map<String, Location> homes = container.get(DataKeys.HOMES);

        if (homes == null || homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        if (homes.remove(home) == null) {
            this.paperPlugin.messenger()
                    .sendPathMessage(player,
                            "homes.not-found",
                            Map.of("home", home)
                    );

            return;
        }

        final String defaultHome = container.get(DataKeys.DEFAULT_HOME);
        if (home.equalsIgnoreCase(defaultHome)) {
            container.remove(DataKeys.DEFAULT_HOME);
            this.paperPlugin.messenger().sendPathMessage(player, "default-home.removed");
        }

        if (homes.isEmpty()) {
            container.remove(DataKeys.HOMES);
        } else {
            container.set(DataKeys.HOMES, homes);
        }

        ByteClans.getAPI()
                .dataContainerManager()
                .save(clan)
                .thenAccept(ignored ->
                        this.paperPlugin.runMainThread(() ->
                                this.paperPlugin.messenger()
                                        .sendPathMessage(player,
                                                "homes.deleted",
                                                Map.of("home", home))
                        ));
    }

    @Command("clan home rename <current> <new>")
    public void renameHome(
            final @NotNull Player player,
            final @NotNull @Argument(value = "current", suggestions = "clan_homes") String current,
            final @NotNull @Argument("new") String newName
    ) {

        final ClanMember clanMember = ByteClans.getAPI().getMemberOrNull(player.getUniqueId());
        if (clanMember == null) {
            this.paperPlugin.messenger().sendPathMessage(player, "general.no-clan");
            return;
        }

        final Clan clan = clanMember.clan();
        final DataContainer container = clan.dataContainer();
        final Map<String, Location> homes = container.get(DataKeys.HOMES);

        if (homes == null || homes.isEmpty()) {
            this.paperPlugin.messenger().sendPathMessage(player, "homes.no-homes");
            return;
        }

        final Location location = homes.remove(current);
        if (location == null) {
            this.paperPlugin.messenger()
                    .sendPathMessage(
                            player,
                            "homes.not-found",
                            Map.of("home", current)
                    );

            return;
        }

        if (homes.containsKey(newName)) {
            homes.put(current, location);

            this.paperPlugin.messenger()
                    .sendPathMessage(
                            player,
                            "homes.already-exists",
                            Map.of("home", newName)
                    );
            return;
        }

        homes.put(newName, location);

        final String defaultHome = container.get(DataKeys.DEFAULT_HOME);
        if (defaultHome != null && defaultHome.equalsIgnoreCase(current)) {
            container.set(DataKeys.DEFAULT_HOME, newName);
        }

        container.set(DataKeys.HOMES, homes);
        ByteClans.getAPI()
                .dataContainerManager()
                .save(clan)
                .thenAccept(ignored ->
                        this.paperPlugin.runMainThread(() ->
                                this.paperPlugin.messenger()
                                        .sendPathMessage(
                                                player,
                                                "homes.renamed",
                                                Map.of(
                                                        "old", current,
                                                        "new", newName
                                                )
                                        )
                        ));
    }

    private Collection<String> clanHomes(final @NotNull Player player) {
        final Optional<ClanMember> clanMemberOptional = ByteClans.getAPI().getMember(player.getUniqueId());
        if (clanMemberOptional.isEmpty()) {
            return Collections.emptyList();
        }

        return clanMemberOptional.get()
                .clan()
                .dataContainer()
                .getOrDefault(DataKeys.HOMES, Collections.emptyMap())
                .keySet();
    }

    @Suggestions("clan_homes")
    public @NotNull @Unmodifiable List<Suggestion> clanHomes(final @NotNull CommandContext<Player> commandContext) {
        return this.clanHomes(commandContext.sender())
                .stream()
                .map(Suggestion::suggestion)
                .toList();
    }

}
