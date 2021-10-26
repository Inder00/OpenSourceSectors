package pl.inder00.opensource.sectors.protocol.buffer;

import io.netty.buffer.ByteBuf;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.utils.SerializeUtils;

import java.nio.charset.StandardCharsets;

public class ByteBufExtension {

    /**
     * Write null to buffer
     */
    private static ByteBuf writeNull(ByteBuf buffer){
        buffer.writeByte('N');
        return buffer;
    }

    /**
     * Write string to buffer
     * @param string String
     */
    public static ByteBuf writeString(ByteBuf buffer, String string){

        // check for null
        if(string == null) return writeNull(buffer);

        // mark as string
        buffer.writeByte('S');

        // string data
        byte[] data = string.getBytes(StandardCharsets.UTF_8);

        // write
        buffer.writeInt(data.length);
        buffer.writeBytes(data);

        // return
        return buffer;

    }

    /**
     * Read string from buffer
     * @param maxLength max length of string which can be handle otherwise throw an error
     * @throws ProtocolException when fails to handle
     * @return String
     */
    public static String readString( ByteBuf buffer, int maxLength ) throws ProtocolException {

        // check for null
        buffer.markReaderIndex();
        if(buffer.readByte() == 'N') return null;
        buffer.resetReaderIndex();

        // check readable bytes
        if(buffer.readableBytes() < 6){

            // throw error
            throw new ProtocolException("Not enough bytes in buffer to read string");

        }

        // mark reader
        buffer.markReaderIndex();

        // try to read
        if(buffer.readByte() == 'S'){

            // try to read size
            int size = buffer.readInt();
            if( size > maxLength * 4 ){

                // reset reader
                buffer.resetReaderIndex();

                // throw error
                throw new ProtocolException("String is larger than given max length");

            }

            // check readable bytes
            if(buffer.readableBytes() < size){

                // reset reader
                buffer.resetReaderIndex();

                // throw error
                throw new ProtocolException("Not enough bytes to read string");

            }

            // decode
            byte[] data = new byte[size];
            buffer.readBytes(data);
            String string = new String(data, StandardCharsets.UTF_8);
            if(string.length() > maxLength){

                // reset reader
                buffer.resetReaderIndex();

                // throw error
                throw new ProtocolException("String is larger than given max length");
            }

            // return string
            return string;

        } else {

            // reset reader
            buffer.resetReaderIndex();

            // throw error
            throw new ProtocolException("Failed to read string from buffer");

        }

    }

    /**
     * Write object to buffer
     * @param object A reference to object
     */
    public static <T> ByteBuf writeObject(ByteBuf buffer, T object){

        // check for null
        if(object == null) return writeNull(buffer);

        // mark as string
        buffer.writeByte('O');

        // object data
        byte[] data = SerializeUtils.serializeObject( object );

        // write
        buffer.writeInt(data.length);
        buffer.writeBytes(data);

        // return
        return buffer;

    }

    /**
     * Write byte array to buffer
     * @param object Array of bytes
     */
    public static <T> ByteBuf writeByteArray(ByteBuf buffer, byte[] object){

        // check for null
        if(object == null) return writeNull(buffer);

        // mark as string
        buffer.writeByte('P');

        // write
        buffer.writeInt(object.length);
        buffer.writeBytes(object);

        // return
        return buffer;

    }

    /**
     * Read byte array from buffer
     * @throws ProtocolException when fails to handle
     * @return Array of bytes
     */
    public static <T> T readByteArray( ByteBuf buffer ) throws ProtocolException {

        // check for null
        buffer.markReaderIndex();
        if(buffer.readByte() == 'N') return null;
        buffer.resetReaderIndex();

        // check readable bytes
        if(buffer.readableBytes() < 6){

            // throw error
            throw new ProtocolException("Not enough bytes in buffer to read object");

        }

        // mark reader
        buffer.markReaderIndex();

        // try to read
        if(buffer.readByte() == 'P'){

            // read size
            int size = buffer.readInt();

            // decode
            byte[] data = new byte[size];
            buffer.readBytes(data);

            // return byte array
            return (T) data;

        } else {

            // reset reader
            buffer.resetReaderIndex();

            // throw error
            throw new ProtocolException("Failed to read object from buffer");

        }

    }

    /**
     * Read object from buffer
     * @throws ProtocolException when fails to handle
     * @return Object
     */
    public static <T> T readObject( ByteBuf buffer ) throws ProtocolException {

        // check for null
        buffer.markReaderIndex();
        if(buffer.readByte() == 'N') return null;
        buffer.resetReaderIndex();

        // check readable bytes
        if(buffer.readableBytes() < 6){

            // throw error
            throw new ProtocolException("Not enough bytes in buffer to read object");

        }

        // mark reader
        buffer.markReaderIndex();

        // try to read
        if(buffer.readByte() == 'O'){

            // read size
            int size = buffer.readInt();

            // decode
            byte[] data = new byte[size];
            buffer.readBytes(data);

            // return object
            return (T) SerializeUtils.deserializeObject( data );

        } else {

            // reset reader
            buffer.resetReaderIndex();

            // throw error
            throw new ProtocolException("Failed to read object from buffer");

        }

    }

}
