package pl.inder00.opensource.sectors.protocol.impl;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import pl.inder00.opensource.sectors.commons.concurrent.FutureCallback;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;
import pl.inder00.opensource.sectors.commons.encryption.impl.DefaultEncryptionProvider;
import pl.inder00.opensource.sectors.protocol.IPacketStatus;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;

import java.net.SocketAddress;
import java.util.UUID;

public class DefaultSectorConnection implements ISectorConnection {

    /**
     * Data
     */
    private final UUID uniqueId;
    private final Channel channel;
    private IEncryptionProvider encryptionProvider;

    /**
     * Implementation
     */
    public DefaultSectorConnection(UUID uniqueId, Channel channel) {
        this(uniqueId,channel,new DefaultEncryptionProvider());
    }

    /**
     * Implementation
     */
    public DefaultSectorConnection(UUID uniqueId, Channel channel, IEncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
        this.uniqueId = uniqueId;
        this.channel = channel;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public SocketAddress getAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }

    @Override
    public IEncryptionProvider getEncryptionProvider() {
        return this.encryptionProvider;
    }

    @Override
    public void setEncryptionProvider(IEncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    public synchronized void sendData(MessageLite message) {

        // send synchronized data
        synchronized (this){

            // write and flush data
            this.channel.writeAndFlush(message);

        }

    }

    @Override
    public synchronized void sendData(MessageLite message, FutureCallback<IPacketStatus> packetStatus) {

        // send synchronized data
        synchronized (this){

            // check does client is already connected
            if(!this.isConnected()) packetStatus.execute(IPacketStatus.ERROR);

            // write and flush data
            this.channel.writeAndFlush(message).addListener(status -> packetStatus.execute(status.isSuccess() ? IPacketStatus.OK : IPacketStatus.ERROR));

        }

    }

    @Override
    public void disconnect() {
        this.channel.close().syncUninterruptibly();
    }

}
