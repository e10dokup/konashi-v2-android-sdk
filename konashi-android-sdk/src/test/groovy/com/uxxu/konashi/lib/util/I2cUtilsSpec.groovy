package com.uxxu.konashi.lib.util;

import spock.lang.Specification;

/**
 * Created by izumin on 10/7/15.
 */
class I2cUtilsSpec extends Specification {
    def ".isValidMode()"() {
        expect:
        I2cUtils.isValidMode(mode) == result

        where:
        mode    | result
        -1      | false
        0       | true
        1       | true
        2       | true
        3       | false
    }

    def ".isValidCondition()"() {
        expect:
        I2cUtils.isValidCondition(condition) == result

        where:
        condition   | result
        -1          | false
        0           | true
        1           | true
        2           | true
        3           | false
    }

    def ".isValidDataLength()"() {
        expect:
        I2cUtils.isValidDataLength(length) == result

        where:
        length  | result
        0       | false
        1       | true
        16      | true
        17      | false
    }

    def ".isTooShortDataLength()"() {
        expect:
        I2cUtils.isTooShortDataLength(length) == result

        where:
        length  | result
        0       | true
        1       | false
        16      | false
        17      | false
    }

    def ".isTooLongDataLength()"() {
        expect:
        I2cUtils.isTooLongDataLength(length) == result

        where:
        length  | result
        0       | false
        1       | false
        16      | false
        17      | true
    }
}