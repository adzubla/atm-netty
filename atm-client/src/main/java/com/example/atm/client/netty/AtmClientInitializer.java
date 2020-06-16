package com.example.atm.client.netty;

import com.example.atm.netty.codec.atm.AtmDecoder;
import com.example.atm.netty.codec.atm.AtmEncoder;
import com.example.atm.netty.codec.crypto.CryptoDecoder;
import com.example.atm.netty.codec.crypto.CryptoEncoder;
import com.example.atm.netty.codec.header.HeaderDecoder;
import com.example.atm.netty.codec.header.HeaderEncoder;
import com.example.atm.netty.codec.length.LengthFrameDecoder;
import com.example.atm.netty.codec.length.LengthPrepender;
import com.example.atm.netty.codec.mac.MacDecoder;
import com.example.atm.netty.codec.mac.MacEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class AtmClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new LengthFrameDecoder());
        pipeline.addLast(new CryptoDecoder());
        pipeline.addLast(new MacDecoder());
        pipeline.addLast(new HeaderDecoder());
        pipeline.addLast(new AtmDecoder());

        pipeline.addLast(new LengthPrepender());
        pipeline.addLast(new CryptoEncoder());
        pipeline.addLast(new MacEncoder());
        pipeline.addLast(new HeaderEncoder());
        pipeline.addLast(new AtmEncoder());

        pipeline.addLast(new AtmClientHandler());
    }

}
