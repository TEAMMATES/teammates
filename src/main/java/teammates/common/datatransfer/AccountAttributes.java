package teammates.common.datatransfer;

import java.util.Date;

import teammates.common.Common;
import static teammates.common.Common.EOL;
import teammates.common.FieldValidator;
import teammates.storage.entity.Account;

public class AccountAttributes extends EntityAttributes {
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
	
	public AccountAttributes(Account a) {
		googleId = a.getGoogleId();
		name = a.getName();
		isInstructor = a.isInstructor();
		email = a.getEmail();
		institute = a.getInstitute();
		createdAt = a.getCreatedAt();
	}
	
	public AccountAttributes() {
		
	}
	
	public AccountAttributes(String googleId, String name, boolean isInstructor,
				String email, String institute) {
		this.googleId = Common.trimIfNotNull(googleId);
		this.name = Common.trimIfNotNull(name);
		this.isInstructor = isInstructor;
		this.email = Common.trimIfNotNull(email);
		this.institute = Common.trimIfNotNull(institute);
	}
	
	public Account toEntity() {
		return new Account(googleId, name, isInstructor, email, institute);
	}
	
	public String getInvalidStateInfo() {
		FieldValidator validator = new FieldValidator();
		String errorMessage = 
				validator.getValidityInfo(FieldValidator.FieldType.PERSON_NAME, name) + EOL +
				validator.getValidityInfo(FieldValidator.FieldType.GOOGLE_ID, googleId) + EOL +
				validator.getValidityInfo(FieldValidator.FieldType.EMAIL, email) + EOL +
				validator.getValidityInfo(FieldValidator.FieldType.INSTITUTE_NAME, institute);
		//No validation for isInstructor and createdAt fields.
		return errorMessage.trim();
	}
	
}
