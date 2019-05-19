package top.kwseeker.api;

public interface Cipher {

    byte[] decrypt(byte[] data);
    byte[] encrypt(byte[] data);
}
