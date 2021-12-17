package pl.inder00.opensource.sectors.spigot.communication.encryption;

import com.google.protobuf.ByteString;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.commons.encryption.impl.DefaultDiffieHellmanProvider;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.math.BigInteger;
import java.security.Key;

public class EncryptionClientHelloPacket implements IPrototypeListener<EncryptionPacket.ClientHello> {

    /**
     * Data
     */
    private ISectorClient sectorClient;

    /**
     * Implementation
     */
    public EncryptionClientHelloPacket(ISectorClient sectorClient) {
        this.sectorClient = sectorClient;
    }

    @Override
    public void onReceivedData(EncryptionPacket.ClientHello message) throws Exception {

        // create key exchange implementation
        IKeyExchangeProvider keyExchangeProvider = new DefaultDiffieHellmanProvider(message.getKeySize(), new BigInteger(message.getPrime().toByteArray()), new BigInteger(message.getPrimeGenerator().toByteArray()));

        // generate key for encryption
        Key encryptionKey = keyExchangeProvider.generateKey(new BigInteger(message.getPublicKey().toByteArray()));

        // setup key for encryption on client connection
        this.sectorClient.getEncryptionProvider().setupKey(encryptionKey);

        // send public key to server
        this.sectorClient.sendData(EncryptionPacket.ServerHello.newBuilder()
                .setPublicKey(ByteString.copyFrom(keyExchangeProvider.getPublicKey().toByteArray()))
                .build());

    }


}
