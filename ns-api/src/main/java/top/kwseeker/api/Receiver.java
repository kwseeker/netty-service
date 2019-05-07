package top.kwseeker.api;

import top.kwseeker.api.protocol.Packet;

public interface Receiver {

    void onReceive(Packet packet, Connection connection);
}
