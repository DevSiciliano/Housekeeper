package local.noto.housekeeper.exception

// Thrown when a login fails, whether the e-mail is unknown or the password is wrong
class InvalidCredentialsException :
    RuntimeException("Invalid e-mail or password.")
