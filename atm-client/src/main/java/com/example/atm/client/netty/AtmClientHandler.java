package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmMessage;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AtmClientHandler extends SimpleChannelInboundHandler<AtmMessage> {
}
