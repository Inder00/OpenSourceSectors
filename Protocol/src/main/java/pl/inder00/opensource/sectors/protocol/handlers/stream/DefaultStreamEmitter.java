package pl.inder00.opensource.sectors.protocol.handlers.stream;

import com.google.protobuf.Message;
import io.rsocket.Payload;
import pl.inder00.opensource.sectors.protocol.IStreamEmiter;
import reactor.core.publisher.FluxSink;

public class DefaultStreamEmitter<T extends Message> implements IStreamEmiter<Payload> {

    /**
     * Data
     */
    private FluxSink<Payload> sink;

    @Override
    public void accept(FluxSink<Payload> sink) {
        this.sink = sink;
    }

    @Override
    public void emit(Payload data){
        this.sink.next( data );
    }

}
