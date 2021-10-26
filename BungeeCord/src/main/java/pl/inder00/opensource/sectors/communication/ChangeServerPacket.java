package pl.inder00.opensource.sectors.communication;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.communication.exceptions.InvalidPlayerException;
import pl.inder00.opensource.sectors.communication.exceptions.InvalidSectorException;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

import java.util.UUID;

public class ChangeServerPacket implements IPacket {

    /**
     * Data
     */
    private Plugin plugin;

    /**
     * Implementation
     */
    public ChangeServerPacket(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ByteBuf bufferIn, ByteBuf bufferOut) throws Throwable {

        // player
        String name = ByteBufExtension.readString( bufferIn, 16 );
        ProxiedPlayer player = this.plugin.getProxy().getPlayer( name );
        if( player == null ) throw new InvalidPlayerException();

        // target sector
        UUID sectorId = UUID.fromString( ByteBufExtension.readString( bufferIn, 64 ) );
        ISector targetSector = SectorManager.getSectorByUniqueId( sectorId );
        if(targetSector == null) throw new InvalidSectorException();

        // move player to target server
        player.connect(targetSector.getServerInfo());


    }
}
