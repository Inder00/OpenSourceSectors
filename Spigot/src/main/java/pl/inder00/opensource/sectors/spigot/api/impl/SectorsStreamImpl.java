package pl.inder00.opensource.sectors.spigot.api.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.bukkit.plugin.java.JavaPlugin;
import org.reactivestreams.Subscriber;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;
import pl.inder00.opensource.sectors.protocol.IStreamEmiter;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;
import pl.inder00.opensource.sectors.protocol.handlers.stream.DefaultStreamSubscriber;
import pl.inder00.opensource.sectors.spigot.basic.ISector;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

public class SectorsStreamImpl<T extends Message> implements ISectorsStream<T> {

    /**
     * Data
     */
    private final JavaPlugin apiPlugin;
    private final ISector sector;
    private final Data<T> streamListener;
    private final IStreamEmiter<Payload> streamEmiter;
    private final Subscriber<Payload> streamSubscriber;
    private final Map<Class<?>, Integer> channelsMap;
    private final Manager<T> streamManager;

    /**
     * Implementation
     */
    public SectorsStreamImpl(JavaPlugin apiPlugin, ISector sector, Data<T> streamListener, IStreamEmiter<Payload> streamEmiter, Map<Class<?>, Integer> channelsMap) {
        this.apiPlugin = apiPlugin;
        this.sector = sector;
        this.streamListener = streamListener;
        this.streamEmiter = streamEmiter;
        this.streamSubscriber = new DefaultStreamSubscriber<>(this);
        this.channelsMap = channelsMap;
        this.streamManager = this.createStreamManager();
    }

    /**
     * Implementation
     */
    public SectorsStreamImpl(JavaPlugin apiPlugin, ISector sector, Data<T> streamListener, IStreamEmiter<Payload> streamEmiter, Subscriber<Payload> streamSubscriber, Map<Class<?>, Integer> channelsMap) {
        this.apiPlugin = apiPlugin;
        this.sector = sector;
        this.streamListener = streamListener;
        this.streamEmiter = streamEmiter;
        this.streamSubscriber = streamSubscriber;
        this.channelsMap = channelsMap;
        this.streamManager = this.createStreamManager();
    }

    /**
     * Creates a stream manager implementation
     *
     * @return Manager
     */
    private Manager<T> createStreamManager(){

        return new Manager<T>() {

            @Override
            public <Y extends T> void sendData(Y data) throws InvalidProtobufMessageException {

                // check data type
                if(!(data instanceof Message)) throw new InvalidProtobufMessageException();

                // send data
                try {

                    // write data to emiter
                    int channelId = getChannelIdByClass(data.getClass());
                    byte[] protobufData = data.toByteArray();

                    // write data to emiter
                    streamEmiter.emit(DefaultPayload.create(ByteBuffer.wrap(protobufData), StandardCharsets.UTF_8.encode(CharBuffer.wrap(String.valueOf(channelId)))));

                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }

        };

    }

    @Override
    public void processData(Payload payload) {

        try {

            // read channel id
            int channelId = Integer.parseInt(payload.getMetadataUtf8());

            // process data
            this.channelsMap.entrySet().stream().filter(data -> data.getValue() == channelId).findFirst().ifPresent(processChannel -> {

                try {

                    // create message builder
                    Class<?> messageClass = processChannel.getKey();
                    Message.Builder messageBuilder = (Message.Builder) messageClass.getMethod("newBuilder").invoke(messageClass);

                    // build message from bytes
                    Message message = messageBuilder.mergeFrom(ByteString.copyFrom(payload.data().nioBuffer())).build();

                    // pass message to event listener
                    this.streamListener.receiveData((T) message);

                } catch (Throwable e){

                    // log
                    this.apiPlugin.getLogger().log(Level.SEVERE, String.format("Failed to process " + processChannel.getKey().getSimpleName() + " data - %s.", e.getMessage()));

                }

            });

        } catch (Throwable ex){

            // log
            this.apiPlugin.getLogger().log(Level.SEVERE, String.format("Failed to process unknown data - %s.", ex.getMessage()));

        }

    }

    /**
     * Returns channel id by class
     * @param clazz Class
     * @return int
     */
    private int getChannelIdByClass(Class<?> clazz){
        return channelsMap.entrySet().stream().filter(predicate -> predicate.getKey().hashCode() == clazz.hashCode()).findFirst().get().getValue();
    }

    @Override
    public Subscriber<Payload> getSubsriber() {
        return this.streamSubscriber;
    }

    @Override
    public Manager<T> getStreamManager() {
        return this.streamManager;
    }

    @Override
    public Map<Class<?>, Integer> getChannels() {
        return this.channelsMap;
    }

    @Override
    public IStreamEmiter<Payload> getStreamEmiter() {
        return this.streamEmiter;
    }

    @Override
    public Data<T> getStreamListener() {
        return this.streamListener;
    }
}
