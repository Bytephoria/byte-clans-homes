package team.bytephoria.byteclanshomes.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.access.ByteClans;

import java.util.Optional;

public final class ByteClansUtil {

    private ByteClansUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static Optional<Clan> getClanByMember(final @NotNull Player player) {
        final Optional<ClanMember> clanMemberOptional = ByteClans.getAPI().getMember(player.getUniqueId());
        return clanMemberOptional.map(ClanMember::clan);
    }

}
