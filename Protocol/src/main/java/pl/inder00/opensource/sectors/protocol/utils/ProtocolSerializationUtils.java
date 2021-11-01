package pl.inder00.opensource.sectors.protocol.utils;

import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;

import java.util.UUID;

public class ProtocolSerializationUtils {

    /**
     * Deserialize ProtoUUID into a uuid
     *
     * @param protoUUID ProtoUUID
     * @return UUID
     */
    public static UUID deserialize(ProtobufGeneric.ProtoUUID protoUUID) {
        return new UUID(protoUUID.getMostSig(), protoUUID.getLeastSig());
    }

    /**
     * Serialize UUID into a ProtoUUID
     *
     * @param uuid UUID
     * @return ProtoUUID
     */
    public static ProtobufGeneric.ProtoUUID serialize(UUID uuid) {
        return ProtobufGeneric.ProtoUUID.newBuilder()
                .setMostSig(uuid.getMostSignificantBits())
                .setLeastSig(uuid.getLeastSignificantBits())
                .build();
    }

}

