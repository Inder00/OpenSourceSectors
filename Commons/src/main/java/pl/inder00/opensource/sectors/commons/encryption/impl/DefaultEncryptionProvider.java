package pl.inder00.opensource.sectors.commons.encryption.impl;

import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;

import javax.crypto.Cipher;
import java.security.Key;

public class DefaultEncryptionProvider implements IEncryptionProvider {

    /**
     * Data
     */
    protected Key key;
    protected Cipher encryptionCipher;
    protected Cipher decryptionCipher;
    protected boolean encryptionEnabled;

    /**
     * Implementation
     */
    public DefaultEncryptionProvider() {

        // encryption
        this.encryptionEnabled = false;

    }

    /**
     * Implementation
     */
    public DefaultEncryptionProvider(Key key) throws Exception {
        this(key, false);
    }

    /**
     * Implementation
     */
    public DefaultEncryptionProvider(Key key, boolean encryptionEnabled) throws Exception {

        // key, encryption
        this.key = key;
        this.encryptionEnabled = encryptionEnabled;

        // setup key
        this.setupKey(this.key, this.key.getAlgorithm());

    }

    @Override
    public void setupKey(Key key) throws Exception {
        this.setupKey(key, key.getAlgorithm());
    }

    @Override
    public void setupKey(Key key, String transformation) throws Exception {

        // key, encryption
        this.key = key;

        // encryption
        this.encryptionCipher = Cipher.getInstance(transformation);
        this.encryptionCipher.init(Cipher.ENCRYPT_MODE, this.key);

        // decryption
        this.decryptionCipher = Cipher.getInstance(transformation);
        this.decryptionCipher.init(Cipher.DECRYPT_MODE, this.key);

    }

    @Override
    public byte[] encryptData(byte[] data) {

        // encrypt data
        try {
            return this.encryptionCipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // return null
        return null;
    }

    @Override
    public byte[] decryptData(byte[] data) {

        // decrypt data
        try {
            return this.decryptionCipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // return null
        return null;

    }

    @Override
    public boolean isEncryptionEnabled() {
        return this.encryptionEnabled && this.key != null && this.decryptionCipher != null && this.encryptionCipher != null;
    }

    @Override
    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

}
