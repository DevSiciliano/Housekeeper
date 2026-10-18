package local.noto.housekeeper.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            // REST API with tokens in the Authorization header: the browser never sends them automatically
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize("/api/auth/**", permitAll)
                authorize("/error", permitAll)
                authorize(anyRequest, authenticated)
            }
            // Reads "Authorization: Bearer <token>" and checks it with the JwtDecoder from JwtConfig;
            // a missing, forged or expired token ends in 401 instead of the default 403
            oauth2ResourceServer {
                jwt { }
            }
        }
        return http.build()
    }
}
