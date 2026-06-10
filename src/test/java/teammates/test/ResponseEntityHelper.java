package teammates.test;

import teammates.common.util.Const;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;

/**
 * Test-only helper for computing the legacy email/team-name identifier of a response giver or recipient.
 *
 * <p>Production code identifies givers/recipients by UUID and type (see
 * {@link ResponseGiver#getKey()} / {@link ResponseRecipient#getKey()}); this helper exists only so
 * that tests can keep matching responses against expected email/team-name based test data.
 */
public final class ResponseEntityHelper {

    private ResponseEntityHelper() {
        // utility class
    }

    /**
     * Gets the giver identifier: team name for team givers, user email for user givers.
     */
    public static String getIdentifier(ResponseGiver giver) {
        if (giver.getGiverTeam() != null) {
            return giver.getGiverTeam().getName();
        }
        if (giver.getGiverUser() != null) {
            return giver.getGiverUser().getEmail();
        }
        return Const.UNKNOWN_USER;
    }

    /**
     * Gets the recipient identifier: team name for team recipients, user email for user recipients.
     */
    public static String getIdentifier(ResponseRecipient recipient) {
        switch (recipient.getRecipientType()) {
        case TEAM:
            return recipient.getRecipientTeam() == null
                    ? Const.UNKNOWN_TEAM : recipient.getRecipientTeam().getName();
        case STUDENT, INSTRUCTOR:
            return recipient.getRecipientUser() == null
                    ? Const.UNKNOWN_USER : recipient.getRecipientUser().getEmail();
        case NO_SPECIFIC_RECIPIENT:
        default:
            return Const.GENERAL_QUESTION;
        }
    }
}
