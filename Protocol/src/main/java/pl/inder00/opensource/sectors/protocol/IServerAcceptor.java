package pl.inder00.opensource.sectors.protocol;

import java.util.UUID;

public interface IServerAcceptor {

    /**
     *
     * Function called on new connections
     *
     * @param uniqueId Remote sector unique id
     * @return bool
     */
    boolean acceptConnection(UUID uniqueId);

}
