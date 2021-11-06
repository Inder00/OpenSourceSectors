package pl.inder00.opensource.sectors.protocol.pipelines;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {

        // protobuf message
        if (msg instanceof MessageLite) {
            out.add(wrappedBuffer(((MessageLite) msg).toByteArray()));
            return;
        }

        // throw exception
        throw new ProtocolException("Invalid message type");

    }

}
