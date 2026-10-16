package local.noto.housekeeper.security

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${housekeeper.jwt.secret}") secret: String
) {

    // HS256 needs at least 256 bits; checked on startup instead of failing on the first login
    private val secretKey: SecretKey = Base64.getDecoder().decode(secret)
        .also { require(it.size >= 32) { "housekeeper.jwt.secret must be at least 32 bytes (Base64-encoded)" } }
        .let { SecretKeySpec(it, "HmacSHA256") }

    // Signs the tokens issued on login
    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey))

    // Checks signature and expiry of every incoming Bearer token; same key, because the server signs and verifies
    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS256)
        .build()
}
