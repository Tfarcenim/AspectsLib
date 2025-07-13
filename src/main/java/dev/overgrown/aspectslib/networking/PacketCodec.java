package dev.overgrown.aspectslib.networking;

import net.minecraft.network.PacketByteBuf;

/**
 * Simple packet codec interface for 1.20.1 compatibility
 * @param <T> The type to encode/decode
 */
public interface PacketCodec<T> {
    /**
     * Decodes a value from the buffer
     * @param buf The packet buffer to read from
     * @return The decoded value
     */
    T decode(PacketByteBuf buf);

    /**
     * Encodes a value to the buffer
     * @param buf The packet buffer to write to
     * @param value The value to encode
     */
    void encode(PacketByteBuf buf, T value);
}