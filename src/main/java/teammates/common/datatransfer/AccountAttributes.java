package teammates.common.datatransfer;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.storage.entity.Account;

/**
 * A data transfer object for Account entities.
 */
public class AccountAttributes extends EntityAttributes {
	
	//Note: be careful when changing these variables as their names are used in *.json files.
	
	public String googleId;
	public String name;
	public boolean isInstructor;
	public String email;
	public String institute;
	public Date createdAt;
	
	
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
			//TODO: this method should follow our normal sanitization policy
			// (when we have one).
				String email, String institute) {
		this.googleId = Common.trimIfNotNull(googleId);
		this.name = Common.trimIfNotNull(name);
		this.isInstructor = isInstructor;
		this.email = Common.trimIfNotNull(email);
		this.institute = Common.trimIfNotNull(institute);
	}
	
	public List<String> getInvalidStateInfo() {
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getValidityInfo(FieldValidator.FieldType.PERSON_NAME, name);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfo(FieldValidator.FieldType.GOOGLE_ID, googleId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfo(FieldValidator.FieldType.EMAIL, email);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfo(FieldValidator.FieldType.INSTITUTE_NAME, institute);
		if(!error.isEmpty()) { errors.add(error); }
		
		//No validation for isInstructor and createdAt fields.
		return errors;
	}

	public Account toEntity() {
		return new Account(googleId, name, isInstructor, email, institute);
	}
	
	//TODO: implement toString method
	
}
