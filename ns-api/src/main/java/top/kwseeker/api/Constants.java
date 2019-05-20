package top.kwseeker.api;

import java.nio.charset.Charset;

/**
 * 常量定义
 *
 * 定义在接口中的话，默认都是 public static final 的
 */
public interface Constants {
    Charset UTF_8 = Charset.forName("UTF-8");

    int HEADER_LEN = 13;
    int MAX_PACKET_SIZE = 1024;

    int COMPRESS_LIMIT = 1024 * 10;
    byte CRYPTO_FLAG = 0x01;
    byte COMPRESS_FLAG = 0x02;
    long TIME_DELAY = 1L;

    int HEARTBEAT_TIME = 1000*60*5; //5min
}
