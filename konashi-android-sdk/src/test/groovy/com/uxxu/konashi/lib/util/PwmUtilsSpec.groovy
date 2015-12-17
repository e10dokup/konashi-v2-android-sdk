package com.uxxu.konashi.lib.util;

import spock.lang.Specification;

/**
 * Created by izumin on 10/7/15.
 */
class PwmUtilsSpec extends Specification {
    def ".isValidPin()"() {
        expect:
        PwmUtils.isValidPin(pin) == result

        where:
        pin | result
        -1  | false
        0   | true
        1   | true
        2   | true
        3   | false
    }

    def ".isValidMode()"() {
        expect:
        PwmUtils.isValidMode(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | true
        3       | false
    }

    def ".isValidPeriod()"() {
        expect:
        PwmUtils.isValidPeriod(period, duty) == result

        where:
        period  | duty  || result
        0       | 0     || true
        1       | 0     || true
        1       | 1     || true
        0       | 1     || false
        0       | -1    || false
        -1      | 0     || false
        -1      | -2    || false
    }

    def ".isValidDuty()"() {
        expect:
        PwmUtils.isValidDuty(duty, period) == result

        where:
        duty    | period    || result
        0       | 0         || true
        0       | 1         || true
        1       | 0         || false
        1       | 1         || true
        -1      | 0         || false
        0       | -1        || false
        -2      | -1        || false
    }

    def ".isValidDutyRatio()"() {
        expect:
        PwmUtils.isValidDutyRatio(dutyRatio) == result;

        where:
        dutyRatio   | result
        -1          | false
        0           | true
        50          | true
        100         | true
        101         | false
    }

    def ".getPwmDuty()"() {
        expect:
        PwmUtils.getPwmDuty(value) == result

        where:
        value                                       | result
        [0x00, 0x00, 0x00, 0x00, 0x00] as byte[]    | 0
        [0x01, 0x00, 0x00, 0x27, 0x10] as byte[]    | 10000
        [0x02, 0x00, 0x01, 0x86, 0xa0] as byte[]    | 100000
        [0x03, 0x00, 0x0f, 0x42, 0x40] as byte[]    | 1000000
    }

    def ".getPwmPeriod()"() {
        expect:
        PwmUtils.getPwmPeriod(value) == result

        where:
        value                                       | result
        [0x00, 0x00, 0x00, 0x00, 0x00] as byte[]    | 0
        [0x01, 0x00, 0x00, 0x27, 0x10] as byte[]    | 10000
        [0x02, 0x00, 0x01, 0x86, 0xa0] as byte[]    | 100000
        [0x03, 0x00, 0x0f, 0x42, 0x40] as byte[]    | 1000000
    }
}
