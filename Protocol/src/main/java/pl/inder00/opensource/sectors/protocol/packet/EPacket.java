package pl.inder00.opensource.sectors.protocol.packet;

public enum EPacket {

    /**
     * List of ids
     */
    CONFIGURATION_REQUEST((short) (1 << 0)),
    SERVER_CHANGE((short) (1 << 1)),
    DATA_EXCHANGE((short) (1 << 2)),
    POSITION_DATA_EXCHANGE((short) (1 << 3));

    private short id;

    /**
     * Implementation
     */
    EPacket(short id) {
        this.id = id;
    }

    /**
     * Get packet id
     * @return short
     */
    public short getPacketId() {
        return id;
    }
}
