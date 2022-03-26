package io.leego.rpa.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Leego Yih
 */
public abstract class AbstractServer implements RpaServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);
    protected static final DefaultThreadFactory bossThreadFactory = new DefaultThreadFactory("server-boss", false);
    protected static final DefaultThreadFactory workerThreadFactory = new DefaultThreadFactory("server-worker", false);
    public static final int INIT = 0;
    public static final int STARTING = 1;
    public static final int RUNNING = 2;
    public static final int STOPPING = 3;
    public static final int STOPPED = 4;
    protected volatile int status = INIT;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected ServerBootstrap bootstrap;
    protected final Integer port;

    public AbstractServer(Integer port) {
        this.port = Objects.requireNonNull(port);
    }

    @Override
    public void startup() {
        if (status == STARTING || status == RUNNING) {
            logger.warn("RpaServer has already started");
            return;
        }
        if (status == STOPPING) {
            logger.warn("RpaServer cannot be started, because it is stopping");
            return;
        }
        synchronized (this) {
            if (status != INIT) {
                return;
            }
            status = STARTING;
            run();
            status = RUNNING;
            logger.info("RpaServer is running on port(s): {}", port);
        }
    }

    @Override
    public void shutdown() {
        if (status == INIT) {
            logger.warn("RpaServer is not started");
            return;
        }
        if (status == STOPPED) {
            logger.warn("RpaServer is already stopped");
            return;
        }
        if (bossGroup == null || workerGroup == null) {
            logger.error("RpaServer is not running");
            return;
        }
        logger.info("RpaServer is stopping");
        if (status == STOPPING) {
            return;
        }
        synchronized (this) {
            if (status == STOPPING) {
                return;
            }
            status = STOPPING;
            try {
                bossGroup.shutdownGracefully().sync();
                logger.info("Boss Group is stopped");
                workerGroup.shutdownGracefully().sync();
                logger.info("Worker Group is stopped");
            } catch (InterruptedException e) {
                logger.error("Failed to stop", e);
                Thread.currentThread().interrupt();
            }
            status = STOPPED;
            logger.info("RpaServer is stopped");
        }
    }

    public abstract void run();

}
