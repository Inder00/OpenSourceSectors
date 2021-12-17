package pl.inder00.opensource.sectors.protocol.pipelines;

import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;

import java.util.List;
import java.util.UUID;

public class ProtobufDecoder extends ByteToMessageDecoder {

    /**
     * Data
     */
    private IPrototypeManager prototypeManager;

    /**
     * Implementation
     */
    public ProtobufDecoder(IPrototypeManager prototypeManager) {
        this.prototypeManager = prototypeManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        try {

            // message class
            if (in.readableBytes() < 8) throw new InvalidProtobufMessageException("Invalid frame - missing hashcode");
            long mostSigBits = in.readLong();
            long leastSigBits = in.readLong();
            UUID messageClass = new UUID(mostSigBits, leastSigBits);

            // message data
            if (in.readableBytes() < 1) throw new InvalidProtobufMessageException("Invalid frame - missing data");
            byte[] messageData = new byte[in.readableBytes()];

            // read message data
            in.readBytes(messageData);

            // process prototype
            MessageLiteOrBuilder messageLiteOrBuilder = this.prototypeManager.getPrototypeByCode(messageClass);
            if (messageLiteOrBuilder == null)
                throw new InvalidProtobufMessageException("Invalid protobuf message (" + messageClass + ")");

            // push object
            out.add(messageLiteOrBuilder.getDefaultInstanceForType().getParserForType().parseFrom(messageData));

        } catch (Throwable e) {

            // throw exception
            throw e;

        }

    }

}
