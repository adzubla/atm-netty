package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmDecoder;
import com.example.atm.netty.codec.atm.AtmEncoder;
import com.example.atm.netty.codec.atm.AtmMessage;
import com.example.atm.netty.codec.crypto.CryptoDecoder;
import com.example.atm.netty.codec.crypto.CryptoEncoder;
import com.example.atm.netty.codec.header.HeaderData;
import com.example.atm.netty.codec.header.HeaderDecoder;
import com.example.atm.netty.codec.header.HeaderEncoder;
import com.example.atm.netty.codec.length.LengthFrameDecoder;
import com.example.atm.netty.codec.length.LengthPrepender;
import com.example.atm.netty.codec.mac.MacEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;

public final class AtmClient implements Closeable {

    private final String host;
    private final int port;
    private final boolean encryption;

    private EventLoopGroup group;
    private Channel channel;
    private final AtmClientHandler atmClientHandler;

    public AtmClient(String host, int port, boolean encryption, AtmClientHandler atmClientHandler) {
        this.host = host;
        this.port = port;
        this.encryption = encryption;
        this.atmClientHandler = atmClientHandler;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFrameDecoder());
                        if (encryption) {
                            pipeline.addLast(new CryptoDecoder());
                        }
                        pipeline.addLast(new HeaderDecoder());
                        pipeline.addLast(new AtmDecoder());

                        pipeline.addLast(new LengthPrepender());
                        if (encryption) {
                            pipeline.addLast(new CryptoEncoder());
                        }
                        pipeline.addLast(new MacEncoder());
                        pipeline.addLast(new HeaderEncoder());
                        pipeline.addLast(new AtmEncoder());

                        pipeline.addLast(atmClientHandler);
                    }
                });

        channel = b.connect(host, port).sync().channel();
    }

    public void write(AtmMessage msg, boolean useMac) throws InterruptedException {
        channel.attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).set(HeaderData.DATA);
        channel.attr(MacEncoder.MAC_ENCODER_USE_ATTRIBUTE_KEY).set(useMac);

        ChannelFuture lastWriteFuture = channel.writeAndFlush(msg);
        if (lastWriteFuture != null) {
            lastWriteFuture.sync();
        }
    }

    public void ping(Long atmId) throws InterruptedException {
        channel.attr(HeaderData.HEADER_TYPE_ATTRIBUTE_KEY).set(HeaderData.PING);

        AtmMessage msg = new AtmMessage(atmId, "");

        ChannelFuture lastWriteFuture = channel.writeAndFlush(msg);
        if (lastWriteFuture != null) {
            lastWriteFuture.sync();
        }
    }

    @Override
    public void close() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}
