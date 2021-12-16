package pl.inder00.opensource.sectors.commons.encryption;

import java.math.BigInteger;
import java.security.Key;

public interface IKeyExchangeProvider {

    /**
     * Returns key size
     *
     * @return int
     */
    int getKeysize();

    /**
     * Returns prime number
     *
     * @return BigInteger
     */
    BigInteger getPrime();

    /**
     * Returns prime generator number
     *
     * @return BigInteger
     */
    BigInteger getPrimeGenerator();

    /**
     * Returns public key
     *
     * @return BigInteger
     */
    BigInteger getPublicKey();

    /**
     * Generates AES key for encryption purposes
     *
     * @param publicKey Client public key
     * @return AES Key
     * @throws Exception
     */
    Key generateKey( BigInteger publicKey ) throws Exception;

}
