package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.MessageLite;
import pl.inder00.opensource.sectors.commons.concurrent.FutureCallback;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;

import java.net.SocketAddress;
import java.util.UUID;

public interface ISectorConnection {

    /**
     * Returns connection unique id
     *
     * @return UUID
     */
    UUID getUniqueId();

    /**
     * Returns client remote address
     *
     * @return SocketAddress
     */
    SocketAddress getAddress();

    /**
     * Returns traffic encryption provider
     *
     * @return IEncryptionProvider
     */
    IEncryptionProvider getEncryptionProvider();

    /**
     * Returns boolean representing does connection is alive
     *
     * @return boolean
     */
    boolean isConnected();

    /**
     * Sets traffic encryption provider
     *
     * @param encryptionProvider IEncryptionProvider
     */
    void setEncryptionProvider(IEncryptionProvider encryptionProvider);

    /**
     * Sends data to client
     *
     * @param message Protobuf prototype message
     */
    void sendData(MessageLite message);

    /**
     * Sends protobuf message to server
     *
     * @param message Protobuf message
     * @param packetStatus Future packet status callback
     */
    void sendData(MessageLite message, FutureCallback<IPacketStatus> packetStatus);

    /**
     * Closes connection with remote-client
     */
    void disconnect();

}
