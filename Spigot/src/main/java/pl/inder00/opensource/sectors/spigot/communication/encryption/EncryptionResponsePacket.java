package pl.inder00.opensource.sectors.spigot.communication.encryption;

import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.nio.charset.StandardCharsets;

public class EncryptionResponsePacket implements IPrototypeListener<EncryptionPacket.EncryptionResponse> {

    /**
     * Data
     */
    private ISectorClient sectorClient;

    /**
     * Implementation
     */
    public EncryptionResponsePacket(ISectorClient sectorClient) {
        this.sectorClient = sectorClient;
    }

    @Override
    public void onReceivedData(EncryptionPacket.EncryptionResponse message) throws Exception {

        // process test
        String decryptedTest = new String(this.sectorClient.getEncryptionProvider().decryptData( message.getEncryptedTest().toByteArray() ), StandardCharsets.UTF_8);
        boolean testPassed = decryptedTest.equals(message.getExpectedTest());

        // send response to server
        this.sectorClient.sendData(EncryptionPacket.EncryptionFinish.newBuilder()
                .setCode(testPassed ? EncryptionPacket.EncryptionCode.OK : EncryptionPacket.EncryptionCode.ERROR)
                .build());

        // check does encryption has been initialized
        if(testPassed)
        {

            // enable encryption
            this.sectorClient.getEncryptionProvider().setEncryptionEnabled( true );

        }

        // fire client ready event
        this.sectorClient.getClientListener().onClientReady(this.sectorClient);

    }


}
