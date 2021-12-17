package pl.inder00.opensource.sectors.commons.encryption.impl;

import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;

import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

public class DefaultDiffieHellmanProvider implements IKeyExchangeProvider {

    /**
     * Data
     */
    protected final int keysize;
    protected final BigInteger secretKey;
    protected final SecureRandom random;
    protected final BigInteger prime;
    protected final BigInteger primeGenerator;
    protected final BigInteger publicKey;

    /**
     * Implementation
     */
    public DefaultDiffieHellmanProvider(int keysize) throws Exception {

        // create random implementation
        this.random = new SecureRandom();

        // create diffie hellman key
        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
        paramGen.init(keysize, this.random);
        AlgorithmParameters params = paramGen.generateParameters();
        DHParameterSpec dhParameterSpec = params.getParameterSpec(DHParameterSpec.class);

        // set data
        this.keysize = keysize;
        this.secretKey = new BigInteger(keysize, this.random);
        this.prime = dhParameterSpec.getP();
        this.primeGenerator = dhParameterSpec.getG();
        this.publicKey = this.primeGenerator.modPow(this.secretKey, this.prime);

    }

    /**
     * Implementation
     */
    public DefaultDiffieHellmanProvider(int keysize, BigInteger prime, BigInteger primeGenerator) throws Exception {

        // create random implementation
        this.random = new SecureRandom();

        // set data
        this.keysize = keysize;
        this.secretKey = new BigInteger(keysize, this.random);
        this.prime = prime;
        this.primeGenerator = primeGenerator;
        this.publicKey = this.primeGenerator.modPow(this.secretKey, this.prime);

    }

    @Override
    public int getKeysize() {
        return this.keysize;
    }

    @Override
    public BigInteger getPrime() {
        return this.prime;
    }

    @Override
    public BigInteger getPrimeGenerator() {
        return this.primeGenerator;
    }

    @Override
    public BigInteger getPublicKey() {
        return this.publicKey;
    }

    @Override
    public Key generateKey(BigInteger publicKey) throws Exception {

        // calculate shared key
        byte[] sharedKey = publicKey.modPow(this.secretKey, this.prime).toByteArray();

        // generate AES-Key
        return new SecretKeySpec(Arrays.copyOf(sharedKey, 16), "AES");

    }

}
