package pl.inder00.opensource.sectors.protocol.pipelines;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;

public class ProtobufEncoder extends MessageToByteEncoder<MessageLiteOrBuilder> {

    /**
     * Data
     */
    private IPrototypeManager prototypeManager;

    /**
     * Implementation
     */
    public ProtobufEncoder(IPrototypeManager prototypeManager) {
        this.prototypeManager = prototypeManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, ByteBuf out) throws Exception {

        // protobuf message
        if (msg instanceof MessageLite) {

            // message class
            out.writeInt( msg.getClass().hashCode() );

            // data
            out.writeBytes( ((MessageLite) msg).toByteArray() );
            return;
        }

        // throw exception
        throw new ProtocolException("Invalid message type");

    }
}
