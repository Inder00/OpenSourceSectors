package pl.inder00.opensource.sectors.protocol.prototype;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import pl.inder00.opensource.sectors.protocol.exceptions.InvalidProtobufMessageException;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IPrototypeManager {

    /**
     * Registers a message lite or builder to prototype list
     *
     * @param messageLite Protobuf Message Prototype
     * @throws InvalidProtobufMessageException
     */
    void registerPrototype(Class<? extends MessageLite> messageLite) throws Exception;

    /**
     * Returns prototype by class hash code
     * May return null if not exists
     *
     * @param messageCode Integer
     * @return MessageLiteOrBuilder
     */
    MessageLiteOrBuilder getPrototypeByCode(int messageCode);

    /**
     * Returns prototype code (class hash code) by prototype
     *
     * @param messageLiteOrBuilder Protobuf MessageLiteOrBuilder
     * @return Integer
     */
    int getCodeByPrototype(MessageLiteOrBuilder messageLiteOrBuilder);

    /**
     * Register prototype listener
     *
     * @param listener Prototype listener
     * @throws InvalidProtobufMessageException
     */
    <T extends MessageLite> void registerListener(IPrototypeListener<T> listener) throws Exception;

    /**
     * Unregister prototype listener
     *
     * @param listener Prototype listener
     * @throws InvalidProtobufMessageException
     */
    <T extends MessageLite> void unregisterListener(IPrototypeListener<T> listener) throws Exception;

    /**
     * Returns list of prototype listeners
     *
     * @param prototype Protobuf prototype
     * @return List of prototype listeners
     * @throws ProtocolException
     */
    <T extends MessageLite> List<IPrototypeListener<T>> getListenersByPrototype(T prototype) throws Exception;

}
