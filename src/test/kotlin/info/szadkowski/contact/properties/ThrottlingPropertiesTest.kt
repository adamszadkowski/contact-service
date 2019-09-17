package info.szadkowski.contact.properties

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Duration

@ExtendWith(SpringExtension::class)
@ContextConfiguration
@TestPropertySource(
    properties = [
        "throttling.clearExpiredRate=5s",
        "throttling.ip.limit=1",
        "throttling.ip.window=5s",
        "throttling.all.limit=2",
        "throttling.all.window=10s"
    ]
)
@EnableConfigurationProperties(ThrottlingProperties::class)
class ThrottlingPropertiesTest {

    @Test
    fun `Should inject throttling configuration`(@Autowired p: ThrottlingProperties) {
        expectThat(p) {
            get { clearExpiredRate }.isEqualTo(Duration.ofSeconds(5))
            get { ip }
                .isNotNull()
                .and {
                    get { limit }.isEqualTo(1)
                    get { window }.isEqualTo(Duration.ofSeconds(5))
                }
            get { all }
                .isNotNull()
                .and {
                    get { limit }.isEqualTo(2)
                    get { window }.isEqualTo(Duration.ofSeconds(10))
                }
        }
    }
}
