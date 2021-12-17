package pl.inder00.opensource.sectors.spigot.protocol;

import com.google.protobuf.ByteString;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorServerListener;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.HandshakePacket;
import pl.inder00.opensource.sectors.spigot.basic.ISector;

import java.util.logging.Level;

public class DefaultInternalServerListener extends AbstractSectorServerListener {

    /**
     * Data
     */
    private final IKeyExchangeProvider keyExchangeProvider;
    private final ISector sector;
    private final JavaPlugin plugin;

    /**
     * Implementation
     */
    public DefaultInternalServerListener(ISector sector, JavaPlugin plugin, IKeyExchangeProvider keyExchangeProvider) {
        this.sector = sector;
        this.plugin = plugin;
        this.keyExchangeProvider = keyExchangeProvider;
    }

    @Override
    public void onServerBoundFailed(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Failed to bound internal server. Stopping server.");

        // stop server
        this.plugin.getServer().shutdown();

    }

    @Override
    public void onServerCreated(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Internal server has been successfully created.");

    }

    @Override
    public void onServerBoundSuccessfully(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Internal server has been successfully bound.");

    }

    @Override
    public void onServerClosed(ISectorServer server) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Internal server has been successfully closed.");

    }

    @Override
    public void onServerException(ISectorServer server, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Internal server occurred an error.", throwable);

    }

    @Override
    public void onServerClientException(ISectorServer server, ISectorConnection connection, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Connection to internal server occurred an error.", throwable);

    }

    @Override
    public void onServerClientConnect(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Connected to internal server @ " + connection.getAddress().toString());

        // check does encryption is enabled
        if (this.keyExchangeProvider != null) {

            // send client hello
            connection.sendData(EncryptionPacket.ClientHello.newBuilder()
                    .setKeySize(this.keyExchangeProvider.getKeysize())
                    .setPrime(ByteString.copyFrom(this.keyExchangeProvider.getPrime().toByteArray()))
                    .setPrimeGenerator(ByteString.copyFrom(this.keyExchangeProvider.getPrimeGenerator().toByteArray()))
                    .setPublicKey(ByteString.copyFrom(this.keyExchangeProvider.getPublicKey().toByteArray()))
                    .build());

        } else {

            // fire ready event
            server.getServerListener().onServerClientReady(server, connection);

        }

    }

    @Override
    public void onServerClientDisconnect(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Disconnected from internal server @ " + connection.getAddress().toString());

        // disable encryption
        connection.getEncryptionProvider().setEncryptionEnabled( false );

    }

    @Override
    public void onServerClientReady(ISectorServer server, ISectorConnection connection) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Internal Connection is ready @ " + connection.getAddress().toString());

    }

}
