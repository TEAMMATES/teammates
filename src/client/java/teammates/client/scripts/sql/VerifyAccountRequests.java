package teammates.client.scripts.sql;

import teammates.storage.sqlentity.AccountRequest;

public class VerifyAccountRequests extends VerifyNonCourseEntityAttributesBaseScript<
    teammates.storage.entity.AccountRequest, AccountRequest> {

	public VerifyAccountRequests() {
		super(
            teammates.storage.entity.AccountRequest.class,
            AccountRequest.class);
	}

	@Override
	protected String generateID(AccountRequest sqlEntity) {
        return teammates.storage.entity.AccountRequest.generateId(
            sqlEntity.getEmail(), sqlEntity.getInstitute());
	}

    public static void main(String[] args) {
        VerifyAccountRequests script = new VerifyAccountRequests();
        script.doOperationRemotely();
    }
}
