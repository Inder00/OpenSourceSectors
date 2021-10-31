package pl.inder00.opensource.sectors.protocol.packet;

import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {

    /**
     * List of packets
     */
    private static final ConcurrentHashMap<Short, IPacket> packetList = new ConcurrentHashMap<Short, IPacket>();

    /**
     * Register packet
     *
     * @param id     Packet id
     * @param packet Packet
     */
    public static void registerPacket(short id, IPacket packet) {
        packetList.put(id, packet);
    }

    /**
     * Register packet
     *
     * @param ePacket Packet id enum
     * @param packet  Packet
     */
    public static void registerPacket(EPacket ePacket, IPacket packet) {
        registerPacket(ePacket.getPacketId(), packet);
    }

    /**
     * Gets packet by the id
     *
     * @param id
     * @return
     */
    public static IPacket getPacketById(short id) {
        return packetList.get(id);
    }
}
