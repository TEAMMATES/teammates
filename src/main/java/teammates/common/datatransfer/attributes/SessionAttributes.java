package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.util.Comparator;

import teammates.common.util.Assumption;

/**
 * Interface for Sessions, to be used for grouping
 * sessions together and sort them based on common attributes (time and name).
 *
 * <p>Current implementing classes: {@link FeedbackSessionAttributes}
 */
public interface SessionAttributes {

    /**
     * Comparator to sort SessionAttributes on ASCENDING order based on
     * end time, followed by start time and session name.
     */
    Comparator<SessionAttributes> ASCENDING_ORDER = (session1, session2) -> {

        Assumption.assertNotNull(session1.getSessionName());
        Assumption.assertNotNull(session1.getSessionStartTime());
        Assumption.assertNotNull(session1.getSessionEndTime());
        Assumption.assertNotNull(session2.getSessionName());
        Assumption.assertNotNull(session2.getSessionStartTime());
        Assumption.assertNotNull(session2.getSessionEndTime());
        int result = 0;

        //Compares end times
        result = session1.getSessionEndTime().isAfter(session2.getSessionEndTime()) ? 1
                : session1.getSessionEndTime().isBefore(session2.getSessionEndTime()) ? -1 : 0;

        //If the end time is same, compares start times
        if (result == 0) {
            result = session1.getSessionStartTime().isAfter(session2.getSessionStartTime()) ? 1
                    : session1.getSessionStartTime().isBefore(session2.getSessionStartTime()) ? -1 : 0;
        }

        //if both end and start time is same, compares session name
        if (result == 0) {
            result = session1.getSessionName().compareTo(session2.getSessionName());
        }
        return result;

    };

    /**
     * Comparator to sort SessionAttributes on DESCENDING order based on
     * end time, followed by start time and session name.
     */
    Comparator<SessionAttributes> DESCENDING_ORDER = (session1, session2) -> {

        Assumption.assertNotNull(session1.getSessionName());
        Assumption.assertNotNull(session1.getSessionStartTime());
        Assumption.assertNotNull(session1.getSessionEndTime());
        Assumption.assertNotNull(session2.getSessionName());
        Assumption.assertNotNull(session2.getSessionStartTime());
        Assumption.assertNotNull(session2.getSessionEndTime());
        int result = 0;

        //Compares end times
        result = session1.getSessionEndTime().isAfter(session2.getSessionEndTime()) ? -1
                : session1.getSessionEndTime().isBefore(session2.getSessionEndTime()) ? 1 : 0;

        //If the end time is same, compares start times
        if (result == 0) {
            result = session1.getSessionStartTime().isAfter(session2.getSessionStartTime()) ? -1
                    : session1.getSessionStartTime().isBefore(session2.getSessionStartTime()) ? 1 : 0;
        }

        //if both end and start time is same, compares session name
        if (result == 0) {
            result = session1.getSessionName().compareTo(session2.getSessionName());
        }
        return result;
    };

    Instant getSessionStartTime();

    Instant getSessionEndTime();

    String getSessionName();

}
