package top.kwseeker.api;

public interface Message {

    Connection getConnection();

    void send();

    void sendRaw();
}
