package pl.inder00.opensource.sectors.bungeecord.protocol;

import com.google.protobuf.ByteString;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorServerListener;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.HandshakePacket;

import java.util.logging.Level;

public class AbstractMasterServerListener extends AbstractSectorServerListener {

    /**
     * Data
     */
    private Sectors plugin;

    /**
     * Implementation
     */
    public AbstractMasterServerListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onServerCreated(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Master server has been successfully created.");

    }

    @Override
    public void onServerBoundSuccessfully(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Master server has been successfully bound.");

    }

    @Override
    public void onServerBoundFailed(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Failed to bind master server. Stopping proxy...");

        // stop proxy
        this.plugin.getProxy().stop();

    }

    @Override
    public void onServerClosed(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Master server has been successfully closed.");

    }

    @Override
    public void onServerException(ISectorServer server, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Master server occurred an error.", throwable);

    }

    @Override
    public void onServerClientException(ISectorServer server, ISectorConnection connection, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Connection to master server occurred an error.", throwable);

    }

    @Override
    public void onServerClientConnect(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Connected to master server @ " + connection.getAddress().toString());

        // check does encryption is enabled
        if(Sectors.getKeyExchangeProvider() != null)
        {

            // key exchange provider
            IKeyExchangeProvider keyExchangeProvider = Sectors.getKeyExchangeProvider();

            // send client hello
            connection.sendData(EncryptionPacket.ClientHello.newBuilder()
                            .setKeySize(keyExchangeProvider.getKeysize())
                            .setPrime(ByteString.copyFrom(keyExchangeProvider.getPrime().toByteArray()))
                            .setPrimeGenerator(ByteString.copyFrom(keyExchangeProvider.getPrimeGenerator().toByteArray()))
                            .setPublicKey(ByteString.copyFrom(keyExchangeProvider.getPublicKey().toByteArray()))
                    .build());

        }
        else
        {

            // send handshake
            connection.sendData(HandshakePacket.ServerHandshake.newBuilder()
                    .setVersion(this.plugin.getDescription().getVersion())
                    .build());

            // fire ready event
            server.getServerListener().onServerClientReady( server, connection );

        }

    }

    @Override
    public void onServerClientDisconnect(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Disconnected from master server @ " + connection.getAddress().toString());

    }

    @Override
    public void onServerClientReady(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Connection is ready @ " + connection.getAddress().toString());

    }
}
