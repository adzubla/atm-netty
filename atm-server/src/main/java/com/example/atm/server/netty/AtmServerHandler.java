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
package com.example.atm.server.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a server-side channel.
 */
public class AtmServerHandler extends SimpleChannelInboundHandler<AtmMessage> {

    private final AtmServerListener listener;

    public AtmServerHandler(AtmServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        listener.onConnect(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AtmMessage msg) {
        listener.onMessage(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        listener.onDisconnect(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
