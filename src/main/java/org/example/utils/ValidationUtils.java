package org.example.utils;

import org.example.model.exceptions.InvalidAmountException;

/**
 * Utility class providing static validation helpers used across the application.
 * <p>
 * All methods are stateless and follow the single-responsibility principle.
 * They throw checked exceptions so callers are forced to handle errors explicitly.
 * </p>
 */
public final class ValidationUtils {

    /** Private constructor — this is a utility class, not meant to be instantiated. */
    private ValidationUtils() {}

    /**
     * Validates that a monetary {@code amount} is strictly positive.
     *
     * @param amount the amount to validate
     * @throws InvalidAmountException if {@code amount} is zero or negative
     */
    public static void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Invalid amount. Amount must be greater than 0. Received: " + amount);
        }
    }

    /**
     * Validates that a {@code name} string is not null or blank.
     *
     * @param name  the name to validate
     * @param field the field label used in the error message (e.g. "Customer Name")
     * @throws IllegalArgumentException if {@code name} is null or blank
     */
    public static void validateName(String name, String field) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
    }

    /**
     * Validates that an {@code age} is within an acceptable range (0–120).
     *
     * @param age the age to validate
     * @throws IllegalArgumentException if {@code age} is outside [0, 120]
     */
    public static void validateAge(int age) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException(
                    "Age must be between 0 and 120. Received: " + age);
        }
    }

    /**
     * Validates that an account number string is non-null and follows the
     * expected {@code ACCnnn} format.
     *
     * @param accountNumber the account number to validate
     * @throws IllegalArgumentException if {@code accountNumber} is null, blank, or malformed
     */
    public static void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (!accountNumber.matches("ACC\\d{3}")) {
            throw new IllegalArgumentException(
                    "Account number must follow the format ACC### (e.g. ACC001). "
                    + "Received: " + accountNumber);
        }
    }
}
