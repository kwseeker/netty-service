package top.kwseeker.api;

public interface Client {

    void init();

    void start();

    void close(final String cause);

    boolean isEnabled();

    boolean isConnected();

    void resetHbTimes();

    int increaseAndGetHbTimes();

    String getHost();

    int getPort();

    String getUri();
}
