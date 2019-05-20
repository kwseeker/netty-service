package top.kwseeker.api;

public interface Client {

    //客户端初始化
    void init();

    //客户端启动
    void start();

    //客户端停止
    void stop();

    //客户端是否连接服务端
    boolean isConnected();

    String getUri();
}
