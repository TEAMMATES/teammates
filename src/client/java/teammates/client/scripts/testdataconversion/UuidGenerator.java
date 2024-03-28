package teammates.client.scripts.testdataconversion;

import java.util.UUID;

/**
 * Generator that counts up to generate an ID for an entity.
 */
public class UuidGenerator {
    int currId;
    String uuidPrefix;

    protected UuidGenerator(int startId, String uuidPrefix) {
        this.currId = startId;
        this.uuidPrefix = uuidPrefix;
    }

    private String leftPad(int digits, String string, Character paddingChar) {
        return String.format("%" + digits + "s", string).replace(' ', paddingChar);
    }

    /**
     * Generates an ID for the test entity.
     * This does not guarantee uniqueness between entities and is merely a counter
     */
    protected UUID generateUuid() {
        String trailingUuid = leftPad(12, Integer.toString(this.currId), '0');
        UUID uuid = UUID.fromString(uuidPrefix + trailingUuid);
        this.currId += 1;
        return uuid;
    }
}
