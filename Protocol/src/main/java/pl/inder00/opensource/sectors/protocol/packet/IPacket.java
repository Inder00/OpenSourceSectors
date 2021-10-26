package pl.inder00.opensource.sectors.protocol.packet;

import io.netty.buffer.ByteBuf;

public interface IPacket {

    /**
     * Packet execution code
     * @param bufferIn Input data
     * @param bufferOut Output data
     * @return Payload
     */
    void execute(ByteBuf bufferIn, ByteBuf bufferOut) throws Throwable;

}
