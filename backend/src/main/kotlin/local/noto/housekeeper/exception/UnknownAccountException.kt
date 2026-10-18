package local.noto.housekeeper.exception

import java.util.UUID

// Thrown when a validly signed token belongs to an account that no longer exists
class UnknownAccountException(id: UUID) :
    RuntimeException("No account with id $id.")
