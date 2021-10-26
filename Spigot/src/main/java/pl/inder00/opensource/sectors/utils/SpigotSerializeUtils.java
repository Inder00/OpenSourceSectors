package pl.inder00.opensource.sectors.utils;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SpigotSerializeUtils {

    /**
     * Serialize bukkit object into a byte array
     * @param object Object
     * @return Array of bytes
     */
    public static byte[] serializeBukkitObject(Object object){

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

        } catch (Throwable e){

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
     * @param data Array of bytes
     * @return object
     */
    public static <T> T deserializeBukkitObject(byte[] data){

        // streamers
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BukkitObjectInputStream oit = null;

        try {

            // create object output stream and write object
            oit = new BukkitObjectInputStream(bis);

            // return bytes
            return (T) oit.readObject();

        } catch (Throwable e){

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

}
