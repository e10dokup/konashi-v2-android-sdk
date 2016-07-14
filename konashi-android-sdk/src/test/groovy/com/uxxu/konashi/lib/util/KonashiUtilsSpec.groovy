import com.uxxu.konashi.lib.util.KonashiUtils
import spock.lang.Specification

/**
 * Created by e10dokup on 1/13/16.
 */
class KonashiUtilsSpec extends Specification {
    def ".getSoftWareRevision()"() {
        expect:
        KonashiUtils.getSoftwareRevision(value) == result

        where:
        value                                        | result
        [0x00] as byte[]                             | ""
        [0x74, 0x65, 0x73, 0x74, 0x00] as byte[]     | "test"
    }
}