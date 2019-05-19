package top.kwseeker.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Connection;
import top.kwseeker.api.Receiver;
import top.kwseeker.api.protocol.Command;
import top.kwseeker.api.protocol.Packet;
import top.kwseeker.core.handler.BindHandler;
import top.kwseeker.core.handler.HandShakeHandler;
import top.kwseeker.core.message.BindMessage;
import top.kwseeker.core.message.HandShakeMessage;

/**
 *
 */
public class MessageDispatcher implements Receiver {

    public static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);

    public final BindHandler bindHandler = new BindHandler();
    public final HandShakeHandler handShakeHandler = new HandShakeHandler();
    //public final FastConnectHandler fastConnectHandler = new FastConnectHandler();
    //public final HeartBeatHandler heartBeatHandler = new HeartbeatHandler();

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
                bindHandler.handle(new BindMessage(packet, connection));
                break;
            case UNKNOWN:
                LOG.error("Received Unknown message ...");
                break;
        }
    }

}