package teammates.ui.output;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Output format of account data.
 */
public class AccountData extends ApiOutput {

    ApiOutput apiOutput = new ApiOutput();

    private final String googleId;
    private final boolean isInstructor;
    private final String email;
    private final String institute;
    private final long createdAtTimeStamp;

    public AccountData(AccountAttributes accountInfo) {
        this.googleId = accountInfo.getGoogleId();
        this.apiOutput.name = accountInfo.getName();
        this.isInstructor = accountInfo.isInstructor();
        this.email = accountInfo.getEmail();
        this.institute = accountInfo.getInstitute();
        this.createdAtTimeStamp = accountInfo.getCreatedAt().toEpochMilli();
    }

    public String getInstitute() {
        return institute;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public String getName() {
        return apiOutput.name;
    }

    public boolean isInstructor() {
        return this.isInstructor;
    }
}
