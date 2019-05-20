package top.kwseeker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Connection;
import top.kwseeker.api.PacketReceiver;
import top.kwseeker.api.message.BindUserMessage;
import top.kwseeker.api.message.FastConnectMessage;
import top.kwseeker.api.message.HandShakeMessage;
import top.kwseeker.api.message.HeartbeatMessage;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.core.handler.BindUserHandler;
import top.kwseeker.core.handler.FastConnectHandler;
import top.kwseeker.core.handler.HandShakeHandler;
import top.kwseeker.core.handler.HeartBeatHandler;

/**
 *
 */
public class MessageDispatcher implements PacketReceiver {

    public static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);

    public final BindUserHandler bindUserHandler = new BindUserHandler();
    public final HandShakeHandler handShakeHandler = new HandShakeHandler();
    public final FastConnectHandler fastConnectHandler = new FastConnectHandler();
    public final HeartBeatHandler heartBeatHandler = new HeartBeatHandler();

    @Override
    public void onReceive(Packet packet, Connection connection) {
        Command cmd = Command.toCMD(packet.cmd);

        switch (cmd) {
            case HANDSHAKE:
                LOG.info("Received Handshake message ...");
                handShakeHandler.handle(new HandShakeMessage(packet, connection));
                break;
            case BIND:
                LOG.info("Received Bind message ...");
                bindUserHandler.handle(new BindUserMessage(packet, connection));
                break;
            case HEARTBEAT:
                heartBeatHandler.handle(new HeartbeatMessage(connection));
                break;
            case FAST_CONNECT:
                LOG.info("Received Fast Connect message ...");
                fastConnectHandler.handle(new FastConnectMessage(packet, connection));
                break;
            case UNKNOWN:
                LOG.error("Received Unknown message ...");
                break;
        }
    }

}
