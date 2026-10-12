package com.shesafe.database;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PasswordUtil
 * Handles secure cryptographic password hashing and verification.
 * Supports BCrypt (via org.mindrot.jbcrypt.BCrypt when present on classpath)
 * and falls back safely to NIST-compliant PBKDF2WithHmacSHA256 with random salt.
 * Ensures plain-text passwords are NEVER stored in the database.
 */
public class PasswordUtil {

    private static final int PBKDF2_ITERATIONS = 1000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;

    private static Boolean isBcryptAvailable = null;

    /**
     * Checks if the external jBCrypt library is present on the classpath.
     */
    public static boolean hasBcryptSupport() {
        if (isBcryptAvailable == null) {
            try {
                Class.forName("org.mindrot.jbcrypt.BCrypt");
                isBcryptAvailable = true;
            } catch (ClassNotFoundException e) {
                isBcryptAvailable = false;
            }
        }
        return isBcryptAvailable;
    }

    /**
     * Hashes a plain-text password using BCrypt (or PBKDF2 if jBCrypt is not yet on classpath).
     *
     * @param plainPassword User's raw password
     * @return Securely hashed string suitable for database storage
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // 1. Attempt BCrypt hashing if jBCrypt library is available
        if (hasBcryptSupport()) {
            try {
                Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
                Method gensalt = bcryptClass.getMethod("gensalt", int.class);
                Method hashpw = bcryptClass.getMethod("hashpw", String.class, String.class);
                String salt = (String) gensalt.invoke(null, 12);
                return (String) hashpw.invoke(null, plainPassword, salt);
            } catch (Exception ex) {
                System.err.println("[PasswordUtil] BCrypt execution failed, falling back to PBKDF2: " + ex.getMessage());
            }
        }

        // 2. Safe built-in fallback: PBKDF2WithHmacSHA256 with cryptographically random salt
        return hashWithPBKDF2(plainPassword);
    }

    /**
     * Verifies whether a plain-text password matches a stored hash.
     * Supports both BCrypt hashes ($2a$, $2b$, $2y$) and PBKDF2 hashes.
     *
     * @param plainPassword Raw password entered by user
     * @param storedHash Hashed password retrieved from the database
     * @return true if valid match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }

        // Check if stored hash is a standard BCrypt format ($2a$, $2b$, $2y$)
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            if (hasBcryptSupport()) {
                try {
                    Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
                    Method checkpw = bcryptClass.getMethod("checkpw", String.class, String.class);
                    return (Boolean) checkpw.invoke(null, plainPassword, storedHash);
                } catch (Exception e) {
                    System.err.println("[PasswordUtil] Error verifying BCrypt hash: " + e.getMessage());
                    return false;
                }
            } else {
                System.err.println("[PasswordUtil] Warning: BCrypt hash detected but jBCrypt library is not yet added to classpath.");
                return false;
            }
        }

        // Check if stored hash is in PBKDF2 format (iterations:salt:hash)
        if (storedHash.contains(":")) {
            return verifyWithPBKDF2(plainPassword, storedHash);
        }

        // Fallback check (for development/legacy comparisons)
        return plainPassword.equals(storedHash);
    }

    // =========================================================================
    // Built-in PBKDF2 Implementation (Zero External JAR dependency required)
    // =========================================================================

    private static String hashWithPBKDF2(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return PBKDF2_ITERATIONS + ":" + saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password with PBKDF2", e);
        }
    }

    private static boolean verifyWithPBKDF2(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            // Constant-time comparison to prevent timing attacks
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
