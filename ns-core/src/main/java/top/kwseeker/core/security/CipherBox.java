package top.kwseeker.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.util.Pair;
import top.kwseeker.util.crypto.RSAUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class CipherBox {

    private static final Logger LOG = LoggerFactory.getLogger(CipherBox.class);

    public static final int AES_KEY_LENGTH = 16;
    public static final CipherBox INSTANCE = new CipherBox();
    private SecureRandom random = new SecureRandom();
    //私钥
    private RSAPrivateKey privateKey;
    //公钥
    private RSAPublicKey publicKey;

    public CipherBox() {
        init();
    }

    /**
     * 生成公钥和私钥密钥对
     */
    public void init() {
        readFromFile();
        if (publicKey == null || privateKey == null) {
            Pair<RSAPublicKey, RSAPrivateKey> pair = RSAUtils.genKeyPair();
            //生成公钥和私钥
            publicKey = pair.key;
            privateKey = pair.value;
            //模
            String modulus = publicKey.getModulus().toString();
            //公钥指数
            String public_exponent = publicKey.getPublicExponent().toString();
            //私钥指数
            String private_exponent = privateKey.getPrivateExponent().toString();
            //使用模和指数生成公钥和私钥
            publicKey = RSAUtils.getPublicKey(modulus, public_exponent);
            privateKey = RSAUtils.getPrivateKey(modulus, private_exponent);
            writeToFile();
        }
    }

    /**
     * 将公私钥对base64编码后存储到文件中（private.key public.key）
     */
    private void writeToFile() {
        try {
            String publicKeyStr = RSAUtils.encodeBase64(publicKey);
            String privateKeyStr = RSAUtils.encodeBase64(privateKey);
            String path = this.getClass().getResource("/").getPath();
            LOG.info("Key file path: " + path);
            FileOutputStream out = new FileOutputStream(new File(path, "private.key"));
            out.write(privateKeyStr.getBytes());
            out.close();
            out = new FileOutputStream(new File(path, "public.key"));
            out.write(publicKeyStr.getBytes());
            out.close();
            System.out.println("write key=" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取公钥和私钥
     */
    private void readFromFile() {
        try {
            InputStream in = this.getClass().getResourceAsStream("/private.key");
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            privateKey = (RSAPrivateKey) RSAUtils.decodePrivateKey(new String(buffer));
            in = this.getClass().getResourceAsStream("/public.key");
            in.read(buffer);
            in.close();
            publicKey = (RSAPublicKey) RSAUtils.decodePublicKey(new String(buffer));
            System.out.println("save privateKey=" + privateKey);
            System.out.println("save publicKey=" + publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RSAPrivateKey getPrivateKey() {
        if (privateKey == null) init();
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        if (publicKey == null) init();
        return publicKey;
    }

    public byte[] randomAESKey() {
        byte[] bytes = new byte[AES_KEY_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] randomAESIV() {
        byte[] bytes = new byte[AES_KEY_LENGTH];
        random.nextBytes(bytes);
        return bytes;
    }

    public byte[] mixKey(byte[] clientKey, byte[] serverKey) {
        byte[] sessionKey = new byte[AES_KEY_LENGTH];
        for (int i = 0; i < AES_KEY_LENGTH; i++) {
            byte a = clientKey[i];
            byte b = serverKey[i];
            int sum = Math.abs(a + b);
            int c = (sum % 2 == 0) ? a ^ b : b ^ a;
            sessionKey[i] = (byte) c;
        }
        return sessionKey;
    }

    public RsaCipher getRsaCipher() {
        return new RsaCipher(privateKey, publicKey);
    }
}
