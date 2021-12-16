package pl.inder00.opensource.sectors.protocol.listeners;

import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;

public abstract class AbstractSectorClientListener implements ISectorClientListener {

    @Override
    public void onClientCreated(ISectorClient client) {

    }

    @Override
    public void onClientReady(ISectorClient client) {

    }

    @Override
    public void onClientConnected(ISectorClient client) {

    }

    @Override
    public void onClientDisconnected(ISectorClient client) {

    }

    @Override
    public void onClientException(ISectorClient client, Throwable throwable) {

    }
}
