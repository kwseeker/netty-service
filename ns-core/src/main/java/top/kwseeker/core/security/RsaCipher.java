package top.kwseeker.core.security;

import top.kwseeker.api.Cipher;
import top.kwseeker.util.crypto.RSAUtils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class RsaCipher implements Cipher {
    private final RSAPrivateKey privateKey;

    private final RSAPublicKey publicKey;

    public RsaCipher(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return RSAUtils.decryptByPrivateKey(data, privateKey);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return RSAUtils.encryptByPublicKey(data, publicKey);
    }
}
