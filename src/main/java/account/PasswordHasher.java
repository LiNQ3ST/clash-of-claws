package account;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Securely hashes and verifies player passwords using PBKDF2.
 *
 * @author Sahtra Green
 * @version 0.1.0
 * @since 8/5/2026
 */

public class PasswordHasher {

    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final int ITERATION_COUNT = 600_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private PasswordHasher() {
        // Utility class.
    }


    public static String hash(String password) {
        validatePassword(password);

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] derivedHash = deriveHash(
                password,
                salt,
                ITERATION_COUNT,
                KEY_LENGTH_BITS
        );

        return ITERATION_COUNT + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derivedHash);
    }

    /**
     * Checks whether an entered password matches a stored hash.
     *
     * @param password entered plain-text password
     * @param storedValue value previously returned by hash()
     * @return true when the password matches
     */
    public static boolean matches(
            String password,
            String storedValue
    ) {
        if (password == null
                || storedValue == null
                || storedValue.isBlank()) {
            return false;
        }

        try {
            String[] parts = storedValue.split(":", 3);

            if (parts.length != 3) {
                return false;
            }

            int iterationCount =
                    Integer.parseInt(parts[0]);

            byte[] salt =
                    Base64.getDecoder().decode(parts[1]);

            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[2]);

            if (iterationCount <= 0
                    || salt.length == 0
                    || expectedHash.length == 0) {
                return false;
            }

            byte[] actualHash = deriveHash(
                    password,
                    salt,
                    iterationCount,
                    expectedHash.length * Byte.SIZE
            );

            return MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );
        } catch (IllegalArgumentException exception) {
            // Covers malformed iteration counts and Base64 values.
            return false;
        }
    }

    /**
     * Derives a password hash using the supplied PBKDF2 settings.
     */
    private static byte[] deriveHash(
            String password,
            byte[] salt,
            int iterationCount,
            int keyLengthBits
    ) {
        char[] passwordCharacters =
                password.toCharArray();

        PBEKeySpec keySpec = new PBEKeySpec(
                passwordCharacters,
                salt,
                iterationCount,
                keyLengthBits
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory
                    .generateSecret(keySpec)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Unable to hash password.",
                    exception
            );
        } finally {
            keySpec.clearPassword();
            Arrays.fill(passwordCharacters, '\0');
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be blank."
            );
        }
    }

}
