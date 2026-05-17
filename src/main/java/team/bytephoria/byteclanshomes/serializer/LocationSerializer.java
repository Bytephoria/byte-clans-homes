package team.bytephoria.byteclanshomes.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.datacontainer.api.DataSerializer;

import java.io.*;

public final class LocationSerializer implements DataSerializer<Location> {

    private final byte serialId;
    LocationSerializer(final byte serialId) {
        this.serialId = serialId;
    }

    @Override
    public byte @NotNull [] serialize(final @NotNull Location value) {
        try (
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
        ) {
            final World world = value.getWorld();

            dataOutputStream.writeUTF(world == null ? "" : world.getName());
            dataOutputStream.writeDouble(value.getX());
            dataOutputStream.writeDouble(value.getY());
            dataOutputStream.writeDouble(value.getZ());
            dataOutputStream.writeFloat(value.getYaw());
            dataOutputStream.writeFloat(value.getPitch());

            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to serialize Location.", exception);
        }
    }

    @Override
    public @NotNull Location deserialize(final byte @NotNull [] value) {
        try (
                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);
                final DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            final String worldName = dataInputStream.readUTF();
            final World world = worldName.isEmpty()
                    ? null
                    : Bukkit.getWorld(worldName);

            return new Location(
                    world,
                    dataInputStream.readDouble(),
                    dataInputStream.readDouble(),
                    dataInputStream.readDouble(),
                    dataInputStream.readFloat(),
                    dataInputStream.readFloat()
            );

        } catch (IOException exception) {
            throw new RuntimeException("Failed to deserialize Location.", exception);
        }
    }

    @Override
    public byte serialId() {
        return this.serialId;
    }
}
