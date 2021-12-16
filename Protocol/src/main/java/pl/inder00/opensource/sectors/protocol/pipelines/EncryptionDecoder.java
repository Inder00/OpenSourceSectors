package pl.inder00.opensource.sectors.protocol.pipelines;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;

import java.util.List;

public class EncryptionDecoder extends MessageToMessageDecoder<ByteBuf> {

    /**
     * Data
     */
    private IEncryptionProvider encryptionProvider;

    /**
     * Implementation
     */
    public EncryptionDecoder(IEncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        // check frame
        if(msg.readableBytes() == 0) throw new ProtocolException("Invalid message frame - frame is empty");

        // check does traffic is encrypted
        if( this.encryptionProvider.isEncryptionEnabled() )
        {

            // read frame
            byte[] frameData = new byte[msg.readableBytes()];
            msg.readBytes(frameData);

            // decrypt data
            out.add(Unpooled.copiedBuffer(this.encryptionProvider.decryptData(frameData)));

        } else {

            // return data
            out.add(Unpooled.copiedBuffer(msg));

        }

    }

}
