package top.kwseeker.api;

import top.kwseeker.util.NetUtil;

public final class ClientLocation {

    private String host;
    private String osName;
    private String clientVersion;
    private String deviceId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public ClientLocation setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public static ClientLocation from(SessionContext context) {
        ClientLocation config = new ClientLocation();
        config.osName = context.osName;
        config.clientVersion = context.clientVersion;
        config.deviceId = context.deviceId;
        config.host = NetUtil.getLocalIp();
        return config;
    }
}
