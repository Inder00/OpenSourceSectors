package pl.inder00.opensource.sectors.communication;

import io.netty.buffer.ByteBuf;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.basic.IPositionData;
import pl.inder00.opensource.sectors.basic.impl.PositionDataImpl;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

import java.util.UUID;

public class PositionDataPacket implements IPacket {

    /**
     * Data
     */
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public PositionDataPacket(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ByteBuf bufferIn, ByteBuf bufferOut) throws Throwable {

        // read uuid
        UUID uniqueId = UUID.fromString( ByteBufExtension.readString(bufferIn, 64) );

        // read position
        double x = bufferIn.readDouble();
        double y = bufferIn.readDouble();
        double z = bufferIn.readDouble();
        float yaw = bufferIn.readFloat();
        float pitch = bufferIn.readFloat();

        // insert data into cache
        IPositionData positionData = new PositionDataImpl( uniqueId, x, y, z, yaw, pitch );
        PositionDataManager.cachePositionData(positionData);

    }
}
