package top.kwseeker.api;

public interface Response {

    void send(byte[] body);

    void sendRaw(byte[] body);

    void sendError(byte[] reason);

    void send(String body);

    void sendRaw(String body);

    void sendError(String reason);
}
