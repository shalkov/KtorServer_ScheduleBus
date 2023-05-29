package ru.shalkoff.bus_schedule.auth

import org.mindrot.jbcrypt.BCrypt

class PasswordEncryptorImpl : PasswordEncryptor {

    override fun validatePassword(password: String, passwordHash: String): Boolean {
        return BCrypt.checkpw(password, passwordHash)
    }

    override fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}

interface PasswordEncryptor {
    fun validatePassword(password: String, passwordHash: String): Boolean
    fun encryptPassword(password: String): String
}