package top.kwseeker.api;

public interface MessageHandler<T extends Message> {
    void handle(T message);
}
