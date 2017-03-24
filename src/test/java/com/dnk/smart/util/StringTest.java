package com.dnk.smart.util;

import org.junit.Test;

public class StringTest {

    @Test
    public void replace() throws Exception {
        String s = "abc xyz opq   ";

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            System.out.println(c);
            if (c == 32) {
                System.out.println("--------------");
            }
        }

    }
}
