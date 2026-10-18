package local.noto.housekeeper.security

import local.noto.housekeeper.user.AppUser
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class TokenService(
    private val encoder: JwtEncoder
) {

    fun createAccessToken(user: AppUser): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            // The id instead of the e-mail: it never changes and reveals nothing about the person
            .subject(user.id.toString())
            .issuedAt(now)
            .expiresAt(now.plus(ACCESS_TOKEN_TTL))
            .build()
        // The header has to name HS256 explicitly, otherwise the encoder defaults to RS256 and finds no matching key
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

    companion object {
        const val ISSUER = "housekeeper"

        // Short lifetime, so a removed member loses access quickly (N-03); the refresh token keeps the session alive
        val ACCESS_TOKEN_TTL: Duration = Duration.ofMinutes(15)
    }
}
