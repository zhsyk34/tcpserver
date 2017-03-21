package com.dnk.smart.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TCP packet protocol
 */
public final class Protocol {
    //header
    public static final List<Byte> HEADERS = Collections.unmodifiableList(Arrays.asList((byte) 0x5A, (byte) 0xA5));
    //footer
    public static final List<Byte> FOOTERS = Collections.unmodifiableList(Arrays.asList((byte) 0xA5, (byte) 0x5A));
    //length byte length
    public static final int LENGTH_BYTES = 2;
    //min dict length
    public static final int MIN_DATA_BYTES = 5;
    //code byte length
    public static final int VERIFY_BYTES = 2;
    //数据部分以外(冗余数据)的长度=8
    public static final int REDUNDANT_BYTES = HEADERS.size() + LENGTH_BYTES + VERIFY_BYTES + FOOTERS.size();
    //total
    public static final int MSG_MIN_LENGTH = REDUNDANT_BYTES + MIN_DATA_BYTES;
}
