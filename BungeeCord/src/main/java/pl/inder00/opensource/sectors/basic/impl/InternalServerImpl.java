package pl.inder00.opensource.sectors.basic.impl;

import pl.inder00.opensource.sectors.basic.IInternalServer;

public class InternalServerImpl implements IInternalServer {

    /**
     * Data
     */
    private final String hostname;
    private final int port;

    /**
     * Implementation
     */
    public InternalServerImpl(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public int getPort() {
        return this.port;
    }

}
