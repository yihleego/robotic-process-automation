package io.leego.rpa.server.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.rpa.util.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
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
public class WebSocketDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketDecoder.class);
    private final ObjectMapper objectMapper;

    public WebSocketDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void decode(ChannelHandlerContext context, TextWebSocketFrame msg, List<Object> out) {
        try {
            out.add(objectMapper.readValue(msg.text(), Message.class));
        } catch (IOException e) {
            logger.error("Invalid message: {}", msg, e);
        }
    }
}
