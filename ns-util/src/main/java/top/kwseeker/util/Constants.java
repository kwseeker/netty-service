package top.kwseeker.util;

import java.nio.charset.Charset;

public interface Constants {
    String JVM_LOG_PATH = "./logs/";
    byte[] EMPTY_BYTES = new byte[0];
    Charset UTF_8 = Charset.forName("UTF-8");

    int THREAD_QUEUE_SIZE = 10000;
    int MIN_POOL_SIZE = 50;
    int MAX_POOL_SIZE = 500;

    int MIN_BOSS_POOL_SIZE = 10;
    int MAX_BOSS_POLL_SIZE = 50;

    int MIN_WORK_POOL_SIZE = 10;
    int MAX_WORK_POOL_SIZE = 250;
}
