package top.kwseeker.core.handler;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kwseeker.api.Constants;
import top.kwseeker.api.MessageHandler;
import top.kwseeker.api.SessionContext;
import top.kwseeker.api.event.HandshakeEvent;
import top.kwseeker.api.message.ErrorMessage;
import top.kwseeker.api.message.HandShakeMessage;
import top.kwseeker.api.message.HandshakeSuccessMessage;
import top.kwseeker.core.EventBus;
import top.kwseeker.core.security.AesCipher;
import top.kwseeker.core.security.CipherBox;
import top.kwseeker.core.security.ReusableSession;
import top.kwseeker.core.security.ReusableSessionManager;
import top.kwseeker.util.NetUtil;

public final class HandShakeHandler implements MessageHandler<HandShakeMessage> {
    public static final Logger LOGGER = LoggerFactory.getLogger(HandShakeHandler.class);

    @Override
    public void handle(HandShakeMessage message) {
        byte[] iv = message.iv;//AES密钥向量16
        byte[] clientKey = message.clientKey;//客户端随机数
        byte[] serverKey = CipherBox.INSTANCE.randomAESKey();//服务端随机数
        byte[] sessionKey = CipherBox.INSTANCE.mixKey(clientKey, serverKey);//会话密钥

        //1.校验客户端消息字段
        if (Strings.isNullOrEmpty(message.deviceId)
                || iv.length != CipherBox.AES_KEY_LENGTH
                || clientKey.length != CipherBox.AES_KEY_LENGTH) {
            ErrorMessage.from(message).setReason("Param invalid").send();
            return;
        }

        //2.重复握手判断
        SessionContext context = message.getConnection().getSessionContext();
        if (message.deviceId.equals(context.deviceId)) {
            ErrorMessage.from(message).setReason("Repeat handshake").send();
            return;
        }

        //3.更换会话密钥RSA=>AES(clientKey)
        context.changeCipher(new AesCipher(clientKey, iv));

        //4.生成可复用session, 用于快速重连
        ReusableSession session = ReusableSessionManager.INSTANCE.genSession(context);
        ReusableSessionManager.INSTANCE.cacheSession(session);

        //5.响应握手成功消息
        HandshakeSuccessMessage
                .from(message)
                .setServerKey(serverKey)
                .setServerHost(NetUtil.getLocalIp())
                .setServerTime(System.currentTimeMillis())
                .setHeartbeat(Constants.HEARTBEAT_TIME)
                .setSessionId(session.sessionId)
                .setExpireTime(session.expireTime)
                .send();

        //6.更换会话密钥AES(clientKey)=>AES(sessionKey)
        context.changeCipher(new AesCipher(sessionKey, iv));

        //7.保存client信息
        context.setOsName(message.osName)
                .setOsVersion(message.osVersion)
                .setClientVersion(message.clientVersion)
                .setDeviceId(message.deviceId);

        //8.触发握手成功事件
        EventBus.INSTANCE.post(new HandshakeEvent(message.getConnection(), Constants.HEARTBEAT_TIME));
        LOGGER.info("会话密钥：{}，clientKey={}, serverKey={}", sessionKey, clientKey, serverKey);
    }
}
