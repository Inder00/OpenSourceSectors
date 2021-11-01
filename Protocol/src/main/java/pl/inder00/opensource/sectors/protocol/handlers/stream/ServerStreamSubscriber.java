package pl.inder00.opensource.sectors.protocol.handlers.stream;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.rsocket.Payload;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;
import pl.inder00.opensource.sectors.protocol.stream.StreamManager;

public class ServerStreamSubscriber implements Subscriber<Payload> {

    /**
     * Data
     */
    private Subscription subscription;


    @Override
    public void onSubscribe(Subscription subscription) {

        // request data
        this.subscription = subscription;
        this.subscription.request( 1 );

    }

    @Override
    public void onNext(Payload payload) {

        try {

            // read channel id
            int channelId = Integer.parseInt(payload.getMetadataUtf8());

            // process data
            ISectorsStream<? extends Message> sectorsStream = StreamManager.getStreamByChannelId(channelId);
            if(sectorsStream != null) {

                // loop channels
                sectorsStream.getChannels().entrySet().stream().filter(channel -> channel.getValue() == channelId).findFirst().ifPresent(processChannel -> {

                    try {

                        // create message builder
                        Class<?> messageClass = processChannel.getKey();
                        Message.Builder messageBuilder = (Message.Builder) messageClass.getMethod("newBuilder").invoke(messageClass);

                        // build message from bytes
                        Message message = messageBuilder.mergeFrom(ByteString.copyFrom(payload.data().nioBuffer())).build();

                        // pass message to event listener
                        ((ISectorsStream) sectorsStream).getStreamListener().receiveData(message);

                    } catch (Throwable ignored2) {
                    }

                });
            }

        } catch (Throwable ignored){}

        // request data
        this.subscription.request( 1 );

    }

    @Override
    public void onError(Throwable throwable) {

        // print stack trace
        throwable.printStackTrace();

    }

    @Override
    public void onComplete() {
    }

}
