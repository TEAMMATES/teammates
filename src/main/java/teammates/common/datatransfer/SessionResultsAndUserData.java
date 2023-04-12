package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teammates.common.util.Const;

/**
 * Bundles both SessionResultsBundle
 * and randomly generated identifier groups for anonymous participants.
 */
public class SessionResultsAndUserData {

    private final SessionResultsBundle bundle;

    private Map<String, String> identifierMap;

    public SessionResultsAndUserData(SessionResultsBundle bundle) {
        this.bundle = bundle;
        this.identifierMap = new HashMap<>();
    }

    /**
     * Creates new randomly generated identifier for anonymous participant,
     * or get existing identifier if it has already been generated before.
     */
    public String getNewIdentifier(String identifier, FeedbackParticipantType type) {
        if (!identifierMap.containsKey(identifier)) {
            String randomIdentifier = UUID.randomUUID().toString();
            String participantType = type.toSingularFormString();
            String newIdentifier = String.format("%s %s %s",
                    Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT,
                    participantType,
                    randomIdentifier);

            identifierMap.put(identifier, newIdentifier);
        }

        return identifierMap.get(identifier);
    }

    public SessionResultsBundle getBundle() {
        return bundle;
    }
}
