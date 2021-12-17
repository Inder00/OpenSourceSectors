package pl.inder00.opensource.sectors.protocol.listeners;

import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;

public class AbstractSectorServerListener implements ISectorServerListener {

    @Override
    public void onServerCreated(ISectorServer server) {

    }

    @Override
    public void onServerBoundSuccessfully(ISectorServer server) {

    }

    @Override
    public void onServerBoundFailed(ISectorServer server) {

    }

    @Override
    public void onServerClosed(ISectorServer server) {

    }

    @Override
    public void onServerException(ISectorServer server, Throwable throwable) {

    }

    @Override
    public void onServerClientException(ISectorServer server, ISectorConnection connection, Throwable throwable) {

    }

    @Override
    public void onServerClientConnect(ISectorServer server, ISectorConnection connection) {

    }

    @Override
    public void onServerClientDisconnect(ISectorServer server, ISectorConnection connection) {

    }

    @Override
    public void onServerClientReady(ISectorServer server, ISectorConnection connection) {

    }

}
