package pl.inder00.opensource.sectors.protocol.prototype;

import com.google.protobuf.MessageLite;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;

public interface IPrototypeListener<T extends MessageLite> {

    /**
     * Prototype received data listener
     *
     * @param message MessageLite
     */
    default void onReceivedData(T message) throws Exception {

    }

    /**
     * Prototype received data listener
     *
     * @param message MessageLite
     */
    default void onReceivedData(ISectorConnection connection, T message) throws Exception {

    }

}
