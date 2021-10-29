package pl.inder00.opensource.sectors.protocol.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils {

    /**
     * Serialize object into a byte array
     *
     * @param object Object
     * @return Array of bytes
     */
    public static byte[] serializeObject(Object object) {

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
    public static Object deserializeObject(byte[] data) {

        // streamers
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream oit = null;

        try {

            // create object output stream and write object
            oit = new ObjectInputStream(bis);

            // return bytes
            return oit.readObject();

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

}
