package pl.inder00.opensource.sectors.spigot.protocol;

import pl.inder00.opensource.sectors.spigot.Sectors;

import java.util.UUID;

public class DefaultServerAcceptor implements IServerAcceptor {

    @Override
    public boolean acceptConnection(UUID uniqueId) {
        return Sectors.getSectorManager().getByKey(uniqueId) != null;
    }
}
