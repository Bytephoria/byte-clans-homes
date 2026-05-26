package team.bytephoria.byteclanshomes;

import org.bukkit.Location;
import team.bytephoria.datacontainer.api.DataKey;
import team.bytephoria.datacontainer.bukkitserializers.BukkitSerializers;
import team.bytephoria.datacontainer.serializers.Serializers;

import java.util.Map;

public final class DataKeys {

    private DataKeys() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static final String NAMESPACE = "Bytephoria";

    public static final DataKey<String> DEFAULT_HOME = DataKey.of(NAMESPACE, "default_home", Serializers.STRING);
    public static final DataKey<Map<String, Location>> HOMES = DataKey.of(NAMESPACE, "homes", BukkitSerializers.STRING_LOCATION_MAP_SERIALIZER);

}
