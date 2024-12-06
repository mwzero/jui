package com.jui.utils;

import java.nio.charset.StandardCharsets;

public class WS {
	
	public static byte[] encodeWebSocketFrame(String message) {
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
        int frameLength = rawData.length + 2;

        byte[] frame = new byte[frameLength];
        frame[0] = (byte) 0x81; 
        frame[1] = (byte) rawData.length; 

        System.arraycopy(rawData, 0, frame, 2, rawData.length);

        return frame;
    }

}
