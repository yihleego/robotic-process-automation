package io.leego.rpa.server;

import io.leego.rpa.config.RpaProperties;
import io.leego.rpa.server.codec.WebSocketDecoder;
import io.leego.rpa.server.codec.WebSocketEncoder;
import io.leego.rpa.server.handler.RpaServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Leego Yih
 */
@Component
public class NioRpaServer extends AbstractServer {
    private static final Logger logger = LoggerFactory.getLogger(NioRpaServer.class);
    private final String path;
    private final Duration idleTimeout;
    private final RpaServerHandler rpaServerHandler;
    private final WebSocketDecoder websocketDecoder;
    private final WebSocketEncoder websocketEncoder;

    public NioRpaServer(RpaProperties rpaProperties, RpaServerHandler rpaServerHandler, WebSocketDecoder websocketDecoder, WebSocketEncoder websocketEncoder) {
        super(rpaProperties.getWebsocket().getPort());
        this.path = rpaProperties.getWebsocket().getPath();
        this.idleTimeout = rpaProperties.getWebsocket().getIdleTimeout();
        this.rpaServerHandler = rpaServerHandler;
        this.websocketDecoder = websocketDecoder;
        this.websocketEncoder = websocketEncoder;
    }

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup(1, bossThreadFactory);
        workerGroup = new NioEventLoopGroup(workerThreadFactory);
        bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast("httpCodec", new HttpServerCodec())
                                .addLast("httpChunked", new ChunkedWriteHandler())
                                .addLast("httpObjectAggregator", new HttpObjectAggregator(65535))
                                .addLast("websocketAggregator", new WebSocketFrameAggregator(65535))
                                .addLast("websocketProtocolHandler", new WebSocketServerProtocolHandler(path, true))
                                .addLast("idleStateHandler", new IdleStateHandler(idleTimeout.toMillis(), 0L, 0L, TimeUnit.MILLISECONDS))
                                .addLast("websocketDecoder", websocketDecoder)
                                .addLast("websocketEncoder", websocketEncoder)
                                .addLast("rpaServerHandler", rpaServerHandler);
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            if (!future.isSuccess()) {
                logger.error("Failed to bind on port(s): {}", port);
            }
        } catch (InterruptedException e) {
            logger.error("Failed to run server on port(s): {}", port, e);
            Thread.currentThread().interrupt();
        }
    }
}
