package team.bytephoria.byteclanshomes.serializer;

import org.bukkit.Location;
import team.bytephoria.datacontainer.serializers.Serializers;

public final class BuiltInSerializers {

    private BuiltInSerializers() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static final LocationSerializer LOCATION = new LocationSerializer((byte) 0x30);
    public static final MapSerializer<String, Location> LOCATION_BIG_MAP_SERIALIZER = new MapSerializer<>(Serializers.STRING, LOCATION, (byte) 0x31);

}
