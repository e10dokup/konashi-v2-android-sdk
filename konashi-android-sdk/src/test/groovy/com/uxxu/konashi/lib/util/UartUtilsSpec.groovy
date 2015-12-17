package com.uxxu.konashi.lib.util

import spock.lang.Specification
/**
 * Created by izumin on 10/7/15.
 */
class UartUtilsSpec extends Specification {
    def ".isValidBaudrate()"() {
        expect:
        UartUtils.isValidBaudrate(baudrate) == result

        where:
        baudrate    | result
        -0x0001     | false
        0x0000      | false
        0x0001      | false
        0x0028      | true
        0x0050      | true
        0x00a0      | true
        0x00f0      | true
        0x0140      | true
        0x01e0      | true
    }

    def ".isValidMode()"() {
        expect:
        UartUtils.isValidMode(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | false
    }

    def ".toFormattedByteArray(String string)"() {
        expect:
        UartUtils.toFormattedByteArray(string) == result

        where:
        string  | result
        ""      | [0] as byte[]
        "test"  | [4, 0x74, 0x65, 0x73, 0x74] as byte[]
    }

    def ".toFormattedByteArray(byte[] bytes)"() {
        expect:
        UartUtils.toFormattedByteArray(bytes) == result

        where:
        bytes                               | result
        [] as byte[]                        | [0] as byte[]
        [0x74, 0x65, 0x73, 0x74] as byte[]  | [4, 0x74, 0x65, 0x73, 0x74] as byte[]
    }

    def ".isValidLength()"() {
        expect:
        UartUtils.isValidLength(length) == result

        where:
        length  | result
        -1      | false
        0       | true
        19      | true
        20      | false
    }

    def ".isLengthTooShort()"() {
        expect:
        UartUtils.isLengthTooShort(length) == result

        where:
        length  | result
        -1      | true
        0       | false
        19      | false
        20      | false
    }

    def ".isLengthTooLong()"() {
        expect:
        UartUtils.isLengthTooLong(length) == result

        where:
        length  | result
        -1      | false
        0       | false
        19      | false
        20      | true
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
