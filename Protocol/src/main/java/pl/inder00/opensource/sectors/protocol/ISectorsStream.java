package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.Message;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;

import java.util.List;
import java.util.Map;

public interface ISectorsStream<T extends Message> {

    /**
     * Returns stream manager
     *
     * @return Manager
     */
    Manager<T> getStreamManager();

    /**
     * Returns stream listener
     *
     * @return Data
     */
    Data<T> getStreamListener();

    interface Data<T extends Message> {

        /**
         * Triggered when data has been received from remote stream
         *
         * @param data Data
         */
        void receiveData(T data);

    }

    interface Manager<T extends Message> {

        /**
         * Sends data to remote stream
         *
         * @return Protobuf message
         * @throws InvalidProtobufMessageException
         */
        <Y extends T> void sendData(Y data) throws InvalidProtobufMessageException;

    }

}
