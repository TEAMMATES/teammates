package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.AccountAttributes;

import java.time.Instant;

/**
 * Output format of account data.
 */
public class AccountDetailsData extends ApiOutput {

    private final String googleId;
    private final String name;
    private final boolean isInstructor;
    private final String email;
    private final String institute;
    private final Instant createdAt;

    public AccountDetailsData(AccountAttributes accountInfo) {
        this.googleId = accountInfo.getGoogleId();
        this.name = accountInfo.getName();
        this.isInstructor = accountInfo.isInstructor();
        this.email = accountInfo.getEmail();
        this.institute = accountInfo.getInstitute();
        this.createdAt = accountInfo.createdAt;
    }

    public AccountAttributes getAccountInfo() {
        return AccountAttributes.builder()
                .withEmail(this.email)
                .withGoogleId(this.googleId)
                .withName(this.name)
                .withInstitute(this.institute)
                .withIsInstructor(this.isInstructor)
                .withCreatedAt(this.createdAt).build();
    }
}
