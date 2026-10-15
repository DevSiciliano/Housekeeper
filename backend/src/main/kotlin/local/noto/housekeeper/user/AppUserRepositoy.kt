package local.noto.housekeeper.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AppUserRepositoy: JpaRepository<AppUser, UUID> {

    fun findByEmail(email: String): AppUser?

    fun existByEmail(email: String): Boolean
}