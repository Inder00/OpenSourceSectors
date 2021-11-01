package pl.inder00.opensource.sectors.protocol;

import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public interface IStreamEmiter<Y> extends Consumer<FluxSink<Y>> {

    /**
     * Emits data to flux
     *
     * @param data Data
     */
    void emit(Y data);

    @Override
    void accept(FluxSink<Y> tFluxSink);
}
