package teammates.common.datatransfer;

import java.util.Date;

import teammates.common.Common;
import teammates.storage.entity.Account;

public class AccountData extends BaseData {
	public String googleId;
	public String name;
	public boolean isInstructor;
	
	// Other Information
	public String email;
	public String institute;
	
	public Date createdAt;
	
	public static final String ERROR_FIELD_ID = "GoogleID Field is invalid\n";
	public static final String ERROR_FIELD_NAME = "Name field cannot be null or empty\n";
	public static final String ERROR_FIELD_EMAIL = "Email Field is invalid\n";
	public static final String ERROR_FIELD_INSTITUTE = "Institute field cannot be null or empty\n";
	
	public AccountData(Account a) {
		googleId = a.getGoogleId();
		name = a.getName();
		isInstructor = a.isInstructor();
		email = a.getEmail();
		institute = a.getInstitute();
		createdAt = a.getCreatedAt();
	}
	
	public AccountData() {
		
	}
	
	public AccountData(String googleId, String name, boolean isInstructor,
				String email, String institute) {
		this.googleId = trimIfNotNull(googleId);
		this.name = trimIfNotNull(name);
		this.isInstructor = isInstructor;
		this.email = trimIfNotNull(email);
		this.institute = trimIfNotNull(institute);
	}
	
	public Account toEntity() {
		return new Account(googleId, name, isInstructor, email, institute);
	}
	
	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidGoogleId(googleId)) {
			errorMessage += ERROR_FIELD_ID;
		}
	
		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		}
		
		if (!Common.isValidEmail(email)) {
			errorMessage += ERROR_FIELD_EMAIL;
		}
			
		if (!Common.isValidName(institute)) {
			errorMessage += ERROR_FIELD_INSTITUTE;
		}
		return errorMessage;

	}
}
