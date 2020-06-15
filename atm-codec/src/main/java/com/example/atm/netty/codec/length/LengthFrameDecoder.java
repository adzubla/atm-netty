package com.example.atm.netty.codec.length;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LengthFrameDecoder extends LengthFieldBasedFrameDecoder {

    public LengthFrameDecoder() {
        super(65535, 0, 2, 0, 2);
    }

}
