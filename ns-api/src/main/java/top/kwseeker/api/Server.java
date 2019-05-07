package top.kwseeker.api;

public interface Server {

    void init();

    void start();

    void stop();

    boolean isRunning();
}
