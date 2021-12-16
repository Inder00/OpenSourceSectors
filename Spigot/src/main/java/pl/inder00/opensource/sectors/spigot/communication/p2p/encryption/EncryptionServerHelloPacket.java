package pl.inder00.opensource.sectors.spigot.communication.p2p.encryption;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class EncryptionServerHelloPacket implements IPrototypeListener<EncryptionPacket.ServerHello> {

    /**
     * Data
     */
    private JavaPlugin plugin;
    private ISectorServer server;
    private final IKeyExchangeProvider keyExchangeProvider;

    /**
     * Implementation
     */
    public EncryptionServerHelloPacket(JavaPlugin plugin, ISectorServer server, IKeyExchangeProvider keyExchangeProvider) {
        this.plugin = plugin;
        this.server = server;
        this.keyExchangeProvider = keyExchangeProvider;

    }

    @Override
    public void onReceivedData(ISectorConnection connection, EncryptionPacket.ServerHello message) throws Exception {

        // check message
        if( this.keyExchangeProvider != null && message.hasPublicKey() )
        {

            // set encryption key
            Key encryptionKey = this.keyExchangeProvider.generateKey(new BigInteger(message.getPublicKey().toByteArray()));
            connection.getEncryptionProvider().setupKey(encryptionKey);

            // generate random test
            String randomTest = Hashing.hmacSha512(encryptionKey).toString();

            // send encryption response
            connection.sendData(EncryptionPacket.EncryptionResponse.newBuilder()
                            .setExpectedTest(randomTest)
                            .setEncryptedTest(ByteString.copyFrom(connection.getEncryptionProvider().encryptData(randomTest.getBytes(StandardCharsets.UTF_8))))
                    .build());


        }

    }

}
