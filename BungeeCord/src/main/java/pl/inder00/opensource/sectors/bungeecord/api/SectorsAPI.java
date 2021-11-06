package pl.inder00.opensource.sectors.bungeecord.api;

import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import io.rsocket.Payload;
import net.jodah.typetools.TypeResolver;
import net.md_5.bungee.api.plugin.Plugin;
import pl.inder00.opensource.sectors.bungeecord.api.impl.SectorsStreamImpl;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;
import pl.inder00.opensource.sectors.protocol.handlers.stream.DefaultStreamEmitter;
import pl.inder00.opensource.sectors.protocol.stream.StreamManager;
import reactor.core.publisher.Flux;

import java.util.*;

public class SectorsAPI implements ISectorsAPI {

    /**
     * Data
     */
    private Plugin plugin;

    /**
     * Implementation
     */
    public SectorsAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Message> ISectorsStream.Manager<T> openStream(ISector sector, ISectorsStream.Data<T> streamListener) throws Throwable {

        // protobuf messages
        Map<Class<?>, Integer> channelsMap = new HashMap<>();
        Class<?> protobufTypeClass = TypeResolver.resolveRawArgument(ISectorsStream.Data.class, streamListener.getClass());

        // protobuf classes
        for(Class<?> protobufClass : Lists.asList(protobufTypeClass, protobufTypeClass.getClasses())){
            try {
                if(com.google.protobuf.Message.class.isAssignableFrom(protobufClass)) {
                    if(protobufClass.getMethod("newBuilder").invoke(protobufClass) != null){
                        channelsMap.put(protobufClass, protobufClass.hashCode());
                    }
                }
            } catch (Throwable ignore){}
        }

        // check channels map size
        if(channelsMap.size() == 0) throw new InvalidProtobufMessageException();

        // sector stream implementation
        IStreamEmiter<Payload> streamEmiter = new DefaultStreamEmitter<>();
        ISectorsStream<T> sectorsStream = new SectorsStreamImpl<>(this.plugin, sector, streamListener, streamEmiter, channelsMap);

        // open stream
        try {

            // register channel ids
            channelsMap.values().forEach(record -> StreamManager.registerStream(record, sectorsStream));

            // request channel
            sector.getSocket().requestChannel(Flux.create(streamEmiter)).subscribe(sectorsStream.getSubsriber());

        } catch (Throwable e){

            // unregister channel ids
            channelsMap.values().forEach(StreamManager::removeStream);

            // throw error and return null
            e.printStackTrace();
            return null;

        }
        // return sectors stream
        return sectorsStream.getStreamManager();

    }

}
