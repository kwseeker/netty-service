package top.kwseeker.api;

import top.kwseeker.api.protocol.Packet;

public interface PacketReceiver {

    void onReceive(Packet packet, Connection connection);
}
