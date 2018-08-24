package com.oneandone.ejbcdiunit5.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
public class FirstJunit5Test {
    @Test
    void myFirstTest() {
        assertEquals(2,1+1);
    }

    static int repeatcount = 0;

    @RepeatedTest(5)
    void myFirstRepeatedTest() {
        assertEquals(2 + repeatcount,1+1 + repeatcount);
    }

    @RepeatedTest(5)
    void myFirstRepeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo, TestInfo testInfo) {
        repeatcount++;
        assertEquals(2 + repeatcount,1+1 + repetitionInfo.getCurrentRepetition());
    }


}
