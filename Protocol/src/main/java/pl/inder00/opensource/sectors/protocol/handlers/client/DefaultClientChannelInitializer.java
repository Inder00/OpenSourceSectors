package pl.inder00.opensource.sectors.protocol.handlers.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;
import pl.inder00.opensource.sectors.protocol.pipelines.ProtobufEncoder;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufHandshake;

public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * Data
     */
    private final ISectorClient sectorClient;
    private final ISectorClientListener clientListener;

    /**
     * Implemenetation
     */
    public DefaultClientChannelInitializer(ISectorClient sectorClient, ISectorClientListener clientListener) {
        this.sectorClient = sectorClient;
        this.clientListener = clientListener;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // pipelines
        ch.pipeline().addLast("p-protoVariantDecoder", new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast("t-protoDecoder_handshake", new ProtobufDecoder(ProtobufHandshake.HandshakePacket.getDefaultInstance()));
        ch.pipeline().addLast("p-protoVariantEncoder", new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast("p-protoEncoder", new ProtobufEncoder());
        ch.pipeline().addLast("p-handler", new DefaultClientHandler(this.sectorClient,this.clientListener));

    }

}

