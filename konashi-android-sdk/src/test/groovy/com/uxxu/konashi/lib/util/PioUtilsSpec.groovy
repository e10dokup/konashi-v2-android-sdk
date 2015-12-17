package com.uxxu.konashi.lib.util

import spock.lang.Specification;

/**
 * Created by izumin on 10/7/15.
 */
class PioUtilsSpec extends Specification {
    def ".isValidPin()"() {
        expect:
        PioUtils.isValidPin(pin) == result

        where:
        pin | result
        -1  | false
        0   | true
        1   | true
        2   | true
        3   | true
        4   | true
        5   | true
        6   | true
        7   | true
        8   | false
    }

    def ".isValidMode()"() {
        expect:
        PioUtils.isValidMode(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | false
    }

    def ".isValidModes()"() {
        expect:
        PioUtils.isValidModes(modes) == result

        where:
        modes   | result
        -0x01   | false
        0x00    | true
        0x04    | true
        0xaa    | true
        0xff    | true
        0x100   | false
    }

    def ".isValidPullup()"() {
        expect:
        PioUtils.isValidPullup(pullup) == result

        where:
        pullup  | result
        -1      | false
        0       | true
        1       | true
        2       | false
    }

    def ".isValidPullups()"() {
        expect:
        PioUtils.isValidPullups(pullups) == result

        where:
        pullups | result
        -0x01   | false
        0x00    | true
        0x04    | true
        0xaa    | true
        0xff    | true
        0x100   | false
    }

    def ".isValidOutput()"() {
        expect:
        PioUtils.isValidOutput(output) == result

        where:
        output  | result
        -1      | false
        0       | true
        1       | true
        2       | false
    }

    def ".isValidOutputs()"() {
        expect:
        PioUtils.isValidOutputs(outputs) == result

        where:
        outputs | result
        -0x01   | false
        0x00    | true
        0x04    | true
        0xaa    | true
        0xff    | true
        0x100   | false
    }
}
