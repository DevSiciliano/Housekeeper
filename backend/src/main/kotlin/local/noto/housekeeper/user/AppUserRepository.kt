package local.noto.housekeeper.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AppUserRepository: JpaRepository<AppUser, UUID> {

    fun findByEmail(email: String): AppUser?

    fun existsByEmail(email: String): Boolean
}