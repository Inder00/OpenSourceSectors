package pl.inder00.opensource.sectors.bungeecord.communication.encryption;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class EncryptionServerHelloPacket implements IPrototypeListener<EncryptionPacket.ServerHello> {

    @Override
    public void onReceivedData(ISectorConnection connection, EncryptionPacket.ServerHello message) throws Exception {

        // check message
        if (message.hasPublicKey()) {

            // set encryption key
            Key encryptionKey = Sectors.getKeyExchangeProvider().generateKey(new BigInteger(message.getPublicKey().toByteArray()));
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
