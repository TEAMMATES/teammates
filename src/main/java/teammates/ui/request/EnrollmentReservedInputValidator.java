package teammates.ui.request;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;

/**
 * Ensures user-supplied team and section values are not equivalent to reserved system identifiers.
 */
public final class EnrollmentReservedInputValidator {

    public static final String ERROR_RESERVED_TEAM_NAME =
            "This team name is reserved for the system. Please choose a different team name.";
    public static final String ERROR_RESERVED_SECTION_NAME =
            "This section name is reserved for the system. Please choose a different section name.";

    /**
     * Prevents instantiation.
     */
    private EnrollmentReservedInputValidator() {
    }

    /**
     * Rejects team/section values that match reserved system identifiers.
     *
     * @param teamName team name from the request
     * @param sectionInput raw section from the form (empty means the default section will apply)
     */
    public static void assertTeamAndSectionNotReserved(String teamName, String sectionInput)
            throws InvalidHttpRequestBodyException {
        List<String> errors = new ArrayList<>();
        if (Const.USER_TEAM_FOR_INSTRUCTOR.equals(teamName)) {
            errors.add(ERROR_RESERVED_TEAM_NAME);
        }
        if (!sectionInput.isEmpty() && Const.DEFAULT_SECTION.equals(sectionInput)) {
            errors.add(ERROR_RESERVED_SECTION_NAME);
        }
        if (!errors.isEmpty()) {
            throw new InvalidHttpRequestBodyException(String.join(" ", errors));
        }
    }
}
