package top.kwseeker.api;

import top.kwseeker.api.protocol.Packet;

public interface Message {

    Packet getPacket();

    void send();

    void sendRaw();

    Connection getConnection();
}
