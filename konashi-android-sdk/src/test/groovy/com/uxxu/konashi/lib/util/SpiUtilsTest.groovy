package com.uxxu.konashi.lib.util

import spock.lang.Specification
/**
 * Created by izumin on 10/7/15.
 */
class SpiUtilsTest extends Specification {
    def ".isValidMode()"() {
        expect:
        SpiUtils.isValidMode(mode) == result

        where:
        mode    | result
        -2      | false
        -1      | true
        0       | true
        1       | true
        2       | true
        3       | true
        4       | false
    }

    def ".isEnabled()"() {
        expect:
        SpiUtils.isEnabled(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | true
        3       | true
        4       | false
    }

    def ".isValidSpeed()"() {
        expect:
        SpiUtils.isValidSpeed(speed) == result

        where:
        speed   | result
        -1      | false
        0       | false
        1       | false
        20      | true
        30      | false
        50      | true
        100     | true
        200     | true
        300     | true
        400     | false
        500     | false
        600     | true
        700     | false
    }

    def ".isValidEndianness()"() {
        expect:
        SpiUtils.isValidEndianness(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | false
    }

    def ".isLengthTooShort()"() {
        expect:
        SpiUtils.isTooShortData(length) == result

        where:
        length  | result
        0       | true
        1       | false
        64      | false
        65      | false
    }

    def ".isLengthTooLong()"() {
        expect:
        SpiUtils.isTooLongData(length) == result

        where:
        length  | result
        0       | false
        1       | false
        64      | false
        65      | true
    }

    def ".removeLengthByte()"() {
        expect:
        UartUtils.removeLengthByte(bytes) == result

        where:
        bytes                                  | result
        [0] as byte[]                          | [] as byte[]
        [4, 0x74, 0x65, 0x73, 0x74] as byte[]  | [0x74, 0x65, 0x73, 0x74] as byte[]
    }
}
