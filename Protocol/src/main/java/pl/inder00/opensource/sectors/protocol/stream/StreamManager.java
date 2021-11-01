package pl.inder00.opensource.sectors.protocol.stream;

import com.google.protobuf.Message;
import pl.inder00.opensource.sectors.protocol.ISectorsStream;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StreamManager {

    /**
     * Static data
     */
    private static final ConcurrentMap<Integer, ISectorsStream<? extends Message>> streamData = new ConcurrentHashMap<>();

    /**
     * Adds stream to list
     * @param channelId channel id
     * @param sectorsStream Reference of ISectorsStream
     */
    public static void registerStream(int channelId, ISectorsStream<? extends Message> sectorsStream){
        streamData.put(channelId,sectorsStream);
    }

    /**
     * Removes stream from list
     * @param channelId channel id
     */
    public static void removeStream(int channelId){
        streamData.remove(channelId);
    }

    /**
     * Returns sector stream by channel id
     * Internal use only
     *
     * @param channelId channelId
     * @return ISectorsStream
     */
    public static ISectorsStream<? extends Message> getStreamByChannelId(int channelId){
        try {
            return streamData.entrySet().stream().filter(stream -> stream.getKey() == channelId).findFirst().get().getValue();
        } catch (NoSuchElementException elementException){
            return null;
        }
    }

}
