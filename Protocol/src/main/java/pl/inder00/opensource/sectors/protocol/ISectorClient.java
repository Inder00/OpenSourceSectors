package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.concurrent.FutureCallback;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;

public interface ISectorClient {

    /**
     * Netty channel pipeline implementation
     *
     * @return ChannelPipeline
     */
    ChannelPipeline getChannelPipeline();

    /**
     * Netty channel implementation
     *
     * @return Channel
     */
    Channel getChannel();

    /**
     * Prototype manager implementation
     *
     * @return Prototype manager
     */
    IPrototypeManager getPrototypeManager();

    /**
     * Returns traffic encryption provider
     *
     * @return IEncryptionProvider
     */
    IEncryptionProvider getEncryptionProvider();

    /**
     * Sets traffic encryption provider
     *
     * @param encryptionProvider IEncryptionProvider
     */
    void setEncryptionProvider(IEncryptionProvider encryptionProvider);

    /**
     * Returns client listener implementation
     *
     * @return ISectorClientListener
     */
    ISectorClientListener getClientListener();

    /**
     * Connects to server
     */
    void connect(IInternalServer internalServer);

    /**
     * Returns boolean representing does client is connected to server
     *
     * @return boolean
     */
    boolean isConnected();

    /**
     * Sends protobuf message to server
     *
     * @param message Protobuf message
     */
    void sendData(MessageLite message);

    /**
     * Sends protobuf message to server
     *
     * @param message      Protobuf message
     * @param packetStatus Future packet status callback
     */
    void sendData(MessageLite message, FutureCallback<IPacketStatus> packetStatus);

    /**
     * Closes connection with remote-client
     */
    void disconnect();

}
