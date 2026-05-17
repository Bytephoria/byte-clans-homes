package team.bytephoria.byteclanshomes.serializer;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.datacontainer.api.DataSerializer;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public final class MapSerializer<K, V> implements DataSerializer<Map<K, V>> {

    private final DataSerializer<K> keySerializer;
    private final DataSerializer<V> valueSerializer;
    private final byte serialId;

    MapSerializer(
            final @NotNull DataSerializer<K> keySerializer,
            final @NotNull DataSerializer<V> valueSerializer,
            final byte serialId
    ) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.serialId = serialId;
    }

    @Override
    public byte @NotNull [] serialize(final @NotNull Map<K, V> value) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final int size = value.size();

        output.write((byte) (size >> 24));
        output.write((byte) (size >> 16));
        output.write((byte) (size >> 8));
        output.write((byte) size);

        for (final Map.Entry<K, V> entry : value.entrySet()) {
            this.write(output, this.keySerializer.serialize(entry.getKey()));
            this.write(output, this.valueSerializer.serialize(entry.getValue()));
        }

        return output.toByteArray();
    }

    @Override
    public @NotNull Map<K, V> deserialize(final byte @NotNull [] data) {
        int offset = 0;

        final int size =
                ((data[offset++] & 0xFF) << 24)
                        | ((data[offset++] & 0xFF) << 16)
                        | ((data[offset++] & 0xFF) << 8)
                        | (data[offset++] & 0xFF);

        final Map<K, V> map = new HashMap<>(size);

        for (int i = 0; i < size; i++) {

            final ReadResult keyResult = read(data, offset);
            offset += keyResult.totalBytes();

            final ReadResult valueResult = read(data, offset);
            offset += valueResult.totalBytes();

            final K key = this.keySerializer.deserialize(keyResult.data());
            final V value = this.valueSerializer.deserialize(valueResult.data());

            map.put(key, value);
        }

        return map;
    }

    private void write(
            final @NotNull ByteArrayOutputStream output,
            final byte @NotNull [] data
    ) {
        final int length = data.length;

        output.write((byte) (length >> 24));
        output.write((byte) (length >> 16));
        output.write((byte) (length >> 8));
        output.write((byte) length);

        output.writeBytes(data);
    }

    private @NotNull ReadResult read(
            final byte @NotNull [] data,
            final int offset
    ) {
        int cursor = offset;

        final int length =
                ((data[cursor++] & 0xFF) << 24)
                        | ((data[cursor++] & 0xFF) << 16)
                        | ((data[cursor++] & 0xFF) << 8)
                        | (data[cursor++] & 0xFF);

        final byte[] bytes = new byte[length];

        System.arraycopy(
                data,
                cursor,
                bytes,
                0,
                length
        );

        return new ReadResult(
                bytes,
                length + 4
        );
    }

    @Override
    public byte serialId() {
        return serialId;
    }

    private record ReadResult(
            byte[] data,
            int totalBytes
    ) {}

    public static <K, V> @NotNull MapSerializer<K, V> of(
            final @NotNull DataSerializer<K> keySerializer,
            final @NotNull DataSerializer<V> valueSerializer,
            final byte serialId
    ) {
        return new MapSerializer<>(
                keySerializer,
                valueSerializer,
                serialId
        );
    }
}