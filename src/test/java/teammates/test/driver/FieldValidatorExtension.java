package teammates.test.driver;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;

/**
 * Holds additional methods for {@link teammates.common.util.FieldValidator} used only in tests.
 */
public final class FieldValidatorExtension {

    private FieldValidatorExtension() {
        // utility class
    }

    /**
     * Checks if the given string is a non-null non-empty string no longer than
     * the specified length {@code maxLength}.
     *
     * @param fieldName
     *            A descriptive name of the field e.g., "student name", to be
     *            used in the return value to make the explanation more
     *            descriptive.
     * @param value
     *            The string to be checked.
     * @return An explanation of why the {@code value} is not acceptable.
     *         Returns an empty string "" if the {@code value} is acceptable.
     */
    public static String getValidityInfoForSizeCappedNonEmptyString(String fieldName, int maxLength, String value) {
        Assumption.assertNotNull("Non-null value expected for " + fieldName, value);

        if (value.isEmpty()) {
            return FieldValidator.getPopulatedEmptyStringErrorMessage(
                    FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING, fieldName, maxLength);
        }
        if (FieldValidator.isUntrimmed(value)) {
            return FieldValidator.WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", fieldName);
        }
        String sanitizedValue = SanitizationHelper.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return FieldValidator.getPopulatedErrorMessage(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                    sanitizedValue, fieldName, FieldValidator.REASON_TOO_LONG, maxLength);
        }
        return "";
    }

}
