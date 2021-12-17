package pl.inder00.opensource.sectors.protocol.prototype.impl;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import net.jodah.typetools.TypeResolver;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrototypeManagerImpl implements IPrototypeManager {

    // List of prototypes
    private Map<UUID, MessageLite> prototypeList = new ConcurrentHashMap<>();
    private Map<IPrototypeListener, UUID> listenersList = new ConcurrentHashMap<>();

    @Override
    public void registerPrototype(Class<? extends MessageLite> messageLite) throws Exception {

        // protobuf messages
        if (!MessageLite.class.isAssignableFrom(messageLite) || messageLite.getMethod("newBuilder").invoke(messageLite) == null || messageLite.getMethod("getDefaultInstance").invoke(null) == null)
            throw new ProtocolException("Invalid prototype");

        // add prototype to the list
        this.prototypeList.put(ProtocolSerializationUtils.getClassHash(messageLite), (MessageLite) messageLite.getMethod("getDefaultInstance").invoke(null));

    }

    @Override
    public MessageLiteOrBuilder getPrototypeByCode(UUID messageCode) {
        return this.prototypeList.get(messageCode);
    }

    @Override
    public UUID getCodeByPrototype(MessageLiteOrBuilder messageLiteOrBuilder) {
        return ProtocolSerializationUtils.getClassHash(messageLiteOrBuilder);
    }

    @Override
    public <T extends MessageLite> void registerListener(IPrototypeListener<T> listener) throws Exception {

        // protobuf messages
        Class<?> protobufTypeClass = TypeResolver.resolveRawArgument(IPrototypeListener.class, listener.getClass());
        if (!MessageLite.class.isAssignableFrom(protobufTypeClass) || protobufTypeClass.getMethod("newBuilder").invoke(protobufTypeClass) == null || protobufTypeClass.getMethod("getDefaultInstance").invoke(null) == null)
            throw new ProtocolException("Invalid protobuf listener");

        // add listener to the list
        this.listenersList.put(listener, ProtocolSerializationUtils.getClassHash(listener));

    }

    @Override
    public <T extends MessageLite> void unregisterListener(IPrototypeListener<T> listener) throws Exception {

        // protobuf type class
        Class<?> protobufTypeClass = TypeResolver.resolveRawArgument(IPrototypeListener.class, listener.getClass());
        if (!MessageLite.class.isAssignableFrom(protobufTypeClass) || protobufTypeClass.getMethod("newBuilder").invoke(protobufTypeClass) == null || protobufTypeClass.getMethod("getDefaultInstance").invoke(null) == null)
            throw new ProtocolException("Invalid protobuf listener");

        // add listener to the list
        this.listenersList.remove(listener);

    }

    @Override
    public <T extends MessageLite> List<IPrototypeListener<T>> getListenersByPrototype(T prototype) throws Exception {

        // protobuf type class
        if (prototype.getClass().getMethod("getDefaultInstance").invoke(null) == null)
            throw new ProtocolException("Invalid prototype class");

        // calculate class hash
        UUID classHash = ProtocolSerializationUtils.getClassHash(prototype);

        // create new empty list
        List<IPrototypeListener<T>> listenerList = new ArrayList<>();

        // loop all listeners
        this.listenersList.forEach((listener, code) -> {
            if (classHash.equals(code)) {
                listenerList.add(listener);
            }
        });

        // return listener list
        return listenerList;
    }
}
