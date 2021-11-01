package pl.inder00.opensource.sectors.bungeecord.protocol;

import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.protocol.IServerAcceptor;

import java.util.UUID;

public class DefaultServerAcceptor implements IServerAcceptor {

    @Override
    public boolean acceptConnection(UUID uniqueId) {
        return Sectors.getSectorManager().getByKey(uniqueId) != null;
    }
}
