package pl.inder00.opensource.sectors.protocol.pipelines;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;

public class EncryptionEncoder extends MessageToByteEncoder<ByteBuf> {

    /**
     * Data
     */
    private IEncryptionProvider encryptionProvider;

    /**
     * Implementation
     */
    public EncryptionEncoder(IEncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

        // check frame
        if(msg.readableBytes() == 0) throw new ProtocolException("Invalid message frame - frame is empty");

        // check does traffic is encrypted
        if( this.encryptionProvider.isEncryptionEnabled() )
        {

            // read frame
            byte[] frameData = new byte[msg.readableBytes()];
            msg.readBytes(frameData);

            // return encrypted data
            out.writeBytes(this.encryptionProvider.encryptData(frameData));

        }
        else
        {

            // return data
            out.writeBytes(msg);

        }

    }

}
