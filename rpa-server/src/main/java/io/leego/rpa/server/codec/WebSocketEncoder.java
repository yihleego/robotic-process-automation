package io.leego.rpa.server.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.rpa.util.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author Leego Yih
 */
@Component
@ChannelHandler.Sharable
public class WebSocketEncoder extends MessageToMessageEncoder<Message> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEncoder.class);
    private final ObjectMapper objectMapper;

    public WebSocketEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Message msg, List<Object> out) {
        try {
            out.add(new TextWebSocketFrame(objectMapper.writeValueAsString(msg)));
        } catch (IOException e) {
            logger.error("Invalid message: {}", msg, e);
        }
    }
}
