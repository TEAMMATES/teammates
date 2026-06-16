package teammates.logic.email.model;

import java.util.List;

/**
 * Data required to recover feedback session links for a given email address.
 *
 * @param recoveryEmailAddress the email address for which the recovery is being performed
 * @param recipientName the name of the email recipient
 * @param noMatchingStudents whether there are no students matching the recovery email address
 * @param recoverableCourseLinks the recoverable course and session links for the recovery email address
 */
public record SessionLinksRecoveryContext(
        String recoveryEmailAddress,
        String recipientName,
        boolean noMatchingStudents,
        List<RecoverableCourseLinks> recoverableCourseLinks) {

    public boolean hasMatchingStudents() {
        return !noMatchingStudents;
    }
}
