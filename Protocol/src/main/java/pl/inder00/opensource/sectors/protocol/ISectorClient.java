package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;

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
    void sendData(Message message);

}
