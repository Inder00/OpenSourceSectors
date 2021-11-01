package pl.inder00.opensource.sectors.spigot.api;

import com.google.protobuf.Message;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;
import pl.inder00.opensource.sectors.spigot.basic.ISector;

public interface ISectorsAPI {

    /**
     * Opens stream to other sector
     *
     * @param sector Sector
     * @param streamListener Stream listener
     * @throws InvalidProtobufMessageException
     */
    <T extends Message> ISectorsStream.Manager<T> openStream(ISector sector, ISectorsStream.Data<T> streamListener) throws Throwable;

}
