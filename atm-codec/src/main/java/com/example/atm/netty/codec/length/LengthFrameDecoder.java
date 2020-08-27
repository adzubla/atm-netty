package com.example.atm.netty.codec.length;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LengthFrameDecoder extends LengthFieldBasedFrameDecoder {

    public static final int MAX_FRAME_LENGTH = 65535;

    public LengthFrameDecoder() {
        super(MAX_FRAME_LENGTH, 0, 2, -2, 2);
    }

}
