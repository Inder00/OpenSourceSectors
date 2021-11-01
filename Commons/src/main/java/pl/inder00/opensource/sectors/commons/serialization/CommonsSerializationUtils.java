package pl.inder00.opensource.sectors.commons.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class CommonsSerializationUtils {

    /**
     * Serialize object into a byte array
     *
     * @param object Object
     * @return Array of bytes
     */
    public static byte[] serializeRaw(Object object) {

        // streamers
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        try {

            // create object output stream and write object
            out = new ObjectOutputStream(bos);
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
     * Deserialize byte array into a object
     *
     * @param data Array of bytes
     * @return object
     */
    public static <T> T deserializeRaw(byte[] data) {

        // streamers
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream oit = null;

        try {

            // create object output stream and write object
            oit = new ObjectInputStream(bis);

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

}
