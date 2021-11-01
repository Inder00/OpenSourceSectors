package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.Message;
import io.rsocket.Payload;
import org.reactivestreams.Subscriber;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;

import java.util.Map;

public interface ISectorsStream<T extends Message> {

    /**
     * Process data from socket stream
     *
     * @param payload Payload
     */
    void processData(Payload payload);

    /**
     * Get stream subsriber
     *
     * @return Subscriber
     */
    Subscriber<Payload> getSubsriber();

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

    /**
     * Returns map of channels
     *
     * @return Map
     */
    Map<Class<?>, Integer> getChannels();

    /**
     * Returns stream emiter implementation
     *
     * @return IStreamEmiter
     */
    IStreamEmiter<Payload> getStreamEmiter();

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
