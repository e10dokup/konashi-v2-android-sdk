package com.uxxu.konashi.lib.util

import spock.lang.Specification

class AioUtilsSpec extends Specification {
    def ".isValidPin"() {
        expect:
        AioUtils.isValidPin(pin) == result

        where:
        pin | result
        -1  | false
        0   | true
        1   | true
        2   | true
        3   | false
    }

    def ".getAnalogValue()"() {
        expect:
        AioUtils.getAnalogValue(pin, value) == result

        where:
        pin | value                     || result
        0   | [0x03, 0xe8] as byte[]    || 1000
        1   | [0x00, 0x64] as byte[]    || 100
        2   | [0x27, 0x10] as byte[]    || 10000
    }
}