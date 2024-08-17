package io.lb.server.extensions

import org.mindrot.jbcrypt.BCrypt

/**
 * Encrypts a string using the BCrypt algorithm.
 *
 * @return The encrypted string.
 */
fun String.encrypt(): String? {
    val salt = BCrypt.gensalt(12)
    return BCrypt.hashpw(this, salt)
}

/**
 * Checks if a string matches an encrypted password.
 *
 * @param encryptedPassword The encrypted password to check against.
 * @return True if the string matches the encrypted password, false otherwise.
 */
fun String.passwordCheck(encryptedPassword: String): Boolean {
    return BCrypt.checkpw(this, encryptedPassword)
}

/**
 * Checks if a string is a valid email.
 *
 * @return True if the string is a valid email, false otherwise.
 */
fun String?.isValidEmail(): Boolean {
    this ?: return false
    val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
    return isNotBlank() && matches(emailRegex)
}
