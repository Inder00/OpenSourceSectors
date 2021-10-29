package pl.inder00.opensource.sectors.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ProtobufUtils {

    /**
     * Serialize bukkit object into a byte array
     *
     * @param object Object
     * @return Array of bytes
     */
    public static byte[] serializeRaw(Object object) {

        // streamers
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = null;

        try {

            // create object output stream and write object
            out = new BukkitObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();

            // return bytes
            return bos.toByteArray();

        } catch (Throwable e) {

            // throw error
            e.printStackTrace();

            // return empty bytes
            return null;

        } finally {
            try {
                bos.close();
                out.close();
            } catch (Throwable ex) {
                // ignore close exception
            }
        }

    }

    /**
     * Deserialize byte array into a bukkit object
     *
     * @param data Array of bytes
     * @return object
     */
    public static <T> T deserializeRaw(byte[] data) {

        // streamers
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BukkitObjectInputStream oit = null;

        try {

            // create object output stream and write object
            oit = new BukkitObjectInputStream(bis);

            // return bytes
            return (T) oit.readObject();

        } catch (Throwable e) {

            // return empty bytes
            return null;

        } finally {
            try {
                bis.close();
                oit.close();
            } catch (Throwable ex) {
                // ignore close exception
            }
        }

    }

    /**
     * Serialize bukkit location into a protolocation
     *
     * @param location Bukkit location
     * @return ProtoLocation
     */
    public static ProtobufGeneric.ProtoLocation serialize(Location location) {
        return ProtobufGeneric.ProtoLocation.newBuilder()
                .setX(location.getX())
                .setY(location.getY())
                .setZ(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .build();
    }

    /**
     * Deserialize ProtoUUID into a uuid
     *
     * @param protoUUID ProtoUUID
     * @return UUID
     */
    public static UUID deserialize(ProtobufGeneric.ProtoUUID protoUUID) {
        return new UUID(protoUUID.getMostSig(), protoUUID.getLeastSig());
    }

    /**
     * Serialize UUID into a ProtoUUID
     *
     * @param uuid UUID
     * @return ProtoUUID
     */
    public static ProtobufGeneric.ProtoUUID serialize(UUID uuid) {
        return ProtobufGeneric.ProtoUUID.newBuilder()
                .setMostSig(uuid.getMostSignificantBits())
                .setLeastSig(uuid.getLeastSignificantBits())
                .build();
    }

    /**
     * Deserialize protolocation into a bukkit location
     *
     * @param world    Bukkit world
     * @param location ProtoLocation
     * @return Bukkit location
     */
    public static Location deserialize(World world, ProtobufGeneric.ProtoLocation location) {
        if (world == null && !location.hasWorld()) return null;
        return new Location(world != null ? world : Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.hasYaw() ? location.getYaw() : 0F, location.hasPitch() ? location.getPitch() : 0F);
    }

    /**
     * Serialize object into byte array
     *
     * @param object Object
     * @return Array of bytes
     */
    public static <T> byte[] serialize(T object) {
        return serializeRaw(object);
    }

    /**
     * Deserialize array of bytes into a object
     *
     * @param bytes Array of bytes
     * @return Object
     */
    public static <T> T deserialize(byte[] bytes) {
        return deserializeRaw(bytes);
    }

    /**
     * Serialization exception
     */
    public static class SerializationException extends Exception {

        public SerializationException() {
            super();
        }

        public SerializationException(String message) {
            super(message);
        }
    }

    /**
     * Deserialization exception
     */
    public static class DeserializationException extends Exception {

        public DeserializationException() {
            super();
        }

        public DeserializationException(String message) {
            super(message);
        }
    }

}
