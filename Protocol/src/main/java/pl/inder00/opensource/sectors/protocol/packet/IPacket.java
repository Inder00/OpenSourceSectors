package pl.inder00.opensource.sectors.protocol.packet;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

public interface IPacket<T extends MessageOrBuilder> {

    /**
     * Returns google protobuf builder
     *
     * @return Message.Builder
     */
    <T extends Message.Builder> T getBuilder();

    /**
     * Packet execution code
     *
     * @param bufferIn  Input data
     * @return Payload
     */
    <Y extends Message> Y execute(T bufferIn) throws Throwable;

}
