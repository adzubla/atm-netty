/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.atm.netty.client;

import com.example.atm.netty.codec.header.HeaderData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class ChatClient {

    private final String host;
    private final int port;
    private final HeaderData headerData;

    private EventLoopGroup group;
    private Channel channel;

    public ChatClient(String id, String host, int port) {
        this.host = host;
        this.port = port;
        this.headerData = new HeaderData(id);
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChatClientInitializer());

        channel = b.connect(host, port).sync().channel();
    }

    public void write(String line) throws InterruptedException {
        channel.attr(HeaderData.HEADER_DATA_ATTRIBUTE_KEY).set(headerData);

        ChannelFuture lastWriteFuture = channel.writeAndFlush(line);
        if (lastWriteFuture != null) {
            lastWriteFuture.sync();
        }
    }

    public void close() throws InterruptedException {
        channel.closeFuture().sync();
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

}
