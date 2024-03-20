package teammates.client.scripts.testdataconversion;

import java.util.UUID;

public class UUIDGenerator {
    int currId;
    String UUIDPrefix;

    protected UUIDGenerator(int startId, String UUIDPrefix) {
        this.currId = startId;
        this.UUIDPrefix = UUIDPrefix;
    }


    private String leftPad(int digits, String string, Character paddingChar) {
        return String.format("%10s", string).replace(' ', paddingChar);
    }

    protected UUID generateUUID() {
        String trailingUUID = leftPad(12, Integer.toString(this.currId), '0');
        UUID uuid = UUID.fromString(UUIDPrefix + trailingUUID);
        this.currId += 1;
        return uuid;
    }
}
