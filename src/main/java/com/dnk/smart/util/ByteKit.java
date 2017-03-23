package com.dnk.smart.util;

import io.netty.buffer.ByteBuf;

public class ByteKit {

    private static final int MARK = 0xff;

    public static int byteArrayToInt(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("byte array is isEmpty.");
        }
        if (bytes.length > 4) {
            throw new RuntimeException("byte array too long.");
        }
        int result = 0, offset = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            result |= (bytes[i] & MARK) << (offset++ << 3);
        }
        return result;
    }

    private static byte[] intToByteArray(int i, int length) {
        if (length < 0 || length > 4) {
            throw new RuntimeException("integer range 1 - 4 bit.");
        }

        byte[] bytes = new byte[length];
        for (int k = 0; k < length; k++) {
            bytes[k] = (byte) (i >> ((length - 1 - k) << 3) & MARK);
        }
        return bytes;
    }

    public static byte[] intToByteArray(int i) {
        return intToByteArray(i, 4);
    }

    public static byte[] smallIntToByteArray(int i) {
        return intToByteArray(i, 2);
    }

    public static byte[] tinyToByteArray(int i) {
        return intToByteArray(i, 1);
    }

    public static String bytesToHex(byte[] bytes, String separator) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b & MARK).toUpperCase());
            if (separator != null && !separator.isEmpty()) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, null);
    }

    //TODO
    public static byte[] hexToBytes(String str) {
        if (str.length() < 1)
            return null;

        str = str.toUpperCase();

        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            int high = Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(str.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }

        return result;
    }

    public static boolean compare(byte[] src, byte[] dest) {
        if (src == null || dest == null) {
            return src == dest;
        }
        if (src.length != dest.length) {
            return false;
        }

        for (int i = 0; i < src.length; i++) {
            if (src[i] != dest[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte[] fillZero(byte[] original) {
        if (original == null) {
            return null;
        }

        int length = original.length;
        if (length % 16 == 0) {
            return original;
        }

        byte[] result = new byte[length + 16 - length % 16];
        System.arraycopy(original, 0, result, 0, length);
        return result;
    }

    public static byte[] trim(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }

        int actual = 0;

        for (byte b : bytes) {
            if (b == 0) {
                break;
            }
            actual++;
        }

        if (actual <= 0) {
            return null;
        }

        byte[] result = new byte[actual];
        System.arraycopy(bytes, 0, result, 0, actual);

        return result;
    }

    public static byte[] getBytes(ByteBuf buf) {
        if (buf.hasArray()) {
            return buf.array();
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        return bytes;
    }

}
