package pl.inder00.opensource.sectors.bungeecord.communication;

import com.google.protobuf.Message;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.inder00.opensource.sectors.bungeecord.communication.exceptions.InvalidPlayerException;
import pl.inder00.opensource.sectors.bungeecord.communication.exceptions.InvalidSectorException;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufChangeSectorData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

import java.util.UUID;

public class ChangeServerPacket implements IPacket<ProtobufChangeSectorData.ChangeSectorPacket> {

    /**
     * Data
     */
    private final Sectors sectors;

    /**
     * Implementation
     */
    public ChangeServerPacket(Sectors sectors) {
        this.sectors = sectors;
    }

    @Override
    public ProtobufChangeSectorData.ChangeSectorPacket.Builder getBuilder() {
        return ProtobufChangeSectorData.ChangeSectorPacket.newBuilder();
    }

    @Override
    public <Y extends Message> Y execute(ProtobufChangeSectorData.ChangeSectorPacket changeSectorPacket) throws Throwable {

        // player
        ProxiedPlayer player = this.sectors.getProxy().getPlayer(changeSectorPacket.getPlayerName());
        if (player == null) throw new InvalidPlayerException();

        // target sector
        UUID sectorId = new UUID(changeSectorPacket.getSector().getUniqueId().getMostSig(), changeSectorPacket.getSector().getUniqueId().getLeastSig());
        ISector targetSector = Sectors.getSectorManager().getByKey(sectorId);
        if (targetSector == null) throw new InvalidSectorException();

        // move player to target server
        player.connect(targetSector.getServerInfo());

        // return null
        return null;

    }
}