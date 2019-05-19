package top.kwseeker.util;

import java.net.InetAddress;

public class NetUtil {

    private static String LOCAL_IP;

    public static String getLocalIp() {
        if(LOCAL_IP == null) {
            try {
                LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                LOCAL_IP = "127.0.0.1";
            }
        }
        return LOCAL_IP;
    }
}
