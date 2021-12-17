package pl.inder00.opensource.sectors.commons.encryption;

import java.security.Key;

public interface IEncryptionProvider {

    /**
     * Setups encryption key
     *
     * @param key            Key
     * @param transformation Transformation
     */
    void setupKey(Key key, String transformation) throws Exception;

    /**
     * Setups encryption key
     *
     * @param key Key
     */
    void setupKey(Key key) throws Exception;

    /**
     * Encrypts data
     *
     * @param data Input bytes
     * @return Encrypted data
     */
    byte[] encryptData(byte[] data);

    /**
     * Decrypts data
     *
     * @param data Input bytes
     * @return Decrypted data
     */
    byte[] decryptData(byte[] data);

    /**
     * Returns boolean representing does encryption is enabled
     *
     * @return boolean
     */
    boolean isEncryptionEnabled();

    /**
     * Sets encryption enabled
     *
     * @param encryptionEnabled boolean
     */
    void setEncryptionEnabled(boolean encryptionEnabled);


}
