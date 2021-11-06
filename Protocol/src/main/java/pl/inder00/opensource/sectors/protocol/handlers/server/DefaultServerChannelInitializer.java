package pl.inder00.opensource.sectors.protocol.handlers.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufHandshake;

import java.util.concurrent.TimeUnit;

public class DefaultServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * Data
     */
    private final ISectorServer sectorServer;
    private final ISectorServerListener serverListener;

    /**
     * Implemenetation
     */
    public DefaultServerChannelInitializer(ISectorServer sectorServer, ISectorServerListener serverListener) {
        this.sectorServer = sectorServer;
        this.serverListener = serverListener;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // pipelines
        ch.pipeline().addLast("p-protoVariantDecoder", new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast("t-protoDecoder_handshake", new ProtobufDecoder(ProtobufHandshake.HandshakePacket.getDefaultInstance()));
        ch.pipeline().addLast("p-protoVariantEncoder", new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast("p-protoEncoder", new ProtobufEncoder());
        ch.pipeline().addLast("p-handler", new DefaultServerHandler(this.sectorServer,this.serverListener));

    }

}
