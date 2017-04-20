package teammates.test.driver;

import teammates.common.util.Assumption;
import teammates.common.util.SanitizationHelper;

/**
 * Holds additional methods for {@link teammates.common.util.FieldValidator} used only in tests.
 */
public class FieldValidator extends teammates.common.util.FieldValidator {

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
    public String getValidityInfoForSizeCappedNonEmptyString(String fieldName, int maxLength, String value) {
        Assumption.assertTrue("Non-null value expected for " + fieldName, value != null);

        if (value.isEmpty()) {
            return getPopulatedErrorMessage(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value,
                    fieldName, REASON_EMPTY, maxLength);
        }
        if (FieldValidator.isUntrimmed(value)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", fieldName);
        }
        String sanitizedValue = SanitizationHelper.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return getPopulatedErrorMessage(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                    sanitizedValue, fieldName, REASON_TOO_LONG, maxLength);
        }
        return "";
    }

}
