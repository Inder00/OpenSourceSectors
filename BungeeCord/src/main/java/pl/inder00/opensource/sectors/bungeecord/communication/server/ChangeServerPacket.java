package pl.inder00.opensource.sectors.bungeecord.communication.server;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.bungeecord.communication.exceptions.InvalidPlayerException;
import pl.inder00.opensource.sectors.bungeecord.communication.exceptions.InvalidSectorException;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.ServerPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;

import java.util.UUID;

public class ChangeServerPacket implements IPrototypeListener<ServerPacket.ChangeServerPacket> {

    @Override
    public void onReceivedData(ISectorConnection connection, ServerPacket.ChangeServerPacket message) throws Exception {

        // player
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(message.getPlayerName());
        if (player == null) throw new InvalidPlayerException();

        // target sector
        UUID sectorId = ProtocolSerializationUtils.deserialize(message.getSector().getUniqueId());
        ISector targetSector = Sectors.getSectorManager().getByKey(sectorId);
        if (targetSector == null) throw new InvalidSectorException();

        // move player to target server
        player.connect(targetSector.getServerInfo());

    }
}
