package pl.inder00.opensource.sectors.protocol.handlers.stream;

import com.google.protobuf.Message;
import io.rsocket.Payload;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;

public class DefaultStreamSubscriber<T extends Message> implements Subscriber<Payload> {

    /**
     * Data
     */
    private final ISectorsStream<T> sectorsStream;
    private Subscription subscription;

    /**
     * Implementation
     */
    public DefaultStreamSubscriber(ISectorsStream<T> sectorsStream) {
        this.sectorsStream = sectorsStream;
    }

    @Override
    public void onSubscribe(Subscription subscription) {

        // request data
        this.subscription = subscription;
        this.subscription.request( 1 );

    }

    @Override
    public void onNext(Payload data) {

        // send data to process
        this.sectorsStream.processData(data);

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
