package local.noto.housekeeper.exception

// Thrown when a registration uses an email that already belongs to an account
class EmailAlreadyRegisteredException(email: String) :
    RuntimeException("The E-Mail $email is already registered.")
